package it.hackerinside.etk.core.Services;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import it.hackerinside.etk.GUI.ETKContext;
import it.hackerinside.etk.core.CAdES.CAdESSigner;
import it.hackerinside.etk.core.Models.EncodingOption;
import it.hackerinside.etk.core.Models.HashAlgorithm;

/**
 * Service responsible for generating CAdES signatures.
 * 
 * Uses {@link ETKContext} for configuration such as buffer size and provider.
 */
public class SignService {

    private final ETKContext ctx;
    private CAdESSigner signer;

    public SignService(ETKContext ctx) {
        this.ctx = ctx;
    }

    /**
     * Signs a file and writes the signature to the output file.
     *
     * @param privateKey the signing private key
     * @param certificate the signer certificate
     * @param encoding encoding format (PEM/DER)
     * @param hash hash algorithm
     * @param detached whether the signature is detached
     * @param input file to sign
     * @param output destination file
     */
    public void sign(
            PrivateKey privateKey,
            X509Certificate certificate,
            EncodingOption encoding,
            HashAlgorithm hash,
            boolean detached,
            File input,
            File output
    ) throws Exception {

        if (privateKey == null) {
            throw new IllegalStateException("Private key not provided");
        }

        if (certificate == null) {
            throw new IllegalStateException("Certificate not provided");
        }

        signer = new CAdESSigner(
                privateKey,
                certificate,
                encoding,
                hash,
                detached,
                ctx.getBufferSize()
        );

        signer.sign(input, output);
    }

    /**
     * Stream version.
     */
    public void sign(
            PrivateKey privateKey,
            X509Certificate certificate,
            EncodingOption encoding,
            HashAlgorithm hash,
            boolean detached,
            InputStream input,
            OutputStream output
    ) throws Exception {

        if (privateKey == null) {
            throw new IllegalStateException("Private key not provided");
        }

        if (certificate == null) {
            throw new IllegalStateException("Certificate not provided");
        }

        signer = new CAdESSigner(
                privateKey,
                certificate,
                encoding,
                hash,
                detached,
                ctx.getBufferSize()
        );


        signer.sign(input, output);
    }

    /**
     * Abort current signing operation.
     */
    public void abort() {
        if (signer != null) {
            signer.abort();
        }
    }
}
