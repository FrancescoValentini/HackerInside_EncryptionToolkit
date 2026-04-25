package it.hackerinside.etk.core.Models;

import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jcajce.spec.MLDSAParameterSpec;
import org.bouncycastle.jcajce.spec.MLKEMParameterSpec;
import org.bouncycastle.jcajce.spec.SLHDSAParameterSpec;

/**
 * Enumeration of Post-Quantum Cryptography (PQC) algorithms
 * @author Francesco Valentini
 */
public enum PQCAlgorithms {
    // -----------------------------
    // SLH-DSA / SPHINCS+ (DSA)
    // -----------------------------
    SLH_DSA_128S("SLH-DSA", "SLH-DSA-SHAKE-128s", SLHDSAParameterSpec.slh_dsa_shake_128s, true,"2.16.840.1.101.3.4.3.26"),
    SLH_DSA_128F("SLH-DSA", "SLH-DSA-SHAKE-128f", SLHDSAParameterSpec.slh_dsa_shake_128f, true,"2.16.840.1.101.3.4.3.27"),
    SLH_DSA_192S("SLH-DSA", "SLH-DSA-SHAKE-192s", SLHDSAParameterSpec.slh_dsa_shake_192s, true,"2.16.840.1.101.3.4.3.28"),
    SLH_DSA_192F("SLH-DSA", "SLH-DSA-SHAKE-192f", SLHDSAParameterSpec.slh_dsa_shake_192f, true,"2.16.840.1.101.3.4.3.29"),
    SLH_DSA_256S("SLH-DSA", "SLH-DSA-SHAKE-256s", SLHDSAParameterSpec.slh_dsa_shake_256s, true,"2.16.840.1.101.3.4.3.30"),
    SLH_DSA_256F("SLH-DSA", "SLH-DSA-SHAKE-256f", SLHDSAParameterSpec.slh_dsa_shake_256f, true,"2.16.840.1.101.3.4.3.31"),

    // -----------------------------
    // ML-DSA (DSA)
    // -----------------------------
    ML_DSA_44("ML-DSA", "ML-DSA-44", MLDSAParameterSpec.ml_dsa_44, true,"2.16.840.1.101.3.4.3.17"),
    ML_DSA_65("ML-DSA", "ML-DSA-65", MLDSAParameterSpec.ml_dsa_65, true,"2.16.840.1.101.3.4.3.18"),
    ML_DSA_87("ML-DSA", "ML-DSA-87", MLDSAParameterSpec.ml_dsa_87, true,"2.16.840.1.101.3.4.3.19"),
	
	// -----------------------------
	// ML-KEM (KEM)
	// -----------------------------
    ML_KEM_44("ML-KEM", "ML-KEM-512", MLKEMParameterSpec.ml_kem_512, false, "2.16.840.1.101.3.4.4.1"),
    ML_KEM_65("ML-KEM", "ML-KEM-768", MLKEMParameterSpec.ml_kem_768, false, "2.16.840.1.101.3.4.4.2"),
    ML_KEM_87("ML-KEM", "ML-KEM-1024", MLKEMParameterSpec.ml_kem_1024, false, "2.16.840.1.101.3.4.4.3");

    /**
     * The algorithm used for key pair generation.
     */
	public final String keyPairAlgorithm;
    /**
     * The string identifier used in the BouncyCastle (BC) API for this algorithm.
     */
    public final String bcName;
    /**
     * The set of parameters specific to this PQC algorithm.
     */
    public final AlgorithmParameterSpec params;
    
    /**
     * Flag indicating whether this algorithm supports signing operations.
     */
    public final boolean canSign;
    
    public final String oid;


    /**
     * Constructs a PQCAlgorithm enum constant with the given parameters.
     * 
     * @param keyPairAlgorithm the algorithm used for key pair generation
     * @param bcName the string identifier used in the BouncyCastle API for the algorithm
     * @param params the parameter specifications for this algorithm
     * @param canSign true if the algorithm supports signing, false otherwise
     */
    PQCAlgorithms(String keyPairAlgorithm, String bcName, AlgorithmParameterSpec params, boolean canSign, String oid) {
        this.keyPairAlgorithm = keyPairAlgorithm;
        this.bcName = bcName;
        this.params = params;
        this.canSign = canSign;
        this.oid = oid;
    }
    
    /**
     * Converts a string value to its corresponding PQCAlgorithms enum constant.
     * 
     * @param algorithm the string representation of the PQC BC algorithm (case-insensitive)
     * @return the PQCAlgorithms enum constant matching the provided algorithm name
     * @throws IllegalArgumentException if the provided algorithm name doesn't match any supported algorithm
     */
    public static PQCAlgorithms fromString(String algorithm) {
        for (PQCAlgorithms alg : PQCAlgorithms.values()) {
            if (alg.bcName.equalsIgnoreCase(algorithm)) {
                return alg;
            }
        }
        throw new IllegalArgumentException("Invalid PQC Algorithm: " + algorithm);
    }
    
    public static boolean isPQC(String algorithm) {
        for (PQCAlgorithms alg : PQCAlgorithms.values()) {
            if (alg.bcName.equalsIgnoreCase(algorithm) || alg.keyPairAlgorithm.equalsIgnoreCase(algorithm)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Returns the string identifier used in the BouncyCastle API for this algorithm.
     * 
     * @return the BC name of the algorithm
     */
    @Override
    public String toString() {
    	return this.bcName;
    }
    
    /**
     * Resolves a PQC algorithm from its ASN.1 object identifier (OID).
     *
     * @param oid the ASN.1 object identifier
     * @return the matching PQCAlgorithms enum, or null if not found
     */
    public static PQCAlgorithms fromOID(ASN1ObjectIdentifier oid) {
        String id = oid.getId();

        for (PQCAlgorithms alg : PQCAlgorithms.values()) {
            if (alg.oid != null && alg.oid.equals(id)) {
                return alg;
            }
        }
        return null;
    }
    

	/**
	 * Extracts the PQC algorithm used in an X.509 certificate.
	 * <p>
	 * First attempts resolution via the public key OID, then falls back
	 * to matching the provider-specific algorithm name if the OID is PQC.
	 *
	 * @param cert the X509 certificate
	 * @return the matching PQCAlgorithms enum, or null if not identified
	 */
    public static PQCAlgorithms fromCertificate(X509Certificate cert) {

        SubjectPublicKeyInfo spki =
            SubjectPublicKeyInfo.getInstance(cert.getPublicKey().getEncoded());

        ASN1ObjectIdentifier oid = spki.getAlgorithm().getAlgorithm();

        String id = oid.getId();

        // OID
        PQCAlgorithms pqc = fromOID(oid);
        if (pqc != null) return pqc;

        // fallback
        if (isPQC(id)) {
            for (PQCAlgorithms alg : values()) {
                if (alg.bcName.equalsIgnoreCase(cert.getPublicKey().getAlgorithm())) {
                    return alg;
                }
            }
        }

        return null;
    }
}
