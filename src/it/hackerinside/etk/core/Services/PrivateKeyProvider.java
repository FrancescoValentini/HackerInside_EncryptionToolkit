package it.hackerinside.etk.core.Services;

import java.security.PrivateKey;

@FunctionalInterface
public interface PrivateKeyProvider {
    PrivateKey getPrivateKey(String alias) throws Exception;
}
