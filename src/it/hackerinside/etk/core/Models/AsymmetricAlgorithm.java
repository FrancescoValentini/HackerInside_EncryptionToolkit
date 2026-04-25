package it.hackerinside.etk.core.Models;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;

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
    EC("EC"),
    
    /**
     * Edwards-curve Digital Signature Algorithm
     */
    EdDSA("EdDSA");
    
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
    
    /**
     * Determines the asymmetric algorithm used in an X.509 certificate.
     * <p>
     * The algorithm is identified from the public key OID.
     *
     * @param cert the X509 certificate
     * @return the corresponding AsymmetricAlgorithm, or null if not recognized
     */
    public static AsymmetricAlgorithm fromCertificate(X509Certificate cert) {
        SubjectPublicKeyInfo spki =
            SubjectPublicKeyInfo.getInstance(cert.getPublicKey().getEncoded());

        ASN1ObjectIdentifier oid = spki.getAlgorithm().getAlgorithm();

        return fromOID(oid);
    }
    
    public static AsymmetricAlgorithm fromOID(ASN1ObjectIdentifier oid) {
        if (isRSA(oid)) return RSA;
        if (isEC(oid)) return EC;
        if (isEdDSA(oid)) return EdDSA;
        if (isDSA(oid)) return DSA;
        return null;
    }
    
    private static boolean isRSA(ASN1ObjectIdentifier oid) {
        return PKCSObjectIdentifiers.rsaEncryption.equals(oid);
    }

    private static boolean isEC(ASN1ObjectIdentifier oid) {
        return X9ObjectIdentifiers.id_ecPublicKey.equals(oid);
    }

    private static boolean isEdDSA(ASN1ObjectIdentifier oid) {
        return EdECObjectIdentifiers.id_Ed25519.equals(oid)
            || EdECObjectIdentifiers.id_Ed448.equals(oid);
    }

    private static boolean isDSA(ASN1ObjectIdentifier oid) {
        return X9ObjectIdentifiers.id_dsa.equals(oid);
    }
}
