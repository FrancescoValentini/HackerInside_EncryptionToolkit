package it.hackerinside.etk.core.Encryption;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.PrivateKey;

import it.hackerinside.etk.core.Models.EncodingOption;

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
    	//TODO IMPLEMENT
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
}
