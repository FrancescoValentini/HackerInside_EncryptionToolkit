package it.hackerinside.etk.Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Base64;

/**
 * A utility class for exporting X509 Certificates
 * 
 * @author Francesco Valentini
 */
public class X509CertificateExporter {
	/**
	 * Exports a certificate to a file in PEM format.
	 * Prompts the user to select a destination file and saves the certificate
	 * with proper PEM encoding (Base64 with headers and line breaks).
	 * 
	 * @param crt the certificate to export
	 * @param output the destination path
	 * @return true if the certificate has been exported
	 * @throws IOException 
	 * @throws CertificateEncodingException 
	 */
	public static boolean exportCertificate(X509Certificate crt, File output) throws IOException, CertificateEncodingException {
	    if (output != null) {
	    	FileWriter fw = new FileWriter(output);
            fw.write("-----BEGIN CERTIFICATE-----\n");
            String encoded = Base64.getMimeEncoder(64, new byte[]{'\n'})
                .encodeToString(crt.getEncoded());
            fw.write(encoded);
            fw.write("\n-----END CERTIFICATE-----\n");
            fw.close();
            return true;
	    }
	    return false;
	}
	
	/**
	 * Exports a certificate to a string in PEM format.
	 * with proper PEM encoding (Base64 with headers and line breaks).
	 * 
	 * @param crt the certificate to export
	 * @return the PEM encoded certificate
	 * @throws CertificateEncodingException 
	 */
	public static String exportCertificateToString(X509Certificate crt) throws CertificateEncodingException {
	    StringBuilder sb = new StringBuilder();
	    sb.append("-----BEGIN CERTIFICATE-----\n");
	    String cert = Base64.getMimeEncoder(64, new byte[]{'\n'})
	    		.encodeToString(crt.getEncoded());
	    sb.append(cert);
	    sb.append("\n-----END CERTIFICATE-----\n");
	    return sb.toString();
	}
	
	/**
	 * Exports a certificate public key to a string in PEM format.
	 * With proper PEM encoding (Base64 with headers and line breaks).
	 * @param crt The certificate from which to export the public key
	 * @return the PEM encoded public key
	 * @throws CertificateEncodingException
	 */
	public static String exportPublicKeyToString(X509Certificate crt) throws CertificateEncodingException {
	    StringBuilder sb = new StringBuilder();
	    sb.append("-----BEGIN PUBLIC KEY-----\n");
	    String cert = Base64.getMimeEncoder(64, new byte[]{'\n'})
	    		.encodeToString(crt.getPublicKey().getEncoded());
	    sb.append(cert);
	    sb.append("\n-----BEGIN PUBLIC KEY-----\n");
	    return sb.toString();
	}
}
