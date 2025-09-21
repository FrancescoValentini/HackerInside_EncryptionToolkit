package it.hackerinside.etk.core.keystore;

import java.math.BigInteger;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Objects;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;

import it.hackerinside.etk.core.Models.RecipientIdentifier;
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
    
    /**
     * Checks if the given certificate exists in the keystore.
     *
     * @param cert the X.509 certificate to check
     * @return the matching certificate if found, or null otherwise
     * @throws KeyStoreException if the keystore has not been initialized
     * @throws NullPointerException if cert is null
     */
    public X509Certificate contains(X509Certificate cert) throws KeyStoreException {
        Objects.requireNonNull(cert, "Certificate cannot be null");
        Enumeration<String> aliases = keyStore.aliases();
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            Certificate storedCert = keyStore.getCertificate(alias);
            if (storedCert instanceof X509Certificate) {
                X509Certificate x509 = (X509Certificate) storedCert;
                if (x509.equals(cert)) {
                    return x509;
                }
            }
        }
        return null;
    }

    /**
     * Checks if a certificate with the given serial number exists in the keystore.
     *
     * @param serialNumber the serial number of the certificate to check (in decimal or hex form)
     * @return the matching certificate if found, or null otherwise
     * @throws KeyStoreException if the keystore has not been initialized
     * @throws NullPointerException if serialNumber is null
     */
    public X509Certificate contains(String serialNumber) throws KeyStoreException {
        Objects.requireNonNull(serialNumber, "Serial number cannot be null");
        Enumeration<String> aliases = keyStore.aliases();
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            Certificate storedCert = keyStore.getCertificate(alias);
            if (storedCert instanceof X509Certificate) {
                X509Certificate x509 = (X509Certificate) storedCert;
                if (x509.getSerialNumber().toString().equalsIgnoreCase(serialNumber)
                        || x509.getSerialNumber().toString(16).equalsIgnoreCase(serialNumber)) {
                    return x509;
                }
            }
        }
        return null;
    }
    

    /**
     * Searches the keystore for an alias whose certificate matches at least one of the provided recipient identifiers.
     * The method iterates through all aliases in the keystore, checking only key entries with valid X.509 certificates.
     * For each certificate, it compares against all recipient identifiers, matching either by Subject Key Identifier (SKI)
     * or by Issuer and Serial Number combination.
     *
     * @param recipientIds a collection of recipient identifiers to search for. If null or empty, returns empty Optional.
     * @return an Optional containing the first matching alias found, or an empty Optional if no match is found
     *         or if the keystore contains no matching certificates.
     * @throws Exception if an error occurs while accessing the keystore or processing certificates.
     */
    public Optional<String> findAliasForRecipients(Collection<RecipientIdentifier> recipientIds) throws Exception {
        if (recipientIds == null || recipientIds.isEmpty()) {
            return Optional.empty();
        }

        Enumeration<String> aliases = keyStore.aliases();
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();

            if (!isKeyEntry(alias)) {
                continue;
            }

            X509Certificate cert = getCertificate(alias);
            if (cert == null) {
                continue;
            }

            if (certificateMatchesAnyRecipient(cert, recipientIds)) {
                return Optional.of(alias);
            }
        }
        return Optional.empty();
    }

    /**
     * Checks if the specified alias represents a valid key entry in the keystore.
     *
     * @param alias the alias to check
     * @return true if the alias represents a key entry, false otherwise or if an error occurs
     */
    private boolean isKeyEntry(String alias) {
        try {
            return keyStore.isKeyEntry(alias);
        } catch (KeyStoreException e) {
            return false;
        }
    }

    /**
     * Determines if a certificate matches any of the provided recipient identifiers.
     * Compares the certificate's Subject Key Identifier, Issuer, and Serial Number against all recipients.
     *
     * @param cert the X.509 certificate to examine
     * @param recipients the collection of recipient identifiers to compare against
     * @return true if the certificate matches any recipient identifier, false otherwise
     */
    private boolean certificateMatchesAnyRecipient(X509Certificate cert, Collection<RecipientIdentifier> recipients) {
        byte[] certSki = extractSubjectKeyIdentifier(cert);
        byte[] certIssuer = extractIssuer(cert);
        BigInteger certSerial = cert.getSerialNumber();

        for (RecipientIdentifier rid : recipients) {
            if (recipientMatchesCertificate(rid, certSki, certIssuer, certSerial)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a single recipient identifier matches the provided certificate data.
     * For SKI-based recipients, compares the Subject Key Identifier.
     * For Issuer/Serial-based recipients, compares both Issuer Distinguished Name and Serial Number.
     *
     * @param rid the recipient identifier to match
     * @param certSki the certificate's Subject Key Identifier (may be null)
     * @param certIssuer the certificate's Issuer DN in ASN.1 encoded form (may be null)
     * @param certSerial the certificate's Serial Number (may be null)
     * @return true if the recipient matches the certificate data, false otherwise
     */
    private boolean recipientMatchesCertificate(RecipientIdentifier rid,
                                                byte[] certSki,
                                                byte[] certIssuer,
                                                BigInteger certSerial) {
        if (rid.getType() == RecipientIdentifier.Type.SUBJECT_KEY_IDENTIFIER) {
            return certSki != null && rid.getSki() != null &&
                   Arrays.equals(rid.getSki(), certSki);
        } else {
            return certIssuer != null && rid.getIssuerEncoded() != null &&
                   certSerial != null && rid.getSerial() != null &&
                   rid.getSerial().equals(certSerial) &&
                   Arrays.equals(rid.getIssuerEncoded(), certIssuer);
        }
    }

    /**
     * Extracts the Subject Key Identifier extension from an X.509 certificate.
     *
     * @param cert the certificate from which to extract the SKI
     * @return the Subject Key Identifier as a byte array, or null if the extension is not present
     *         or an error occurs during extraction
     */
    private byte[] extractSubjectKeyIdentifier(X509Certificate cert) {
        try {
            byte[] extVal = cert.getExtensionValue(Extension.subjectKeyIdentifier.getId());
            if (extVal != null) {
                ASN1Primitive obj = ASN1Primitive.fromByteArray(extVal);
                ASN1OctetString oct = ASN1OctetString.getInstance(obj);
                SubjectKeyIdentifier skid =
                        SubjectKeyIdentifier.getInstance(ASN1Primitive.fromByteArray(oct.getOctets()));
                return skid.getKeyIdentifier();
            }
        } catch (Exception ignored) {}
        return null;
    }

    /**
     * Extracts the Issuer Distinguished Name from an X.509 certificate in ASN.1 encoded form.
     *
     * @param cert the certificate from which to extract the issuer
     * @return the Issuer DN as a byte array in ASN.1 encoding, or null if an error occurs
     */
    private byte[] extractIssuer(X509Certificate cert) {
        try {
            X500Name issuerName = X500Name.getInstance(cert.getIssuerX500Principal().getEncoded());
            return issuerName.getEncoded();
        } catch (Exception ignored) {}
        return null;
    }
}