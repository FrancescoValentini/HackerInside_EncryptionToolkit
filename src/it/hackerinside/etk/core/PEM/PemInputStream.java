package it.hackerinside.etk.core.PEM;

import java.io.*;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

/**
 * An input stream that decodes PEM-encoded data.
 * This class reads a PEM object from an underlying InputStream and
 * provides access to the raw decoded bytes.
 * 
 * <p>Usage example:
 * <pre>
 * try (PemInputStream pemIn = new PemInputStream(inputStream)) {
 *     byte[] data = pemIn.readAllBytes();
 * }
 * </pre>
 * 
 * @see org.bouncycastle.util.io.pem.PemReader
 * @see org.bouncycastle.util.io.pem.PemObject
 * @author Francesco Valentini
 */
public class PemInputStream extends InputStream {

    private final ByteArrayInputStream buffer;

    /**
     * Constructs a PemInputStream that reads a PEM object from the given InputStream.
     *
     * @param underlying the InputStream containing PEM-encoded data
     * @throws IOException if an I/O error occurs or if no PEM object is found
     */
    public PemInputStream(InputStream underlying) throws IOException {
        try (PemReader pemReader = new PemReader(new InputStreamReader(underlying))) {
            PemObject pemObject = pemReader.readPemObject();
            if (pemObject == null) {
                throw new IOException("No PEM object found in the input stream");
            }
            this.buffer = new ByteArrayInputStream(pemObject.getContent());
        }
    }

    /**
     * Reads the next byte of data from the internal buffer.
     *
     * @return the next byte of data, or -1 if the end of the stream is reached
     * @throws IOException if an I/O error occurs
     */
    @Override
    public int read() throws IOException {
        return buffer.read();
    }

    /**
     * Reads some number of bytes from the internal buffer into the provided array.
     *
     * @param b   the buffer into which the data is read
     * @param off the start offset in the array
     * @param len the maximum number of bytes to read
     * @return the number of bytes read, or -1 if the end of the stream is reached
     * @throws IOException if an I/O error occurs
     */
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return buffer.read(b, off, len);
    }

    /**
     * Returns the number of bytes that can be read from the internal buffer without blocking.
     *
     * @return the number of available bytes
     * @throws IOException if an I/O error occurs
     */
    @Override
    public int available() throws IOException {
        return buffer.available();
    }

    /**
     * Closes this input stream and releases any system resources associated with it.
     */
    @Override
    public void close() throws IOException {
        buffer.close();
    }
}
