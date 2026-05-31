package it.hackerinside.etk.GUI.DTOs;

/**
 * Represents a row in a password table,
 * 
 * @param original the original representation of the password
 */
public record PasswordTableRow(char[] original) implements ETKRecipient<char[]> {
	@Override
	public String toString() {
		return "PasswordRecipient";
	}
	
	/**
	 * Always null, a password has no keystore
	 */
	@Override
	public String keystoreAlias() {
		return null;
	}
}