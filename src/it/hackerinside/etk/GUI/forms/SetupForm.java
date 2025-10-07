package it.hackerinside.etk.GUI.forms;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import it.hackerinside.etk.GUI.DialogUtils;
import it.hackerinside.etk.GUI.ETKContext;
import it.hackerinside.etk.GUI.FileDialogUtils;
import it.hackerinside.etk.core.Models.ApplicationPreferences;
import it.hackerinside.etk.core.Models.DefaultExtensions;
import it.hackerinside.etk.core.keystore.AbstractKeystore;
import it.hackerinside.etk.core.keystore.PKCS12Keystore;

import java.awt.Font;
import java.io.File;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.security.spec.ECGenParameterSpec;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class SetupForm {

	private JFrame frame;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private int currentStep = 0;

    private JButton btnBack;
    private JButton btnNext;
    private JTextField txtbKeystorePath;
    private JTextField txtbKnownCerts;
    private JTextField txtbPKCS11Config;
    private JTextField txtbCommonName;
    private JTextField txtbCountryCode;
    private JTextField txtbStateName;
    private ETKContext ctx;
	private JCheckBox chckbPkcs11;
	private JSpinner spinnerExpDays;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SetupForm window = new SetupForm();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public SetupForm() {
		ctx = ETKContext.getInstance();
		initialize();
	}

	public void setVisible() {
		frame.setVisible(true);
	}
	/**
	 * Initialize the contents of the frame.
	 */
    private void initialize() {
        frame = new JFrame("Setup Wizard");
        frame.setAlwaysOnTop(true);
        frame.setResizable(false);
        frame.setBounds(100, 100, 715, 558);
        frame.getContentPane().setLayout(new BorderLayout());

        // Card layout for different screens
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Step 1: Welcome screen
        JPanel welcomePanel = new JPanel(new BorderLayout());

        // Step 2: Selection screen
        JPanel selectionPanel = new JPanel();
        selectionPanel.setLayout(null);
        JLabel lblKeystoreSetup = new JLabel("Keystore Setup");
        lblKeystoreSetup.setHorizontalAlignment(SwingConstants.CENTER);
        lblKeystoreSetup.setFont(new Font("Tahoma", Font.PLAIN, 18));
        lblKeystoreSetup.setBounds(135, 5, 428, 30);
        selectionPanel.add(lblKeystoreSetup);

        // Step 3: Self-signed certificate creation
        JPanel certPanel = new JPanel();
        certPanel.setLayout(null);
        JLabel lblSelfsignedCertificate = new JLabel("Self-Signed certificate");
        lblSelfsignedCertificate.setHorizontalAlignment(SwingConstants.CENTER);
        lblSelfsignedCertificate.setFont(new Font("Tahoma", Font.PLAIN, 18));
        lblSelfsignedCertificate.setBounds(227, 5, 245, 30);
        certPanel.add(lblSelfsignedCertificate);

        // Step 4: Final confirmation
        JPanel finalPanel = new JPanel(new BorderLayout());
        JLabel finalLabel = new JLabel("<html>\r\n\r\n<center>\r\n<b>\r\nSetup complete!\r\n</b><br /><br />\r\nLog in to the keystore or restart the program to load the keys correctly\r\n</center>\r\n</html>", SwingConstants.CENTER);
        finalLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
        finalPanel.add(finalLabel, BorderLayout.CENTER);

        // Add cards
        cardPanel.add(welcomePanel, "Step0");
        
        JLabel lblNewLabel_2 = new JLabel("<html>\r\n<center><b>Welcome!</b></center><br/>\r\n\r\n<p>\r\nNo trusted certificates keystore and no key pair keystore were found.<br/>\r\nThis wizard will guide you through the creation process.\r\n</p>\r\n</html>");
        lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 19));
        lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
        welcomePanel.add(lblNewLabel_2, BorderLayout.CENTER);
        cardPanel.add(selectionPanel, "Step1");
        
        txtbKeystorePath = new JTextField();
        txtbKeystorePath.setText("keystore.pfx");
        txtbKeystorePath.setFont(new Font("Tahoma", Font.PLAIN, 14));
        txtbKeystorePath.setColumns(10);
        txtbKeystorePath.setBounds(177, 46, 455, 23);
        selectionPanel.add(txtbKeystorePath);
        
        JLabel lblNewLabel = new JLabel("Keystore path:");
        lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblNewLabel.setBounds(68, 49, 91, 17);
        selectionPanel.add(lblNewLabel);
        
        txtbKnownCerts = new JTextField();
        txtbKnownCerts.setText("knowncerts.pfx");
        txtbKnownCerts.setFont(new Font("Tahoma", Font.PLAIN, 14));
        txtbKnownCerts.setColumns(10);
        txtbKnownCerts.setBounds(177, 78, 455, 23);
        selectionPanel.add(txtbKnownCerts);
        
        JLabel lblKnownCertificatesPath = new JLabel("Known Certificates path:");
        lblKnownCertificatesPath.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblKnownCertificatesPath.setBounds(10, 81, 149, 17);
        selectionPanel.add(lblKnownCertificatesPath);
        
        JButton btnOpenKnownCerts = new JButton("...");

        btnOpenKnownCerts.setFont(new Font("Tahoma", Font.PLAIN, 16));
        btnOpenKnownCerts.setBounds(642, 78, 47, 23);
        selectionPanel.add(btnOpenKnownCerts);
        
        JButton btnOpenKeystore = new JButton("...");

        btnOpenKeystore.setFont(new Font("Tahoma", Font.PLAIN, 16));
        btnOpenKeystore.setBounds(642, 46, 47, 23);
        selectionPanel.add(btnOpenKeystore);
        
        chckbPkcs11 = new JCheckBox("Use PKCS#11");
        chckbPkcs11.setFont(new Font("Tahoma", Font.PLAIN, 15));
        chckbPkcs11.setBounds(177, 108, 163, 23);
        selectionPanel.add(chckbPkcs11);
        
        JButton btnOpenPKCS11Config = new JButton("...");
        btnOpenPKCS11Config.setEnabled(false);
        btnOpenPKCS11Config.setFont(new Font("Tahoma", Font.PLAIN, 16));
        btnOpenPKCS11Config.setBounds(642, 138, 47, 23);
        selectionPanel.add(btnOpenPKCS11Config);
        
        txtbPKCS11Config = new JTextField();
        txtbPKCS11Config.setEnabled(false);
        txtbPKCS11Config.setFont(new Font("Tahoma", Font.PLAIN, 14));
        txtbPKCS11Config.setColumns(10);
        txtbPKCS11Config.setBounds(177, 138, 455, 23);
        selectionPanel.add(txtbPKCS11Config);
        
        JLabel lblPkcsConfigPath = new JLabel("PKCS11 Config path:");
        lblPkcsConfigPath.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblPkcsConfigPath.setBounds(10, 141, 149, 17);
        selectionPanel.add(lblPkcsConfigPath);
        
        JLabel lblNewLabel_1 = new JLabel("<html>\r\nThe personal keystore contains your public/private key pairs. The trusted certificate store (also PKCS12) contains only public keys.\r\n<br><br>\r\nEnabling PKCS#11 lets you configure a cryptographic device. When used, only the trusted certificate store is created, the personal keystore is not generated.\r\n<br><br>\r\n\r\n<p style=\"color: red\">\r\nPKCS#11 is an advanced feature requiring dedicated hardware. Do not enable it unless you are familiar with this technology.\r\n</p>\r\n</html>");
        lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lblNewLabel_1.setVerticalAlignment(SwingConstants.TOP);
        lblNewLabel_1.setHorizontalAlignment(SwingConstants.LEFT);
        lblNewLabel_1.setBounds(20, 227, 659, 178);
        selectionPanel.add(lblNewLabel_1);
        cardPanel.add(certPanel, "Step2");
        
        txtbCommonName = new JTextField();
        txtbCommonName.setFont(new Font("Tahoma", Font.PLAIN, 14));
        txtbCommonName.setColumns(10);
        txtbCommonName.setBounds(201, 50, 455, 23);
        certPanel.add(txtbCommonName);
        
        JLabel lblCommonName = new JLabel("Common Name:");
        lblCommonName.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblCommonName.setBounds(45, 53, 146, 17);
        certPanel.add(lblCommonName);
        
        JLabel lblCountryCode = new JLabel("Country Code:");
        lblCountryCode.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblCountryCode.setBounds(45, 121, 146, 17);
        certPanel.add(lblCountryCode);
        
        txtbCountryCode = new JTextField();
        txtbCountryCode.setFont(new Font("Tahoma", Font.PLAIN, 14));
        txtbCountryCode.setColumns(10);
        txtbCountryCode.setBounds(201, 118, 85, 23);
        certPanel.add(txtbCountryCode);
        
        txtbStateName = new JTextField();
        txtbStateName.setFont(new Font("Tahoma", Font.PLAIN, 14));
        txtbStateName.setColumns(10);
        txtbStateName.setBounds(201, 84, 455, 23);
        certPanel.add(txtbStateName);
        
        JLabel lblStateName = new JLabel("State Name:");
        lblStateName.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblStateName.setBounds(45, 87, 146, 17);
        certPanel.add(lblStateName);
        
        spinnerExpDays = new JSpinner();
        spinnerExpDays.setFont(new Font("Tahoma", Font.PLAIN, 16));
        spinnerExpDays.setModel(new SpinnerNumberModel(Integer.valueOf(1095), Integer.valueOf(1), null, Integer.valueOf(1)));
        spinnerExpDays.setBounds(201, 155, 85, 23);
        certPanel.add(spinnerExpDays);
        
        JLabel lblExpire = new JLabel("Expire (Days):");
        lblExpire.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblExpire.setBounds(45, 157, 146, 17);
        certPanel.add(lblExpire);
        
        JLabel lblNewLabel_1_1 = new JLabel("<html>\r\nThis procedure generates a private key and a self-signed X.509 certificate.\r\nThe cryptographic algorithm used is Elliptic Curve Cryptography (ECC) based on the NIST P-384 curve.\r\n<br><br>\r\n\r\n<p color=\"red\">\r\nA self-signed certificate is not signed by a recognized Certificate Authority (CA) and, therefore, has no legal value for trustworthy identification by third parties.\r\n</p>\r\n</html>");
        lblNewLabel_1_1.setVerticalAlignment(SwingConstants.TOP);
        lblNewLabel_1_1.setHorizontalAlignment(SwingConstants.LEFT);
        lblNewLabel_1_1.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lblNewLabel_1_1.setBounds(20, 222, 659, 108);
        certPanel.add(lblNewLabel_1_1);
        cardPanel.add(finalPanel, "Step3");

        frame.getContentPane().add(cardPanel, BorderLayout.CENTER);

        // Navigation buttons
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnBack = new JButton("Back");
        btnBack.setFont(new Font("Tahoma", Font.PLAIN, 15));
        btnNext = new JButton("Next");
        btnNext.setFont(new Font("Tahoma", Font.PLAIN, 15));

        btnBack.setEnabled(false);

        btnBack.addActionListener(e -> {
            if (currentStep > 0) {
                currentStep--;
                updateStep();
            }
        });

        btnNext.addActionListener(e -> {
            if (currentStep < 3) {
                if (currentStep == 2) {
                    applyAndCreate();
                } else {
                    currentStep++;
                    updateStep();
                }
            } else {
                frame.dispose(); 
            }
        });


        navPanel.add(btnBack);
        navPanel.add(btnNext);

        frame.getContentPane().add(navPanel, BorderLayout.SOUTH);

        updateStep();
        
        btnOpenKnownCerts.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		openPKCS12(txtbKnownCerts);
        	}
        });

        btnOpenKeystore.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		openPKCS12(txtbKeystorePath);
        	}
        });
        
        btnOpenPKCS11Config.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		openPKCS11Config();
        	}
        });
        
        chckbPkcs11.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
    			txtbKeystorePath.setEnabled(!chckbPkcs11.isSelected());
    			btnOpenKeystore.setEnabled(!chckbPkcs11.isSelected());
    			
    			txtbPKCS11Config.setEnabled(chckbPkcs11.isSelected());
    			btnOpenPKCS11Config.setEnabled(chckbPkcs11.isSelected());
        	}
        });
        
        loadSettings();
    }

    /**
     * Updates the wizard to show the current step and manage button states.
     */
    private void updateStep() {
        cardLayout.show(cardPanel, "Step" + currentStep);
        
        // Ability or disability text boxes for generating certificates
    	txtbCommonName.setEnabled(!chckbPkcs11.isSelected());
    	txtbCountryCode.setEnabled(!chckbPkcs11.isSelected());
    	txtbStateName.setEnabled(!chckbPkcs11.isSelected());
    	spinnerExpDays.setEnabled(!chckbPkcs11.isSelected());

        btnBack.setEnabled(currentStep > 0);
        if (currentStep == 3) {
            btnNext.setText("Finish");
        } else {
            btnNext.setText("Next");
        }
    }

    /**
     * Applies the configuration settings and creates the necessary certificates and keystores.
     * This method gathers user input, initializes the keystore if needed, creates X509 certificates,
     * and displays appropriate success or error messages to the user.
     */
    private void applyAndCreate() {
        ctx.setKeyStorePath(txtbKeystorePath.getText());
        ctx.setPkcs11Driver(txtbPKCS11Config.getText());
        ctx.setKnownCertsPath(txtbKnownCerts.getText());
        ctx.setUsePkcs11(chckbPkcs11.isSelected());

        boolean success = true;
        StringBuilder errors = new StringBuilder();

        try {
            if (!chckbPkcs11.isSelected()) {
                initializePersonalKeystore();
                
                createX509Cert();
            }
        } catch (Exception e) {
            success = false;
            errors.append("Error: ").append(e.getMessage()).append("\n");
            e.printStackTrace();
        }

        if (success) {
            JOptionPane.showMessageDialog(
                frame,
                "Setup completed successfully!",
                "Setup completed successfully!",
                JOptionPane.INFORMATION_MESSAGE
            );
            currentStep = 3;
            updateStep();
        } else {
            JOptionPane.showMessageDialog(
                frame,
                "Setup was not completed successfully",
                errors.toString(),
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Opens a file dialog for selecting PKCS12 files and updates the specified text field with the selected path.
     * 
     * @param txtField the text field where the selected file path will be displayed
     */
    private void openPKCS12(JTextField txtField) {
        File file = FileDialogUtils.openFileDialog(
                null,
                "Select PKCS12 Container",
                ".",
                DefaultExtensions.CRYPTO_P12,
                DefaultExtensions.CRYPTO_PFX
        );

        if (file != null && file.exists() && !file.isDirectory()) {
            txtField.setText(file.getAbsolutePath());
        }
    }

    /**
     * Opens a file dialog for selecting the PKCS11 configuration file and updates the corresponding text field.
     */
    private void openPKCS11Config() {
        File file = FileDialogUtils.openFileDialog(
                null,
                "Select PKCS11 Config file",
                "."
        );

        if (file != null && file.exists() && !file.isDirectory()) {
            txtbPKCS11Config.setText(file.getAbsolutePath());
        }
    }

    /**
     * Loads the default application settings into the corresponding UI fields.
     * This includes keystore paths, known certificates path, PKCS11 driver path, and PKCS11 usage preference.
     */
    private void loadSettings() {
        txtbKeystorePath.setText(ApplicationPreferences.KEYSTORE_PATH.getValue());
        txtbKnownCerts.setText(ApplicationPreferences.KNOWN_CERTS_PATH.getValue());
        txtbPKCS11Config.setText(ApplicationPreferences.PKCS11_DRIVER.getValue());
        
        chckbPkcs11.setSelected(Boolean.valueOf(ApplicationPreferences.USE_PKCS11.getValue()));
    }

    /**
     * Initializes the personal keystore if it doesn't exist.
     * Prompts the user for a master password and creates a new PKCS12 keystore.
     * Displays error messages if the keystore creation fails.
     */
    private void initializePersonalKeystore() {
        File keystore = new File(txtbKeystorePath.getText());
        if(!keystore.exists()) {
            String pwd = askKeystorePassword();
            AbstractKeystore pkcs12 = new PKCS12Keystore(keystore, pwd.toCharArray());
            try {
            	pkcs12.load();
                pkcs12.save();
            } catch (Exception e) {
                e.printStackTrace();
                DialogUtils.showMessageBox(
                        null,
                        "Error during keystore setup",
                        "Error during keystore setup!",
                        e.getMessage(),
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    /**
     * Creates an X509 certificate with the specified parameters.
     * Handles user input, generates the key pair, builds the certificate,
     * and saves it to the keystore.
     */
    private void createX509Cert() {
        String name = txtbCommonName.getText();
        String cc = txtbCountryCode.getText();
        String state = txtbStateName.getText();
        int expDays = (int) spinnerExpDays.getValue();

        try {
            // Collect user input
            String ksMaster = askKeystorePassword();
            String alias = askAlias();
            String pwd = askPrivateKeyPassword();

            // Add BouncyCastle provider
            Security.addProvider(new BouncyCastleProvider());

            // Generate EC P-384 key pair
            KeyPair keyPair = generateECKeyPair();
            PrivateKey privk = keyPair.getPrivate();
            PublicKey pubk = keyPair.getPublic();

            // Build X509 certificate
            X509Certificate crt = buildCertificate(name, cc, state, expDays, pubk, privk);

            // Save certificate into keystore
            saveCertificateToKeystore(ksMaster, alias, pwd, privk, crt);

            DialogUtils.showMessageBox(
                null,
                "Certificate generated",
                "Certificate successfully generated!",
                "Alias: " + alias,
                JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception e) {
            e.printStackTrace();
            DialogUtils.showMessageBox(
                null,
                "Error during certificate generation!",
                "Error during certificate generation!",
                e.getMessage(),
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Prompts the user for the keystore master password.
     */
    private String askKeystorePassword() {
        return DialogUtils.showInputBox(
            null,
            "Keystore master password",
            "Keystore master password",
            "Password:",
            true
        );
    }

    /**
     * Prompts the user for the alias of the private key.
     */
    private String askAlias() {
        return DialogUtils.showInputBox(
            null,
            "Private key alias",
            "Private key alias",
            "Alias:",
            false
        );
    }

    /**
     * Prompts the user for the private key password.
     */
    private String askPrivateKeyPassword() {
        return DialogUtils.showInputBox(
            null,
            "Private key password",
            "Private key password",
            "Password:",
            true
        );
    }

    /**
     * Generates an EC P-384 key pair using BouncyCastle provider.
     */
    private KeyPair generateECKeyPair() throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC", "BC");
        kpg.initialize(new ECGenParameterSpec("secp384r1"), new SecureRandom());
        return kpg.generateKeyPair();
    }

    /**
     * Builds an X509 certificate with the provided subject details, validity, and keys.
     */
    private X509Certificate buildCertificate(
            String commonName,
            String countryCode,
            String state,
            int expDays,
            PublicKey pubk,
            PrivateKey privk) throws Exception {

        X500Name subject = new X500Name("CN=" + commonName + ", C=" + countryCode + ", ST=" + state);

        Date notBefore = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(notBefore);
        cal.add(Calendar.DAY_OF_YEAR, expDays);
        Date notAfter = cal.getTime();

        BigInteger serial = new BigInteger(64, new SecureRandom());

        JcaX509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
            subject, serial, notBefore, notAfter, subject, pubk
        );

        certBuilder.addExtension(
            Extension.keyUsage,
            true,
            new KeyUsage(KeyUsage.digitalSignature | KeyUsage.nonRepudiation | KeyUsage.dataEncipherment)
        );

        ContentSigner signer = new JcaContentSignerBuilder("SHA384withECDSA")
            .setProvider("BC")
            .build(privk);

        return new JcaX509CertificateConverter()
            .setProvider("BC")
            .getCertificate(certBuilder.build(signer));
    }

    /**
     * Saves the private key and certificate into the keystore.
     */
    private void saveCertificateToKeystore(
            String ksMaster,
            String alias,
            String pwd,
            PrivateKey privk,
            X509Certificate crt) throws Exception {

        ctx.loadKeystore(ksMaster);
        ctx.getKeystore().addPrivateKey(alias, privk, pwd.toCharArray(), new X509Certificate[]{crt});
        ctx.getKeystore().save();
    }

}
