package it.hackerinside.etk.core.keystore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
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
    
    /**
     * Updates the password protecting a keystore entry identified by the given alias.
     * <p>
     * The current password is verified by attempting to recover the entry. If the
     * current password is invalid, a {@link SecurityException} is thrown.
     *
     * @param alias the alias of the entry whose password must be updated
     * @param currentPassword the current password protecting the entry
     * @param newPassword the new password to apply to the entry
     * @throws SecurityException if the current password is not valid
     * @throws KeyStoreException if the keystore is not initialized or an error occurs
     * @throws NullPointerException if alias, currentPassword, or newPassword is null
     */
    public void updateEntryPassword(String alias, char[] currentPassword, char[] newPassword)
            throws KeyStoreException {

        Objects.requireNonNull(alias);
        Objects.requireNonNull(currentPassword);
        Objects.requireNonNull(newPassword);

        try {
            KeyStore.ProtectionParameter oldProt =
                    new KeyStore.PasswordProtection(currentPassword);

            // This will fail if the password is wrong
            KeyStore.Entry entry = keyStore.getEntry(alias, oldProt);
            if (entry == null) {
                throw new KeyStoreException("No entry found for alias: " + alias);
            }

            KeyStore.ProtectionParameter newProt =
                    new KeyStore.PasswordProtection(newPassword);

            keyStore.setEntry(alias, entry, newProt);
        } catch (UnrecoverableEntryException | NoSuchAlgorithmException e) {
            throw new SecurityException("Invalid current password for alias: " + alias, e);
        }
    }

    /**
     * Renames an existing keystore entry from one alias to another.
     * <p>
     * The entry content is preserved and only the alias is changed.
     *
     * @param oldAlias the current alias name
     * @param newAlias the new alias name
     * @throws KeyStoreException if the keystore is not initialized, the old alias
     *         does not exist, or the new alias already exists
     * @throws UnrecoverableEntryException 
     * @throws NoSuchAlgorithmException 
     * @throws NullPointerException if oldAlias or newAlias is null
     */
    public void renameEntry(String oldAlias, String newAlias, char[] password)
            throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException {

        Objects.requireNonNull(oldAlias);
        Objects.requireNonNull(newAlias);

        if (!keyStore.containsAlias(oldAlias)) {
            throw new KeyStoreException("Alias does not exist: " + oldAlias);
        }
        if (keyStore.containsAlias(newAlias)) {
            throw new KeyStoreException("Alias already exists: " + newAlias);
        }

        KeyStore.ProtectionParameter protection = null;
        if (keyStore.isKeyEntry(oldAlias) && password != null) {
            protection = new KeyStore.PasswordProtection(password);
        }

        KeyStore.Entry entry = keyStore.getEntry(oldAlias, protection);
        if (entry == null) {
            throw new KeyStoreException("Unable to retrieve entry for alias: " + oldAlias);
        }

        keyStore.setEntry(newAlias, entry, protection);
        keyStore.deleteEntry(oldAlias);
    }

}
