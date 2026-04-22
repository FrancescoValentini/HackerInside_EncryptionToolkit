package it.hackerinside.etk.core.Services;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import it.hackerinside.etk.GUI.ETKContext;
import it.hackerinside.etk.core.CAdES.CAdESUtils;
import it.hackerinside.etk.core.CAdES.CAdESVerifier;
import it.hackerinside.etk.core.Models.EncodingOption;
import it.hackerinside.etk.core.Models.VerificationResult;
import it.hackerinside.etk.core.PEM.PEMUtils;

/**
 * Service responsible for verifying CAdES signatures.
 * <p>
 * Supports both attached and detached signatures. In the case of detached
 * signatures, a {@link FileProvider} must be set to supply the original data file.
 */
public class SignatureVerificationService {
	private final ETKContext ctx;
	private CAdESVerifier verifier = null;
	private FileProvider fileProvider;
	

    /**
     * Creates a new verification service with the given context.
     *
     * @param ctx the ETK context containing configuration such as buffer size
     */
    public SignatureVerificationService(ETKContext ctx) {
        this.ctx = ctx;
    }
    
    /**
     * Sets the {@link FileProvider} used to retrieve the original data file
     * for detached signature verification.
     *
     * @param fileProvider the provider that supplies the data file
     */
    public void setFileProvider(FileProvider fileProvider) {
        this.fileProvider = fileProvider;
    }

    private File invokeFileProvider() {
        return requireProvider(fileProvider, "FileProvider").getFile();
    }

    private <T> T requireProvider(T provider, String name) {
        Objects.requireNonNull(provider, name + " is not set");
        return provider;
    }
    
    /**
     * Detects the encoding of the given file (PEM or DER)
     *
     * @param file the file to analyze
     * @return the detected encoding option
     * @throws IOException if reading the file fails
     */
    private EncodingOption findEncoding(File file) throws IOException {
    	return PEMUtils.findFileEncoding(file);
    }
    
    /**
     * Determines whether the given signature file is detached.
     *
     * @param file the signature file
     * @param encoding the encoding of the file
     * @return {@code true} if the signature is detached, {@code false} otherwise
     * @throws IOException if reading the file fails
     */
    public boolean isDetached(File file, EncodingOption encoding) throws IOException {
    	return CAdESUtils.isDetached(file, encoding);
    }
    
    /**
     * Verifies the given signature file.
     * <p>
     * If the signature is attached, verification is performed directly.
     * If the signature is detached, a data file is requested via the configured
     * {@link FileProvider}.
     *
     * @param file the signature file to verify
     * @return the {@link VerificationResult}, or {@code null} if verification
     *         was aborted due to missing data file
     * @throws IOException if reading the file fails
     */
    private VerificationResult verify(File file) throws IOException {
    	EncodingOption encoding = findEncoding(file);
    	boolean detached = isDetached(file, encoding);
    	
    	if(!detached) {
    		this.verifier = new CAdESVerifier(encoding, false, ctx.getBufferSize());
    		return verifier.verify(file);
    	}else {
    		this.verifier = new CAdESVerifier(encoding, true, ctx.getBufferSize());
            File dataFile = invokeFileProvider();
            if(dataFile == null) return null; // file selection aborted
    		return verifier.verifyDetached(file, dataFile);
    	}
    }
}
