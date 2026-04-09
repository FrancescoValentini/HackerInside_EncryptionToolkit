package it.hackerinside.etk.core.Services;

import java.security.KeyStoreException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import it.hackerinside.etk.GUI.ETKContext;
import it.hackerinside.etk.GUI.DTOs.CertificateTableRow;
import it.hackerinside.etk.GUI.DTOs.KeysLocations;
import it.hackerinside.etk.core.keystore.AbstractKeystore;

public class KeysManagementService {
	private final ETKContext ctx;
	PasswordProvider pwdProvider;
	ConfirmationProvider confirmationProvider;
	AliasProvider aliasProvider;
	
	public KeysManagementService(ETKContext ctx) {
		this.ctx = ctx;
	}

	public void setPwdProvider(PasswordProvider pwdProvider) {this.pwdProvider = pwdProvider;}
	public void setConfirmationProvider(ConfirmationProvider confirmationProvider) {this.confirmationProvider = confirmationProvider;}
	public void setAliasProvider(AliasProvider aliasProvider) {this.aliasProvider = aliasProvider;}
	
	private <T> T requireProvider(T provider, String name) {
		Objects.requireNonNull(provider,name + " is not set");
	    return provider;
	}
	
	public char[] invokePwdProvider() {return requireProvider(pwdProvider, "PasswordProvider").getPassword();}
	public boolean invokeConfirmationProvider() {return requireProvider(confirmationProvider, "ConfirmationProvider").confirm();}
	public String invokeAliasProvider() {return requireProvider(aliasProvider, "AliasProvider").getAlias();}
	
	/*
	 * ====
	 * COMMON FUNCTIONS
	 * ====
	 * 
	 */
	
	/**
	 * Retrieves all certificates from available keystore locations.
	 *
	 * @param includePrivate if true, includes certificates from the private keystore (PKCS11 or PKCS12)
	 * @return a list of all certificate table rows
	 * @throws KeyStoreException if an error occurs while accessing the keystore
	 */
	public List<CertificateTableRow> getAllCertificates(boolean includePrivate) throws KeyStoreException {
		List<CertificateTableRow> rows = new ArrayList<>();

	    if (includePrivate) {
	        KeysLocations privateLocation = ctx.usePKCS11()
	            ? KeysLocations.PKCS11
	            : KeysLocations.PKCS12;

	        rows.addAll(getCertificates(privateLocation));
	    }

	    rows.addAll(getCertificates(KeysLocations.KNWOWN_CERTIFICATES));

	    return rows;
	}
	
	/**
	 * Retrieves certificates from a specific keystore location.
	 *
	 * @param location the source location of the certificates
	 * @return a list of certificate table rows from the specified location
	 * @throws KeyStoreException if an error occurs while accessing the keystore
	 */
	public List<CertificateTableRow> getCertificates(KeysLocations location) throws KeyStoreException {
	    AbstractKeystore keystore;

	    switch (location) {
	        case KNWOWN_CERTIFICATES:
	            keystore = ctx.getKnownCerts();
	            break;

	        case PKCS11:
	        case PKCS12:
	            keystore = ctx.getKeystore();
	            break;

	        default:
	            return Collections.emptyList();
	    }

	    return getDtos(keystore, location);
	}
	
	/**
	 * Rename an alias
	 * @param row the entry to rename
	 */
	public String renameAlias(CertificateTableRow row) throws Exception {
	    String currentAlias = row.keystoreAlias();
	    KeysLocations location = row.location();

	    if (location == KeysLocations.PKCS11) {
	        throw new UnsupportedOperationException(
	            "Renaming certificates from PKCS11 devices is not supported!"
	        );
	    }

	    String newAlias = invokeAliasProvider();
	    if (newAlias == null || newAlias.isBlank()) {
	        return null;
	    }

	    char[] pwd = null;

	    try {
	        if (location == KeysLocations.PKCS12) {
	            pwd = invokePwdProvider();
	            if (pwd == null || pwd.length == 0) return null;

	            ctx.getKeystore().renameEntry(currentAlias, newAlias, pwd);
	            ctx.getKeystore().save();

	        } else if (location == KeysLocations.KNWOWN_CERTIFICATES) {
	            ctx.getKnownCerts().renameEntry(currentAlias, newAlias, null);
	            ctx.getKnownCerts().save();
	        }
	        
	        return newAlias;

	    } finally {
	        if (pwd != null) Arrays.fill(pwd, (char) 0x00);
	    }
	}
	

	/**
	 * Converts certificates from the given keystore into data transfer objects.
	 *
	 * @param keystore the keystore to read certificates from
	 * @param location the location associated with the certificates
	 * @return a list of certificate table rows
	 * @throws KeyStoreException if an error occurs while accessing the keystore
	 */
	private List<CertificateTableRow> getDtos(AbstractKeystore keystore, KeysLocations location) throws KeyStoreException {
	    List<CertificateTableRow> dtos = new ArrayList<>();

	    if (keystore == null) {
	        return Collections.emptyList();
	    }

	    for (String alias : Collections.list(keystore.listAliases())) {
	        X509Certificate crt = (X509Certificate) keystore.getCertificate(alias);
	        if (crt != null) {
	            dtos.add(new CertificateTableRow(alias, location, crt));
	        }
	    }

	    return dtos;
	}
	
}
