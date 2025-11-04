package it.hackerinside.etk.GUI;

import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.function.Supplier;

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
	
	/**
	 * Retrieves the password for the given alias.
	 * This method checks if the password is already cached for the given alias. 
	 * If so, it will return the cached password. Otherwise, it will retrieve the password from the 
	 * supplier and store it in the cache for future use.
	 *
	 * @param alias The alias associated with the password. This is printed for debugging 
	 *              purposes and may be used for future cache lookups.
	 * @param passwordSupplier A supplier that provides the password as a char array. 
	 *                         This is used to fetch the password if it's not found in the cache.
	 * @return A char array containing the password obtained from the supplier. 
	 *         If the supplier provides a null password, a null value may be returned.
	 */
	public static char[] passwordCacheHitOrMiss(String alias, Supplier<char[]> passwordSupplier) {
	    System.out.println("CACHE - ALIAS: " + alias); // Debugging: print alias to console
	    return passwordSupplier.get(); // Fetch password from supplier (cache miss for now)
	}

}
