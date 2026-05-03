package it.hackerinside.etk.GUI.DTOs;

import javax.crypto.SecretKey;

/**
 * Represents a row in a secret key table, containing essential information
 * about a key-encryption key (KEK) stored in a keystore.
 *
 * @param keystoreAlias the alias under which the secret key is stored in the keystore
 * @param location indicates where the key material is stored
 * @param original the original representation of the secret key
 *
 *
 * @author Francesco Valentini
 */
public record SecretKeyTableRow(
		String keystoreAlias,
	    KeysLocations location,
	    SecretKey original) implements ETKRecipient<SecretKey> {
	
	
	@Override
	public String toString() {
		return "KEK/" + keystoreAlias;
	}
}
