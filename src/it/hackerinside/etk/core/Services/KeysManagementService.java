package it.hackerinside.etk.core.Services;

import java.security.KeyStoreException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
