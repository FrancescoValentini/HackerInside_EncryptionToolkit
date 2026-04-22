package it.hackerinside.etk.core.Services;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.PrivateKey;
import java.util.Collection;
import java.util.Optional;

import it.hackerinside.etk.GUI.ETKContext;
import it.hackerinside.etk.core.Encryption.CMSCryptoUtils;
import it.hackerinside.etk.core.Encryption.CMSDecryptor;
import it.hackerinside.etk.core.Models.EncodingOption;
import it.hackerinside.etk.core.Models.RecipientIdentifier;
import it.hackerinside.etk.core.PEM.PEMUtils;

/**
 * Service responsible for identifying recipients and decrypting CMS-encrypted data.
 * 
 * This class uses an {@link ETKContext} to access cryptographic configuration,
 * keystore, and buffer settings. 
 */

public class DecryptionService {

    private final ETKContext ctx;
    private CMSDecryptor decryptor;

    /**
     * Constructs a new {@code DecryptionService} with the given context.
     *
     * @param ctx {@link ETKContext}
     */
    public DecryptionService(ETKContext ctx) {
        this.ctx = ctx;
    }
 
    
    /**
     * Identifies the recipient of an encrypted CMS file by matching recipient identifiers
     * found in the file against the aliases available in the keystore.
     *
     * @param file the encrypted file to analyze; must not be {@code null}
     * @return an {@link Optional} containing the alias of the matching recipient in the keystore.
     */
    public Optional<String> identifyRecipient(File file) throws Exception {
        EncodingOption encoding = PEMUtils.findFileEncoding(file);
        Collection<RecipientIdentifier> recipients =
                CMSCryptoUtils.extractRecipientIdentifiers(file, encoding);

        return ctx.getKeystore().findAliasForRecipients(recipients);
    }
    
    /**
     * Identifies the recipient of an encrypted CMS file by matching recipient identifiers
     * found in the file against the aliases available in the keystore.
     *
     * @param in the encrypted InputStream to analyze; must not be {@code null}
     * @param encoding the encoding format of the encrypted input
     * @return an {@link Optional} containing the alias of the matching recipient in the keystore.
     */
    public Optional<String> identifyRecipient(InputStream in, EncodingOption encoding) throws Exception {
        Collection<RecipientIdentifier> recipients =
                CMSCryptoUtils.extractRecipientIdentifiers(in, encoding);

        return ctx.getKeystore().findAliasForRecipients(recipients);
    }

    /**
     * Decrypts an encrypted input file and writes the decrypted content to the specified output file.
     *
     * @param privateKey the private key used for decryption
     * @param input the encrypted input file
     * @param output the destination file for decrypted content
     * @throws IllegalStateException if the provided private key is null
     * @throws Exception if an error occurs during encoding detection or decryption
     */
    public void decrypt(PrivateKey privateKey, File input, File output) throws Exception {
        if (privateKey == null) throw new IllegalStateException("Private key not provided");

        EncodingOption encoding = PEMUtils.findFileEncoding(input);

        decryptor = new CMSDecryptor(privateKey, encoding, ctx.getBufferSize());

        if (ctx.usePKCS11()) decryptor.setProvider(ctx.getKeystore().getProvider());

        decryptor.decrypt(input, output);
    }
    

    /**
     * Decrypts encrypted data from an input stream and writes the decrypted result
     * to an output stream.
     *
     * @param privateKey the private key used for decryption
     * @param encoding the encoding format of the encrypted input
     * @param input the input stream containing encrypted data
     * @param output the output stream where decrypted data will be written
     * @throws IllegalStateException if the provided private key is {@code null}
     * @throws Exception if an error occurs during decryption
     */
    public void decrypt(PrivateKey privateKey, EncodingOption encoding, InputStream input, OutputStream output) throws Exception {
        if (privateKey == null) throw new IllegalStateException("Private key not provided");

        decryptor = new CMSDecryptor(privateKey, encoding, ctx.getBufferSize());

        if (ctx.usePKCS11()) decryptor.setProvider(ctx.getKeystore().getProvider());

        decryptor.decrypt(input, output);
    }


    /**
     * Aborts an ongoing decryption operation, if one is in progress.
     * If no decryption is currently active, this method has no effect.
     */
    public void abort() {
        if (decryptor != null) {
            decryptor.abort();
        }
    }
}