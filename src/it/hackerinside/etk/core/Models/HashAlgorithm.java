package it.hackerinside.etk.core.Models;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;

/**
 * Represents supported cryptographic hash algorithms.
 * This enum provides commonly used Secure Hash Algorithm (SHA) variants.
 * 
 * @author Francesco Valentini
 */
public enum HashAlgorithm {
    /**
     * SHA-256 hash algorithm (produces 256-bit/32-byte hash values).
     */
    SHA256("sha256",NISTObjectIdentifiers.id_sha256),
    
    /**
     * SHA-384 hash algorithm (produces 384-bit/48-byte hash values).
     */
    SHA384("sha384",NISTObjectIdentifiers.id_sha384),
    
    /**
     * SHA-512 hash algorithm (produces 512-bit/64-byte hash values).
     */
    SHA512("sha512",NISTObjectIdentifiers.id_sha384);
    
    private final String algorithm;
    
    /**
     * The ASN.1 object identifier for the algorithm.
     */
    private final ASN1ObjectIdentifier asn1;
    
    private HashAlgorithm(String algorithm,ASN1ObjectIdentifier asn1) {
        this.algorithm = algorithm;
        this.asn1 = asn1;
    }
    
    /**
     * Converts a string value to its corresponding HashAlgorithm enum constant.
     * 
     * @param algorithm the string representation of the hash algorithm (case-insensitive)
     * @return the HashAlgorithm enum constant matching the provided algorithm name
     * @throws IllegalArgumentException if the provided algorithm name doesn't match any supported algorithm
     */
    public static HashAlgorithm fromString(String algorithm) {
        for (HashAlgorithm alg : HashAlgorithm.values()) {
            if (alg.algorithm.equalsIgnoreCase(algorithm)) {
                return alg;
            }
        }
        throw new IllegalArgumentException("Invalid Hash Algorithm: " + algorithm);
    }
    
    /**
     * Returns the standard algorithm name for this hash algorithm.
     * 
     * @return the string representation of this hash algorithm
     */
    @Override
    public String toString() {
        return algorithm;
    }
    
    
    /**
     * Returns the ASN.1 object identifier for the algorithm.
     *
     * @return the ASN.1 object identifier
     */
    public ASN1ObjectIdentifier getASN1() {
    	return this.asn1;
    }
}
