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
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Arrays;

import it.hackerinside.etk.GUI.DialogUtils;
import it.hackerinside.etk.GUI.ETKContext;
import it.hackerinside.etk.GUI.FileDialogUtils;
import it.hackerinside.etk.Utils.X509Builder;
import it.hackerinside.etk.core.Models.ApplicationPreferences;
import it.hackerinside.etk.core.Models.DefaultExtensions;
import it.hackerinside.etk.core.Models.HashAlgorithm;
import it.hackerinside.etk.core.Models.SymmetricAlgorithms;
import it.hackerinside.etk.core.keystore.AbstractKeystore;
import it.hackerinside.etk.core.keystore.PKCS12Keystore;

import java.awt.Font;
import java.io.File;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;

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
	private JComboBox<HashAlgorithm> cmbHashAlgPath;
	private JComboBox<SymmetricAlgorithms> cmbEncAlgPath;
	private JLabel lblPkcsConfigPath;
	private JButton btnOpenPKCS11Config;
	private JPasswordField txtPwd;
	private JPasswordField txtPwdConfirm;
	private JLabel lblPwdWarning;

	private char[] keystorePassword;
	private JCheckBox chckbSkipCert;

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
        JLabel lblKeystoreSetup = new JLabel("Preferences");
        lblKeystoreSetup.setHorizontalAlignment(SwingConstants.CENTER);
        lblKeystoreSetup.setFont(new Font("Tahoma", Font.PLAIN, 18));
        lblKeystoreSetup.setBounds(135, 5, 428, 30);
        selectionPanel.add(lblKeystoreSetup);
        
        // Step 3: Keystore setup
        JPanel keystorePanel = new JPanel();
        keystorePanel.setLayout(null);
        JLabel lblKeystorePanel = new JLabel("Keystore Setup");
        lblKeystorePanel.setHorizontalAlignment(SwingConstants.CENTER);
        lblKeystorePanel.setFont(new Font("Tahoma", Font.PLAIN, 18));
        lblKeystorePanel.setBounds(227, 5, 245, 30);
        keystorePanel.add(lblKeystorePanel);
        
        txtPwd = new JPasswordField();
        txtPwd.setBounds(300, 100, 250, 25);
        keystorePanel.add(txtPwd);

        JLabel lblPwd = new JLabel("Password:");
        lblPwd.setFont(new Font("Tahoma", Font.PLAIN, 16));
        lblPwd.setBounds(100, 100, 170, 25);
        keystorePanel.add(lblPwd);

        txtPwdConfirm = new JPasswordField();
        txtPwdConfirm.setBounds(300, 140, 250, 25);
        keystorePanel.add(txtPwdConfirm);

        JLabel lblPwdConfirm = new JLabel("Confirm Password:");
        lblPwdConfirm.setFont(new Font("Tahoma", Font.PLAIN, 16));
        lblPwdConfirm.setBounds(100, 140, 190, 25);
        keystorePanel.add(lblPwdConfirm);

        lblPwdWarning = new JLabel(
        		"<html><center><b>WARNING</b></center><br/>\r\nIf you lose this password, your keystore cannot be recovered. Make sure to store it securely in a safe place.</html>"
        		);
        lblPwdWarning.setFont(new Font("Tahoma", Font.PLAIN, 16));
        lblPwdWarning.setBounds(41, 200, 616, 131);
        keystorePanel.add(lblPwdWarning);
        
        // Step 4: Self-signed certificate creation
        JPanel certPanel = new JPanel();
        certPanel.setLayout(null);
        JLabel lblSelfsignedCertificate = new JLabel("Self-Signed certificate");
        lblSelfsignedCertificate.setHorizontalAlignment(SwingConstants.CENTER);
        lblSelfsignedCertificate.setFont(new Font("Tahoma", Font.PLAIN, 18));
        lblSelfsignedCertificate.setBounds(227, 5, 245, 30);
        certPanel.add(lblSelfsignedCertificate);

        // Step 5: Final confirmation
        JPanel finalPanel = new JPanel(new BorderLayout());
        JLabel finalLabel = new JLabel("<html>\r\n\r\n<center>\r\n<b>\r\nSetup complete!\r\n</b><br /><br />\r\nLog in to the keystore or restart the program to load the keys correctly\r\n</center>\r\n</html>", SwingConstants.CENTER);
        finalLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
        finalPanel.add(finalLabel, BorderLayout.CENTER);
        
        JLabel lblNewLabel_2 = new JLabel("<html>\r\n<center><b>Welcome!</b></center><br/>\r\n\r\n<p>\r\nNo trusted certificates keystore and no key pair keystore were found.<br/>\r\nThis wizard will guide you through the creation process.\r\n</p>\r\n</html>");
        lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 19));
        lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
        welcomePanel.add(lblNewLabel_2, BorderLayout.CENTER);
        
        txtbKeystorePath = new JTextField();
        txtbKeystorePath.setText("keystore.pfx");
        txtbKeystorePath.setFont(new Font("Tahoma", Font.PLAIN, 14));
        txtbKeystorePath.setColumns(10);
        txtbKeystorePath.setBounds(240, 46, 392, 23);
        selectionPanel.add(txtbKeystorePath);
        
        JLabel lblNewLabel = new JLabel("Keystore path:");
        lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblNewLabel.setBounds(10, 49, 149, 17);
        selectionPanel.add(lblNewLabel);
        
        txtbKnownCerts = new JTextField();
        txtbKnownCerts.setText("knowncerts.pfx");
        txtbKnownCerts.setFont(new Font("Tahoma", Font.PLAIN, 14));
        txtbKnownCerts.setColumns(10);
        txtbKnownCerts.setBounds(240, 78, 392, 23);
        selectionPanel.add(txtbKnownCerts);
        
        JLabel lblKnownCertificatesPath = new JLabel("Known Certificates path:");
        lblKnownCertificatesPath.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblKnownCertificatesPath.setBounds(10, 81, 189, 17);
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
        chckbPkcs11.setBounds(240, 108, 200, 23);
        selectionPanel.add(chckbPkcs11);
        
        btnOpenPKCS11Config = new JButton("...");
        btnOpenPKCS11Config.setEnabled(false);
        btnOpenPKCS11Config.setFont(new Font("Tahoma", Font.PLAIN, 16));
        btnOpenPKCS11Config.setBounds(642, 138, 47, 23);
        selectionPanel.add(btnOpenPKCS11Config);
        
        txtbPKCS11Config = new JTextField();
        txtbPKCS11Config.setEnabled(false);
        txtbPKCS11Config.setFont(new Font("Tahoma", Font.PLAIN, 14));
        txtbPKCS11Config.setColumns(10);
        txtbPKCS11Config.setBounds(240, 138, 392, 23);
        selectionPanel.add(txtbPKCS11Config);
        
        lblPkcsConfigPath = new JLabel("PKCS11 Config path:");
        lblPkcsConfigPath.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblPkcsConfigPath.setBounds(10, 141, 172, 17);
        selectionPanel.add(lblPkcsConfigPath);
        
        JLabel lblNewLabel_1 = new JLabel("<html>\r\nThe personal keystore contains your public/private key pairs. The trusted certificate store contains only public keys.\r\n<br><br>\r\n\r\n<p style=\"color: red\"><b>\r\nPKCS#11 is an advanced feature that requires dedicated hardware and configuration. Do not enable it unless you are familiar with this technology\r\n</p></b>\r\n</html>");
        lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lblNewLabel_1.setVerticalAlignment(SwingConstants.TOP);
        lblNewLabel_1.setHorizontalAlignment(SwingConstants.LEFT);
        lblNewLabel_1.setBounds(20, 303, 659, 135);
        selectionPanel.add(lblNewLabel_1);
        
        cmbHashAlgPath = new JComboBox();
        cmbHashAlgPath.setFont(new Font("Tahoma", Font.PLAIN, 14));
        cmbHashAlgPath.setBounds(240, 219, 242, 25);
        selectionPanel.add(cmbHashAlgPath);
        
        JLabel lblNewLabel_1_1_1 = new JLabel("Default Hash Algorithm:");
        lblNewLabel_1_1_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblNewLabel_1_1_1.setBounds(10, 223, 212, 17);
        selectionPanel.add(lblNewLabel_1_1_1);
        
        cmbEncAlgPath = new JComboBox();
        cmbEncAlgPath.setFont(new Font("Tahoma", Font.PLAIN, 14));
        cmbEncAlgPath.setBounds(240, 183, 242, 25);
        selectionPanel.add(cmbEncAlgPath);
        
        JLabel lblNewLabel_1_2 = new JLabel("Default Encryption Algorithm:");
        lblNewLabel_1_2.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblNewLabel_1_2.setBounds(10, 187, 226, 17);
        selectionPanel.add(lblNewLabel_1_2);
        
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
        
        JLabel lblNewLabel_1_1 = new JLabel("<html>\r\nThis procedure generates a private key and a self-signed X.509 certificate.\r\nThe cryptographic algorithm used is Elliptic Curve Cryptography (ECC) based on the NIST P-384 curve.\r\n<br><br>\r\n<center><b>WARNING</b></center><br />\r\nIf you lose the private key password, the private key cannot be recovered and access to it will be permanently lost. Be sure to store it securely in a safe place.\r\n<br><br>\r\n\r\n<p color=\"red\">\r\nA self-signed certificate is not signed by a recognized Certificate Authority (CA) and, therefore, has no legal value for trustworthy identification by third parties.\r\n</p>\r\n</html>");
        lblNewLabel_1_1.setVerticalAlignment(SwingConstants.TOP);
        lblNewLabel_1_1.setHorizontalAlignment(SwingConstants.LEFT);
        lblNewLabel_1_1.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lblNewLabel_1_1.setBounds(30, 243, 639, 211);
        certPanel.add(lblNewLabel_1_1);
        
        cardPanel.add(welcomePanel, "Step0");
        cardPanel.add(selectionPanel, "Step1");
        cardPanel.add(keystorePanel, "Step2");
        cardPanel.add(certPanel, "Step3");
        
        chckbSkipCert = new JCheckBox("Skip certificate generation");
        chckbSkipCert.setFont(new Font("Tahoma", Font.PLAIN, 16));
        chckbSkipCert.setBounds(45, 196, 241, 23);
        
        chckbSkipCert.addItemListener(e -> {
            txtbCommonName.setEnabled(!chckbSkipCert.isSelected());
            txtbCountryCode.setEnabled(!chckbSkipCert.isSelected());
            txtbStateName.setEnabled(!chckbSkipCert.isSelected());
            spinnerExpDays.setEnabled(!chckbSkipCert.isSelected());
        });
        
        certPanel.add(chckbSkipCert);
        cardPanel.add(finalPanel, "Step4");

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
                currentStep = getPreviousStep(currentStep);
                updateStep();
            }
        });

        btnNext.addActionListener(e -> {

            if (currentStep == 2) {
                if (!validatePasswordStep()) return;
            }

            if (currentStep < 4) {

                int nextStep = getNextStep(currentStep);

                if (nextStep == 4) {
                    if (!applyAndCreate()) return;
                }

                currentStep = nextStep;
                updateStep();

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
    			
				txtbPKCS11Config.setVisible(chckbPkcs11.isSelected());
				lblPkcsConfigPath.setVisible(chckbPkcs11.isSelected());
				btnOpenPKCS11Config.setVisible(chckbPkcs11.isSelected());
        	}
        });
        
        loadSettings();
    }
    
    private int getNextStep(int step) {
        if (step == 1 && chckbPkcs11.isSelected()) return 4;
        return step + 1;
    }

    private int getPreviousStep(int step) {
        if (step == 4 && chckbPkcs11.isSelected()) return 1;
        return step - 1;
    }

    /**
     * Updates the wizard to show the current step and manage button states.
     */
    private void updateStep() {
    	if (currentStep == 2 && chckbPkcs11.isSelected()) {
    	    currentStep = 4;
    	}
        cardLayout.show(cardPanel, "Step" + currentStep);

        txtbCommonName.setEnabled(!chckbPkcs11.isSelected());
        txtbCountryCode.setEnabled(!chckbPkcs11.isSelected());
        txtbStateName.setEnabled(!chckbPkcs11.isSelected());
        spinnerExpDays.setEnabled(!chckbPkcs11.isSelected());

        btnBack.setEnabled(currentStep > 0);

        if (currentStep == 4) {
            btnNext.setText("Finish");
        } else {
            btnNext.setText("Next");
        }
    }
    
    private boolean validatePasswordStep() {
        char[] p1 = txtPwd.getPassword();
        char[] p2 = txtPwdConfirm.getPassword();

        if (p1.length == 0) {
            DialogUtils.showMessageBox(
                    null,
                    "Invalid keystore password!",
                    "Password cannot be empty",
                    "",
                    JOptionPane.ERROR_MESSAGE
                );
            return false;
        }

        if (!Arrays.areEqual(p1, p2)) {
            DialogUtils.showMessageBox(
                    null,
                    "Invalid keystore password!",
                    "Passwords do not match",
                    "",
                    JOptionPane.ERROR_MESSAGE
                );
            return false;
        }

        keystorePassword = p1.clone();
        return true;
    }
    
    /**
     * Applies the configuration settings and creates the necessary certificates and keystores.
     */
    private boolean applyAndCreate() {
        ctx.setKeyStorePath(txtbKeystorePath.getText());
        ctx.setPkcs11Driver(txtbPKCS11Config.getText());
        ctx.setKnownCertsPath(txtbKnownCerts.getText());
        ctx.setUsePkcs11(chckbPkcs11.isSelected());
        ctx.setHashAlgorithm((HashAlgorithm) cmbHashAlgPath.getSelectedItem());
        ctx.setCipher((SymmetricAlgorithms) cmbEncAlgPath.getSelectedItem());
        try {
            if (!chckbPkcs11.isSelected()) {
                initializePersonalKeystore();
                if (!chckbSkipCert.isSelected()) {
                    // Fields not valid
                    if (!checkCertFields()) return false;

                    // Try certificate creation
                    return createX509Cert();
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                frame,
                "Error: " + e.getMessage(),
                "Setup failed",
                JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
            return false; // do not proceed
        }

        return true; // all good
    }

    /**
     * Validates the certificate fields.
     * @return true if all fields are filled and valid, false otherwise
     */
    private boolean checkCertFields() {
        String name = txtbCommonName.getText();
        String cc   = txtbCountryCode.getText();
        String state = txtbStateName.getText();

        if (name.isBlank() || cc.isBlank() || state.isBlank()) {
            DialogUtils.showMessageBox(
                    null,
                    "Invalid parameters!",
                    "All fields must be filled in!",
                    "",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }

        if (cc.length() != 2) {
            DialogUtils.showMessageBox(
                    null,
                    "Invalid parameters!",
                    "Invalid country code!",
                    "Country code must have 2 letters!",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }

        return true;
    }

    /**
     * Creates an X509 certificate using values from the UI form.
     * Prompts the user for alias and private key password.
     * @return true if the certificate was created and saved successfully, false otherwise
     */
    private boolean createX509Cert() {
        String name    = txtbCommonName.getText();
        String cc      = txtbCountryCode.getText();
        String state   = txtbStateName.getText();
        int    expDays = (int) spinnerExpDays.getValue();

        char[] ksMaster = null;
        char[] pwd      = null;
        try {
            String alias = askAlias();
            if (alias == null || alias.isBlank()) return false;

            ksMaster = askKeystorePassword();
            pwd      = askPrivateKeyPassword();
            if (pwd == null) return false;

            Security.addProvider(new BouncyCastleProvider());

            KeyPair      keyPair = X509Builder.generateECKeyPair("secp384r1");
            PrivateKey   privk   = keyPair.getPrivate();
            PublicKey    pubk    = keyPair.getPublic();

            X509Certificate crt = X509Builder.buildCertificate(name, cc, state, expDays, pubk, privk);

            saveCertificateToKeystore(ksMaster, alias, pwd, privk, crt);

            DialogUtils.showMessageBox(
                    null,
                    "Certificate generated",
                    "Certificate successfully generated!",
                    "Alias: " + alias,
                    JOptionPane.INFORMATION_MESSAGE
            );
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            DialogUtils.showMessageBox(
                    null,
                    "Error during certificate generation!",
                    "Error during certificate generation!",
                    e.getMessage(),
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        } finally {
            if (ksMaster != null)        Arrays.fill(ksMaster, (char) 0x00);
            if (pwd != null)             Arrays.fill(pwd,      (char) 0x00);
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
		txtbPKCS11Config.setVisible(chckbPkcs11.isSelected());
		lblPkcsConfigPath.setVisible(chckbPkcs11.isSelected());
		btnOpenPKCS11Config.setVisible(chckbPkcs11.isSelected());
		
        loadEncAlgos();
        loadHashAlgo();
        txtbKeystorePath.setText(ApplicationPreferences.KEYSTORE_PATH.getValue());
        txtbKnownCerts.setText(ApplicationPreferences.KNOWN_CERTS_PATH.getValue());
        txtbPKCS11Config.setText(ApplicationPreferences.PKCS11_DRIVER.getValue());
        
        chckbPkcs11.setSelected(Boolean.valueOf(ApplicationPreferences.USE_PKCS11.getValue()));
        
        cmbEncAlgPath.setSelectedItem(SymmetricAlgorithms.fromString(ApplicationPreferences.CIPHER.getValue()));
        cmbHashAlgPath.setSelectedItem(HashAlgorithm.fromString(ApplicationPreferences.HASH_ALGORITHM.getValue()));
    }

    /**
     * Initializes the personal keystore if it doesn't exist.
     * Prompts the user for a master password and creates a new PKCS12 keystore.
     * Displays error messages if the keystore creation fails.
     * @throws Exception 
     */
    private void initializePersonalKeystore() throws Exception {
    	ensureDirectoryExists(txtbKeystorePath.getText());
        File keystore = new File(txtbKeystorePath.getText());
        if(!keystore.exists()) {
            char[] pwd = askKeystorePassword();
            AbstractKeystore pkcs12 = new PKCS12Keystore(keystore, pwd);
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
            }finally {
            	if(pwd != null) Arrays.fill(pwd, (char)0x00);
            }
        }
    }
    
    /**
     * Ensures that the specified directory path exists. If the directory does not exist,
     * it attempts to create it, including any necessary parent directories.
     *
     * @param path the file system path to the directory that should exist
     * @throws Exception if the directory cannot be created
     */
    private void ensureDirectoryExists(String path) throws Exception {
        File f = new File(path);

        // Check if the directory exists
        if (!f.exists()) {
            // If it doesn't, create the parent directories
            File parentDir = f.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                boolean dirsCreated = parentDir.mkdirs(); // Create all necessary parent directories
                if (!dirsCreated) {
                    throw new Exception("Unable to create the directory: " + parentDir.getAbsolutePath());
                }
            }
        }
    }

    /**
     * Prompts the user for the keystore master password.
     * @throws Exception 
     */
    private char[] askKeystorePassword() throws Exception {
       return Arrays.copyOf(keystorePassword, keystorePassword.length);
    }

    /**
     * Prompts the user for the alias of the private key.
     */
    private String askAlias() {
        return DialogUtils.showInputBox(
            null,
            "Private key alias",
            "Private key alias",
            "Alias:"
        );
    }

    /**
     * Prompts the user for the private key password, retrying on empty or mismatched input.
     */
    private char[] askPrivateKeyPassword() {
        while (true) {
            char[] p1 = DialogUtils.showPasswordInputBox(
                null,
                "Private key password",
                "Private key password",
                "Password:"
            );

            if (p1 == null) return null; // user cancelled

            if (p1.length == 0) {
                DialogUtils.showMessageBox(
                    null,
                    "Invalid private key password!",
                    "Password cannot be empty",
                    "",
                    JOptionPane.ERROR_MESSAGE
                );
                continue; // retry
            }

            char[] p2 = DialogUtils.showPasswordInputBox(
                null,
                "Private key password",
                "Confirm private key password",
                "Password:"
            );

            if (p2 == null) return null; // user cancelled

            if (!Arrays.areEqual(p1, p2)) {
                Arrays.fill(p1, (char) 0x00);
                Arrays.fill(p2, (char) 0x00);
                DialogUtils.showMessageBox(
                    null,
                    "Invalid private key password!",
                    "Passwords do not match",
                    "",
                    JOptionPane.ERROR_MESSAGE
                );
                continue; // retry
            }

            Arrays.fill(p2, (char) 0x00);
            return p1.clone(); 
        }
    }

    /**
     * Saves the private key and certificate into the keystore.
     */
    private void saveCertificateToKeystore(
            char[] ksMaster,
            String alias,
            char[] pwd,
            PrivateKey privk,
            X509Certificate crt) throws Exception {

        ctx.loadKeystore(ksMaster);
        ctx.getKeystore().addPrivateKey(alias, privk, pwd, new X509Certificate[]{crt});
        ctx.getKeystore().save();
    }
    
	/**
	 * Populates a combo box with all available symmetric algorithms.
	 */
	private void loadEncAlgos() {
		cmbEncAlgPath.removeAllItems();
	    for (SymmetricAlgorithms alg : SymmetricAlgorithms.values()) {
	    	cmbEncAlgPath.addItem(alg);
	    }
	}
	
	/**
	 * Populates a combo box with all available hash algorithms.
	 */
	private void loadHashAlgo() {
		cmbHashAlgPath.removeAllItems();
	    for (HashAlgorithm alg : HashAlgorithm.values()) {
	    	cmbHashAlgPath.addItem(alg);
	    }
	}
}
