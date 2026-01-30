package it.hackerinside.etk.core.Models;

import java.security.cert.X509Certificate;
import java.util.Date;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1UTCTime;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.CMSAttributes;
import org.bouncycastle.cms.SignerInformation;

/**
 * Represents the result of a digital signature verification process.
 *
 * @param validSignature           A boolean indicating whether the digital signature is valid or not.
 * @param hasSigningCertificateV2  A boolean indicating whether the signing certificate used is of version 2.
 * @param hasSigningTime           A boolean indicating whether a signing time is present in the signature.
 * @param signer                   The X509Certificate representing the signer's certificate.
 * @param signerInfo               CMS SignerInfo block
 * @param digestAlgorithm          The digital signature digest algorithm (OID)
 * @param contentDigest            The content digest
 * @author Francesco Valentini
 */
public record VerificationResult(
    boolean validSignature,
    boolean hasSigningCertificateV2,
    boolean hasSigningTime,
    X509Certificate signer,
    SignerInformation signerInfo,
    String digestAlgorithm,
    byte[] contentDigest
) {
	
	/**
	 * This method extracts the timestamp from the CMS signingTime attribute and converts it
	 * to a standard Java Date object.
	 * 
	 */
	public Date getSigningTime() {
	    if (!hasSigningTime) {
	        return null;
	    }
	    
	    try {
	        AttributeTable attrs = signerInfo.getSignedAttributes();
	        Attribute signingTimeAttr = attrs.get(CMSAttributes.signingTime);
	        
	        if (signingTimeAttr == null) {
	            return null;
	        }
	        
	        ASN1Set attrValues = signingTimeAttr.getAttrValues();
	        if (attrValues.size() == 0) {
	            return null;
	        }
	        
	        // Get the ASN.1 Time object and convert to Java Date
	        ASN1Primitive timePrimitive = attrValues.getObjectAt(0).toASN1Primitive();
	        ASN1UTCTime asn1Time = ASN1UTCTime.getInstance(timePrimitive);
	        
	        return asn1Time.getDate();
	        
	    } catch (Exception e) {
	        throw new IllegalStateException("Failed to parse signing time attribute", e);
	    }
	}

	@Override
	public String toString() {
		return "validSignature=" + validSignature + "\nhasSigningCertificateV2="
				+ hasSigningCertificateV2 + "\nhasSigningTime=" + hasSigningTime + "\nsigner=" + signer
				+ "\nsignerInfo=" + signerInfo + "";
	}	
}
