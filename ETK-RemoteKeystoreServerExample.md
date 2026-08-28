# ETK Remote Keystore server EXAMPLE

> [!CAUTION]
> This Python server is provided for demonstration purposes only. 
> It is intended to illustrate and demonstrate the underlying protocol and its usage.
> 
> This implementation is not production-ready software and should not be used in production environments. 
> It may lack important features such as security hardening, comprehensive error handling, scalability, reliability, 
> and proper configuration for real-world deployments.
> 
> Use this example as a reference for understanding the protocol and building your own production-ready implementation.

```py
# This Python server is provided for demonstration purposes only. 
# It is intended to illustrate and demonstrate the underlying protocol and its usage.
# 
# This implementation is not production-ready software and should not be used in production environments. 
# It may lack important features such as security hardening, comprehensive error handling, scalability, reliability, 
# and proper configuration for real-world deployments.
# 
# Use this example as a reference for understanding the protocol and building your own production-ready implementation.

from http.server import BaseHTTPRequestHandler, HTTPServer
from pathlib import Path
from urllib.parse import urlparse, unquote, parse_qs
import argparse
import base64
import hashlib
import ssl

HOST = "0.0.0.0"
PORT = 8080

BASE_DIR = Path("files")
BASE_DIR.mkdir(parents=True, exist_ok=True)


def user_storage_id(username: str, password: str) -> str:
    """Generate the user's SHA-256 storage identifier."""
    value = f"{username}:{password}".encode("utf-8")
    return hashlib.sha256(value).hexdigest()


def file_sha256(path: Path) -> str:
    """Calculate a file's SHA-256 hash without loading it entirely into RAM."""
    digest = hashlib.sha256()

    with path.open("rb") as f:
        while chunk := f.read(1024 * 1024):
            digest.update(chunk)

    return digest.hexdigest()


def safe_join(base: Path, requested_path: str) -> Path | None:
    """Resolve a relative path while preventing directory traversal."""
    requested_path = unquote(requested_path)

    if requested_path.startswith("/"):
        return None

    candidate = (base / requested_path).resolve()
    base = base.resolve()

    try:
        candidate.relative_to(base)
    except ValueError:
        return None

    return candidate


class AuthError(Exception):
    pass


def authenticate(handler: BaseHTTPRequestHandler) -> tuple[str, str]:
    """Read the Authorization header and return username and password."""
    header = handler.headers.get("Authorization")

    if not header:
        raise AuthError("Missing Authorization header")

    if not header.startswith("Basic "):
        raise AuthError("Unsupported authentication method")

    encoded = header[6:].strip()

    try:
        decoded = base64.b64decode(encoded, validate=True).decode("utf-8")
    except (ValueError, UnicodeDecodeError):
        raise AuthError("Invalid Authorization header")

    if ":" not in decoded:
        raise AuthError("Invalid credentials")

    username, password = decoded.split(":", 1)

    if not username:
        raise AuthError("Empty username")

    return username, password


class FileServer(BaseHTTPRequestHandler):

    def require_auth(self):
        """Authenticate the request and initialize the user's storage directory."""
        try:
            username, password = authenticate(self)
        except AuthError as e:
            self.send_response(401)
            self.send_header("WWW-Authenticate", 'Basic realm="ETK-RemoteKeystoreServer"')
            self.send_header("Content-Type", "text/plain; charset=utf-8")
            self.end_headers()
            self.wfile.write(f"Unauthorized: {e}\n".encode())
            return False

        self.username = username
        self.password = password

        storage_id = user_storage_id(username, password)
        self.user_dir = BASE_DIR / storage_id
        self.user_dir.mkdir(parents=True, exist_ok=True)

        return True

    def get_file_path(self):
        """Convert the request path into a path inside the user's storage directory."""
        parsed = urlparse(self.path)
        requested = parsed.path.lstrip("/")

        if not requested:
            return None

        return safe_join(self.user_dir, requested)

    def send_text(self, status: int, text: str):
        data = text.encode("utf-8")

        self.send_response(status)
        self.send_header("Content-Type", "text/plain; charset=utf-8")
        self.send_header("Content-Length", str(len(data)))
        self.end_headers()

        self.wfile.write(data)

    def do_PUT(self):
        if not self.require_auth():
            return

        file_path = self.get_file_path()

        if file_path is None:
            self.send_error(400, "Invalid file name")
            return

        content_length = self.headers.get("Content-Length")

        if content_length is None:
            self.send_error(411, "Content-Length required")
            return

        try:
            content_length = int(content_length)
        except ValueError:
            self.send_error(400, "Invalid Content-Length")
            return

        if content_length < 0:
            self.send_error(400, "Invalid Content-Length")
            return

        try:
            data = self.rfile.read(content_length)
        except OSError as e:
            self.send_error(400, f"Error while reading request: {e}")
            return

        try:
            file_path.parent.mkdir(parents=True, exist_ok=True)
            file_path.write_bytes(data)
        except OSError as e:
            self.send_error(500, f"Error while saving file: {e}")
            return

        self.send_text(200, "OK\n")

    def do_GET(self):
        if not self.require_auth():
            return

        parsed = urlparse(self.path)
        query = parse_qs(parsed.query)

        file_path = self.get_file_path()

        if file_path is None:
            self.send_error(400, "Invalid file name")
            return

        if not file_path.is_file():
            self.send_error(404, "File not found")
            return

        hash_requested = query.get("h")

        if hash_requested and hash_requested[0].lower() == "sha256":
            try:
                digest = file_sha256(file_path)
            except OSError as e:
                self.send_error(500, f"Error while calculating hash: {e}")
                return

            self.send_text(200, digest)
            return

        try:
            data = file_path.read_bytes()
        except OSError as e:
            self.send_error(500, f"Error while reading file: {e}")
            return

        self.send_response(200)
        self.send_header("Content-Type", "application/octet-stream")
        self.send_header("Content-Length", str(len(data)))
        self.end_headers()

        self.wfile.write(data)

    def log_message(self, format, *args):
        print(f"[HTTP] {self.address_string()} - {format % args}")


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--server-key", help="Path to the SSL private key")
    parser.add_argument("--server-cert", help="Path to the SSL certificate")

    args = parser.parse_args()

    server = HTTPServer((HOST, PORT), FileServer)

    if args.server_key or args.server_cert:
        if not args.server_key or not args.server_cert:
            parser.error("--server-key and --server-cert must be used together")

        context = ssl.SSLContext(ssl.PROTOCOL_TLS_SERVER)
        context.load_cert_chain(
            certfile=args.server_cert,
            keyfile=args.server_key
        )

        server.socket = context.wrap_socket(
            server.socket,
            server_side=True
        )

        protocol = "https"
    else:
        protocol = "http"

    print(f"Server listening on {protocol}://{HOST}:{PORT}")
    print(f"Files are stored in {BASE_DIR}")

    try:
        server.serve_forever()
    except KeyboardInterrupt:
        print("\nServer stopped.")
    finally:
        server.server_close()
```