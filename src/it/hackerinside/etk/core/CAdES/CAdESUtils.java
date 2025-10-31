package it.hackerinside.etk.core.CAdES;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.SignedData;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import it.hackerinside.etk.core.Models.EncodingOption;
import it.hackerinside.etk.core.PEM.PemInputStream;

/**
 * Utility class for working with CAdES (CMS Advanced Electronic Signatures) signatures.
 * @author Francesco Valentini
 */
public class CAdESUtils {
    
    /**
     * Determines whether a CAdES signature is detached or enveloping by analyzing the ASN.1 structure.
     * This method checks the signature format without loading the entire content into memory.
     * 
     * A detached signature is one where the original data is separate from the signature itself.
     * An enveloping signature contains the original data within the signature structure.
     * 
     * The determination is made by checking if the eContent field in the SignedData structure is null.
     * 
     * @param inputStream the input stream containing the signature data
     * @param encoding the encoding format of the signature (PEM or DER)
     * @return true if the signature is detached (eContent is null), false if enveloping (eContent is present)
     * @throws IOException if an I/O error occurs while reading from the input stream
     * @throws IllegalArgumentException if the input does not contain valid SignedData
     */
	public static boolean isDetached(InputStream inputStream, EncodingOption encoding) throws IOException {
	    try (InputStream decodingStream = (encoding == EncodingOption.ENCODING_PEM) 
	            ? new PemInputStream(inputStream) // If it's PEM, use PemInputStream
	            : inputStream;                     // Otherwise use inputStream directly
	         ASN1InputStream asn1In = new ASN1InputStream(decodingStream)) {

	        ASN1Sequence seq = (ASN1Sequence) asn1In.readObject();
	        ContentInfo contentInfo = ContentInfo.getInstance(seq);

	        if (!contentInfo.getContentType().equals(PKCSObjectIdentifiers.signedData)) {
	            throw new IllegalArgumentException("Input is not valid SignedData");
	        }

	        SignedData signedData = SignedData.getInstance(contentInfo.getContent());
	        
	        return signedData.getEncapContentInfo().getContent() == null;
	    }
	}


    /**
     * Determines whether a CAdES signature file is detached or enveloping.
     * This is a convenience method that works with File objects instead of InputStreams.
     * 
     * @param file the file containing the signature data
     * @param encoding the encoding format of the signature (PEM or DER)
     * @return true if the signature is detached, false if enveloping
     * @throws IOException if an I/O error occurs while reading the file
     * @throws IllegalArgumentException if the file does not contain valid SignedData
     */
    public static boolean isDetached(File file, EncodingOption encoding) throws IOException {
        try (InputStream in = new FileInputStream(file)) {
            return isDetached(in, encoding);
        }
    }
}