package it.hackerinside.etk.GUI;

import java.io.File;
import java.security.Security;
import java.util.prefs.Preferences;

import javax.swing.UIManager;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Arrays;

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
	
	public static final String ETK_VERSION = "1.0.4";
	
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
     * Keystore entry password cache
     */
    private PasswordCache passwordCache;
    
    /**
     * A reference to the shutdown hook thread to allow removal in the destroyCache method.
     */
    private Thread shutdownHook;
    
    /**
     * The Keystore Master Password
     */
    private char[] keystoreMasterPassword;
    
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
        if(this.getUseCacheEntryPasswords()) initCache();
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Arrays.fill(this.keystoreMasterPassword, (char)0x00);
        }));

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
     * Initializes the password cache and sets up a shutdown hook to ensure
     * the cache is zeroized when the JVM shuts down.
     */
    public void initCache() {
        passwordCache = new PasswordCache(this.getCacheEntryTimeout());

        // Create the shutdown hook thread
        Thread shutdownHook = new Thread(() -> {
            passwordCache.zeroize();
        });

        // Register the shutdown hook
        Runtime.getRuntime().addShutdownHook(shutdownHook);

        // Store the shutdown hook reference for later removal
        this.shutdownHook = shutdownHook;
    }
    
    /**
     * Destroys the password cache and removes the shutdown hook to prevent
     * future zeroization when the JVM shuts down.
     */
    public void destroyCache() {
        if (passwordCache != null) {
            passwordCache.zeroize();
        }

        // Safely remove the shutdown hook if it was added
        if (shutdownHook != null) {
            Runtime.getRuntime().removeShutdownHook(shutdownHook);
        }
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
    	ensureDirectoryExists(path);
        this.knownCerts = new PKCS12Keystore(path, "".toCharArray());
        knownCerts.load();

    }
    
    /**
     * Ensures that the specified directory path exists. If the directory does not exist,
     * it attempts to create it, including any necessary parent directories.
     *
     * @param path the file system path to the directory that should exist
     * @throws Exception if the directory cannot be created
     */
    private void ensureDirectoryExists(String path) throws Exception {
        File f = new File(path);

        // Check if the directory exists
        if (!f.exists()) {
            // If it doesn't, create the parent directories
            File parentDir = f.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                boolean dirsCreated = parentDir.mkdirs(); // Create all necessary parent directories
                if (!dirsCreated) {
                    throw new Exception("Unable to create the directory: " + parentDir.getAbsolutePath());
                }
            }
        }
    }

    
    /**
     * Loads the main keystore using the provided password.
     * The type of keystore (PKCS11 or PKCS12) is determined by the application preferences.
     * 
     * @param pwd the password to access the keystore
     * @return true if the keystore was successfully loaded and is not empty
     * @throws Exception if keystore loading fails
     */
    public boolean loadKeystore(char[] pwd) throws Exception {
        // Copy the parameter pwd into a global attribute
        // This is done to prevent the zeroization of pwd from causing the keystore password to be reset
        this.keystoreMasterPassword = Arrays.copyOf(pwd, pwd.length);
        try {
            if (this.usePKCS11()) {
                this.keystore = new PKCS11Keystore(this.getPkcs11Driver(), this.keystoreMasterPassword);
            } else {
                ensureDirectoryExists(this.getKeyStorePath());
                this.keystore = new PKCS12Keystore(this.getKeyStorePath(), this.keystoreMasterPassword);
            }

            this.keystore.load();
            return !this.keystore.isNull();
        } catch (Exception e) {
            // If it fails, destroy everything!
            Arrays.fill(this.keystoreMasterPassword, (char) 0x00);
            throw new Exception(e);
        } finally {
            Arrays.fill(pwd, (char) 0x00);
        }
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
    
    /**
     * Get the buffer size
     * @return the buffer size
     */
    public int getBufferSize() {
    	String size = preferences.get(ApplicationPreferences.BUFFER_SIZE.getKey(), ApplicationPreferences.BUFFER_SIZE.getValue());
    	return Integer.parseInt(size);
    }
    
    /**
     * Set the buffer size
     * @param size the buffer size
     */
    public void setBufferSize(int size) {
    	preferences.put(ApplicationPreferences.BUFFER_SIZE.getKey(), String.valueOf(size));
    }
    
    
    /**
     * Returns whether the application should cache passwords
     * @return
     */
    public boolean getUseCacheEntryPasswords() {
    	String use = preferences.get(ApplicationPreferences.CACHE_ENTRY_PASSWORDS.getKey(), ApplicationPreferences.CACHE_ENTRY_PASSWORDS.getValue());
    	return Boolean.valueOf(use);
    }
    
    /**
     * Sets whether the application should cache passwords
     * @param value true/false
     */
    public void setUseCacheEntryPassword(boolean value) {
    	preferences.put(ApplicationPreferences.CACHE_ENTRY_PASSWORDS.getKey(), String.valueOf(value));
    }
    
    /**
     * Returns the password cache
     * @return
     */
    protected PasswordCache getCache() {
    	return this.passwordCache;
    }
    
    /**
     * Sets the cache entry timeout
     * @param timeout timeout in seconds
     */
    public void setCacheEntryTimeout(int timeout) {
    	preferences.put(ApplicationPreferences.CACHE_ENTRY_TIMEOUT.getKey(), String.valueOf(timeout));
    }
    
    /**
     * Returns the cache entry timeut
     * @return timeout in seconds
     */
    public int getCacheEntryTimeout() {
    	String timeout = preferences.get(ApplicationPreferences.CACHE_ENTRY_TIMEOUT.getKey(), ApplicationPreferences.CACHE_ENTRY_TIMEOUT.getValue());
    	return Integer.parseInt(timeout);
    }
    
	@Override
	public String toString() {
		return "ETKContext\n    - keystore=" + keystore + "\n    - knownCerts=" + knownCerts + "\n    - preferences=" + preferences
				+ "\n    - getKeyStorePath()=" + getKeyStorePath() + "\n    - getKnownCertsPath()=" + getKnownCertsPath()
				+ "\n    - getHashAlgorithm()=" + getHashAlgorithm() + "\n    - getCipher()=" + getCipher() + "\n    - getPkcs11Driver()="
				+ getPkcs11Driver() + "\n    - usePKCS11()=" + usePKCS11() + "\n    - usePEM()=" + usePEM() + "\n    - getTheme()=" + getTheme()+ "\n    - getBufferSize()=" + getBufferSize()
				+ "\n    - getUseCacheEntryPasswords()=" + getUseCacheEntryPasswords() 
				+ "\n    - getCacheEntryTimeout()=" + getCacheEntryTimeout();
	}
    
	
    
}
