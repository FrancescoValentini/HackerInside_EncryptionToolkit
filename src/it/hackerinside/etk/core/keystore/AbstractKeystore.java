package it.hackerinside.etk.core.keystore;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.Objects;

import javax.crypto.SecretKey;

import it.hackerinside.etk.core.Models.SupportedKeystores;

/**
 * An abstract base class for managing different types of keystores.
 * Provides common functionality for CRUD operations on secret keys, asymmetric keys,
 * and certificates across various keystore implementations.
 * 
 * @author Francesco Valentini
 */
public abstract class AbstractKeystore {
    
    /**
     * The type of keystore being implemented.
     */
    private SupportedKeystores keystoreType;

    /**
     * The underlying KeyStore instance managed by this class.
     */
    protected KeyStore keyStore;

    /**
     * Loads the keystore from its persistent storage.
     * Concrete implementations must provide the specific loading mechanism
     * based on the keystore type and location.
     *
     */
    public abstract void load() throws Exception;

    /**
     * Saves the keystore to its persistent storage.
     * Concrete implementations must provide the specific saving mechanism
     * based on the keystore type and location.
     *
     */
    public abstract void save() throws Exception;
    
    /**
     * Constructs a new AbstractKeystore with the specified keystore type.
     *
     * @param keystoreType the type of keystore to be managed (cannot be null)
     * @throws NullPointerException if keystoreType is null
     */
    public AbstractKeystore(SupportedKeystores keystoreType) {
        Objects.requireNonNull(keystoreType);
        this.keystoreType = keystoreType;
    }
    
    /**
     * Adds a secret key to the keystore with the specified alias and password protection.
     *
     * @param alias the alias name for the key entry
     * @param key the secret key to store
     * @param password the password used to protect the key entry
     * @throws KeyStoreException if the keystore has not been initialized,
     *         or if the key cannot be stored for any reason
     * @throws NullPointerException if alias, key, or password is null
     */
    public void addSecretKey(String alias, SecretKey key, char[] password) throws KeyStoreException {
        KeyStore.SecretKeyEntry entry = new KeyStore.SecretKeyEntry(key);
        KeyStore.ProtectionParameter protection = new KeyStore.PasswordProtection(password);
        keyStore.setEntry(alias, entry, protection);
    }

    /**
     * Retrieves a secret key from the keystore using the specified alias and password.
     *
     * @param alias the alias name for the key entry
     * @param password the password used to recover the key
     * @return the requested secret key
     * @throws UnrecoverableKeyException if the key cannot be recovered (e.g., wrong password)
     * @throws NoSuchAlgorithmException if the algorithm used to recover the key is not available
     * @throws KeyStoreException if the keystore has not been initialized
     * @throws NullPointerException if alias or password is null
     */
    public SecretKey getSecretKey(String alias, char[] password)
            throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
        return (SecretKey) keyStore.getKey(alias, password);
    }

    /**
     * Deletes a secret key entry from the keystore.
     *
     * @param alias the alias name for the key entry to delete
     * @throws KeyStoreException if the keystore has not been initialized,
     *         or if the entry cannot be deleted
     * @throws NullPointerException if alias is null
     */
    public void deleteSecretKey(String alias) throws KeyStoreException {
        keyStore.deleteEntry(alias);
    }

    /**
     * Adds a private key to the keystore with the specified alias, password protection,
     * and certificate chain.
     *
     * @param alias the alias name for the key entry
     * @param privateKey the private key to store
     * @param password the password used to protect the key entry
     * @param chain the certificate chain for the corresponding public key
     * @throws KeyStoreException if the keystore has not been initialized,
     *         or if the key cannot be stored for any reason
     * @throws NullPointerException if alias, privateKey, password, or chain is null
     */
    public void addPrivateKey(String alias, PrivateKey privateKey, char[] password, X509Certificate[] chain)
            throws KeyStoreException {
        keyStore.setKeyEntry(alias, privateKey, password, chain);
    }

    /**
     * Retrieves a private key from the keystore using the specified alias and password.
     *
     * @param alias the alias name for the key entry
     * @param password the password used to recover the key
     * @return the requested private key
     * @throws UnrecoverableKeyException if the key cannot be recovered (e.g., wrong password)
     * @throws NoSuchAlgorithmException if the algorithm used to recover the key is not available
     * @throws KeyStoreException if the keystore has not been initialized
     * @throws NullPointerException if alias or password is null
     */
    public PrivateKey getPrivateKey(String alias, char[] password)
            throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
        return (PrivateKey) keyStore.getKey(alias, password);
    }

    /**
     * Adds a certificate to the keystore with the specified alias.
     *
     * @param alias the alias name for the certificate entry
     * @param cert the X.509 certificate to store
     * @throws KeyStoreException if the keystore has not been initialized,
     *         or if the certificate cannot be stored for any reason
     * @throws NullPointerException if alias or cert is null
     */
    public void addCertificate(String alias, X509Certificate cert) throws KeyStoreException {
        keyStore.setCertificateEntry(alias, cert);
    }

    /**
     * Retrieves a certificate from the keystore using the specified alias.
     *
     * @param alias the alias name for the certificate entry
     * @return the requested X.509 certificate, or null if no certificate exists for the given alias
     * @throws KeyStoreException if the keystore has not been initialized
     * @throws NullPointerException if alias is null
     */
    public X509Certificate getCertificate(String alias) throws KeyStoreException {
        return (X509Certificate) keyStore.getCertificate(alias);
    }

    /**
     * Deletes a key or certificate entry from the keystore.
     *
     * @param alias the alias name for the entry to delete
     * @throws KeyStoreException if the keystore has not been initialized,
     *         or if the entry cannot be deleted
     * @throws NullPointerException if alias is null
     */
    public void deleteKeyOrCertificate(String alias) throws KeyStoreException {
        keyStore.deleteEntry(alias);
    }

    /**
     * Lists all alias names in this keystore.
     *
     * @return an enumeration of the alias names
     * @throws KeyStoreException if the keystore has not been initialized
     */
    public Enumeration<String> listAliases() throws KeyStoreException {
        return keyStore.aliases();
    }

    /**
     * Checks if the keystore contains the specified alias.
     *
     * @param alias the alias name to check
     * @return true if the alias exists, false otherwise
     * @throws KeyStoreException if the keystore has not been initialized
     * @throws NullPointerException if alias is null
     */
    public boolean containsAlias(String alias) throws KeyStoreException {
        return keyStore.containsAlias(alias);
    }

	public SupportedKeystores getKeystoreType() {
		return keystoreType;
	}
	
	public boolean isNull() {
		return this.keyStore == null;
	}
    
    
}