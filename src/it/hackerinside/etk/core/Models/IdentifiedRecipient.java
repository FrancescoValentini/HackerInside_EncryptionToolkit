package it.hackerinside.etk.core.Models;

import java.util.Optional;

/**
 * Result returned after identifying recipients
 */
public record IdentifiedRecipient(
			Optional<String> keystoreAlias,
			boolean hasPassword
		){}
