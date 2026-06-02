package it.hackerinside.etk.core.Services;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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
     * Encrypts data from an input stream and writes the encrypted CMS output
     * to an output stream using certificate, symmetric key, and password recipients.
     *
     * @param cipher the symmetric encryption algorithm
     * @param encoding the output format (PEM or DER)
     * @param recipients the certificate-based recipients
     * @param symRecipients the symmetric key recipients
     * @param pwdRecipients the password-based recipients
     * @param input the source data stream to encrypt
     * @param output the destination stream for encrypted data
     * @param useSki whether to identify certificate recipients using Subject Key Identifier only
     * @param useOaep whether to use RSA-OAEP for key transport
     * @throws Exception if encryption fails
     */
    public void encrypt(
            SymmetricAlgorithms cipher,
            EncodingOption encoding,
            Collection<X509Certificate> recipients,
            Map<byte[], SecretKey> symRecipients,
            List<char[]> pwdRecipients,
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
        
        pwdRecipients.forEach(encryptor::addRecipients);
        
        encryptor.setUseOnlySKI(useSki);
        encryptor.setUseOAEP(useOaep);

        encryptor.encrypt(input, output);
    }
    
    /**
     * Encrypts data from an input file and writes the encrypted CMS output
     * to an output file using certificate, symmetric key, and password recipients.
     *
     * @param cipher the symmetric encryption algorithm
     * @param encoding the output format (PEM or DER)
     * @param recipients the certificate-based recipients
     * @param symRecipients the symmetric key recipients
     * @param pwdRecipients the password-based recipients
     * @param input the source file to encrypt
     * @param output the destination file for encrypted data
     * @param useSki whether to identify certificate recipients using Subject Key Identifier only
     * @param useOaep whether to use RSA-OAEP for key transport
     * @throws Exception if encryption fails
     */
    public void encrypt(
            SymmetricAlgorithms cipher,
            EncodingOption encoding,
            Collection<X509Certificate> recipients,
            Map<byte[], SecretKey> symRecipients,
            List<char[]> pwdRecipients,
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
        
        pwdRecipients.forEach(encryptor::addRecipients);
        
        encryptor.setUseOnlySKI(useSki);
        encryptor.setUseOAEP(useOaep);

        encryptor.encrypt(input, output);
    }
    
	
	/**
	 * Certificate recipients only (File).
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
	
	    encrypt(
	            cipher,
	            encoding,
	            recipients,
	            Collections.emptyMap(),
	            Collections.emptyList(),
	            input,
	            output,
	            useSki,
	            useOaep
	    );
	}
	
	/**
	 * Certificate + symmetric recipients (File).
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
	
	    encrypt(
	            cipher,
	            encoding,
	            recipients,
	            symRecipients,
	            Collections.emptyList(),
	            input,
	            output,
	            useSki,
	            useOaep
	    );
	}
	
	/**
	 * Certificate recipients only (Stream).
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
	
	    encrypt(
	            cipher,
	            encoding,
	            recipients,
	            Collections.emptyMap(),
	            Collections.emptyList(),
	            input,
	            output,
	            useSki,
	            useOaep
	    );
	}
	
	/**
	 * Certificate + symmetric recipients (Stream).
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
	
	    encrypt(
	            cipher,
	            encoding,
	            recipients,
	            symRecipients,
	            Collections.emptyList(),
	            input,
	            output,
	            useSki,
	            useOaep
	    );
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