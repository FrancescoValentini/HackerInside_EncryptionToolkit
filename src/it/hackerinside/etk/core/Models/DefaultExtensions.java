package it.hackerinside.etk.core.Models;

import java.io.File;

/**
 * Enumeration of commonly used file extensions with their descriptions.
 * 
 * <p>The extensions are grouped into categories:
 * <ul>
 *   <li>STD_* - Standard file types</li>
 *   <li>CRYPTO_* - Cryptographic and security-related file types</li>
 * </ul>
 * 
 * <p>Each enum constant provides both the file extension and a human-readable description
 * suitable for display in user interfaces.</p>
 * 
 * @author Francesco Valentini
 */
public enum DefaultExtensions {
    /** Text file format. */
    STD_TXT(".txt", "Text File"),
    
    /** Portable Document Format file. */
    STD_PDF(".pdf", "PDF File"),
    
    /** Compressed archive file format. */
    STD_ZIP(".zip", "ZIP File"),
    
    /** PKCS#12 format keystore file (commonly used for personal information exchange). */
    CRYPTO_PFX(".pfx", "PKCS#12 Keystore"),
    
    /** PKCS#12 format keystore file (alternative extension). */
    CRYPTO_P12(".p12", "PKCS#12 Keystore"),
    
    /** Privacy Enhanced Mail format file, commonly used for certificates and keys. */
    CRYPTO_PEM(".pem", "PEM file"),
    
    /** X.509 certificate file format. */
    CRYPTO_CRT(".crt", "X.509 Certificate"),
    
    /** X.509 certificate file format (alternative extension). */
    CRYPTO_CER(".cer", "X.509 Certificate"),
    
    /** Distinguished Encoding Rules format for X.509 certificates. */
    CRYPTO_DER(".der", "X.509 Certificate"),
    
    /** PKCS#7 format file containing enveloping signature (data encapsulated within signature). */
    CRYPTO_P7M(".p7m", "PKCS#7 Enveloping signature"),
    
    /** PKCS#7 format file containing detached signature (signature separate from data). */
    CRYPTO_P7S(".p7s", "PKCS#7 Detached signature"),
    
    /** PKCS#7 format file containing encrypted data. */
    CRYPTO_P7E(".p7e", "PKCS#7 Encrypted data");

    private final String ext;
    private final String description;

    /**
     * Constructs a new DefaultExtensions enum constant with the specified extension and description.
     * 
     * @param ext the file extension (including the dot prefix, e.g., ".txt")
     * @param description a human-readable description of the file type
     */
    DefaultExtensions(String ext, String description) {
        this.ext = ext;
        this.description = description;
    }

    /**
     * Returns the file extension string for this file type.
     * The extension includes the dot prefix (e.g., ".txt", ".pdf").
     * 
     * @return the file extension string
     */
    public String getExt() {
        return ext;
    }

    /**
     * Returns a human-readable description of the file type.
     * 
     * @return the file type description
     */
    public String getDescription() {
        return description;
    }
    
    public String toString() {
    	return this.ext;
    }
    
    public static File applyExtension(File file, DefaultExtensions ext) {
        String name = file.getName();
        return new File(file.getParent(), name + ext);
    }
    
    public static File removeExtension(File file, DefaultExtensions ext) {
        String name = file.getName();
        if (name.endsWith(ext.toString())) {
            return new File(file.getParent(), name.substring(0, name.length() - ext.toString().length()));
        }
        return file;
    }
}