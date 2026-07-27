package it.hackerinside.etk.core.Models;

import java.util.Map;

/**
 * Utility class containing plaintext size limitations for supported
 * symmetric encryption algorithms.
 *
 * <p>
 * Some authenticated encryption algorithms impose a maximum amount of
 * plaintext that can be safely encrypted with a single key/nonce pair.
 * Classical block cipher modes such as CBC do not have an algorithm-specific
 * limit and are therefore not included in this table.
 * </p>
 *
 * @author Francesco Valentini
 */
public final class CryptoLimits {

    private CryptoLimits() {
        // Utility class
    }

    /**
     * AES-GCM maximum plaintext length according to NIST SP 800-38D.
     *
     * <p>
     * The limit is:
     * (2^32 - 2) blocks × 16 bytes = 68,719,476,704 bytes (~64 GiB)
     * </p>
     */
    public static final long AES_GCM_MAX_SIZE =
            ((1L << 32) - 2L) * 16L;

    /**
     * ChaCha20-Poly1305 maximum plaintext length according to RFC 8439.
     *
     * <p>
     * The limit is:
     * (2^32 - 1) blocks × 64 bytes = 274,877,906,880 bytes (~256 GiB)
     * </p>
     */
    public static final long CHACHA20_POLY1305_MAX_SIZE =
            ((1L << 32) - 1L) * 64L;


    /**
     * Maximum plaintext size allowed for each algorithm.
     *
     * <p>
     * Algorithms not present in this map have no algorithm-specific
     * plaintext size restriction.
     * </p>
     */
    private static final Map<SymmetricAlgorithms, Long> MAX_PLAINTEXT_SIZE =
            Map.of(
                SymmetricAlgorithms.AES_128_GCM, AES_GCM_MAX_SIZE,
                SymmetricAlgorithms.AES_256_GCM, AES_GCM_MAX_SIZE,
                SymmetricAlgorithms.CHACHA20_POLY1305, CHACHA20_POLY1305_MAX_SIZE
            );


    /**
     * Checks whether a plaintext size is supported by the selected algorithm.
     *
     * @param algorithm encryption algorithm
     * @param plaintextSize plaintext size in bytes
     * @return {@code true} if the size is supported, otherwise {@code false}
     */
    public static boolean isSupported(
            SymmetricAlgorithms algorithm,
            long plaintextSize) {

        Long maxSize = MAX_PLAINTEXT_SIZE.get(algorithm);

        return maxSize == null || plaintextSize <= maxSize;
    }


    /**
     * Returns the maximum supported plaintext size for the selected algorithm.
     *
     * @param algorithm encryption algorithm
     * @return maximum plaintext size in bytes, or {@code -1} if no
     *         algorithm-specific limit applies
     */
    public static long getMaxPlaintextSize(
            SymmetricAlgorithms algorithm) {

        return MAX_PLAINTEXT_SIZE.getOrDefault(algorithm, -1L);
    }
}