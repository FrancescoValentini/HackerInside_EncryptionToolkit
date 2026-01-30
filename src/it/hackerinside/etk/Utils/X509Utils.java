package it.hackerinside.etk.Utils;

import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;

public class X509Utils {
	/**
	 * Checks the expiration of an x509 certificate to the current date.
	 * 
	 * @param cert The certificate to validate
	 * @return true if the certificate is valid
	 */
	public static boolean checkTimeValidity(X509Certificate cert) {
		try {
			cert.checkValidity();
			return true;
		} catch (CertificateExpiredException | CertificateNotYetValidException e) {
			return false;
		}
	}

}
