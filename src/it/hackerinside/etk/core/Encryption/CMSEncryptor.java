package it.hackerinside.etk.core.Encryption;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;

import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.CMSEnvelopedDataStreamGenerator;
import org.bouncycastle.cms.RecipientInfoGenerator;
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder;
import org.bouncycastle.cms.jcajce.JceKEMRecipientInfoGenerator;
import org.bouncycastle.cms.jcajce.JceKeyAgreeRecipientInfoGenerator;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientInfoGenerator;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.operator.jcajce.JcaAlgorithmParametersConverter;
import it.hackerinside.etk.core.Models.EncodingOption;
import it.hackerinside.etk.core.Models.PQCAlgorithms;
import it.hackerinside.etk.core.Models.SymmetricAlgorithms;
import it.hackerinside.etk.core.PEM.PemOutputStream;

/**
 * A CMS (Cryptographic Message Syntax) encryptor for encrypting data using
 * various symmetric algorithms and encoding options. Supports both RSA key
 * transport and ECC key agreement schemes.
 * 
 * @author Francesco Valentini
 */
public class CMSEncryptor implements Encryptor {
    private SymmetricAlgorithms encryptionAlgorithm;
    private EncodingOption encoding;
    private int bufferSize;
    private ArrayList<X509Certificate> recipients;
    private boolean useOnlySKI = false;
    private volatile boolean aborted = false;
    private boolean useOAEP = true;
    
    /**
     * Constructs a new CMSEncryptor with the specified parameters.
     *
     * @param encryptionAlgorithm the symmetric encryption algorithm to use
     * @param encoding the encoding option for the output (DER or PEM)
     * @param bufferSize the buffer size
     */
    public CMSEncryptor(SymmetricAlgorithms encryptionAlgorithm, EncodingOption encoding, int bufferSize) {
        this.encryptionAlgorithm = encryptionAlgorithm;
        this.encoding = encoding;
        this.bufferSize = bufferSize;
        this.recipients = new ArrayList<>();
    }
    
    /**
     * Adds one or more recipient certificates to the list of recipients for the encryption process.
     * This method allows for adding multiple recipients at once using varargs.
     *
     * @param recipient one or more X.509 certificates representing the recipients
     *                  to be included in the encryption process.
     *                  The certificates will be used to encrypt the data for each recipient.
     */
    public void addRecipients(X509Certificate... recipient) {
        recipients.addAll(Arrays.asList(recipient));
    }
    
    /**
     * If TRUE, forces the RecipientInfoGenerator to include ONLY the recipient's SKI.
     * @param value
     */
    public void setUseOnlySKI(boolean value) {
    	this.useOnlySKI = value;
    }
    
    /**
     * Aborts the encryption
     */
    public void abort() {
    	this.aborted = true;
    }
    
    /**
     * Enables or disables the use of OAEP padding.
     *
     * @param value {@code true} to use OAEP, {@code false} otherwise
     */
    public void setUseOAEP(boolean value) {
    	this.useOAEP = value;
    }
    
    /**
     * Encrypts data from an InputStream to an OutputStream using CMS encryption.
     * The method automatically handles the appropriate recipient info generation
     * based on the recipient certificate's public key algorithm.
     *
     * @param input the input stream containing the data to encrypt
     * @param output the output stream where the encrypted data will be written
     * @throws Exception if encryption fails due to cryptographic errors, I/O errors,
     *                   or unsupported algorithms
     */
    public void encrypt(InputStream input, OutputStream output) throws Exception  {
        CMSEnvelopedDataStreamGenerator generator = getGenerator();
        
        // Configure encryptor
        OutputEncryptor encryptor = new JceCMSContentEncryptorBuilder(encryptionAlgorithm.getCipherASN1())
                .setProvider("BC")
                .setSecureRandom(new SecureRandom())
                .build();
        
        // Encrypt
        try (OutputStream encodedOut = wrapEncoding(output);
             OutputStream cmsOut = generator.open(encodedOut, encryptor)) {

            byte[] buffer = new byte[bufferSize];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                if (aborted || Thread.currentThread().isInterrupted()) {
                    throw new InterruptedIOException("Encryption aborted");
                }
                cmsOut.write(buffer, 0, bytesRead);
            }
        }
    }
    
    /**
     * Encrypts a file using CMS encryption and writes the result to another file.
     *
     * @param inputFile the file containing the data to encrypt
     * @param outputFile the file where the encrypted data will be written
     * @throws Exception if encryption fails due to cryptographic errors, I/O errors,
     *                   or unsupported algorithms
     */
    public void encrypt(File inputFile, File outputFile) throws Exception {
        try (InputStream in = new FileInputStream(inputFile);
                OutputStream out = new FileOutputStream(outputFile)) {
               encrypt(in, out);
           }
    }
    
    /**
     * Wraps the output stream with the appropriate encoding based on the encoding option.
     * If PEM encoding is selected, returns a PemOutputStream; otherwise returns the
     * original output stream for DER encoding.
     *
     * @param output the original output stream
     * @return an output stream wrapped with the appropriate encoding
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
     * Creates a {@link CMSEnvelopedDataStreamGenerator} for generating the CMS (Cryptographic Message Syntax)
     * enveloped data stream. This method configures the generator with the recipient information
     * (including the recipient's certificate) to ensure the data is encrypted for the correct recipients.
     * It iterates over the list of recipients and adds the appropriate recipient information for each.
     *
     * @return a configured {@link CMSEnvelopedDataStreamGenerator} that will be used
     *         to encrypt data for the recipients.
     * @throws Exception if an error occurs while generating recipient information
     *                   or if the recipient's certificate cannot be processed.
     *                   This could happen if the recipient's public key algorithm is unsupported.
     */
    private CMSEnvelopedDataStreamGenerator getGenerator() throws Exception {
        CMSEnvelopedDataStreamGenerator generator = new CMSEnvelopedDataStreamGenerator();
        // Add recipients
        for(X509Certificate recipient : recipients) {
        	generator.addRecipientInfoGenerator(createRecipientInfoGenerator(recipient));
        }
        return generator;
    }

    /**
     * Creates an appropriate RecipientInfoGenerator based on the recipient certificate's
     * public key algorithm. Supports RSA key transport, ECC key agreement schemes and PQC
     *
     * @param recipientCert the recipient's X.509 certificate
     * @return a RecipientInfoGenerator configured for the recipient's public key algorithm
     * @throws Exception if the public key algorithm is not supported or if key generation fails
     */
    private RecipientInfoGenerator createRecipientInfoGenerator(X509Certificate recipientCert) throws Exception {
        String algorithm = recipientCert.getPublicKey().getAlgorithm().toUpperCase();

        if (isRSA(algorithm)) return buildRSARecipientInfo(recipientCert);
        if (isEC(algorithm)) return buildECRecipientInfo(recipientCert);
        if (isSupportedPQC(algorithm)) return buildPQCRecipientInfo(recipientCert);

        throw new IllegalArgumentException(
                "Unsupported public key algorithm: " + algorithm
        );
    }

    
    
    private boolean isRSA(String algorithm) {
        return "RSA".equals(algorithm);
    }

    private boolean isEC(String algorithm) {
        return "EC".equals(algorithm) || "ECDH".equals(algorithm);
    }

    private boolean isSupportedPQC(String algorithm) {
        if (!PQCAlgorithms.isPQC(algorithm)) {
            return false;
        }

        if (!algorithm.contains("ML-KEM")) {
            throw new IllegalArgumentException(
                    "PQC algorithm not supported for encryption: " + algorithm
            );
        }

        return true;
    }

    /**
     * Creates an ML-KEM RecipientInfoGenerator
     * @param recipientCert the recipient's X.509 certificate
     * @return a RecipientInfoGenerator configured for the recipient's public key 
     */    
    private RecipientInfoGenerator buildPQCRecipientInfo(X509Certificate recipientCert) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, CertificateEncodingException, NoSuchProviderException, InvalidKeySpecException {
    	ASN1ObjectIdentifier keyWrapAlg = encryptionAlgorithm.suggestedKeyWrap(); 
    	byte[] encoded = recipientCert.getPublicKey().getEncoded();

    	// Force loading as ML-KEM instead of Kyber
    	KeyFactory kf = KeyFactory.getInstance("ML-KEM", "BC");
    	PublicKey mlkemPub = kf.generatePublic(new X509EncodedKeySpec(encoded));

    	return new JceKEMRecipientInfoGenerator(
    			getSKI(recipientCert),
    			mlkemPub,
    	        keyWrapAlg
    	).setProvider("BC");
    }
    
    /**
     * Creates an ECC RecipientInfoGenerator
     * @param recipientCert the recipient's X.509 certificate
     * @return a RecipientInfoGenerator configured for the recipient's public key 
     */
    private RecipientInfoGenerator buildECRecipientInfo(X509Certificate recipientCert) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, CertificateEncodingException, NoSuchProviderException {
        // Elliptic Curve Diffie-Hellman (ECDH)
        KeyPair eph = getEphemeralECCKeys(recipientCert);
        
        JceKeyAgreeRecipientInfoGenerator recipientInfoGenerator = new JceKeyAgreeRecipientInfoGenerator(
                CMSAlgorithm.ECDH_SHA256KDF,
                eph.getPrivate(),
                eph.getPublic(),
                encryptionAlgorithm.suggestedKeyWrap()
        );
        if(this.useOnlySKI) { // SKI only
        	recipientInfoGenerator.addRecipient(getSKI(recipientCert),recipientCert.getPublicKey());
        }else { // Issuer + Recipient SN
        	recipientInfoGenerator.addRecipient(recipientCert);
        }
        return recipientInfoGenerator;
    }

    
    /**
     * Creates an RSA RecipientInfoGenerator
     * @param recipientCert the recipient's X.509 certificate
     * @return a RecipientInfoGenerator configured for the recipient's public key 
     */
    private RecipientInfoGenerator buildRSARecipientInfo(X509Certificate recipientCert) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, CertificateEncodingException {
    	if(useOAEP) {
        	JcaAlgorithmParametersConverter paramsConverter = new JcaAlgorithmParametersConverter();
        	OAEPParameterSpec oaepParams = new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT);
        	AlgorithmIdentifier algorithmIdentifier = paramsConverter.getAlgorithmIdentifier(PKCSObjectIdentifiers.id_RSAES_OAEP, oaepParams);
        	
        	if(this.useOnlySKI) { // SKI only
        		return new JceKeyTransRecipientInfoGenerator(
        				getSKI(recipientCert),
        				algorithmIdentifier,
        				recipientCert.getPublicKey()
        		);
        	}
        	
        	// Issuer + Recipient SN
            return new JceKeyTransRecipientInfoGenerator(recipientCert,algorithmIdentifier);
    	}else {
        	if(this.useOnlySKI) { // SKI only
        		return new JceKeyTransRecipientInfoGenerator(
        				getSKI(recipientCert),
        				recipientCert.getPublicKey()
        		);
        	}
        	
        	// Issuer + Recipient SN
            return new JceKeyTransRecipientInfoGenerator(recipientCert);
    	}
    }

    /**
     * Return a RFC 3280 type 1 key identifier
     * @param recipientCert
     * @return Return a RFC 3280 type 1 key identifier
     * @throws NoSuchAlgorithmException
     */
    private byte[] getSKI(X509Certificate recipientCert) throws NoSuchAlgorithmException {
    	return new JcaX509ExtensionUtils()
    			.createSubjectKeyIdentifier(recipientCert.getPublicKey())
    			.getKeyIdentifier();
    }

    /**
     * Generates ephemeral ECC key pairs for ECDH key agreement.
     * The key parameters are derived from the recipient's public key to ensure compatibility.
     *
     * @return a KeyPair containing ephemeral ECC keys
     * @throws NoSuchAlgorithmException if the ECC algorithm is not available
     * @throws NoSuchProviderException if the BC provider is not available
     * @throws InvalidAlgorithmParameterException if the key parameters are invalid
     */
    private KeyPair getEphemeralECCKeys(X509Certificate recipient) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC", "BC");
        kpg.initialize(((ECPublicKey)recipient.getPublicKey()).getParams());
        return kpg.generateKeyPair();
    }
}