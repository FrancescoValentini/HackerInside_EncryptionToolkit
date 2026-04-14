package it.hackerinside.etk.core.Services;

import java.io.File;
import java.security.KeyStoreException;
import java.security.PrivateKey;
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
import it.hackerinside.etk.core.keystore.PKCS12Keystore;

public class KeysManagementService {
	private final ETKContext ctx;
	PasswordProvider pwdProvider;
	ConfirmationProvider confirmationProvider;
	AliasProvider aliasProvider;
	PasswordProvider newPwdProvider;
	PasswordProvider confirmPwdProvider;
	CertificateValidationProvider certValProvider;
	NamedPasswordProvider namedPasswordProvider;
	
	
	public KeysManagementService(ETKContext ctx) {
		this.ctx = ctx;
	}

	public void setPwdProvider(PasswordProvider pwdProvider) {this.pwdProvider = pwdProvider;}
	public void setPwdProvider(NamedPasswordProvider namedPasswordProvider) {this.namedPasswordProvider = namedPasswordProvider;}
	public void setConfirmationProvider(ConfirmationProvider confirmationProvider) {this.confirmationProvider = confirmationProvider;}
	public void setAliasProvider(AliasProvider aliasProvider) {this.aliasProvider = aliasProvider;}
	public void setConfirmPwdProvider(PasswordProvider provider) { this.confirmPwdProvider = provider; }
	public void setNewPwdProvider(PasswordProvider provider) { this.newPwdProvider = provider; }
	public void setCertificateValidationProvider(CertificateValidationProvider certValProvider) {this.certValProvider = certValProvider;}
	private <T> T requireProvider(T provider, String name) {
		Objects.requireNonNull(provider,name + " is not set");
	    return provider;
	}
	
	public char[] invokePwdProvider() {return requireProvider(pwdProvider, "PasswordProvider").getPassword();}
	public char[] invokePwdProvider(String alias) {return requireProvider(namedPasswordProvider, "NamedPasswordProvider").getPassword(alias);}

	public boolean invokeConfirmationProvider() {return requireProvider(confirmationProvider, "ConfirmationProvider").confirm();}
	public String invokeAliasProvider() {return requireProvider(aliasProvider, "AliasProvider").getAlias();}
	private char[] invokeNewPwdProvider() {return requireProvider(confirmPwdProvider, "NewPasswordProvider").getPassword();}
	private char[] invokeConfirmPwdProvider() { return requireProvider(confirmPwdProvider, "ConfirmPasswordProvider").getPassword();}
	public boolean invokeCertificateValidationProvider(X509Certificate crt) {return requireProvider(certValProvider, "CertificateValidationProvider").acceptX509Certificate(crt);}

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
	 * Changes the password for a certificate entry in the keystore.
	 *
	 * @param row the certificate table row containing alias and location
	 * @return true if the password was successfully changed, false otherwise
	 * @throws Exception if password retrieval fails or passwords do not match
	 * @throws UnsupportedOperationException if the operation is not supported for the certificate type
	 */
	public boolean changeEntryPassword(CertificateTableRow row) throws Exception {
	    String alias = row.keystoreAlias();
	    KeysLocations location = row.location();

	    if(location == KeysLocations.PKCS12) {

	        char[] currPwd = null;
	        char[] newPwd  = null;

	        try {
	            currPwd = invokePwdProvider();

	            if(currPwd == null || currPwd.length == 0) return false;
	            
	            newPwd = passwordConfirm();
	            if(newPwd == null || newPwd.length == 0) return false;
	            
	            ctx.getKeystore().updateEntryPassword(alias, currPwd, newPwd);
	            ctx.getKeystore().save();
	            return true;
	        } finally {
	            if(currPwd != null) Arrays.fill(currPwd, (char)0x00);
	            if(newPwd != null) Arrays.fill(newPwd, (char)0x00);
	        }

	    } else if(location == KeysLocations.KNWOWN_CERTIFICATES) {
	        throw new UnsupportedOperationException("Cannot change password for certificates without private key");
	    } else if(location == KeysLocations.PKCS11) {
	        throw new UnsupportedOperationException("Operation not supported for PKCS11");
	    }
	    return false;
	}
	
	/**
	 * Deletes a certificate from the appropriate keystore based on its location.
	 * PKCS11 certificates cannot be deleted and will show a warning message.
	 * 
	 * @param row the certificate table row containing the certificate to delete
	 */
	public boolean deleteAlias(CertificateTableRow row) throws Exception {
	    KeysLocations location = row.location();
	    if(location == KeysLocations.PKCS12) {
	    	boolean ok = invokeConfirmationProvider();
	    	if(ok) {
	            ctx.getKeystore().deleteKeyOrCertificate(row.keystoreAlias());
	            ctx.getKeystore().save();
	    		return true;
	    	}
	    } else if(location == KeysLocations.KNWOWN_CERTIFICATES) {
            ctx.getKnownCerts().deleteKeyOrCertificate(row.keystoreAlias());
            ctx.getKnownCerts().save();
	    } else if(location == KeysLocations.PKCS11) {
	        throw new UnsupportedOperationException("Deleting certificates from PKCS11 devices is not supported!");
	    }
	    return false;
	}
	
	/**
	 * Prompts the user to enter a new password and confirm it.
	 *
	 * @return the confirmed password, or null if input was cancelled or empty
	 * @throws Exception if the new password and confirmation do not match
	 */
	private char[] passwordConfirm() throws Exception {
        char newPwd[] = invokeNewPwdProvider();
        if(newPwd == null || newPwd.length == 0) return null;
        char confirmPwd[] = invokeConfirmPwdProvider();
        if(confirmPwd == null || confirmPwd.length == 0) return null;
        
        if(!Arrays.equals(newPwd, confirmPwd)) {
            throw new Exception("The two entry passwords do not match");
        }
        if(newPwd != null) Arrays.fill(newPwd, (char)0x00);
        return confirmPwd;
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
	
	
	/*
	 * ====
	 * KNOWN CERTIFICATES
	 * ====
	 */
	
	/**
	 * Saves a known X.509 certificate to the application's certificate store after prompting
	 * 
	 * @param cert the X.509 certificate to save
	 */
	public boolean saveKnownCertificate(X509Certificate cert) throws Exception {
		boolean ok = invokeConfirmationProvider();
		if(ok) {
			String alias = invokeAliasProvider();
			if(alias == null || alias.isEmpty()) return false;
            ctx.getKnownCerts().addCertificate(alias, cert);
            ctx.getKnownCerts().save();
            return true;
		}
		return false;
	}
	
	/*
	 * ====
	 * KEYPAIR
	 * ====
	 */
	
	/**
	 * Method to export a key pair to a PKCS12 keystore
	 * 
	 * @param row The key pair to export
	 */
	public boolean exportKeypair(CertificateTableRow row, File outputFile) throws Exception {
		if(outputFile == null) return false;
		if(row.location() == KeysLocations.PKCS12) {
			char[] keyPassword = null;
			try {
				keyPassword = invokePwdProvider();
	            if(keyPassword == null || keyPassword.length == 0) return false;
	            
				PrivateKey privk = ctx.getKeystore().getPrivateKey(row.keystoreAlias(), keyPassword);
				X509Certificate cert = ctx.getKeystore().getCertificate(row.keystoreAlias());
				
				AbstractKeystore newKeystore = new PKCS12Keystore(outputFile, keyPassword);
				newKeystore.load();
				newKeystore.addPrivateKey(
						row.keystoreAlias(), 
						privk, 
						keyPassword, 
						new X509Certificate[]{cert}
				);
				newKeystore.save();
				return true;
	            
			} finally {
				if(keyPassword != null) Arrays.fill(keyPassword, (char)0x00);
			}
			
		} else if(row.location() == KeysLocations.KNWOWN_CERTIFICATES) {
			throw new UnsupportedOperationException("The key pair export operation cannot be performed on known certificates as they do not have a private key.");
		} else if(row.location() == KeysLocations.PKCS11) {
			throw new UnsupportedOperationException("Operation not supported for PKCS11");
		}
		
		return false;
	}
	
	/**
	 * Imports key pairs from an external keystore file into the application's current keystore.
	 * @throws Exception 
	 */
	public boolean importKeyPair(File inputFile) throws Exception {
		if(ctx.usePKCS11()) throw new UnsupportedOperationException("Operation not supported for PKCS11");
		if(inputFile == null) return false;
		
		
		char[] srcPwd, keyPwd; srcPwd = keyPwd = null;
		AbstractKeystore src = null;
		try {
			srcPwd = invokePwdProvider(); // Source keystore password
			if(srcPwd == null || srcPwd.length == 0) return false;
	    	src = new PKCS12Keystore(inputFile, srcPwd);
	    	
	    	src.load(); // Loads the source keystore
	    	
	    	List<String> srcAliases = Collections.list(src.listAliases());
	    	
	    	for(String alias : srcAliases) { // Import aliases
	    		keyPwd = invokePwdProvider(alias); // used for aliases password
	    		if(keyPwd == null || keyPwd.length == 0) return false;
			    X509Certificate crt = src.getCertificate(alias);
			    if(!invokeCertificateValidationProvider(crt)) return false;
			    PrivateKey key = src.getPrivateKey(alias, keyPwd);
			    ctx.getKeystore().addPrivateKey(alias, key, keyPwd, new X509Certificate[] {crt});
			    ctx.getKeystore().save();
	    	}
		}finally {
			if(srcPwd != null) Arrays.fill(srcPwd, (char)0x00);
			if(keyPwd != null) Arrays.fill(keyPwd, (char)0x00);
		}
		return false;
	}
}
