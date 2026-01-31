package it.hackerinside.etk.Utils;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;
import java.security.cert.PKIXRevocationChecker;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.hackerinside.etk.core.keystore.AbstractKeystore;

/**
 * Validates X.509 certificate trust chains against a configured trust store.
 * The validator supports soft-fail revocation checking when revocation
 * information is available but unreachable.
 * 
 * @author Francesco Valentini
 * 
 */
public class X509TrustChainValidator {
    /**
     * The trust store containing trusted root CA certificates.
     */
	private AbstractKeystore trustStore;
	
    /**
     * Set of trust anchors derived from the trust store certificates.
     * Each trust anchor represents a trusted root certification authority.
     */
	Set<TrustAnchor> trustAnchors;
	
    /**
     * Constructs a new X509TrustChainValidator with the specified trust store.
     *
     * @param trustStore the trust store containing trusted root certificates.
     *                   Cannot be null and must contain at least one valid
     *                   X.509 certificate.
     */
	public X509TrustChainValidator(AbstractKeystore trustStore) {
		this.trustAnchors = new HashSet<>();
		this.trustStore = trustStore;
		buildTrustStore();

	}
	
	/**
     * Builds the set of trust anchors from the configured trust store.
     */
	private void buildTrustStore() {
		try {
	        Enumeration<String> aliases = trustStore.listAliases();
	        while (aliases.hasMoreElements()) {
	            String alias = aliases.nextElement();
	            if (trustStore.containsAlias(alias)) {
	                X509Certificate caCert =
	                        (X509Certificate) trustStore.getCertificate(alias);
	                trustAnchors.add(new TrustAnchor(caCert, null));
	            }
	        };

		}catch (Exception e) {
			e.printStackTrace();
		}

	}
	   /**
     * Checks if the specified certificate contains revocation information
     * extensions. Examines both CRL Distribution Points (OID 2.5.29.31) and
     * Authority Information Access for OCSP (OID 1.3.6.1.5.5.7.1.1).
     *
     * @param cert the X.509 certificate to check for revocation information.
     *             Cannot be null.
     * @return true if the certificate contains either CRL Distribution Points
     *         or OCSP AIA extensions, false otherwise.
     */
	private boolean hasRevocationInfo(X509Certificate cert) {
	    // CRL Distribution Points
	    byte[] crlDp = cert.getExtensionValue("2.5.29.31");
	    if (crlDp != null) {
	        return true;
	    }

	    // Authority Information Access (OCSP)
	    byte[] aia = cert.getExtensionValue("1.3.6.1.5.5.7.1.1");
	    return aia != null;
	}
	
    /**
     * Validates a single X.509 certificate against the configured trust anchors.
     * Performs PKIX path validation with optional revocation checking based on
     * certificate extensions. When revocation information is present, uses
     * soft-fail mode where unreachable revocation services don't cause validation
     * failure.
     *
     * @param cert the X.509 certificate to validate. Cannot be null.
     * @throws CertificateException 
     * @throws InvalidAlgorithmParameterException 
     * @throws CertPathValidatorException 
     * 
     */
	public void checkCertificate(X509Certificate cert) throws CertificateException, CertPathValidatorException, InvalidAlgorithmParameterException {
        if (trustAnchors.isEmpty()) throw new CertPathValidatorException("trustAnchors is empty.");
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        CertPath certPath = cf.generateCertPath(List.of(cert));

        PKIXParameters params = null;
		try {
			params = new PKIXParameters(trustAnchors);
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}

        boolean checkRevocation = hasRevocationInfo(cert);
        params.setRevocationEnabled(checkRevocation);

        if (checkRevocation) {
            CertPathValidator validator = null;
			try {
				validator = CertPathValidator.getInstance("PKIX");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}

            PKIXRevocationChecker revChecker =
                    (PKIXRevocationChecker) validator.getRevocationChecker();

            revChecker.setOptions(Set.of(
                    PKIXRevocationChecker.Option.SOFT_FAIL
            ));

            params.addCertPathChecker(revChecker);
            validator.validate(certPath, params);
        } else { // No OSCP/CRL checks
            CertPathValidator validator = null;
			try {
				validator = CertPathValidator.getInstance("PKIX");
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            validator.validate(certPath, params);
        }
	}
}
