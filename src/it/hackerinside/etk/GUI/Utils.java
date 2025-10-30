package it.hackerinside.etk.GUI;

import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;

import javax.swing.JOptionPane;

public class Utils {
	
	/**
	 * Checks the expiration of an x509 certificate to the current date, 
	 * if the certificate is expired it displays a warning message.
	 * 
	 * @param cert The certificate to validate
	 * @return true if the certificate is automatically or explicitly accepted by the user, false otherwise
	 */
	public static boolean acceptX509Certificate(X509Certificate cert) {
		try {
			cert.checkValidity();
			return true;
		} catch (CertificateExpiredException | CertificateNotYetValidException e) {
    		return DialogUtils.showConfirmBox(
    				null,
    				"Invalid certificate!", 
    				"The certificate is INVALID, accept the risk?", 
    		        e.getMessage() + "\n\nPress OK to accept the certificate, cancel otherwise.",
    		        JOptionPane.WARNING_MESSAGE
    		);
		}
	}
}
