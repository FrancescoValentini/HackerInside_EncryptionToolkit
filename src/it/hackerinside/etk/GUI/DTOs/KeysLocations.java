package it.hackerinside.etk.GUI.DTOs;

/**
 * Enumeration representing different storage locations for cryptographic keys and certificates.
 * 
 * <p>This enum categorizes where cryptographic materials are stored, distinguishing between
 * public-only certificates and combinations of private and public keys.</p>
 * 
 * @author Francesco Valentini
 */
public enum KeysLocations {
    /**
     * Represents known certificates containing only public keys.
     */
	KNWOWN_CERTIFICATES("Known Certificates (PUB)"),
    /**
     * Represents certificates and private keys stored on a PKCS#11 compatible hardware device.
     */
	PKCS11("PKCS11 Device (PRIV + PUB)"),
	
    /**
     * Represents certificates and private keys stored in a PKCS#12 file.
     */
	PKCS12("PKCS12 File (PRIV + PUB)");
	
	private String displayString;
	
    /**
     * Constructs a KeysLocations enum constant with the specified display string.
     *
     * @param displayString the human-readable description of the key location
     * @throws NullPointerException if the displayString is null
     */
	private KeysLocations(String displayString) {
		this.displayString = displayString;
	}
	
    /**
     * Returns the human-readable display string for this key location.
     *
     * @return the display string describing this key location
     */
	public String toString() {
		return this.displayString;
	}
}
