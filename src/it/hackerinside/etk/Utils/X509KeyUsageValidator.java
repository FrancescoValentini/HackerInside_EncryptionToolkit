package it.hackerinside.etk.Utils;

import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import it.hackerinside.etk.core.Models.KeyUsageBit;
import it.hackerinside.etk.core.Models.KeyUsageProfile;

/**
 * Utility class for validating X.509 certificate KeyUsage extensions
 * against a predefined set of expected usage bits.
 *
 * <p>This validator supports multiple matching strategies defined by {@link Mode}.</p>
 *
 * <p>If the certificate does not define any KeyUsage extension
 * (i.e. {@code cert.getKeyUsage() == null}), it is considered valid for all checks.</p>
 */
public class X509KeyUsageValidator {

    /**
     * Matching mode used to evaluate KeyUsage constraints.
     */
    public enum Mode {

        /**
         * All specified KeyUsage bits must be present in the certificate.
         */
        ALL,

        /**
         * At least one of the specified KeyUsage bits must be present.
         */
        ANY,

        /**
         * The certificate must contain exactly the specified KeyUsage bits
         * and no others.
         */
        EXACT_SET,

        /**
         * Exactly one of the specified KeyUsage bits must be present,
         * and no other KeyUsage bits outside the set are allowed.
         */
        EXACTLY_ONE
    }

    /**
     * Validates the KeyUsage extension of a certificate against a predefined profile.
     *
     * @param cert    the X.509 certificate to validate
     * @param mode    the matching mode used for validation
     * @param profile the KeyUsage profile containing required usages
     * @return {@code true} if the certificate satisfies the KeyUsage constraints,
     *         {@code false} otherwise
     */
    public static boolean hasKeyUsage(
            X509Certificate cert,
            Mode mode,
            KeyUsageProfile profile
    ) {
        return hasKeyUsage(cert, mode, profile.requiredUsages());
    }

    /**
     * Validates the KeyUsage extension of a certificate against a list of required usages.
     *
     * @param cert   the X.509 certificate to validate
     * @param mode   the matching mode used for validation
     * @param usages the list of required KeyUsage bits
     * @return {@code true} if the certificate satisfies the KeyUsage constraints,
     *         {@code false} otherwise
     */
    public static boolean hasKeyUsage(
            X509Certificate cert,
            Mode mode,
            List<KeyUsageBit> usages
    ) {

        boolean[] ku = cert.getKeyUsage();

        // If certificate does not define KeyUsage, accept it
        if (ku == null) {
            return true;
        }

        if (usages == null || usages.isEmpty()) {
            return true;
        }

        Set<Integer> allowed = usages.stream()
                .map(KeyUsageBit::index)
                .collect(Collectors.toSet());

        switch (mode) {

            case ALL:
                for (KeyUsageBit u : usages) {
                    int i = u.index();
                    if (i >= ku.length || !ku[i]) {
                        return false;
                    }
                }
                return true;

            case ANY:
                for (KeyUsageBit u : usages) {
                    int i = u.index();
                    if (i < ku.length && ku[i]) {
                        return true;
                    }
                }
                return false;

            case EXACT_SET:
                return isExactSet(ku, allowed);

            case EXACTLY_ONE:
                return isExactlyOne(ku, allowed);

            default:
                throw new IllegalArgumentException("Unknown mode: " + mode);
        }
    }

    /**
     * Checks whether the certificate KeyUsage matches exactly the allowed set:
     * <ul>
     *     <li>No additional KeyUsage bits outside the allowed set are set</li>
     *     <li>All allowed bits are present in the certificate</li>
     * </ul>
     *
     * @param ku      KeyUsage boolean array from the certificate
     * @param allowed set of allowed KeyUsage indices
     * @return {@code true} if the KeyUsage matches exactly the allowed set
     */
    private static boolean isExactSet(boolean[] ku, Set<Integer> allowed) {

        for (int i = 0; i < ku.length; i++) {
            if (ku[i] && !allowed.contains(i)) {
                return false;
            }
        }

        for (Integer i : allowed) {
            if (i >= ku.length || !ku[i]) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks whether exactly one KeyUsage bit from the allowed set is present,
     * and no other bits are set outside the allowed set.
     *
     * @param ku      KeyUsage boolean array from the certificate
     * @param allowed set of allowed KeyUsage indices
     * @return {@code true} if exactly one allowed KeyUsage bit is set and no others
     */
    private static boolean isExactlyOne(boolean[] ku, Set<Integer> allowed) {

        int count = 0;

        for (Integer i : allowed) {
            if (i < ku.length && ku[i]) {
                count++;
            }
        }

        if (count != 1) {
            return false;
        }

        for (int i = 0; i < ku.length; i++) {
            if (ku[i] && !allowed.contains(i)) {
                return false;
            }
        }

        return true;
    }
}