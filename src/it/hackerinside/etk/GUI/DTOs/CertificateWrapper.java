package it.hackerinside.etk.GUI.DTOs;

import it.hackerinside.etk.core.keystore.AbstractKeystore;

public class CertificateWrapper {
    private String alias;
    private AbstractKeystore keystore;
    
    public CertificateWrapper(String alias, AbstractKeystore keystore) {
        this.alias = alias;
        this.keystore = keystore;
    }
    
    @Override
    public String toString() {
        return alias;
    }
    
    // Getter methods
    public String getAlias() { return alias; }
    public AbstractKeystore getKeystore() { return keystore; }
}