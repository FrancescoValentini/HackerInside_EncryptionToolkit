package it.hackerinside.etk.GUI.DTOs;

import java.security.cert.X509Certificate;

import java.security.MessageDigest;
import java.util.HexFormat;

/**
 * Represents a row in a certificate table, containing key certificate information
 * for display and management purposes. This record encapsulates both the original
 * certificate and derived properties suitable for UI presentation.
 * 
 *
 * @param keystoreAlias the alias under which the certificate is stored in the keystore
 * @param CommonName the Common Name (CN) extracted from the certificate's subject DN
 * @param truncatedFingerprint a truncated SHA-256 fingerprint of the certificate (20 hex characters)
 * @param location indicates where the certificate keys are stored (software, hardware, etc.)
 * @param original the original X509Certificate object
 * @author Francesco Valentini
 */
public record CertificateTableRow(
    String keystoreAlias,
    String CommonName,
    String truncatedFingerprint,
    KeysLocations location,
    X509Certificate original) {

    /**
     * Constructs a CertificateTableRow by extracting relevant information from
     * the provided certificate.
     *
     * @param alias the keystore alias for the certificate
     * @param location the storage location of the certificate keys
     * @param cert the X.509 certificate to extract information from
     * @throws NullPointerException if any of the parameters are null
     * 
     */
    public CertificateTableRow(String alias, KeysLocations location, X509Certificate cert) {
        this(
            alias,
            extractCommonName(cert),
            generateTruncatedFingerprint(cert),
            location,
            cert
        );
    }

    /**
     * Extracts the Common Name (CN) from the certificate's subject Distinguished Name.
     * 
     *
     * @param cert the X.509 certificate from which to extract the Common Name
     * @return the Common Name if found, otherwise an empty string
     * @throws NullPointerException if the certificate parameter is null
     */
    private static String extractCommonName(X509Certificate cert) {
        try {
            String dn = cert.getSubjectX500Principal().getName();
            // Extract CN from Distinguished Name
            String[] dnComponents = dn.split(",");
            for (String component : dnComponents) {
                if (component.trim().startsWith("CN=")) {
                    return component.trim().substring(3);
                }
            }
            return ""; // Fallback if CN not found
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Generates a truncated SHA-256 fingerprint for the certificate.
     * 
     *
     * @param cert the X.509 certificate for which to generate the fingerprint
     * @return the truncated fingerprint in uppercase hexadecimal format, or
     *         "Error generating fingerprint" if an exception occurs
     * @throws NullPointerException if the certificate parameter is null
     */
    private static String generateTruncatedFingerprint(X509Certificate cert) {
        try {
            // Generate SHA-256 fingerprint
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] fingerprint = md.digest(cert.getEncoded());
            
            // Convert to hex string
            String fullFingerprint = HexFormat.of().formatHex(fingerprint);
            
            // Truncate to 80 bits (20 hex characters, since 80 bits = 80/4 = 20 hex chars)
            // 80 bits = 10 bytes = 20 hex characters
            return fullFingerprint.substring(0, 20).toUpperCase();
            
        } catch (Exception e) {
        	e.printStackTrace();
            return "Error generating fingerprint";
        }
    }

	@Override
	public String toString() {
		return keystoreAlias + "/" + CommonName+"-"+truncatedFingerprint;
	}
    
    
}