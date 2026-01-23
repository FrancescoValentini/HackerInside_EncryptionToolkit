package it.hackerinside.etk.GUI;

import java.security.PrivateKey;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.function.Supplier;

import javax.swing.JOptionPane;

import org.bouncycastle.util.Arrays;

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
		ETKContext ctx = ETKContext.getInstance();
		if (ctx.getUseCacheEntryPasswords()) {
			PasswordCache pwc = ctx.getCache();
			// Try to get password from cache
			char[] pwd = pwc.get(alias);

			// Cache HIT: Return cached password if available
			if (pwd != null) return pwd;

			// Cache MISS: fetch password from supplier and cache it
			pwd = passwordSupplier.get();
			if (pwd != null) return pwc.set(alias, pwd);
			else return null;
		}

		return passwordSupplier.get();
	}

	/**
	 * Removes a password in the cache
	 *
	 * @param key the unique identifier for the password entry
	 */
	public static void passwordCacheRemoveEntry(String alias) {
		ETKContext ctx = ETKContext.getInstance();
		if (ctx.getUseCacheEntryPasswords()) {
			PasswordCache pwc = ctx.getCache();
			pwc.remove(alias);
		}
		return;
	}

	/**
	 * Retrieves a private key from the keystore by prompting the user for a password
	 * via a dialog. This method handles password caching to avoid repeated prompts
	 * and provides appropriate error handling and user feedback.
	 * @param alias The keystore alias identifying the private key entry.
	 * @return The retrieved {@link PrivateKey} object, or {@code null} if:
	 *         <ul>
	 *           <li>The user cancels the password dialog</li>
	 *           <li>The password is incorrect</li>
	 *           <li>The alias doesn't exist in the keystore or the alias is empty</li>
	 *           <li>Any keystore access error occurs</li>
	 *         </ul>
	 */
	public static PrivateKey getPrivateKeyDialog(String alias) {
		if(alias == null || alias.isBlank()) return null;
		ETKContext ctx = ETKContext.getInstance();
		char[] pwd = Utils.passwordCacheHitOrMiss(alias, () -> {
			return DialogUtils.showPasswordInputBox(
					null,
					"Unlock Private key",
					"Password for " + alias,
					"Password:"
					);
		});

		PrivateKey priv = null;
		try {
			priv = ctx.getKeystore().getPrivateKey(alias, pwd);
		}catch (Exception e) {
			DialogUtils.showMessageBox(
					null,
					"Unable to access private key",
					"Unable to access private key",
					e.getMessage(),
					JOptionPane.ERROR_MESSAGE
					);
			e.printStackTrace();
			Utils.passwordCacheRemoveEntry(alias);
			priv = null;
		}finally {
			if(pwd != null) Arrays.fill(pwd, (char) 0x00);
		}

		return priv;
	}

}
