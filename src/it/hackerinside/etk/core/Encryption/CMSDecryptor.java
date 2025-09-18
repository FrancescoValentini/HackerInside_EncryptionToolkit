package it.hackerinside.etk.core.Encryption;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.PrivateKey;
import java.util.Collection;

import org.bouncycastle.cms.CMSEnvelopedDataParser;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientInformationStore;
import org.bouncycastle.cms.jcajce.JceKeyAgreeEnvelopedRecipient;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;

import it.hackerinside.etk.core.Models.AsymmetricAlgorithm;
import it.hackerinside.etk.core.Models.EncodingOption;
import it.hackerinside.etk.core.PEM.PemInputStream;

/**
 * A CMS (Cryptographic Message Syntax) decryptor for decrypting data using
 * various symmetric algorithms and encoding options. Supports both RSA key
 * transport and ECC key agreement schemes.
 * 
 * @author Francesco Valentini
 */
public class CMSDecryptor {
	private PrivateKey privateKey;
	private EncodingOption encoding;
	/**
	 * Constructs a new CMSDecryptor with the specified parameters.
	 * 
	 * @param privateKey
	 * @param encoding the encoding option for the output (DER or PEM)
	 */
	public CMSDecryptor(PrivateKey privateKey, EncodingOption encoding) {
		this.privateKey = privateKey;
		this.encoding = encoding;
	}
	
    /**
     * Decrypts data from an InputStream to an OutputStream using CMS decryption.
     * The method automatically handles the appropriate recipient selection
     *
     * @param input the input stream containing the data to decrypt
     * @param output the output stream where the decrypted data will be written
     * @throws Exception if decryption fails due to cryptographic errors, I/O errors,
     *                   or unsupported algorithms
     */
    public void decrypt(InputStream input, OutputStream output) throws Exception  {
    	InputStream cmsInput = wrapDecoding(input);
    	boolean success = false;
    	
    	Collection<RecipientInformation> recipients = findReciepients(cmsInput);
    	
    	for (RecipientInformation recipient : recipients) {
    		try {
    			InputStream decryptedStream = createRecipientContentStream(recipient);
    			
    			 byte[] buffer = new byte[8192];
                 int bytesRead;
                 while ((bytesRead = decryptedStream.read(buffer)) != -1) {
                     output.write(buffer, 0, bytesRead);
                 }
                 decryptedStream.close();
                 success = true;
                 break;
                 
    		}catch (Exception e) {
                
            }
    	}
    	
        if (!success) {
            throw new Exception("Unable to decrypt CMS data with the provided private key.");
        }
    }
    
    /**
     * Decrypts a file using CMS decryption and writes the result to another file.
     *
     * @param inputFile the file containing the data to decrypt
     * @param outputFile the file where the decrypted data will be written
     * @throws Exception if decryption fails due to cryptographic errors, I/O errors,
     *                   or unsupported algorithms
     */
    public void decrypt(File inputFile, File outputFile) throws Exception {
        try (InputStream in = new FileInputStream(inputFile);
                OutputStream out = new FileOutputStream(outputFile)) {
        		decrypt(in, out);
           }
    }
    /**
     * Parses and extracts recipient information from CMS (Cryptographic Message Syntax) enveloped data.
     *
     * @param cmsInput the input stream containing CMS enveloped data
     * @return a collection of {@link RecipientInformation} objects representing the recipients
     *         in the CMS data structure
     * @throws CMSException if the CMS data is malformed, cannot be parsed, or if no recipient
     *         information is found in the data
     * @throws IOException if an I/O error occurs while reading from the input stream
     * @throws NullPointerException if the cmsInput parameter is null
     *
     */
    private Collection<RecipientInformation> findReciepients(InputStream cmsInput) throws CMSException, IOException{
        CMSEnvelopedDataParser parser = new CMSEnvelopedDataParser(cmsInput);
        RecipientInformationStore recipients = parser.getRecipientInfos();
        Collection<RecipientInformation> recipientInfos = recipients.getRecipients();

        if (recipientInfos.isEmpty()) {
            throw new CMSException("No recipient information found in CMS data.");
        }
        
        return recipientInfos;
    }
    
    /**
     * Creates an input stream for reading the decrypted content of a CMS recipient.
     * The method automatically selects the appropriate decryption mechanism based on
     * the asymmetric algorithm of the provided private key.
     *
     * @param recipient the recipient information containing the encrypted content
     * @return an InputStream providing access to the decrypted content
     * @throws CMSException if the asymmetric algorithm is not supported or if
     *         decryption fails. Supported algorithms are RSA and EC (Elliptic Curve)
     * @throws IOException if an I/O error occurs during stream creation or decryption
     * @throws NullPointerException if the recipient or private key is null
     *
     */
	private InputStream createRecipientContentStream(RecipientInformation recipient) throws CMSException, IOException {
        AsymmetricAlgorithm keyAlgo = AsymmetricAlgorithm.fromPrivateKey(privateKey);
        
        return switch (keyAlgo) {
            case RSA -> recipient
                        .getContentStream(new JceKeyTransEnvelopedRecipient(privateKey).setProvider("BC"))
                        .getContentStream();
            case EC -> recipient
                        .getContentStream(new JceKeyAgreeEnvelopedRecipient(privateKey).setProvider("BC"))
                        .getContentStream();
            default -> throw new CMSException("Unsupported asymmetric algorithm: " + keyAlgo);
        };
    }
	
    /**
     * Wraps the input stream with the appropriate decoding based on the encoding option.
     * If PEM encoding is selected, returns a PemInputStream; otherwise returns the
     * original input stream for DER encoding.
     *
     * @param input the original input stream
     * @return an input stream wrapped with the appropriate decoding
     * @throws IOException 
     */
    private InputStream wrapDecoding(InputStream input) throws IOException {
        if (encoding == EncodingOption.ENCODING_PEM) {
            return new PemInputStream(input);
        } else {
            return input;
        }
    }


}
