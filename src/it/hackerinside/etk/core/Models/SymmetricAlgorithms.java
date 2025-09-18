package it.hackerinside.etk.core.Models;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;

/**
 * Enumeration of supported symmetric encryption algorithms.
 * Provides mappings between algorithm names, ASN.1 identifiers, and related properties
 * such as key sizes and suggested key wrapping algorithms.
 * 
 * @author Francesco Valentini
 */
public enum SymmetricAlgorithms {
    
    /** AES 128-bit key with CBC (Cipher Block Chaining) mode */
    AES_128_CBC("AES-128-CBC", NISTObjectIdentifiers.id_aes128_CBC),
    
    /** AES 128-bit key with CFB (Cipher Feedback) mode */
    AES_128_CFB("AES-128-CFB", NISTObjectIdentifiers.id_aes128_CFB),
    
    /** AES 128-bit key with GCM (Galois/Counter Mode) mode */
    AES_128_GCM("AES-128-GCM", NISTObjectIdentifiers.id_aes128_GCM),
    
    /** AES 128-bit key with CCM (Counter with CBC-MAC) mode */
    AES_128_CCM("AES-128-CCM", NISTObjectIdentifiers.id_aes128_CCM),
    
    /** AES 256-bit key with CBC (Cipher Block Chaining) mode */
    AES_256_CBC("AES-256-CBC", NISTObjectIdentifiers.id_aes256_CBC),
    
    /** AES 256-bit key with CFB (Cipher Feedback) mode */
    AES_256_CFB("AES-256-CFB", NISTObjectIdentifiers.id_aes256_CFB),
    
    /** AES 256-bit key with GCM (Galois/Counter Mode) mode */
    AES_256_GCM("AES-256-GCM", NISTObjectIdentifiers.id_aes256_GCM),
    
    /** AES 256-bit key with CCM (Counter with CBC-MAC) mode */
    AES_256_CCM("AES-256-CCM", NISTObjectIdentifiers.id_aes256_CCM);

    
    /**
     * The algorithm name as a string.
     */
    private final String algorithm;
    
    /**
     * The ASN.1 object identifier for the cipher.
     */
    private final ASN1ObjectIdentifier cipher;
    
    /**
     * Constructs a new SymmetricAlgorithms enum value.
     *
     * @param algorithm the algorithm name as a string
     * @param cipher the ASN.1 object identifier for the cipher
     */
    private SymmetricAlgorithms(String algorithm, ASN1ObjectIdentifier cipher) {
        this.algorithm = algorithm;
        this.cipher = cipher;
    }

    /**
     * Returns the SymmetricAlgorithms enum value corresponding to the given algorithm name.
     * The comparison is case-insensitive.
     *
     * @param algorithm the algorithm name as a string
     * @return the corresponding SymmetricAlgorithms enum value
     * @throws IllegalArgumentException if the algorithm name is not supported
     */
    public static SymmetricAlgorithms fromString(String algorithm) {
        for (SymmetricAlgorithms alg : SymmetricAlgorithms.values()) {
            if (alg.algorithm.equalsIgnoreCase(algorithm)) {
                return alg;
            }
        }
        throw new IllegalArgumentException("Invalid Symmetric Algorithm: " + algorithm);
    }
    
    /**
     * Returns the algorithm name as a string.
     *
     * @return the algorithm name
     */
    public String getAlgorithmName() {
        return algorithm;
    }

    /**
     * Returns the ASN.1 object identifier for the cipher.
     *
     * @return the ASN.1 object identifier
     */
    public ASN1ObjectIdentifier getCipherASN1() {
        return cipher;
    }
    
    /**
     * Returns the suggested key wrapping algorithm ASN.1 object identifier
     * based on the key size of this algorithm.
     *
     * @return the suggested key wrapping algorithm ASN.1 identifier
     * @throws IllegalArgumentException if the algorithm has an unsupported key size
     */
    public ASN1ObjectIdentifier suggestedKeyWrap() {
        switch(this.keySize()) {
        case 128:
            return NISTObjectIdentifiers.id_aes128_wrap;
        case 256:
            return NISTObjectIdentifiers.id_aes256_wrap;
        default:
            throw new IllegalArgumentException("Invalid Symmetric Algorithm: " + algorithm);

        }
    }
    
    /**
     * Returns the key size in bits for this algorithm.
     * The key size is extracted from the algorithm name.
     *
     * @return the key size in bits
     */
    public int keySize() {
        return Integer.parseInt(algorithm.split("-")[1]);
    }
    
    
    /**
     * Returns the algorithm name as a string.
     *
     * @return the algorithm name
     */
    @Override
    public String toString() {
        return algorithm;
    }
    
}