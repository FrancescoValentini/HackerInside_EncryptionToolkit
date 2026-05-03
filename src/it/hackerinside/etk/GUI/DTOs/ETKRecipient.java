package it.hackerinside.etk.GUI.DTOs;

/**
 * Class representing a recipient
 * @param <T> Original recipient type
 */
public interface ETKRecipient<T> {
	/**
	 * Returns the recipient's alias
	 * @return the recipient's alias
	 */
	String keystoreAlias();
	/**
	 * Returns the original recipient
	 * @return the original recipient
	 */
	T original();
	
	/**
	 * Provides a string representation of the recipient
	 * @return a string representation of the recipient
	 */
	String toString();
}
