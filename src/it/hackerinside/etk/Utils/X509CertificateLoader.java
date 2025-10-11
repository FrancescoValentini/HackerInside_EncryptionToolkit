package it.hackerinside.etk.Utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

/**
 * A utility class for loading X509 Certificates
 * 
 * @author Francesco Valentini
 */
public class X509CertificateLoader {
	
    /**
     * Loads an X.509 certificate from a specified file.
     * 
     * This method reads a certificate file in X.509 format and creates
     * an X509Certificate object representing the certificate. The method
     * handles the necessary file input stream operations and ensures proper
     * resource cleanup.
     *
     * @param file the file containing the X.509 certificate to be loaded.
     *             Must not be null and must point to a valid certificate file.
     * @return an X509Certificate object representing the loaded certificate.
     * 
     * @example
     * <pre>
     * File certFile = new File("certificate.cer");
     * X509Certificate certificate = dataLoader.loadCertificateFromFile(certFile);
     * </pre>
     * 
     */
	public static X509Certificate loadFromFile(File file) throws CertificateException, IOException {
		CertificateFactory fact = CertificateFactory.getInstance("X.509");
		FileInputStream is = new FileInputStream(file);
		X509Certificate cer = (X509Certificate) fact.generateCertificate(is);
		is.close();
		return cer;
	}
	
	/**
	 * Loads an X.509 certificate from a String.
	 *
	 * This method reads a certificate provided as a String (for example, in PEM format)
	 * and creates an X509Certificate object representing it. The input string may include
	 * the PEM header and footer lines ("-----BEGIN CERTIFICATE-----" ... "-----END CERTIFICATE-----").
	 *
	 * @param certString the String containing the X.509 certificate to be loaded.
	 *                   Must not be null and must contain valid certificate data.
	 * @return an X509Certificate object representing the loaded certificate.
	 * @throws IOException 
	 *
	 */
	public static X509Certificate loadFromString(String certString) throws CertificateException, IOException {
	    if (certString == null || certString.isEmpty()) {
	        throw new IllegalArgumentException("Certificate string cannot be null or empty");
	    }

	    certString = certString
	            .replaceAll("-----BEGIN CERTIFICATE-----", "")
	            .replaceAll("-----END CERTIFICATE-----", "")
	            .replaceAll("\\s+", "");

	    byte[] decoded = Base64.getDecoder().decode(certString);

	    CertificateFactory factory = CertificateFactory.getInstance("X.509");
	    try (ByteArrayInputStream bais = new ByteArrayInputStream(decoded)) {
	        return (X509Certificate) factory.generateCertificate(bais);
	    }
	}

}
