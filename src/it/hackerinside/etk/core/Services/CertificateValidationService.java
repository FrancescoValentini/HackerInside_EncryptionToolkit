package it.hackerinside.etk.core.Services;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStoreException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Date;

import it.hackerinside.etk.GUI.ETKContext;
import it.hackerinside.etk.Utils.X509TrustChainValidator;

public class CertificateValidationService {
	private class CAValidationResult {
	    private final boolean valid;
	    private final String message;

	    public CAValidationResult(boolean valid, String message) {
	        this.valid = valid;
	        this.message = message;
	    }

	    public boolean isValid() {
	        return valid;
	    }

	    public String getMessage() {
	        return message;
	    }
	}
	private final ETKContext ctx;
	
	public CertificateValidationService(ETKContext ctx) {
		this.ctx = ctx;
	}
	
	
	/**
	 * Checks if the specified certificate is trusted by comparing it against
	 * the application's keystore, known certificates and CA truststore
	 * 
	 * @param cert the X509Certificate to check for trust
	 * @return true if the certificate is trusted
	 */
	public boolean isTrusted(X509Certificate cert) {
	    try {
	        if (ctx.getKeystore() != null && ctx.getKeystore().contains(cert) != null) return true;
	        if (ctx.getKnownCerts() != null && ctx.getKnownCerts().contains(cert) != null) return true;
	        if (ctx.useTrustStore() && ctx.getTrustStore() != null) return checkCA(cert).isValid();
	    } catch (KeyStoreException e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	
	/**
	 * Check if the certificate is issued by a valid CA
	 * @param cert the X509Certificate to check for trust
	 * @return true if the certificate is issued by a valid CA
	 */
	public CAValidationResult checkCA(X509Certificate cert) {
	    String caCheckOutput;
	    boolean valid = false;

	    try {
	        new X509TrustChainValidator(ctx.getTrustStore())
	                .checkCertificate(cert);

	        valid = true;
	        caCheckOutput = "Certificate validated by certification authority!";

	    } catch (CertificateException | CertPathValidatorException | InvalidAlgorithmParameterException e) {
	        e.printStackTrace();
	        caCheckOutput = e.getMessage();
	    }

	    return new CAValidationResult(valid, caCheckOutput);
	}
	
	/**
	 * Check whether a certificate is valid at a given reference date
	 * and at the current time.
	 *
	 * @param cert the X509Certificate to check
	 * @param referenceTime the date to check against; if null, current time is used
	 * @return Message with verification result or empty string if fully valid
	 */
	public String checkCertTimeValidity(X509Certificate cert, Date referenceTime) {
	    Date now = new Date();

	    if (referenceTime == null) {
	        referenceTime = now;
	    }

	    // Check at reference time
	    try {
	        cert.checkValidity(referenceTime);
	    } catch (CertificateExpiredException e) {
	        return "Expired at reference date";
	    } catch (CertificateNotYetValidException e) {
	        return "Not valid at reference date";
	    }

	    // Check at current time
	    try {
	        cert.checkValidity(now);
	        return ""; // valid
	    } catch (CertificateExpiredException e) {
	        return "Expired at current date (was valid at reference date)";
	    } catch (CertificateNotYetValidException e) {
	        return "Not valid at current date (was valid at reference date)";
	    }
	}
}
