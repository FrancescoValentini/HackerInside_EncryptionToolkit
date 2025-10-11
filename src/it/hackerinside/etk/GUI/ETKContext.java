package it.hackerinside.etk.GUI;

import java.security.Security;
import java.util.prefs.Preferences;

import javax.swing.UIManager;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import it.hackerinside.etk.core.Models.ApplicationPreferences;
import it.hackerinside.etk.core.Models.HashAlgorithm;
import it.hackerinside.etk.core.Models.SymmetricAlgorithms;
import it.hackerinside.etk.core.keystore.AbstractKeystore;
import it.hackerinside.etk.core.keystore.PKCS11Keystore;
import it.hackerinside.etk.core.keystore.PKCS12Keystore;

/**
 * ETKContext is a singleton class that manages the application's keystore context
 * @author Francesco Valentini
 */
public class ETKContext {
	
	public static final String ETK_VERSION = "1.0.3";
	
    /**
     * Singleton instance of ETKContext.
     */
    private static ETKContext instance;
    
    /**
     * The main keystore used for cryptographic operations.
     */
    private AbstractKeystore keystore;
    
    /**
     * Keystore containing known/trusted certificates.
     */
    private AbstractKeystore knownCerts;
    
    /**
     * Application preferences storage.
     */
    private Preferences preferences;
    
    /**
     * Private constructor to enforce singleton pattern.
     * Initializes the context by loading preferences and known certificates.
     */
    private ETKContext() {
        init();
    }
    
    /**
     * Returns the singleton instance of ETKContext.
     * If the instance doesn't exist, it creates one in a thread-safe manner.
     * 
     * @return the singleton ETKContext instance
     */
    public static synchronized ETKContext getInstance() {
        if (instance == null) {
            instance = new ETKContext();
        }
        return instance;
    }
    
    /**
     * Initializes the ETKContext by loading preferences and initializing known certificates.
     * This method is called during construction and should not be called directly.
     */
    private void init() {
    	preferences = Preferences.userNodeForPackage(ETKContext.class);
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
        enforceKeystoreSecurityParameters();
		try {
		    UIManager.setLookAndFeel( getTheme().getLookAndFeel());
		} catch( Exception ex ) {
			System.out.println(ex.toString());
		    System.err.println( "Failed to initialize LaF" );
		}
        try {
            initOrLoadKnownCerts(this.getKnownCertsPath());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("ETKContext initialization error", e);
        }
    }
    
    /**
     * Enforces secure PKCS#12 keystore parameters globally for the current JVM.
     * <p>
     * This method sets the following security properties:
     * <ul>
     *     <li>{@code keystore.pkcs12.keyProtectionAlgorithm} = PBEWithHmacSHA256AndAES_256</li>
     *     <li>{@code keystore.pkcs12.certProtectionAlgorithm} = PBEWithHmacSHA256AndAES_256</li>
     *     <li>{@code keystore.pkcs12.macAlgorithm} = HmacPBESHA256</li>
     *     <li>{@code keystore.pkcs12.keyPbeIterationCount} = 100000</li>
     *     <li>{@code keystore.pkcs12.certPbeIterationCount} = 100000</li>
     * </ul>
     * <p>
     * These settings ensure that any PKCS#12 keystore created or loaded by the JVM
     * will use AES-256 encryption with HMAC-SHA256 for integrity, and a high
     * iteration count for key derivation, providing strong protection against brute-force attacks.
     * <p>
     * <b>Important:</b> This method must be called <em>before</em> any call to
     * {@code KeyStore.getInstance("PKCS12")} or keystore loading/saving
     */
    private void enforceKeystoreSecurityParameters() {
    	Security.setProperty("keystore.pkcs12.keyProtectionAlgorithm","PBEWithHmacSHA256AndAES_256");
    	Security.setProperty("keystore.pkcs12.certProtectionAlgorithm","PBEWithHmacSHA256AndAES_256");
    	Security.setProperty("keystore.pkcs12.macAlgorithm","HmacPBESHA256");
    	Security.setProperty("keystore.pkcs12.keyPbeIterationCount","100000");
    	Security.setProperty("keystore.pkcs12.certPbeIterationCount","100000");
    }
    
    /**
     * Initializes or loads the known certificates keystore from the specified path.
     * 
     * NOTE:
     * This PKCS12 keystore only contains public certificates (no private keys).
     *
     * @param path the file system path to the known certificates keystore
     * @throws Exception if loading the keystore fails
     */
    private void initOrLoadKnownCerts(String path) throws Exception {
        this.knownCerts = new PKCS12Keystore(path, "".toCharArray());
        knownCerts.load();
    }


    /**
     * Loads the main keystore using the provided password.
     * The type of keystore (PKCS11 or PKCS12) is determined by the application preferences.
     * 
     * @param pwd the password to access the keystore
     * @return true if the keystore was successfully loaded and is not empty
     * @throws Exception if keystore loading fails
     */
    public boolean loadKeystore(String pwd) throws Exception {
        if(this.usePKCS11())
            this.keystore = new PKCS11Keystore(this.getPkcs11Driver(), pwd.toCharArray());
        else
            this.keystore = new PKCS12Keystore(this.getKeyStorePath(), pwd.toCharArray());
        
        this.keystore.load();
        return !this.keystore.isNull();
    }
    
    /**
     * Returns the main keystore used for cryptographic operations.
     * 
     * @return the main keystore instance
     */
    public AbstractKeystore getKeystore() {
        return keystore;
    }

    /**
     * Returns the keystore containing known/trusted certificates.
     * 
     * @return the known certificates keystore
     */
    public AbstractKeystore getKnownCerts() {
        return knownCerts;
    }

    /**
     * Returns the application preferences storage.
     * 
     * @return the Preferences object containing application settings
     */
    public Preferences getPreferences() {
        return preferences;
    }
    
    /**
     * Returns the file system path to the main keystore.
     * If not set in preferences, returns the default value.
     * 
     * @return the path to the keystore file
     */
    public String getKeyStorePath() {
        return preferences.get(
            ApplicationPreferences.KEYSTORE_PATH.getKey(), 
            ApplicationPreferences.KEYSTORE_PATH.getValue()
        );
    }

    /**
     * Sets the file system path to the main keystore.
     * 
     * @param path the path to the keystore file
     */
    public void setKeyStorePath(String path) {
        preferences.put(ApplicationPreferences.KEYSTORE_PATH.getKey(), path);
    }

    /**
     * Returns the file system path to the known certificates keystore.
     * If not set in preferences, returns the default value.
     * 
     * @return the path to the known certificates keystore file
     */
    public String getKnownCertsPath() {
        return preferences.get(
            ApplicationPreferences.KNOWN_CERTS_PATH.getKey(), 
            ApplicationPreferences.KNOWN_CERTS_PATH.getValue()
        );
    }

    /**
     * Sets the file system path to the known certificates keystore.
     * 
     * @param path the path to the known certificates keystore file
     */
    public void setKnownCertsPath(String path) {
        preferences.put(ApplicationPreferences.KNOWN_CERTS_PATH.getKey(), path);
    }

    /**
     * Returns the hash algorithm used for cryptographic operations.
     * If not set in preferences, returns the default value.
     * 
     * @return the hash algorithm enumeration value
     */
    public HashAlgorithm getHashAlgorithm() {
        String algorithm = preferences.get(
            ApplicationPreferences.HASH_ALGORITHM.getKey(), 
            ApplicationPreferences.HASH_ALGORITHM.getValue()
        );
        return HashAlgorithm.fromString(algorithm);
    }

    /**
     * Sets the hash algorithm used for cryptographic operations.
     * 
     * @param algorithm the hash algorithm enumeration value to set
     */
    public void setHashAlgorithm(HashAlgorithm algorithm) {
        preferences.put(ApplicationPreferences.HASH_ALGORITHM.getKey(), algorithm.toString());
    }

    /**
     * Returns the symmetric cipher algorithm used for encryption/decryption.
     * If not set in preferences, returns the default value.
     * 
     * @return the symmetric algorithm enumeration value
     */
    public SymmetricAlgorithms getCipher() {
        String cipher = preferences.get(
            ApplicationPreferences.CIPHER.getKey(), 
            ApplicationPreferences.CIPHER.getValue()
        );
        return SymmetricAlgorithms.fromString(cipher);
    }

    /**
     * Sets the symmetric cipher algorithm used for encryption/decryption.
     * 
     * @param cipher the symmetric algorithm enumeration value to set
     */
    public void setCipher(SymmetricAlgorithms cipher) {
        preferences.put(ApplicationPreferences.CIPHER.getKey(), cipher.getAlgorithmName());
    }

    /**
     * Returns the file system path to the PKCS11 driver library.
     * If not set in preferences, returns the default value.
     * 
     * @return the path to the PKCS11 driver library
     */
    public String getPkcs11Driver() {
        return preferences.get(
            ApplicationPreferences.PKCS11_DRIVER.getKey(), 
            ApplicationPreferences.PKCS11_DRIVER.getValue()
        );
    }

    /**
     * Sets the file system path to the PKCS11 driver library.
     * 
     * @param driverPath the path to the PKCS11 driver library
     */
    public void setPkcs11Driver(String driverPath) {
        preferences.put(ApplicationPreferences.PKCS11_DRIVER.getKey(), driverPath);
    }

    /**
     * Returns whether the application should use PKCS11 (hardware security module)
     * instead of PKCS12 (software-based keystore).
     * If not set in preferences, returns the default value.
     * 
     * @return true if PKCS11 should be used, false if PKCS12 should be used
     */
    public boolean usePKCS11() {
        String usePkcs11 = preferences.get(
            ApplicationPreferences.USE_PKCS11.getKey(), 
            ApplicationPreferences.USE_PKCS11.getValue()
        );
        return Boolean.parseBoolean(usePkcs11);
    }

    /**
     * Sets whether the application should use PEM Encoding
     */
    public void setUsePEM(boolean pem) {
        preferences.put(ApplicationPreferences.USE_PEM.getKey(), Boolean.toString(pem));
    }
    
    /**
     * Returns whether the application should use PEM Encoding
     * @return if PEM should be used
     */
    public boolean usePEM() {
        String usePEM = preferences.get(
            ApplicationPreferences.USE_PEM.getKey(), 
            ApplicationPreferences.USE_PEM.getValue()
        );
        return Boolean.parseBoolean(usePEM);
    }

    /**
     * Sets whether the application should use PKCS11 (hardware security module)
     * instead of PKCS12 (software-based keystore).
     * 
     * @param usePkcs11 true to use PKCS11, false to use PKCS12
     */
    public void setUsePkcs11(boolean usePkcs11) {
        preferences.put(ApplicationPreferences.USE_PKCS11.getKey(), Boolean.toString(usePkcs11));
    }
    
    /**
     * Set the UI theme
     * @param theme UI theme
     */
    public void setTheme(UIThemes theme) {
    	preferences.put(ApplicationPreferences.UI_THEME.getKey(), theme.toString());
    }
    
    /**
     * Get the UI theme or default FLATLAF_MACOS_DARK
     * @return UI theme
     */
    public UIThemes getTheme() {
    	String theme = preferences.get(ApplicationPreferences.UI_THEME.getKey(), UIThemes.FLATLAF_MACOS_DARK.toString());
    	return UIThemes.fromString(theme);
    }
    
    
	@Override
	public String toString() {
		return "ETKContext\n    - keystore=" + keystore + "\n    - knownCerts=" + knownCerts + "\n    - preferences=" + preferences
				+ "\n    - getKeyStorePath()=" + getKeyStorePath() + "\n    - getKnownCertsPath()=" + getKnownCertsPath()
				+ "\n    - getHashAlgorithm()=" + getHashAlgorithm() + "\n    - getCipher()=" + getCipher() + "\n    - getPkcs11Driver()="
				+ getPkcs11Driver() + "\n    - usePKCS11()=" + usePKCS11() + "\n    - usePEM()=" + usePEM() + "";
	}
    
    
}
