package it.hackerinside.etk.core.CAdES;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

import org.bouncycastle.cms.SignerInformation;
import it.hackerinside.etk.core.Models.EncodingOption;
import it.hackerinside.etk.core.Models.VerificationResult;

import java.io.*;
import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.CMSAttributes;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cms.*;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

/**
 * A verifier for CAdES (CMS Advanced Electronic Signatures) signatures.
 * This class provides functionality to verify both enveloping and detached CAdES signatures,
 * as well as extract content from attached signatures.
 * 
 * @see VerificationResult
 * @author Francesco Valentini
 */
public class CAdESVerifier {
    private final EncodingOption encoding;
    private final boolean detachedSignature;
    private int bufferSize;

    /**
     * Constructs a new CAdESVerifier with the specified encoding option and signature type.
     * 
     * @param encoding the encoding format of the signature (DER or PEM)
     * @param detachedSignature true if the signature is detached (separate from data),
     *                          false if attached (data encapsulated within signature)
     * @param bufferSize the buffer size
     */
    public CAdESVerifier(EncodingOption encoding, boolean detachedSignature, int bufferSize) {
        this.encoding = encoding;
        this.detachedSignature = detachedSignature;
        this.bufferSize = bufferSize;
    }


    /**
     * Verifies an attached CAdES signature where the signed content is encapsulated
     * within the signature structure.
     * 
     * @param signature the input stream containing the CAdES signature with encapsulated content
     * @return VerificationResult containing the verification status and signature details
     * @throws SecurityException if verification fails or the signature is malformed
     * @throws IllegalStateException if the verifier was configured for detached signatures
     */
    public VerificationResult verify(InputStream signature) {
        return verifyInternal(signature, null, "Verification failed");
    }

    /**
     * Verifies an attached CAdES signature from a file where the signed content is
     * encapsulated within the signature structure.
     * 
     * @param signature the file containing the CAdES signature with encapsulated content
     * @return VerificationResult containing the verification status and signature details
     * @throws SecurityException if verification fails, the signature is malformed, or IO errors occur
     * @throws IllegalStateException if the verifier was configured for detached signatures
     */
    public VerificationResult verify(File signature) {
        return verifyFromFiles(signature, null, "Verification failed - IO");
    }

    /**
     * Verifies a detached CAdES signature where the signed content is provided separately
     * from the signature.
     * 
     * @param signature the input stream containing the CAdES signature (without content)
     * @param data the input stream containing the original data that was signed
     * @return VerificationResult containing the verification status and signature details
     * @throws SecurityException if verification fails or the signature is malformed
     * @throws IllegalStateException if the verifier was configured for attached signatures
     */
    public VerificationResult verifyDetached(InputStream signature, InputStream data) {
        return verifyInternal(signature, data, "Detached verification failed");
    }

    /**
     * Verifies a detached CAdES signature from files where the signed content is
     * provided separately from the signature.
     * 
     * @param signature the file containing the CAdES signature (without content)
     * @param data the file containing the original data that was signed
     * @return VerificationResult containing the verification status and signature details
     * @throws SecurityException if verification fails, the signature is malformed, or IO errors occur
     * @throws IllegalStateException if the verifier was configured for attached signatures
     */
    public VerificationResult verifyDetached(File signature, File data) {
        return verifyFromFiles(signature, data, "Detached verification failed - IO");
    }


    /**
     * Extracts the original content from an enveloping CAdES signature.
     * 
     * @param signature the input stream containing the CAdES signature with encapsulated content
     * @param data the output stream where the extracted content will be written
     * @throws IllegalStateException if the verifier was configured for detached signatures
     * @throws SecurityException if content extraction fails or no content is found
     */
    public void extractContent(InputStream signature, OutputStream data) {
        if (detachedSignature)
            throw new IllegalStateException("Cannot extract content from a detached signature");

        try {
            CMSTypedStream signedContent = buildParser(signature, null).getSignedContent();
            if (signedContent == null) throw new IllegalStateException("No encapsulated content");
            try (InputStream in = signedContent.getContentStream()) {
                copy(in, data);
            }
        } catch (Exception e) {
            throw new SecurityException("Failed to extract content", e);
        }
    }

    /**
     * Extracts the original content from an enveloping CAdES signature file and writes it to the specified output file.
     * 
     * @param signature the file containing the CAdES signature with encapsulated content
     * @param outputFile the file where the extracted content will be written
     * @throws IllegalStateException if the verifier was configured for detached signatures
     * @throws SecurityException if content extraction fails, no content is found, or IO errors occur
     */
    public void extractContent(File signature, File outputFile) {
        try (OutputStream out = new FileOutputStream(outputFile);
             InputStream in = new FileInputStream(signature)) {
            extractContent(in, out);
        } catch (IOException e) {
            throw new SecurityException("Failed to extract content - IO", e);
        }
    }

    /**
     * Internal method to verify a signature from input streams.
     * 
     * @param sig the input stream containing the signature
     * @param data the input stream containing the data (null for enveloping signatures)
     * @param errMsg the error message to use if verification fails
     * @return VerificationResult containing the verification results
     * @throws SecurityException if verification fails
     */
    private VerificationResult verifyInternal(InputStream sig, InputStream data, String errMsg) {
        try {
            return verifyParser(buildParser(sig, data));
        } catch (Exception e) {
            throw new SecurityException(errMsg, e);
        }
    }

    /**
     * Internal method to verify a signature from files.
     * 
     * @param sig the file containing the signature
     * @param data the file containing the data (null for enveloping signatures)
     * @param errMsg the error message to use if verification fails
     * @return VerificationResult containing the verification results
     * @throws SecurityException if verification fails or IO errors occur
     */
    private VerificationResult verifyFromFiles(File sig, File data, String errMsg) {
        try (InputStream sigIn = new FileInputStream(sig);
             InputStream dataIn = (data != null ? new FileInputStream(data) : null)) {
            return verifyInternal(sigIn, dataIn, errMsg);
        } catch (IOException e) {
            throw new SecurityException(errMsg, e);
        }
    }

    /**
     * Builds a CMS signed data parser from the provided input streams.
     * 
     * @param sigIn the input stream containing the signature
     * @param detachedData the input stream containing detached data (null for attached signatures)
     * @return CMSSignedDataParser configured for signature verification
     * @throws Exception if parser construction fails
     */
    private CMSSignedDataParser buildParser(InputStream sigIn, InputStream detachedData) throws Exception {
        InputStream effectiveSig = decodeIfPem(sigIn);
        DigestCalculatorProvider digestProvider = new JcaDigestCalculatorProviderBuilder().build();
        return (detachedData != null)
                ? new CMSSignedDataParser(digestProvider, new CMSTypedStream(detachedData), effectiveSig)
                : new CMSSignedDataParser(digestProvider, effectiveSig);
    }

    /**
     * Verifies the signature using the provided CMS parser.
     * 
     * @param parser the CMSSignedDataParser containing the signature to verify
     * @return VerificationResult containing the verification status and signature attributes
     * @throws Exception if verification fails
     */
    private VerificationResult verifyParser(CMSSignedDataParser parser) throws Exception {
        if (parser.getSignedContent() != null) parser.getSignedContent().drain();

        SignerInformation signerInfo = parser.getSignerInfos().getSigners().iterator().next();
        X509Certificate cert = extractCertificate(parser, signerInfo);

        boolean valid = signerInfo.verify(new JcaSimpleSignerInfoVerifierBuilder()
                .setProvider(BouncyCastleProvider.PROVIDER_NAME).build(cert));

        AttributeTable attrs = signerInfo.getSignedAttributes();
        return new VerificationResult(
                valid,
                hasAttribute(attrs, PKCSObjectIdentifiers.id_aa_signingCertificateV2),
                hasAttribute(attrs, CMSAttributes.signingTime),
                cert,
                signerInfo
        );
    }

    /**
     * Extracts the signing certificate from the CMS parser based on the signer information.
     * 
     * @param parser the CMSSignedDataParser containing the certificates
     * @param signerInfo the SignerInformation identifying which certificate to extract
     * @return X509Certificate the signing certificate
     * @throws Exception if certificate extraction fails
     */
    private X509Certificate extractCertificate(CMSSignedDataParser parser, SignerInformation signerInfo) throws Exception {
        Collection<X509CertificateHolder> matches = (Collection<X509CertificateHolder>) parser.getCertificates().getMatches(signerInfo.getSID());
        return new JcaX509CertificateConverter()
                .setProvider(BouncyCastleProvider.PROVIDER_NAME)
                .getCertificate(matches.iterator().next());
    }

    /**
     * Checks if a specific attribute exists in the attribute table.
     * 
     * @param attrs the AttributeTable to search
     * @param oid the ASN.1 object identifier of the attribute to find
     * @return true if the attribute exists, false otherwise
     */
    private boolean hasAttribute(AttributeTable attrs, ASN1ObjectIdentifier oid) {
        return attrs != null && attrs.get(oid) != null;
    }

    /**
     * Decodes PEM-encoded input to DER format if PEM encoding is specified.
     * 
     * @param in the input stream to decode
     * @return InputStream containing DER-encoded data
     * @throws IOException if PEM decoding fails
     */
    private InputStream decodeIfPem(InputStream in) throws IOException {
        if (encoding != EncodingOption.ENCODING_PEM) return in;
        try (PemReader pr = new PemReader(new InputStreamReader(in))) {
            PemObject obj = pr.readPemObject();
            if (obj == null) throw new IOException("Invalid PEM signature");
            return new ByteArrayInputStream(obj.getContent());
        }
    }

    /**
     * Copies data from an input stream to an output stream.
     * 
     * @param in the input stream to read from
     * @param out the output stream to write to
     * @throws IOException if copying fails
     */
    private void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[bufferSize];
        int n;
        while ((n = in.read(buf)) >= 0) {
            out.write(buf, 0, n);
        }
    }
}

