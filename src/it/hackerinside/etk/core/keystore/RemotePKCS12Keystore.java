package it.hackerinside.etk.core.keystore;

import java.security.KeyStore;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.cert.X509Certificate;
import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import it.hackerinside.etk.core.Encryption.CMSDecryptor;
import it.hackerinside.etk.core.Encryption.CMSEncryptor;
import it.hackerinside.etk.core.Models.EncodingOption;
import it.hackerinside.etk.core.Models.SymmetricAlgorithms;

/**
 * A PKCS#12 keystore whose contents are stored on a remote HTTP-accessible
 * service.
 *
 * <p>The remote keystore is accessed using the configured HTTP endpoint.
 * HTTP Basic Authentication is used when both username and password are
 * configured.</p>
 */
public class RemotePKCS12Keystore extends PKCS12Keystore {
	
    private String httpEndpoint;
    private String httpUsername;
    private String httpPassword;

    private HttpClient httpClient;
    private SecretKey syncKey;
    
    private final static String WRAPPING_KEY_ID = "it.hackerinside.etk.core.keystore.RemotePKCS12Keystore.wrapping-key";

    /**
     * Creates a remote PKCS#12 keystore.
     *
     * @param password the password used to protect the PKCS#12 keystore
     * @param httpEndpoint the HTTP endpoint used to access the keystore
     * @param httpUsername the username used for HTTP Basic Authentication;
     *        authentication is disabled when null or empty
     * @param httpPassword the password used for HTTP Basic Authentication;
     *        authentication is disabled when null or empty
     */
    public RemotePKCS12Keystore(char[] password,String httpEndpoint,String httpUsername,String httpPassword) {
        super(password);
        this.httpEndpoint = httpEndpoint;
        this.httpUsername = httpUsername;
        this.httpPassword = httpPassword;
        this.httpClient = HttpClient.newHttpClient();
    }
    
    public RemotePKCS12Keystore(char[] password,String httpEndpoint,String httpUsername,String httpPassword, X509Certificate certificate) {
        super(password);
        this.httpEndpoint = httpEndpoint;
        this.httpUsername = httpUsername;
        this.httpPassword = httpPassword;
        try {
			this.httpClient = createHttpClient(certificate);
		} catch (Exception e) {
			e.printStackTrace();
			this.httpClient = HttpClient.newHttpClient();
		}
    }
    
    public void setSyncKey(byte[] key) {
        this.syncKey = new SecretKeySpec(key, "AES");
    }
    
    private HttpClient createHttpClient(X509Certificate certificate) throws Exception {
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(null, null);

        trustStore.setCertificateEntry("trusted-certificate", certificate);

        TrustManagerFactory tmf =TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);

        return HttpClient.newBuilder()
                .sslContext(sslContext)
                .build();
    }

    @Override
    public void load() throws Exception {
        keyStore = KeyStore.getInstance("PKCS12");

        byte[] keystoreData = getKeystore();

        if (keystoreData == null || keystoreData.length == 0) {
            initialize();
            return;
        }else {
        	try {
        		keystoreData = decrypt(keystoreData);
        	}catch (Exception e) {
                initialize();
                return;
        	}
        	
        }

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(keystoreData)) {
            keyStore.load(inputStream, super.getPassword());
        }
    }

    @Override
    public void save() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        keyStore.store(outputStream, super.getPassword());
        
        byte[] data = outputStream.toByteArray();
        
        if(syncKey != null) data = encrypt(data);
        
        putKeystore(data);
    }

    /**
     * Retrieves the PKCS#12 keystore from the configured remote endpoint.
     *
     * <p>A HTTP 404 response is treated as an absent keystore and causes
     * {@link #load()} to initialize an empty keystore.</p>
     *
     * @return the serialized PKCS#12 keystore, or {@code null} if it does
     *         not exist remotely
     * @throws Exception if the remote request fails
     */
    private byte[] getKeystore() throws Exception {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(httpEndpoint))
                .GET();

        addAuthentication(requestBuilder);
        HttpResponse<byte[]> response = httpClient.send(requestBuilder.build(),HttpResponse.BodyHandlers.ofByteArray());
        int statusCode = response.statusCode();
        if (statusCode == 404) return null;
        
        handleResponseCode(statusCode, "retrieve");
        return response.body();
    }

    /**
     * Stores the PKCS#12 keystore on the configured remote endpoint.
     *
     * @param keystoreData the serialized PKCS#12 keystore
     * @throws Exception if the remote request fails
     */
    private void putKeystore(byte[] keystoreData) throws Exception {
        byte[] hash = MessageDigest
                .getInstance("SHA-256")
                .digest(keystoreData);

        String sha256 = Base64.getEncoder().encodeToString(hash);

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(httpEndpoint))
                .header("Content-Type", "application/x-pkcs12")
                .header("X-Keystore-sha256", sha256)
                .PUT(HttpRequest.BodyPublishers.ofByteArray(keystoreData));

        addAuthentication(requestBuilder);

        HttpResponse<Void> response = httpClient.send(requestBuilder.build(),HttpResponse.BodyHandlers.discarding());
        handleResponseCode(response.statusCode(), "store");
    }

    /**
     * Adds HTTP Basic Authentication when credentials are configured.
     *
     * <p>If either username or password is null or empty, no Authorization
     * header is added.</p>
     */
    private void addAuthentication(
            HttpRequest.Builder requestBuilder) {

        if (httpUsername == null
                || httpUsername.isEmpty()
                || httpPassword == null
                || httpPassword.isEmpty()) {
            return;
        }

        String credentials = httpUsername + ":" + httpPassword;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        requestBuilder.header("Authorization","Basic " + encodedCredentials);
    }

    /**
     * Handles HTTP response codes returned by the remote service.
     *
     * @param responseCode the HTTP response code
     * @param operation the operation being performed
     * @throws IOException if the response indicates an error
     */
    private void handleResponseCode(int responseCode,String operation) throws IOException {
        if (responseCode == 401) {
            throw new IOException(
                    "Unauthorized while attempting to "
                            + operation
                            + " remote PKCS#12 keystore "
                            + "(HTTP 401).");
        }

        if (responseCode == 403) {
            throw new IOException(
                    "Forbidden while attempting to "
                            + operation
                            + " remote PKCS#12 keystore "
                            + "(HTTP 403).");
        }

        if (responseCode < 200 || responseCode >= 300) {
            throw new IOException(
                    "Unable to "
                            + operation
                            + " remote PKCS#12 keystore. "
                            + "HTTP status: "
                            + responseCode);
        }
    }
    
    private byte[] encrypt(byte[] plaintext) throws Exception {
    	ByteArrayInputStream input = new ByteArrayInputStream(plaintext);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CMSEncryptor enc = new CMSEncryptor(SymmetricAlgorithms.AES_256_GCM, EncodingOption.ENCODING_DER, 8192);
        enc.addRecipients(WRAPPING_KEY_ID.getBytes(), syncKey);
    	enc.encrypt(input, outputStream);
    	return outputStream.toByteArray();
    }
    
    private byte[] decrypt(byte[] ciphertext) throws Exception {
    	ByteArrayInputStream input = new ByteArrayInputStream(ciphertext);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CMSDecryptor enc = new CMSDecryptor(syncKey,EncodingOption.ENCODING_DER,8192);
        enc.decrypt(input, outputStream);
    	return outputStream.toByteArray();
    }
}

