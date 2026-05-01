package it.hackerinside.etk.GUI.DTOs;

import it.hackerinside.etk.core.keystore.AbstractKeystore;

public class SecretKeyWrapper {
    private String alias;
    private AbstractKeystore keystore;
    
	public SecretKeyWrapper(String alias, AbstractKeystore keystore) {
		this.alias = alias;
		this.keystore = keystore;
	}
	
	
	public String getAlias() {
		return alias;
	}
	public AbstractKeystore getKeystore() {
		return keystore;
	}


	@Override
	public String toString() {
		return alias;
	}
	
	
    
    
}
