package it.hackerinside.etk.Utils;

import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.HexFormat;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X962Parameters;

public class X509Utils {
	/**
	 * Checks the expiration of an x509 certificate to the current date.
	 * 
	 * @param cert The certificate to validate
	 * @return true if the certificate is valid
	 */
	public static boolean checkTimeValidity(X509Certificate cert) {
		try {
			cert.checkValidity();
			return true;
		} catch (CertificateExpiredException | CertificateNotYetValidException e) {
			return false;
		}
	}
	
	/**
	 * Converts a binary X.500 Distinguished Name (DN) from a certificate subject field
	 * into a human-readable string representation.
	 * 
	 * @param subjectX500Principal The DER-encoded X.500 Distinguished Name as a byte array,
	 *                             typically obtained from an X.509 certificate's subject field.
	 *                             Must not be null and must contain valid DER-encoded data.
	 * 
	 * @return A formatted string representation of the Distinguished Name, with attribute
	 *         type OIDs converted to their display names
	 */
    public static String getPrettySubject(byte[] subjectX500Principal) {
        X500Name name = X500Name.getInstance(
        		subjectX500Principal
        );

        StringBuilder sb = new StringBuilder();
        for (RDN rdn : name.getRDNs()) {
            if (rdn.getFirst() != null) {
                sb.append(BCStyle.INSTANCE.oidToDisplayName(rdn.getFirst().getType()))
                  .append("=")
                  .append(rdn.getFirst().getValue().toString())
                  .append(", ");
            }
        }

        return sb.substring(0, sb.length() - 2);
    	
    }
    
    /**
     * Extracts the Common Name (CN) from a Distinguished Name (DN) string.
     * 
     * @param dn the Distinguished Name string to parse
     * @return the Common Name value, or an empty string if not found
     */
    public static String extractCN(String dn) {
        for (String part : dn.split(",")) {
            if (part.trim().startsWith("CN=")) {
                return part.trim().substring(3);
            }
        }
        return "";
    }
    
    /**
     * Generates the SHA-256 fingerprint of a certificate.
     *
     * @param cert the X509Certificate
     * @return the fingerprint as an uppercase hexadecimal string, or an error message if generation fails
     */
    public static String getCertificateFingerprint(X509Certificate cert) {
	    try {
	        MessageDigest md = MessageDigest.getInstance("SHA-256");
	        byte[] fingerprint = md.digest(cert.getEncoded());
	        return HexFormat.of().formatHex(fingerprint).toUpperCase();
	    } catch (Exception e) {
	        e.printStackTrace();
	        return "Error generating fingerprint";
	    }
	}
	
    /**
     * Formats a hexadecimal string by splitting it into blocks of 4 characters for readability.
     *
     * @param hex the hexadecimal string
     * @return the formatted string with spaces every 4 characters
     */
	public static String formatFingerprint(String hex) {
	    // Break the hex string into blocks of 4 characters separated by spaces
	    StringBuilder sb = new StringBuilder();
	    for (int i = 0; i < hex.length(); i++) {
	        if (i > 0 && i % 4 == 0) sb.append(' ');
	        sb.append(hex.charAt(i));
	    }
	    return sb.toString();
	}
	
	/**
	 * Returns a human-readable description of the given public key.
	 *
	 * @param key the public key
	 * @return a descriptive string for the key, or an empty string if {@code key} is {@code null}
	 */
	public static String getPublicKeyDescription(PublicKey key) {
        if (key == null) {
            return "";
        }

        try {
            String alg = key.getAlgorithm();

            switch (alg) {
                case "RSA":
                    return getRsaDescription(key);

                case "EC":
                    return getEcDescription(key);

                default:
                    return alg;
            }
        } catch (Exception e) {
            return key.getAlgorithm();
        }
    }

	/**
	 * Returns the RSA key description.
	 *
	 * @param key the RSA public key
	 * @return the key size (for example, {@code RSA-2048}) or {@code RSA}
	 */
    private static String getRsaDescription(PublicKey key) {
        if (key instanceof RSAPublicKey rsa) {
            return "RSA-" + rsa.getModulus().bitLength();
        }

        return "RSA";
    }

    /**
     * Returns the EC key description.
     *
     * @param key the EC public key
     * @return the curve name if available; otherwise {@code EC}
     */
    private static String getEcDescription(PublicKey key) {
        String curve = getEcCurveName(key);
        return curve == null ? "EC" : "EC (" + curve + ")";
    }

    /**
     * Returns the name of the EC curve used by the public key.
     *
     * @param key the EC public key
     * @return the curve name, the curve OID if no name is available, or {@code null} if it cannot be determined
     */
    private static String getEcCurveName(PublicKey key) {
        try {
            SubjectPublicKeyInfo spki = SubjectPublicKeyInfo.getInstance(key.getEncoded());

            X962Parameters params = X962Parameters.getInstance(spki.getAlgorithm().getParameters());

            if (!params.isNamedCurve()) {
                return null;
            }

            ASN1ObjectIdentifier oid = ASN1ObjectIdentifier.getInstance(params.getParameters());

            String name = ECNamedCurveTable.getName(oid);

            return name != null ? name : oid.getId();

        } catch (Exception e) {
            return null;
        }
    }

}
