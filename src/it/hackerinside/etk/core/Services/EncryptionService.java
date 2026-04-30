package it.hackerinside.etk.core.Services;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Map;

import javax.crypto.SecretKey;

import it.hackerinside.etk.GUI.ETKContext;
import it.hackerinside.etk.core.Encryption.CMSEncryptor;
import it.hackerinside.etk.core.Models.EncodingOption;
import it.hackerinside.etk.core.Models.SymmetricAlgorithms;

/**
 * Service responsible for encrypting data using CMS.
 * 
 * This class uses an {@link ETKContext} to access cryptographic configuration,
 * keystore, and buffer settings.
 */
public class EncryptionService {

    private final ETKContext ctx;
    private CMSEncryptor encryptor;

    /**
     * Constructs a new {@code EncryptionService} with the given context.
     *
     * @param ctx {@link ETKContext}
     */
    public EncryptionService(ETKContext ctx) {
        this.ctx = ctx;
    }

    /**
     * Encrypts an input file and writes the encrypted content to the specified output file.
     *
     * @param cipher the symmetric algorithm to use
     * @param encoding the output encoding (PEM or DER)
     * @param recipients collection of recipients
     * @param input the plaintext file
     * @param output the destination encrypted file
     * @param useSki whether to use Subject Key Identifier
     * @param useOaep whether to use RSA OAEP
     * @throws Exception if an error occurs during encryption
     */
    public void encrypt(
            SymmetricAlgorithms cipher,
            EncodingOption encoding,
            Collection<X509Certificate> recipients,
            File input,
            File output,
            boolean useSki,
            boolean useOaep
    ) throws Exception {

        encryptor = new CMSEncryptor(cipher, encoding, ctx.getBufferSize());

        recipients.forEach(encryptor::addRecipients);

        encryptor.setUseOnlySKI(useSki);
        encryptor.setUseOAEP(useOaep);

        encryptor.encrypt(input, output);
    }
    

    /**
     * Encrypts a file using both certificate and symmetric key recipients.
     *
     * @param cipher the symmetric encryption algorithm
     * @param encoding the output format (PEM or DER)
     * @param recipients the certificate-based recipients
     * @param symRecipients the symmetric key recipients
     * @param input the input file to encrypt
     * @param output the output file for encrypted data
     * @param useSki whether to use Subject Key Identifier
     * @param useOaep whether to use RSA OAEP
     * @throws Exception if encryption fails
     */
    public void encrypt(
            SymmetricAlgorithms cipher,
            EncodingOption encoding,
            Collection<X509Certificate> recipients,
            Map<byte[], SecretKey> symRecipients,
            File input,
            File output,
            boolean useSki,
            boolean useOaep
    ) throws Exception {

        encryptor = new CMSEncryptor(cipher, encoding, ctx.getBufferSize());

        recipients.forEach(encryptor::addRecipients);
        symRecipients.forEach((key, value) -> {
            encryptor.addRecipients(key, value);
        });
        
        encryptor.setUseOnlySKI(useSki);
        encryptor.setUseOAEP(useOaep);

        encryptor.encrypt(input, output);
    }

    /**
     * Encrypts data from an input stream to an output stream.
     *
     * @param cipher the symmetric encryption algorithm
     * @param encoding the output format (PEM or DER)
     * @param recipients the certificate-based recipients
     * @param input the input stream
     * @param output the output stream
     * @param useSki whether to use Subject Key Identifier
     * @param useOaep whether to use RSA OAEP
     * @throws Exception if encryption fails
     */
    public void encrypt(
            SymmetricAlgorithms cipher,
            EncodingOption encoding,
            Collection<X509Certificate> recipients,
            InputStream input,
            OutputStream output,
            boolean useSki,
            boolean useOaep
    ) throws Exception {

        encryptor = new CMSEncryptor(cipher, encoding, ctx.getBufferSize());

        recipients.forEach(encryptor::addRecipients);

        encryptor.setUseOnlySKI(useSki);
        encryptor.setUseOAEP(useOaep);

        encryptor.encrypt(input, output);
    }
    
    /**
     * Encrypts stream data using both certificate and symmetric key recipients.
     *
     * @param cipher the symmetric encryption algorithm
     * @param encoding the output format (PEM or DER)
     * @param recipients the certificate-based recipients
     * @param symRecipients the symmetric key recipients
     * @param input the input stream
     * @param output the output stream
     * @param useSki whether to use Subject Key Identifier
     * @param useOaep whether to use RSA OAEP
     * @throws Exception if encryption fails
     */
    public void encrypt(
            SymmetricAlgorithms cipher,
            EncodingOption encoding,
            Collection<X509Certificate> recipients,
            Map<byte[], SecretKey> symRecipients,
            InputStream input,
            OutputStream output,
            boolean useSki,
            boolean useOaep
    ) throws Exception {

        encryptor = new CMSEncryptor(cipher, encoding, ctx.getBufferSize());

        recipients.forEach(encryptor::addRecipients);
        
        symRecipients.forEach((key, value) -> {
            encryptor.addRecipients(key, value);
        });
        
        encryptor.setUseOnlySKI(useSki);
        encryptor.setUseOAEP(useOaep);

        encryptor.encrypt(input, output);
    }

    /**
     * Aborts an ongoing encryption operation, if one is in progress.
     */
    public void abort() {
        if (encryptor != null) {
            encryptor.abort();
        }
    }
}