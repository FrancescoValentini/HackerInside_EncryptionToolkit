package it.hackerinside.etk.core.Encryption;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cms.CMSAuthEnvelopedDataParser;
import org.bouncycastle.cms.CMSEnvelopedDataParser;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.KEKRecipientId;
import org.bouncycastle.cms.KEMRecipientId;
import org.bouncycastle.cms.KeyAgreeRecipientId;
import org.bouncycastle.cms.KeyTransRecipientId;
import org.bouncycastle.cms.PasswordRecipientId;
import org.bouncycastle.cms.RecipientId;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientInformationStore;

import it.hackerinside.etk.core.Models.EncodingOption;
import it.hackerinside.etk.core.Models.RecipientIdentifier;
import it.hackerinside.etk.core.PEM.PemInputStream;



/**
 * Utility helper for extracting recipient identifiers from a CMS (EnvelopedData and AuthEnvelopedData)
 */
public class CMSCryptoUtils {
	/**
	 * Parse a CMS EnvelopedData stream (DER or PEM via PemInputStream) and returns
	 * a collection of RecipientIdentifier objects (one per recipientInfo).
	 *
	 * IMPORTANT: streaming - non-destructive for big encrypted contents.
	 *
	 * @param input CMS data stream (may be PEM or DER depending on encodingStream wrapper)
	 * @param isPem true if the provided stream is PEM-wrapped (so caller should pass a PemInputStream)
	 * @return list of recipient identifiers (may be empty if none found)
	 * @throws IOException on I/O problems
	 */
	/**
	 * Parse a CMS EnvelopedData stream and returns recipient identifiers.
	 *
	 * @param input    CMS data stream
	 * @param encoding Encoding option (DER or PEM)
	 * @return list of recipient identifiers (one per recipientInfo)
	 * @throws IOException on I/O problems or parsing errors
	 * @throws CMSException 
	 */
	public static List<RecipientIdentifier> extractRecipientIdentifiers(
	        InputStream input,
	        EncodingOption encoding) throws IOException, CMSException {

	    Objects.requireNonNull(input, "input must not be null");
	    Objects.requireNonNull(encoding, "encoding must not be null");

	    InputStream decoded = wrapEncoding(input, encoding);
	    return resolveRecipientIdentifiers(decoded);
	}
	
	/**
	 * Resolves recipient identifiers from a CMS message by first attempting to parse
	 * it as an EnvelopedData structure and, if that fails, as an AuthEnvelopedData structure.
	 *
	 * @param input the CMS message input stream
	 * @return the list of recipient identifiers found in the message
	 * @throws IOException if an I/O error occurs while reading the stream
	 * @throws CMSException if neither CMS format can be successfully parsed
	 */
	private static List<RecipientIdentifier> resolveRecipientIdentifiers(InputStream input)
	        throws IOException, CMSException {

	    try {
	        return parseEnveloped(input);
	    } catch (CMSException envFail) {
	        return parseAuthEnveloped(input);
	    }
	}
	
	/**
	 * Parses a CMS EnvelopedData message and extracts its recipient identifiers.
	 *
	 * @param input the CMS message input stream
	 * @return the list of recipient identifiers
	 * @throws IOException if an I/O error occurs while reading the stream
	 * @throws CMSException if the message cannot be parsed as EnvelopedData
	 */
	private static List<RecipientIdentifier> parseEnveloped(InputStream input)
	        throws IOException, CMSException {

	    CMSEnvelopedDataParser parser = new CMSEnvelopedDataParser(input);

	    try {
	        return collectRecipientIdentifiers(parser.getRecipientInfos());
	    } finally {
	        parser.close();
	    }
	}
	
	
	/**
	 * Parses a CMS AuthEnvelopedData message and extracts its recipient identifiers.
	 *
	 * @param input the CMS message input stream
	 * @return the list of recipient identifiers
	 * @throws IOException if an I/O error occurs while reading the stream
	 * @throws CMSException if the message cannot be parsed as AuthEnvelopedData
	 */
	private static List<RecipientIdentifier> parseAuthEnveloped(InputStream input)
	        throws IOException, CMSException {

	    CMSAuthEnvelopedDataParser parser = new CMSAuthEnvelopedDataParser(input);

	    try {
	        return collectRecipientIdentifiers(parser.getRecipientInfos());
	    } finally {
	        parser.close();
	    }
	}

	/**
	 * Iterates a RecipientInformationStore and maps each entry
	 * to a RecipientIdentifier (SKI, KEK key ID, or issuer/serial).
	 *
	 * @param rstore recipient info store from a parsed CMS object
	 * @return list of recipient identifiers; empty if none could be mapped
	 */
	private static List<RecipientIdentifier> collectRecipientIdentifiers(
	        RecipientInformationStore rstore) {

	    List<RecipientIdentifier> result = new ArrayList<>();

	    for (RecipientInformation ri : rstore.getRecipients()) {
	        RecipientIdentifier id = toRecipientIdentifier(ri.getRID());
	        if (id != null) {
	            result.add(id);
	        }
	    }

	    return result;
	}

	/**
	 * Converts a single RecipientId to a RecipientIdentifier,
	 * preferring key identifier over issuer/serial.
	 *
	 * @param rid the recipient ID from a RecipientInformation entry
	 * @return mapped identifier, or null if neither form is present
	 */
	private static RecipientIdentifier toRecipientIdentifier(RecipientId rid) {
		
		if(rid instanceof  PasswordRecipientId) return RecipientIdentifier.passwordRecipient(); 

	    byte[] keyId = getSubjectKeyIdentifier(rid);

	    if (keyId != null) {
	    	return switch (rid) {
		        case KEKRecipientId kek -> RecipientIdentifier.fromKekKeyId(keyId);
		        default -> RecipientIdentifier.fromSki(keyId);
	    	};
	    }

	    X500Name issuer = getIssuer(rid);
	    BigInteger serial = getSerialNumber(rid);

	    if (issuer != null && serial != null) {
	        try {
	            return RecipientIdentifier.fromIssuerSerial(issuer.getEncoded(), serial);
	        } catch (IOException e) {
	            return null;
	        }
	    }

	    return null;
	}

	//Extracts the SubjectKeyIdentifier from a RecipientId
	private static byte[] getSubjectKeyIdentifier(RecipientId rid) {
	    if (rid instanceof KeyTransRecipientId) {
	        return ((KeyTransRecipientId) rid).getSubjectKeyIdentifier();
	    } else if (rid instanceof KeyAgreeRecipientId) {
	        return ((KeyAgreeRecipientId) rid).getSubjectKeyIdentifier();
	    } else if (rid instanceof KEMRecipientId) {
	        return ((KEMRecipientId) rid).getSubjectKeyIdentifier();
	    } else if (rid instanceof KEKRecipientId) {
	        return ((KEKRecipientId) rid).getKeyIdentifier();
	    }
	    return null;
	}

	//Extracts the Issuer from a RecipientId
	private static X500Name getIssuer(RecipientId rid) {
		if (rid instanceof KeyTransRecipientId) {
			return ((KeyTransRecipientId) rid).getIssuer();
		} else if (rid instanceof KeyAgreeRecipientId) {
			return ((KeyAgreeRecipientId) rid).getIssuer();
		} else if (rid instanceof KEMRecipientId) {
			return ((KEMRecipientId) rid).getIssuer();
		}
		try {
			return (X500Name) rid.getClass().getMethod("getIssuer").invoke(rid);
		} catch (Exception ex) {
			return null;
		}
	}

	//Extracts the SerialNumber from a RecipientId
	private static BigInteger getSerialNumber(RecipientId rid) {
		if (rid instanceof KeyTransRecipientId) {
			return ((KeyTransRecipientId) rid).getSerialNumber();
		} else if (rid instanceof KeyAgreeRecipientId) {
			return ((KeyAgreeRecipientId) rid).getSerialNumber();
		} else if (rid instanceof KEMRecipientId) {
			return ((KEMRecipientId) rid).getSerialNumber();
		}
		try {
			return (BigInteger) rid.getClass().getMethod("getSerialNumber").invoke(rid);
		} catch (Exception ex) {
			return null;
		}
	}


	/**
	 * Convenience wrapper: parse a CMS EnvelopedData file.
	 *
	 * @param file     the CMS file (DER or PEM)
	 * @param encoding encoding of the file
	 * @return list of recipient identifiers
	 * @throws IOException on I/O problems
	 * @throws CMSException 
	 */
	public static List<RecipientIdentifier> extractRecipientIdentifiers(File file,
			EncodingOption encoding) throws IOException, CMSException {
		try (InputStream in = new FileInputStream(file)) {
			return extractRecipientIdentifiers(in, encoding);
		}
	}

	/**
	 * Wraps the input stream with a PemInputStream if encoding is PEM.
	 * @throws IOException 
	 */
	private static InputStream wrapEncoding(InputStream input, EncodingOption encoding) throws IOException {
		if (encoding == EncodingOption.ENCODING_PEM) {
			return new PemInputStream(input);
		} else {
			return input; // DER: no wrapping
		}
	}
}
