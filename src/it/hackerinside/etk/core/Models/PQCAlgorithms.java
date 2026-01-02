package it.hackerinside.etk.core.Models;

import java.security.spec.AlgorithmParameterSpec;

import org.bouncycastle.jcajce.spec.MLDSAParameterSpec;
import org.bouncycastle.jcajce.spec.SLHDSAParameterSpec;

/**
 * Enumeration of Post-Quantum Cryptography (PQC) algorithms
 * @author Francesco Valentini
 */
public enum PQCAlgorithms {
    // -----------------------------
    // SLH-DSA / SPHINCS+ (DSA)
    // -----------------------------
    SLH_DSA_128S("SLH-DSA", "SLH-DSA-SHAKE-128s", SLHDSAParameterSpec.slh_dsa_shake_128s, true),
    SLH_DSA_128F("SLH-DSA", "SLH-DSA-SHAKE-128f", SLHDSAParameterSpec.slh_dsa_shake_128f, true),
    SLH_DSA_192S("SLH-DSA", "SLH-DSA-SHAKE-192s", SLHDSAParameterSpec.slh_dsa_shake_192s, true),
    SLH_DSA_192F("SLH-DSA", "SLH-DSA-SHAKE-192f", SLHDSAParameterSpec.slh_dsa_shake_192f, true),
    SLH_DSA_256S("SLH-DSA", "SLH-DSA-SHAKE-256s", SLHDSAParameterSpec.slh_dsa_shake_256s, true),
    SLH_DSA_256F("SLH-DSA", "SLH-DSA-SHAKE-256f", SLHDSAParameterSpec.slh_dsa_shake_256f, true),

    // -----------------------------
    // ML-DSA (DSA)
    // -----------------------------
    ML_DSA_44("ML-DSA", "ML-DSA-44", MLDSAParameterSpec.ml_dsa_44, true),
    ML_DSA_65("ML-DSA", "ML-DSA-65", MLDSAParameterSpec.ml_dsa_65, true),
    ML_DSA_87("ML-DSA", "ML-DSA-87", MLDSAParameterSpec.ml_dsa_87, true);

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


    /**
     * Constructs a PQCAlgorithm enum constant with the given parameters.
     * 
     * @param keyPairAlgorithm the algorithm used for key pair generation
     * @param bcName the string identifier used in the BouncyCastle API for the algorithm
     * @param params the parameter specifications for this algorithm
     * @param canSign true if the algorithm supports signing, false otherwise
     */
    PQCAlgorithms(String keyPairAlgorithm, String bcName, AlgorithmParameterSpec params, boolean canSign) {
        this.keyPairAlgorithm = keyPairAlgorithm;
        this.bcName = bcName;
        this.params = params;
        this.canSign = canSign;
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
    
    /**
     * Returns the string identifier used in the BouncyCastle API for this algorithm.
     * 
     * @return the BC name of the algorithm
     */
    @Override
    public String toString() {
    	return this.bcName;
    }
}
