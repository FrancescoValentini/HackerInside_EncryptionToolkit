package it.hackerinside.etk.GUI.forms;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.BorderLayout;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import it.hackerinside.etk.GUI.DialogUtils;
import it.hackerinside.etk.GUI.ETKContext;
import it.hackerinside.etk.GUI.FileDialogUtils;
import it.hackerinside.etk.GUI.TimeUtils;
import it.hackerinside.etk.GUI.DTOs.CertificateWrapper;
import it.hackerinside.etk.Utils.X509CertificateLoader;
import it.hackerinside.etk.core.Encryption.CMSEncryptor;
import it.hackerinside.etk.core.Models.DefaultExtensions;
import it.hackerinside.etk.core.Models.EncodingOption;
import it.hackerinside.etk.core.Models.SymmetricAlgorithms;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import javax.swing.JCheckBox;
import javax.swing.border.EtchedBorder;
import java.awt.Color;
import javax.swing.JProgressBar;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Toolkit;

public class EncryptForm {

	private JFrame frmEncrypt;
	private JTextField txtbCertFile;
	private JTextField txtbOutputFile;
	private JComboBox cmbEncAlgorithm;
	private JCheckBox chckbPemOutput;
	private JProgressBar progressBarEncrypt;
    private long startTime;
    private long endTime;
	
	private File plaintextFile;
	private File ciphertextFile;
	private X509Certificate recipient;
	private JLabel lblStatus;
	private static ETKContext ctx;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					EncryptForm window = new EncryptForm();
					window.frmEncrypt.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public EncryptForm() {
		ctx = ETKContext.getInstance();
		initialize();
	}
	
	public void setVisible() {
		frmEncrypt.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmEncrypt = new JFrame();
		frmEncrypt.setIconImage(Toolkit.getDefaultToolkit().getImage(EncryptForm.class.getResource("/it/hackerinside/etk/GUI/icons/encrypt.png")));
		frmEncrypt.setResizable(false);
		frmEncrypt.setTitle("HackerInside Encryption Toolkit | Encrypt");
		frmEncrypt.setBounds(100, 100, 593, 715);
		
		JLabel lblNewLabel = new JLabel("ENCRYPT");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 27));
		frmEncrypt.getContentPane().add(lblNewLabel, BorderLayout.NORTH);
		
		JPanel panel = new JPanel();
		frmEncrypt.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		JLabel lblNewLabel_1 = new JLabel("Recipient Certificate");
		lblNewLabel_1.setBounds(10, 28, 151, 20);
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panel.add(lblNewLabel_1);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setFont(new Font("Tahoma", Font.PLAIN, 16));
		tabbedPane.setBounds(10, 59, 557, 84);
		panel.add(tabbedPane);
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Known Certificates", null, panel_1, null);
		panel_1.setLayout(null);
		
		JComboBox cmbRecipientCert = new JComboBox();
		cmbRecipientCert.setFont(new Font("Tahoma", Font.PLAIN, 16));
		cmbRecipientCert.setBounds(10, 11, 414, 28);
		panel_1.add(cmbRecipientCert);
		
		JButton btnCertDetails = new JButton("DETAILS");
		btnCertDetails.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnCertDetails.setBounds(434, 11, 108, 28);
		panel_1.add(btnCertDetails);
		
		JPanel panel_2 = new JPanel();
		tabbedPane.addTab("File", null, panel_2, null);
		panel_2.setLayout(null);
		
		JButton btnOpenCertFile = new JButton("...");
		btnOpenCertFile.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnOpenCertFile.setBounds(481, 12, 61, 25);
		panel_2.add(btnOpenCertFile);
		
		txtbCertFile = new JTextField();
		txtbCertFile.setFont(new Font("Tahoma", Font.PLAIN, 16));
		txtbCertFile.setColumns(10);
		txtbCertFile.setBounds(10, 11, 448, 26);
		panel_2.add(txtbCertFile);
		
		JLabel lblInputFile = new JLabel("Output File");
		lblInputFile.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblInputFile.setBounds(10, 154, 120, 20);
		panel.add(lblInputFile);
		
		txtbOutputFile = new JTextField();
		txtbOutputFile.setFont(new Font("Tahoma", Font.PLAIN, 16));
		txtbOutputFile.setColumns(10);
		txtbOutputFile.setBounds(10, 185, 448, 26);
		panel.add(txtbOutputFile);
		
		JButton btnOpenOutputFile = new JButton("...");

		btnOpenOutputFile.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnOpenOutputFile.setBounds(476, 185, 85, 25);
		panel.add(btnOpenOutputFile);
		
		JPanel panel_1_1 = new JPanel();
		panel_1_1.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panel_1_1.setLayout(null);
		panel_1_1.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "Encryption Settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1_1.setBounds(10, 233, 551, 68);
		panel.add(panel_1_1);
		
		JLabel lblEncryptionAlgorithm = new JLabel("Encryption Algorithm:");
		lblEncryptionAlgorithm.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblEncryptionAlgorithm.setBounds(10, 29, 173, 20);
		panel_1_1.add(lblEncryptionAlgorithm);
		
		cmbEncAlgorithm = new JComboBox();
		cmbEncAlgorithm.setFont(new Font("Tahoma", Font.PLAIN, 16));
		cmbEncAlgorithm.setBounds(176, 25, 214, 28);
		panel_1_1.add(cmbEncAlgorithm);
		
		chckbPemOutput = new JCheckBox("PEM output");
		chckbPemOutput.setFont(new Font("Tahoma", Font.PLAIN, 18));
		chckbPemOutput.setBounds(396, 27, 133, 23);
		panel_1_1.add(chckbPemOutput);
		
		progressBarEncrypt = new JProgressBar();
		progressBarEncrypt.setIndeterminate(true);
		progressBarEncrypt.setFont(new Font("Tahoma", Font.PLAIN, 16));
		progressBarEncrypt.setEnabled(false);
		progressBarEncrypt.setBounds(141, 446, 295, 14);
		progressBarEncrypt.setVisible(false);
		
		panel.add(progressBarEncrypt);
		
		JButton btnEncrypt = new JButton("ENCRYPT");
		btnEncrypt.setFont(new Font("Tahoma", Font.BOLD, 18));
		btnEncrypt.setBounds(219, 350, 138, 55);
		panel.add(btnEncrypt);
		
		lblStatus = new JLabel("");
		lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
		lblStatus.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblStatus.setBounds(155, 508, 267, 20);
		panel.add(lblStatus);
		
		// Button Actions
		btnCertDetails.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showRecipientDetails();
			}
		});
		
		btnOpenCertFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadCertificateFromFile();
			}
		});
		
		btnEncrypt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				encryptFile();
			}
		});
		
		btnOpenOutputFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				outputFileSelection();
			}
		});
		
		cmbRecipientCert.addActionListener(e -> {
			CertificateWrapper selected = (CertificateWrapper) cmbRecipientCert.getSelectedItem();
			if (selected != null) {
				try {
					this.recipient = selected.getKeystore().getCertificate(selected.getAlias());
				} catch (KeyStoreException e1) {
					e1.printStackTrace();
				}
			}
		});

		populateSymmetricAlgorithms(cmbEncAlgorithm);
		populateKnowCerts(cmbRecipientCert);
		this.chckbPemOutput.setSelected(ctx.usePEM());
		this.cmbEncAlgorithm.setSelectedItem(ctx.getCipher());
		

		
		if(this.plaintextFile == null) fileInitialization();
	}

	
	/**
	 * Populates a combo box with all available symmetric algorithms.
	 * 
	 * @param combo the combo box to populate with symmetric algorithm values
	 */
	private void populateSymmetricAlgorithms(JComboBox<SymmetricAlgorithms> combo) {
	    combo.removeAllItems();
	    for (SymmetricAlgorithms alg : SymmetricAlgorithms.values()) {
	        combo.addItem(alg);
	    }
	}

	/**
	 * Populates a combo box with certificates from both the personal keystore and known certificates store.
	 * Includes a null option for empty selection.
	 * 
	 * @param combo the combo box to populate with certificate wrappers
	 */
	private void populateKnowCerts(JComboBox<CertificateWrapper> combo) {
	    Enumeration<String> knownCerts = null;
	    Enumeration<String> personalCerts = null;
	    try {
	        if (ctx.getKnownCerts() != null) knownCerts = ctx.getKnownCerts().listAliases();
	        if (ctx.getKeystore() != null) personalCerts = ctx.getKeystore().listAliases();
	    } catch (KeyStoreException e) {
	        e.printStackTrace();
	    }
	    
	    combo.removeAllItems();
	    combo.addItem(null);
	    
	    if (personalCerts != null) {
	        while (personalCerts.hasMoreElements()) {
	            String alias = personalCerts.nextElement();
	            combo.addItem(new CertificateWrapper(alias, ctx.getKeystore()));
	        }
	    }
	    
	    if (knownCerts != null) {
	        while (knownCerts.hasMoreElements()) {
	            String alias = knownCerts.nextElement();
	            combo.addItem(new CertificateWrapper(alias, ctx.getKnownCerts()));
	        }
	    }
	}

	/**
	 * Initializes the file selection process for encryption.
	 * Selects an input file and creates a default output path.
	 */
	private void fileInitialization() {
	    this.plaintextFile = selectInputFile();
	    if (this.plaintextFile != null) {
	        createOutputFilePath();
	    }else {
	        SwingUtilities.invokeLater(() -> {
	            frmEncrypt.dispose();
	        });
	    }
	}

	/**
	 * Opens a file dialog to select an input file for encryption.
	 * 
	 * @return the selected file, or null if no file was selected
	 */
	private File selectInputFile() {
	    return FileDialogUtils.openFileDialog(
	            null,
	            "Select the file to encrypt",
	            "."
	    );
	}

	/**
	 * Opens a file dialog to select an output file for the encrypted result.
	 */
	private void outputFileSelection() {
	    File file = FileDialogUtils.saveFileDialog(
	            null,
	            "Encrypted file",
	            this.plaintextFile != null ? this.plaintextFile.getAbsolutePath() : ".",
	            DefaultExtensions.CRYPTO_P7E
	    );
	    if (file != null) {
	        setOutputFile(file);
	    }
	}

	/**
	 * Sets the output file and updates the corresponding text field.
	 * 
	 * @param file the output file to set
	 */
	private void setOutputFile(File file) {
	    this.ciphertextFile = file;
	    txtbOutputFile.setText(file.getAbsolutePath());
	}

	/**
	 * Creates a default output file path by applying the cryptographic extension to the input file.
	 */
	private void createOutputFilePath() {
	    File file = DefaultExtensions.applyExtension(plaintextFile, DefaultExtensions.CRYPTO_P7E);
	    setOutputFile(file);
	}

	/**
	 * Loads a recipient certificate from a file selected via file dialog.
	 */
	private void loadCertificateFromFile() {
	    File certFile = FileDialogUtils.openFileDialog(
	            null,
	            "Select recipient certificate",
	            ".",
	            DefaultExtensions.CRYPTO_PEM,
	            DefaultExtensions.CRYPTO_CER,
	            DefaultExtensions.CRYPTO_CRT,
	            DefaultExtensions.CRYPTO_DER,
	            DefaultExtensions.STD_ANY
	    );

	    if (certFile != null) {
	        txtbCertFile.setText(certFile.getAbsolutePath());
	        this.recipient = loadX509Certificate(certFile);
	    }
	}

	/**
	 * Loads an X.509 certificate from the specified file.
	 * 
	 * @param certFile the file containing the certificate
	 * @return the loaded X509Certificate, or null if loading failed
	 */
	private X509Certificate loadX509Certificate(File certFile) {
	    try {
	        return X509CertificateLoader.loadFromFile(certFile);
	    } catch (CertificateException | IOException e) {
	        e.printStackTrace();
	        lblStatus.setText("Invalid certificate.");
			DialogUtils.showMessageBox(null, "Invalid certificate", "Invalid certificate!", 
			        e.getMessage(), 
			        JOptionPane.ERROR_MESSAGE);
	        return null;
	    }
	}

	/**
	 * Displays details of the currently selected recipient certificate.
	 */
	private void showRecipientDetails() {
	    if (this.recipient != null) {
	        new CertificateDetailsForm(this.recipient);
	    } else {
	        lblStatus.setText("No certificate selected.");
	    }
	}

	/**
	 * Initiates the file encryption process using the selected parameters.
	 */
	private void encryptFile() {
	    if (!allReady()) {
	        lblStatus.setText("Missing required inputs.");
	        return;
	    }

	    startEncryptionUI();

	    SymmetricAlgorithms cipher = (SymmetricAlgorithms) cmbEncAlgorithm.getSelectedItem();
	    EncodingOption encoding = chckbPemOutput.isSelected()
	            ? EncodingOption.ENCODING_PEM
	            : EncodingOption.ENCODING_DER;
	    File cipherFile = new File(txtbOutputFile.getText());

	    CMSEncryptor encryptor = new CMSEncryptor(recipient, cipher, encoding);

	    SwingWorker<Void, Void> worker = new SwingWorker<>() {
	        @Override
	        protected Void doInBackground() throws Exception {
	        	startTime = System.currentTimeMillis();
	            encryptor.encrypt(plaintextFile, cipherFile);
	            return null;
	        }

	        @Override
	        protected void done() {
	            finishEncryptionUI(this);
	        }
	    };

	    worker.execute();
	}

	/**
	 * Updates the UI to indicate that encryption is in progress.
	 */
	private void startEncryptionUI() {
	    progressBarEncrypt.setVisible(true);
	    progressBarEncrypt.setEnabled(true);
	    lblStatus.setText("Encrypting...");
	    lblStatus.setVisible(true);
	}

	/**
	 * Finalizes the UI after encryption completes or fails.
	 * 
	 * @param worker the SwingWorker that performed the encryption
	 */
	private void finishEncryptionUI(SwingWorker<?, ?> worker) {
	    progressBarEncrypt.setVisible(false);
	    endTime = System.currentTimeMillis();
	    try {
	        worker.get();
	        lblStatus.setText("File Encrypted!");
			DialogUtils.showMessageBox(null, "File encrypted!", "File encrypted!", 
			        "File Encrypted\nFor: "+ this.recipient.getSubjectX500Principal().getName("RFC2253")  + "\n\nSaved to: " 
			        		+ this.ciphertextFile.getAbsolutePath().toString() +
			        		"\n\nElapsed: " + TimeUtils.formatElapsedTime(startTime, endTime), 
			        JOptionPane.INFORMATION_MESSAGE);
	    } catch (InterruptedException | ExecutionException e) {
			DialogUtils.showMessageBox(null, "Error during encryption", "Error during encryption!", 
			        e.getMessage(), 
			        JOptionPane.ERROR_MESSAGE);
	        lblStatus.setText("Encryption failed.");
	        e.printStackTrace();
	    }
	}

	/**
	 * Checks if all required inputs are ready for encryption.
	 * 
	 * @return true if all required inputs are present and valid, false otherwise
	 */
	private boolean allReady() {
	    return this.recipient != null &&
	           this.plaintextFile != null &&
	           this.plaintextFile.exists() &&
	           !this.txtbOutputFile.getText().isEmpty();
	}
}
