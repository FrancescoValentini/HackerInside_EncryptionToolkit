package it.hackerinside.etk.core.Models;

import java.security.PrivateKey;

/**
 * Represents supported asymmetric key algorithms for public-key cryptography.
 * 
 * @author Francesco Valentini
 */
public enum AsymmetricAlgorithm {
    /**
     * Rivest-Shamir-Adleman (RSA) algorithm.
    */
    RSA("RSA"),
    
    /**
     * Digital Signature Algorithm (DSA).
    */
    DSA("DSA"),
    
    /**
     * Elliptic Curve (EC) cryptography algorithm.
    */
    EC("EC");
    
    private final String algorithm;
    
    private AsymmetricAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }
    
    /**
     * Converts a string value to its corresponding AsymmetricAlgorithm enum constant.
     * 
     * @param algorithm the string representation of the asymmetric algorithm (case-insensitive)
     * @return the AsymmetricAlgorithm enum constant matching the provided algorithm name
     * @throws IllegalArgumentException if the provided algorithm name doesn't match any supported algorithm
     */
    public static AsymmetricAlgorithm fromString(String algorithm) {
        for (AsymmetricAlgorithm alg : AsymmetricAlgorithm.values()) {
            if (alg.algorithm.equalsIgnoreCase(algorithm)) {
                return alg;
            }
        }
        throw new IllegalArgumentException("Invalid Asymmetric Algorithm: " + algorithm);
    }
    
    /**
     * Determines the asymmetric algorithm from a PrivateKey instance.
     * 
     * @param privateKey the private key from which to extract the algorithm
     * @return the AsymmetricAlgorithm enum constant corresponding to the private key's algorithm
     * @throws IllegalArgumentException if the private key's algorithm doesn't match any supported algorithm
     */
    public static AsymmetricAlgorithm fromPrivateKey(PrivateKey privateKey) {
        return fromString(privateKey.getAlgorithm());
    }
    
    /**
     * Returns the standard algorithm name for this asymmetric algorithm.
     * 
     * @return the string representation of this asymmetric algorithm
     */
    @Override
    public String toString() {
        return algorithm;
    }
}
