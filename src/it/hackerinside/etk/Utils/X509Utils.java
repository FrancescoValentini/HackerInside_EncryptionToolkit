package it.hackerinside.etk.Utils;

import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;

import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;

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

}
