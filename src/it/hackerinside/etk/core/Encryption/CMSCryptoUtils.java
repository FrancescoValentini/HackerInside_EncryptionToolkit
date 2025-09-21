package it.hackerinside.etk.core.Encryption;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cms.CMSEnvelopedDataParser;
import org.bouncycastle.cms.KeyAgreeRecipientId;
import org.bouncycastle.cms.KeyTransRecipientId;
import org.bouncycastle.cms.RecipientId;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientInformationStore;

import it.hackerinside.etk.core.Models.EncodingOption;
import it.hackerinside.etk.core.Models.RecipientIdentifier;
import it.hackerinside.etk.core.PEM.PemInputStream;

/**
 * Utility helper for extracting recipient identifiers from a CMS (EnvelopedData)
 */
public class CMSCryptoUtils {

    /**
     * Parse a CMS EnvelopedData stream (DER or PEM via PemInputStream) and returns
     * a collection of RecipientIdentifier objects (one per recipientInfo).
     *
     * IMPORTANT: streaming — non-destructive for big encrypted contents.
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
     */
    public static List<RecipientIdentifier> extractRecipientIdentifiers(InputStream input,
                                                                        EncodingOption encoding) throws IOException {
        Objects.requireNonNull(input, "input must not be null");
        Objects.requireNonNull(encoding, "encoding must not be null");

        InputStream decodingStream = wrapEncoding(input, encoding);

        List<RecipientIdentifier> result = new ArrayList<>();
        CMSEnvelopedDataParser parser = null;
        try {
            parser = new CMSEnvelopedDataParser(decodingStream); // streaming parser
            RecipientInformationStore rstore = parser.getRecipientInfos();
            Collection<RecipientInformation> recipients = rstore.getRecipients();

            for (RecipientInformation ri : recipients) {
                RecipientId rid = ri.getRID();

                if (rid instanceof KeyTransRecipientId) {
                    KeyTransRecipientId ktr = (KeyTransRecipientId) rid;
                    byte[] subjKeyId = ktr.getSubjectKeyIdentifier();
                    if (subjKeyId != null) {
                        result.add(new RecipientIdentifier(subjKeyId));
                    } else {
                        X500Name issuer = ktr.getIssuer();
                        BigInteger serial = ktr.getSerialNumber();
                        result.add(new RecipientIdentifier(issuer.getEncoded(), serial));
                    }
                } else if (rid instanceof KeyAgreeRecipientId) {
                    KeyAgreeRecipientId kar = (KeyAgreeRecipientId) rid;
                    byte[] subjKeyId = kar.getSubjectKeyIdentifier();
                    if (subjKeyId != null) {
                        result.add(new RecipientIdentifier(subjKeyId));
                    } else {
                        X500Name issuer = kar.getIssuer();
                        BigInteger serial = kar.getSerialNumber();
                        result.add(new RecipientIdentifier(issuer.getEncoded(), serial));
                    }
                } else {
                    // fallback: issuer+serial 
                    try {
                        X500Name issuer = (X500Name) rid.getClass()
                                .getMethod("getIssuer").invoke(rid);
                        BigInteger serial = (BigInteger) rid.getClass()
                                .getMethod("getSerialNumber").invoke(rid);
                        if (issuer != null && serial != null) {
                            result.add(new RecipientIdentifier(issuer.getEncoded(), serial));
                        }
                    } catch (Exception ex) {
                        // invalid RecipientId
                    }
                }
            }
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException("Failed to parse CMS recipients: " + e.getMessage(), e);
        } finally {
            if (parser != null) {
                try { parser.close(); } catch (Exception ignore) {}
            }
        }

        return result;
    }

    /**
     * Convenience wrapper: parse a CMS EnvelopedData file.
     *
     * @param file     the CMS file (DER or PEM)
     * @param encoding encoding of the file
     * @return list of recipient identifiers
     * @throws IOException on I/O problems
     */
    public static List<RecipientIdentifier> extractRecipientIdentifiers(File file,
                                                                        EncodingOption encoding) throws IOException {
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
