package it.hackerinside.etk.core.CAdES;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.PrivateKey;

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
	public CAdESSigner(PrivateKey privateKey, EncodingOption encoding, HashAlgorithm hashAlgorithm, boolean detachedSignature) {
		this.privateKey = privateKey;
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
		// TODO: 
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
    	sb.append(AsymmetricAlgorithm.fromPrivateKey(privateKey).toString().toUpperCase());
    	return sb.toString();
    }
	
}
