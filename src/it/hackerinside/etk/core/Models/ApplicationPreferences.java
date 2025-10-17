package it.hackerinside.etk.core.Models;

/**
 * ApplicationPreferences enum defines configuration keys and their default values
 * used throughout the application.
 * 
 * @author Francesco Valentini
 */
public enum ApplicationPreferences {
    /**
     * Path to the keystore file used for storing cryptographic keys.
     * Default value: "keystore.pfx"
     */
    KEYSTORE_PATH("KEYSTORE_PATH", "keystore.pfx"),
    
    /**
     * Path to the keystore file containing known/trusted certificates.
     * Default value: "knowncerts.pfx"
     */
    KNOWN_CERTS_PATH("KNOWN_CERTS_PATH", "knowncerts.pfx"),
    
    /**
     * Default hash algorithm used for cryptographic operations.
     * Default value: "SHA256" (from HashAlgorithm.SHA256)
     */
    HASH_ALGORITHM("DEFAULT_HASH_ALGORITHM", HashAlgorithm.SHA256.toString()),
    
    /**
     * Default symmetric cipher algorithm used for encryption/decryption.
     * Default value: "AES_256_CBC" (from SymmetricAlgorithms.AES_256_CBC)
     */
    CIPHER("DEFAULT_CIPHER", SymmetricAlgorithms.AES_256_CBC.getAlgorithmName()),
    
    /**
     * File system path to the PKCS#11 driver library (if using hardware security module).
     * Default value: "" (empty string, indicating not configured)
     */
    PKCS11_DRIVER("PKCS11_DRIVER_PATH", ""),
    
    /**
     * Flag indicating whether PKCS#11 (hardware security module) should be used.
     * Default value: "false" (disabled)
     */
    USE_PKCS11("USE_PKCS11", "false"),
	
	/**
	 * PEM encoding
	 */
	USE_PEM("USE_PEM","false"),
	
	/**
	 * UI Theme
	 */
	UI_THEME("UI_THEME","FlatLaf MacOS Dark"),
	
	/**
	 * I/O Buffer size
	 */
	BUFFER_SIZE("BUFFER_SIZE","8192");
    
    private final String key;
    private final String value;
    
    /**
     * Constructs an ApplicationPreferences enum constant.
     *
     * @param key the configuration key name used in properties files
     * @param value the default value for this configuration setting
     */
    private ApplicationPreferences(String key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Returns the configuration key associated with this preference.
     *
     * @return the key string used to identify this preference
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns the default value associated with this preference.
     *
     * @return the default value for this preference
     */
    public String getValue() {
        return value;
    }
}