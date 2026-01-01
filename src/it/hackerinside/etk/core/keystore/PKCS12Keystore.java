package it.hackerinside.etk.core.keystore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.util.Objects;

import it.hackerinside.etk.core.Models.SupportedKeystores;

/**
 * Concrete implementation of AbstractKeystore for PKCS12 format keystores.
 * This class provides functionality to load and save PKCS12 keystores from/to files.
 * 
 * @author Francesco Valentini
 */
public class PKCS12Keystore extends AbstractKeystore {
	
    /**
     * The file where the PKCS12 keystore is stored.
     */
    private final File file;
    
    /**
     * The password used to protect the keystore.
     * (keystore master password)
     */
    private char[] password;

    /**
     * Constructs a new PKCS12Keystore with the specified file path and password.
     *
     * @param filePath the path to the PKCS12 keystore file
     * @param password the password used to protect the keystore
     * @throws NullPointerException if filePath or password is null
     */
    public PKCS12Keystore(String filePath, char[] password) {
    	super(SupportedKeystores.PKCS12);
        this.file = new File(filePath);
        this.password = password;
        
    }
    
    public void setPassword(char[] password) {
    	this.password = password;
    }
    
    /**
     * Constructs a new PKCS12Keystore with the specified File object and password.
     *
     * @param file the PKCS12 keystore file
     * @param password the password used to protect the keystore
     * @throws NullPointerException if file or password is null
     */
    public PKCS12Keystore(File file, char[] password) {
    	super(SupportedKeystores.PKCS12);
    	Objects.requireNonNull(file);
        this.file = file;
        this.password = password;
    }

    @Override
    public void load() throws Exception {

        keyStore = KeyStore.getInstance("PKCS12");
        // If file doesn't exist, initialize an empty keystore
        if (!file.exists()) {
            initialize();
            return;
        }
        try (FileInputStream fis = new FileInputStream(file)) {
            keyStore.load(fis, password);
        }
    }

    @Override
    public void save() throws Exception {

        try (FileOutputStream fos = new FileOutputStream(file)) {
            keyStore.store(fos, password);
        }
    }
    
    /**
     * Initializes a new empty PKCS12 keystore and creates the file if it doesn't exist.
     * This method is automatically called during load() if the file doesn't exist.
     *
     * @throws Exception if initialization fails
     */
    public void initialize() throws Exception {
        keyStore = KeyStore.getInstance("PKCS12");
        // Load with null input stream to create an empty keystore
        keyStore.load(null, password);
        
        // Save the empty keystore to create the file
        save();
    }
}
