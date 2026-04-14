package it.hackerinside.etk.core.Services;

@FunctionalInterface
public interface NamedPasswordProvider {
	char[] getPassword(String alias);
}
