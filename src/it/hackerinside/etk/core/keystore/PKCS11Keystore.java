package it.hackerinside.etk.core.keystore;

import java.io.File;
import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;

import it.hackerinside.etk.core.Models.SupportedKeystores;

/**
 * This class provides functionality to interact with PKCS11-compliant cryptographic tokens
 * such as smart cards, HSMs, and other hardware security devices.
 * 
 * @author Francesco Valentini
 */
public class PKCS11Keystore extends AbstractKeystore {

    /**
     * The configuration file path for the PKCS11 provider.
     */
    private final String configFile;

    /**
     * The PIN used to access the PKCS11 token.
     */
    private final char[] pin;

    /**
     * The PKCS11 security provider instance.
     */
    private Provider provider;

    /**
     * Constructs a new PKCS11Keystore with the specified configuration file path and PIN.
     *
     * @param configFile the path to the PKCS11 configuration file
     * @param pin the PIN used to access the PKCS11 token
     * @throws NullPointerException if configFile or pin is null
     */
    public PKCS11Keystore(String configFile, char[] pin) {
        super(SupportedKeystores.PKCS11);
        this.configFile = configFile;
        this.pin = pin;
    }
    
    /**
     * Constructs a new PKCS11Keystore with the specified configuration File object and PIN.
     *
     * @param configFile the PKCS11 configuration file
     * @param pin the PIN used to access the PKCS11 token
     * @throws NullPointerException if configFile or pin is null
     */
    public PKCS11Keystore(File configFile, char[] pin) {
        super(SupportedKeystores.PKCS11);
        this.configFile = configFile.getAbsolutePath();
        this.pin = pin;
    }

    @Override
    public void load() throws Exception {
        Provider p = Security.getProvider("SunPKCS11");
        if (p == null) {
            throw new IllegalStateException("SunPKCS11 provider not available.");
        }
        provider = p.configure(configFile);
        Security.addProvider(provider);

        keyStore = KeyStore.getInstance("PKCS11", provider);
        keyStore.load(null, pin);
    }

    /**
     * Save operation is not supported for PKCS11 keystores.
     * PKCS11 represents hardware security modules where keys are stored persistently
     * on the hardware device and cannot be saved through this interface.
     *
     * @throws UnsupportedOperationException always, as save is not supported for PKCS11 keystores
     */
    @Override
    public void save() throws Exception {
        throw new UnsupportedOperationException("Save not supported for PKCS11");
    }
    
    public void setPassword(char[] password) {
    	throw new UnsupportedOperationException("setPassword not supported for PKCS11");
    }
}
