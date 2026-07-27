package it.hackerinside.etk.core.Encryption;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.util.Objects;
import java.security.PrivateKey;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.InputStreamWithMAC;
import org.bouncycastle.cms.RecipientOperator;
import org.bouncycastle.cms.jcajce.JceKEMRecipient;
import org.bouncycastle.operator.InputAEADDecryptor;

/**
 * CMS recipient implementation for authenticated enveloped data using a KEM-based
 * key agreement mechanism.
 *
 * <p>This recipient extracts the content encryption key using the recipient's
 * private key and provides an AEAD decryptor for the encrypted CMS content.</p>
 */
public class JceKEMAuthEnvelopedRecipient extends JceKEMRecipient {

    public JceKEMAuthEnvelopedRecipient(PrivateKey recipientKey) {
        super(recipientKey);
    }


    @Override
    public RecipientOperator getRecipientOperator(AlgorithmIdentifier keyEncryptionAlgorithm,final AlgorithmIdentifier contentEncryptionAlgorithm,byte[] encryptedContentEncryptionKey)throws CMSException {
        Key secretKey = extractSecretKey(keyEncryptionAlgorithm,contentEncryptionAlgorithm,encryptedContentEncryptionKey);
        final Cipher cipher = contentHelper.createContentCipher(secretKey,contentEncryptionAlgorithm);
        return new RecipientOperator(new KEMAeadInputDecryptor(contentEncryptionAlgorithm,cipher));
    }


    /**
     * Provides AEAD content decryption capabilities for CMS authenticated
     * enveloped data.
     *
     * <p>The decryptor exposes the content cipher stream and an output stream
     * used to supply additional authenticated data (AAD) to the cipher.</p>
     */
    private static final class KEMAeadInputDecryptor implements InputAEADDecryptor {
        private final AlgorithmIdentifier algorithmIdentifier;
        private final Cipher cipher;
        private InputStream source;

        KEMAeadInputDecryptor(AlgorithmIdentifier algorithmIdentifier,Cipher cipher) {
            this.algorithmIdentifier = Objects.requireNonNull(algorithmIdentifier);
            this.cipher = Objects.requireNonNull(cipher);
        }

        @Override
        public InputStream getInputStream(InputStream input) {
            this.source = input;
            return new CipherInputStream(input, cipher);
        }

        @Override
        public OutputStream getAADStream() {
            return new OutputStream() {
                @Override
                public void write(byte[] b, int off, int len) {
                    cipher.updateAAD(b, off, len);
                }

                @Override
                public void write(int b) {
                    cipher.updateAAD(new byte[]{(byte)b});
                }
            };
        }

        @Override
        public byte[] getMAC() {
            if (source instanceof InputStreamWithMAC withMac) return withMac.getMAC();
            return null;
        }
        
        @Override
        public AlgorithmIdentifier getAlgorithmIdentifier() {return algorithmIdentifier;}
    }
}