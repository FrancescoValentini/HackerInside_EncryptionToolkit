package it.hackerinside.etk.core.CAdES;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Date;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.CMSAttributes;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.Time;
import org.bouncycastle.asn1.ess.ESSCertIDv2;
import org.bouncycastle.asn1.ess.SigningCertificateV2;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.IssuerSerial;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSSignedDataStreamGenerator;
import org.bouncycastle.cms.DefaultSignedAttributeTableGenerator;
import org.bouncycastle.cms.SignerInfoGenerator;
import org.bouncycastle.cms.SignerInfoGeneratorBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;

import it.hackerinside.etk.core.Models.AsymmetricAlgorithm;
import it.hackerinside.etk.core.Models.EncodingOption;
import it.hackerinside.etk.core.Models.HashAlgorithm;
import it.hackerinside.etk.core.PEM.PemOutputStream;

/**
 * A class for creating CAdES (CMS Advanced Electronic Signatures) compliant signatures.
 * Supports both enveloping and detached signature modes.
 * 
 * @author Francesco Valentini
 */
public class CAdESSigner {
	private PrivateKey privateKey;
	private X509Certificate signer;
	private EncodingOption encoding;
	private HashAlgorithm hashAlgorithm;
	private boolean detachedSignature;
	
    /**
     * Constructs a new CAdESSigner with the specified configuration parameters.
     *
     * @param privateKey the private key to use for signing. Must not be null.
     * @param encoding the encoding option for the output signature (PEM or DER)
     * @param hashAlgorithm the hash algorithm to use for signature generation
     * @param detachedSignature if true, creates a detached signature; if false, creates
     *                          an attached signature that includes the original data
     */
	public CAdESSigner(PrivateKey privateKey, X509Certificate signer, EncodingOption encoding, HashAlgorithm hashAlgorithm, boolean detachedSignature) {
		this.privateKey = privateKey;
		this.signer = signer;
		this.encoding = encoding;
		this.hashAlgorithm = hashAlgorithm;
		this.detachedSignature = detachedSignature;
	}
	
    /**
     * Signs the input data from the specified input stream and writes the signature
     * to the specified output stream.
     *
     * @param input the input stream containing the data to be signed
     * @param output the output stream where the signature will be written
     * @throws IllegalArgumentException if the input data is invalid or cannot be processed
     * @throws SecurityException if signing fails due to cryptographic issues
     */
    public void sign(InputStream input, OutputStream output) {
        try {
            OutputStream wrapped = wrapEncoding(output);
            CMSSignedDataStreamGenerator generator = createGenerator();

            boolean encapsulate = !detachedSignature;
            try (OutputStream sigOut = generator.open(wrapped, encapsulate)) {
                writeSignature(input, sigOut);
            }

            if (wrapped != output) {
                wrapped.close();
            }
        } catch (Exception e) {
            throw new SecurityException("Signing failed", e);
        }
    }
	
    /**
     * Signs the content of the specified input file and writes the signature to the
     * specified output file. This is a convenience method that handles file stream
     * management automatically.
     *
     * @param inputFile the file containing the data to be signed
     * @param outputFile the file where the signature will be written
     * @throws Exception if an I/O error occurs or if signing fails
     * @throws IllegalArgumentException if the input file does not exist or is not readable
     */
    public void sign(File inputFile, File outputFile) throws Exception {
        try (InputStream in = new FileInputStream(inputFile);
                OutputStream out = new FileOutputStream(outputFile)) {
        		sign(in, out);
           }
    }
    
    /**
     * Creates and configures a CMS (Cryptographic Message Syntax) signed data stream generator.
     * The generator is configured with a signer information generator and includes the signer's
     * certificate in the resulting CMS structure.
     *
     * @return a configured CMSSignedDataStreamGenerator instance ready for creating signed data streams
     */
    private CMSSignedDataStreamGenerator createGenerator() throws Exception {
        CMSSignedDataStreamGenerator generator = new CMSSignedDataStreamGenerator();
        generator.addSignerInfoGenerator(createSignerInfoGenerator());
        generator.addCertificates(new JcaCertStore(Collections.singletonList(signer)));
        return generator;
    }

    /**
     * Creates a SignerInfoGenerator configured with the appropriate cryptographic algorithms
     * and signed attributes for CMS signature generation.
     *
     * @return a configured SignerInfoGenerator instance
     */
    private SignerInfoGenerator createSignerInfoGenerator() throws Exception {
        String jcaSigAlg = getSignatureAlgorithm();
        ContentSigner contentSigner = new JcaContentSignerBuilder(jcaSigAlg).build(privateKey);

        JcaDigestCalculatorProviderBuilder dcProvBuilder = new JcaDigestCalculatorProviderBuilder();
        SignerInfoGeneratorBuilder builder = new SignerInfoGeneratorBuilder(dcProvBuilder.build());
        builder.setSignedAttributeGenerator(new DefaultSignedAttributeTableGenerator(createSignedAttributes()));
        return builder.build(contentSigner, new X509CertificateHolder(signer.getEncoded()));
    }

    /**
     * Creates the signed attributes table required for CMS signature generation.
     * The attributes include content type, signing time, and signing certificate information.
     *
     * @return an AttributeTable containing the required signed attributes for CMS signatures
     */
    private AttributeTable createSignedAttributes() throws Exception {
        ASN1EncodableVector v = new ASN1EncodableVector();

        // contentType
        v.add(new Attribute(CMSAttributes.contentType, new DERSet(CMSObjectIdentifiers.data)));

        // signingTime
        v.add(new Attribute(CMSAttributes.signingTime, new DERSet(new Time(new Date()))));

        // signingCertificateV2
        SigningCertificateV2 scv2 = getSigningCertificateV2();
        v.add(new Attribute(PKCSObjectIdentifiers.id_aa_signingCertificateV2, new DERSet(scv2)));

        return new AttributeTable(v);
    }

    /**
     * Generates a SigningCertificateV2 structure containing the hash of the signer's certificate.
     * This attribute identifies the signing certificate using the specified hash algorithm.
     *
     * @return a SigningCertificateV2 structure containing the certificate digest
     * @throws NoSuchAlgorithmException if the specified hash algorithm is not available
     * @throws CertificateEncodingException if the signer certificate cannot be encoded properly
     */
    private SigningCertificateV2 getSigningCertificateV2()
            throws NoSuchAlgorithmException, CertificateEncodingException {

        // Hash algoritmh
        AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(hashAlgorithm.getASN1());

        // signer certificate digest
        MessageDigest md = MessageDigest.getInstance(hashAlgorithm.toString());
        byte[] certDigest = md.digest(signer.getEncoded());

        // issuer + serial
        X500Name issuerName = new X500Name(signer.getIssuerX500Principal().getName());
        GeneralName generalName = new GeneralName(issuerName);
        GeneralNames generalNames = new GeneralNames(generalName);
        ASN1Integer serial = new ASN1Integer(signer.getSerialNumber());

        IssuerSerial issuerSerial = new IssuerSerial(generalNames, serial);

        // ESSCertIDv2 with hash + issuerSerial
        ESSCertIDv2 essCert = new ESSCertIDv2(algorithmIdentifier, certDigest, issuerSerial);

        // SigningCertificateV2
        return new SigningCertificateV2(new ESSCertIDv2[]{essCert});
    }


    /**
     * Writes data from an input stream to an output stream while generating a cryptographic signature.
     * This method efficiently streams data in chunks to handle large files without excessive memory usage.
     *
     * @param input the input stream containing the data to be signed
     * @param sigOut the output stream where the signed data should be written
     * @throws IOException if any I/O error occurs during reading or writing operations
     */
    private void writeSignature(InputStream input, OutputStream sigOut) throws IOException {
        byte[] buffer = new byte[8192];
        int n;
        while ((n = input.read(buffer)) != -1) {
            sigOut.write(buffer, 0, n);
        }
        sigOut.flush();
    }
    
	/**
     * Wraps the output stream with the appropriate encoding based on the encoding option.
     * If PEM encoding is selected, returns a PemOutputStream; otherwise returns the
     * original output stream for DER encoding.
     *
     * @param output the original output stream
     * @return an output stream wrapped with the appropriate encoding. For PEM encoding,
     *         returns a PemOutputStream configured for "CMS"; for DER encoding, returns
     *         the original output stream unchanged.
     */
    private OutputStream wrapEncoding(OutputStream output) {
        if (encoding == EncodingOption.ENCODING_PEM) {
            // Returns a PemOutputStream parameterized for "CMS"
            return new PemOutputStream(output, "CMS");
        } else {
            // DER: direct writing
            return output;
        }
    }
    
    /**
     * Constructs a standard Java Cryptography Architecture (JCA) signature algorithm string
     * by combining the configured hash algorithm with the asymmetric algorithm of the private key.
     * The generated string follows the pattern "{HashAlgorithm}with{AsymmetricAlgorithm}".
     *
     * <p>Examples of generated algorithm strings:
     * <ul>
     *   <li>{@code SHA256withRSA}</li>
     *   <li>{@code SHA384withECDSA}</li>
     *   <li>{@code SHA512withRSA}</li>
     * </ul>
     *
     * @return a JCA-compliant signature algorithm string in the format
     *         "{HashAlgorithm}with{AsymmetricAlgorithm}"
     * @throws IllegalArgumentException if the private key algorithm is not supported
     *                                  or cannot be determined
     */
	private String getSignatureAlgorithm() {
    	StringBuilder sb = new StringBuilder();
    	sb.append(hashAlgorithm.toString().toUpperCase());
    	sb.append("with");
    	
    	if(AsymmetricAlgorithm.fromPrivateKey(privateKey) == AsymmetricAlgorithm.EC) {
    		sb.append("ECDSA");
    	}else {
    		sb.append(AsymmetricAlgorithm.fromPrivateKey(privateKey).toString().toUpperCase());
    	}
    	return sb.toString();
    }
	
}
