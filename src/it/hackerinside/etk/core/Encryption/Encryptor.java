package it.hackerinside.etk.core.Encryption;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.cert.X509Certificate;

/**
 * Interface for a CMS (Cryptographic Message Syntax) encryptor 
 * 
 * @author Francesco Valentini
 */
public interface Encryptor {
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
	public void encrypt(InputStream input, OutputStream output) throws Exception;
	
    /**
     * Encrypts a file using CMS encryption and writes the result to another file.
     *
     * @param inputFile the file containing the data to encrypt
     * @param outputFile the file where the encrypted data will be written
     * @throws Exception if encryption fails due to cryptographic errors, I/O errors,
     *                   or unsupported algorithms
     */
	public void encrypt(File inputFile, File outputFile) throws Exception;
	
	/**
	 * Add one or more recipients
	 * 
	 * @param recipient
	 */
	public void addRecipients(X509Certificate... recipient);
}
