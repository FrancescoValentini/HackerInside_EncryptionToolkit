package it.hackerinside.etk.GUI.forms;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;

import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import it.hackerinside.etk.GUI.DialogUtils;
import it.hackerinside.etk.GUI.ETKContext;
import it.hackerinside.etk.GUI.FileDialogUtils;
import it.hackerinside.etk.GUI.TimeUtils;
import it.hackerinside.etk.GUI.Utils;
import it.hackerinside.etk.GUI.DTOs.CertificateTableRow;
import it.hackerinside.etk.GUI.DTOs.CertificateWrapper;
import it.hackerinside.etk.Utils.X509CertificateLoader;
import it.hackerinside.etk.Utils.X509Utils;
import it.hackerinside.etk.core.Encryption.CMSEncryptor;
import it.hackerinside.etk.core.Models.DefaultExtensions;
import it.hackerinside.etk.core.Models.EncodingOption;
import it.hackerinside.etk.core.Models.SymmetricAlgorithms;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JComboBox;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;

import javax.swing.JCheckBox;
import javax.swing.border.EtchedBorder;
import java.awt.Color;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;

import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
    private JList<CertificateTableRow> recipientsList;
    private DefaultListModel<CertificateTableRow> listModel = new DefaultListModel<>();
    private List<X509Certificate> recipients;
    private boolean running = false;
    private SwingWorker<Void, Void> currentWorker;
    
	
	private File plaintextFile;
	private File ciphertextFile;
	private X509Certificate recipient;
	private JLabel lblStatus;
	private JButton btnEncrypt;
	private JCheckBox chckbxUseSki;
	private CMSEncryptor encryptor;
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
		recipients = new ArrayList<>();
		frmEncrypt = new JFrame();
		frmEncrypt.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if(running) abortEncryption();
			}
		});
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
		lblNewLabel_1.setBounds(10, 28, 557, 20);
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
		lblInputFile.setBounds(10, 285, 541, 20);
		panel.add(lblInputFile);
		
		txtbOutputFile = new JTextField();
		txtbOutputFile.setFont(new Font("Tahoma", Font.PLAIN, 16));
		txtbOutputFile.setColumns(10);
		txtbOutputFile.setBounds(10, 316, 448, 26);
		panel.add(txtbOutputFile);
		
		JButton btnOpenOutputFile = new JButton("...");

		btnOpenOutputFile.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnOpenOutputFile.setBounds(476, 316, 85, 26);
		panel.add(btnOpenOutputFile);
		
		JPanel panel_1_1 = new JPanel();
		panel_1_1.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panel_1_1.setLayout(null);
		panel_1_1.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "Encryption Settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1_1.setBounds(10, 364, 551, 112);
		panel.add(panel_1_1);
		
		JLabel lblEncryptionAlgorithm = new JLabel("Encryption Algorithm:");
		lblEncryptionAlgorithm.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblEncryptionAlgorithm.setBounds(10, 29, 173, 20);
		panel_1_1.add(lblEncryptionAlgorithm);
		
		cmbEncAlgorithm = new JComboBox();
		cmbEncAlgorithm.setFont(new Font("Tahoma", Font.PLAIN, 16));
		cmbEncAlgorithm.setBounds(192, 25, 214, 28);
		panel_1_1.add(cmbEncAlgorithm);
		
		chckbPemOutput = new JCheckBox("PEM output");
		chckbPemOutput.setFont(new Font("Tahoma", Font.PLAIN, 18));
		chckbPemOutput.setBounds(10, 67, 133, 23);
		panel_1_1.add(chckbPemOutput);
		
		progressBarEncrypt = new JProgressBar();
		progressBarEncrypt.setIndeterminate(true);
		progressBarEncrypt.setFont(new Font("Tahoma", Font.PLAIN, 16));
		progressBarEncrypt.setEnabled(false);
		progressBarEncrypt.setBounds(141, 571, 295, 14);
		progressBarEncrypt.setVisible(false);
		
		panel.add(progressBarEncrypt);
		
		btnEncrypt = new JButton("ENCRYPT");
		btnEncrypt.setFont(new Font("Tahoma", Font.BOLD, 18));
		btnEncrypt.setBounds(218, 505, 138, 55);
		panel.add(btnEncrypt);
		
		lblStatus = new JLabel("");
		lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
		lblStatus.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblStatus.setBounds(155, 596, 267, 20);
		panel.add(lblStatus);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "Recipients", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_3.setBounds(10, 154, 557, 127);
		panel.add(panel_3);
		panel_3.setLayout(new BorderLayout(0, 0));

		recipientsList = new JList<>(listModel);
		recipientsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollPane = new JScrollPane(recipientsList);
		panel_3.add(scrollPane, BorderLayout.CENTER);

		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new GridLayout(3, 1, 5, 5));
		panel_3.add(buttonsPanel, BorderLayout.EAST);

		JButton btnAddRecipient = new JButton("+");

		btnAddRecipient.setFont(new Font("Tahoma", Font.PLAIN, 14));
		JButton btnRemoveRecipient = new JButton("-");

		btnRemoveRecipient.setFont(new Font("Tahoma", Font.PLAIN, 14));
		JButton btnRecipientInfo = new JButton("Info");

		btnRecipientInfo.setFont(new Font("Tahoma", Font.PLAIN, 14));

		buttonsPanel.add(btnAddRecipient);
		buttonsPanel.add(btnRemoveRecipient);
		buttonsPanel.add(btnRecipientInfo);
		
		chckbxUseSki = new JCheckBox("Use SKI");
		chckbxUseSki.setSelected(false);
		chckbxUseSki.setFont(new Font("Tahoma", Font.PLAIN, 18));
		chckbxUseSki.setBounds(145, 67, 133, 23);
		panel_1_1.add(chckbxUseSki);
		
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
				if(!running) {
					encryptFile();
				}else {
					if(DialogUtils.showConfirmBox(null,
							"Abort?", 
							"Are you sure you want to cancel the operation?", 
							"Press OK to abort encryption", 
							JOptionPane.QUESTION_MESSAGE)) {
						abortEncryption();
					}
					
				}
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

		btnAddRecipient.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(recipient != null && Utils.acceptX509Certificate(recipient)) {
					recipients.add(recipient);
					Object selectedCert = cmbRecipientCert.getSelectedItem();
					if(selectedCert != null) {
						listModel.addElement(new CertificateTableRow(((CertificateWrapper) selectedCert).getAlias(), null, recipient));
						cmbRecipientCert.setSelectedItem(null);
					}else {
						listModel.addElement(new CertificateTableRow("file", null, recipient));
					}
				}
			}
		});
		
		btnRemoveRecipient.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CertificateTableRow selected = recipientsList.getSelectedValue();
				if(selected != null) {
					recipients.remove(selected.original());
					listModel.removeElement(selected);
				}
			}
		});
		
		btnRecipientInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CertificateTableRow selected = recipientsList.getSelectedValue();
				if(selected != null) showRecipientDetails(selected.original());
			}
		});
		
		populateSymmetricAlgorithms(cmbEncAlgorithm);
		populateKnowCerts(cmbRecipientCert);
		this.chckbPemOutput.setSelected(ctx.usePEM());
		this.cmbEncAlgorithm.setSelectedItem(ctx.getCipher());
		this.chckbxUseSki.setSelected(ctx.useSKI());
		
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
	    combo.removeAllItems();
	    combo.addItem(null);

	    try {
	        Predicate<X509Certificate> personalCertPredicate = cert -> {
	            String alg = cert.getPublicKey().getAlgorithm();
	            boolean validCert = ctx.hideInvalidCerts() ? X509Utils.checkTimeValidity(cert) : true;

	            boolean isDSA = alg != null && alg.toUpperCase().contains("DSA");
	            boolean hideECC = ctx.usePKCS11() && !ctx.isPkcs11SignOnly()
	                              && alg != null && alg.toUpperCase().contains("EC");

	            return alg != null && validCert && !isDSA && !hideECC;
	        };

	        
	        Predicate<X509Certificate> knownCertPredicate = cert -> {
	            String alg = cert.getPublicKey().getAlgorithm();
	            boolean validCert = ctx.hideInvalidCerts() ? X509Utils.checkTimeValidity(cert) : true;
	            boolean isDSA = alg != null && alg.toUpperCase().contains("DSA");

	            return alg != null && validCert && !isDSA;
	        };

	        // PERSONAL CERTS
	        if (ctx.getKeystore() != null) {
	            ctx.getKeystore()
	               .listAliases(personalCertPredicate)
	               .forEach(alias -> combo.addItem(new CertificateWrapper(alias, ctx.getKeystore())));
	        }

	        // KNOWN CERTS
	        if (ctx.getKnownCerts() != null) {
	            ctx.getKnownCerts()
	               .listAliases(knownCertPredicate)
	               .forEach(alias -> combo.addItem(new CertificateWrapper(alias, ctx.getKnownCerts())));
	        }

	    } catch (KeyStoreException e) {
	        e.printStackTrace();
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
	            DefaultExtensions.CRYPTO_DER
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
		showRecipientDetails(this.recipient);
	}
	
	private void showRecipientDetails(X509Certificate crt) {
	    if (crt != null) {
	        new CertificateDetailsForm(crt);
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

	    SymmetricAlgorithms cipher = (SymmetricAlgorithms) cmbEncAlgorithm.getSelectedItem();
	    EncodingOption encoding = chckbPemOutput.isSelected()
	            ? EncodingOption.ENCODING_PEM
	            : EncodingOption.ENCODING_DER;
	    File cipherFile = new File(txtbOutputFile.getText());
	    if(!FileDialogUtils.overwriteIfExists(cipherFile)) return;
	    
	    startEncryptionUI();
	    encryptor = new CMSEncryptor(cipher, encoding,ctx.getBufferSize());
	    
	    recipients.forEach(encryptor::addRecipients); // Add recipients
	    encryptor.setUseOnlySKI(chckbxUseSki.isSelected());
	    encryptor.setUseOAEP(ctx.useRsaOaep());
	    
	    running = true;
	    btnEncrypt.setText("ABORT"); 
	    
	    currentWorker = new SwingWorker<>() {
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

	    currentWorker.execute();
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
	
	private void abortEncryption() {
		encryptor.abort();
	    if (currentWorker != null && !currentWorker.isDone()) {
	        currentWorker.cancel(true);
	        lblStatus.setText("Encryption aborted.");
	    }
	    running = false;
	    btnEncrypt.setText("Encrypt");
	    progressBarEncrypt.setVisible(false);
	}


	/**
	 * Finalizes the UI after encryption completes or fails.
	 * 
	 * @param worker the SwingWorker that performed the encryption
	 */
	private void finishEncryptionUI(SwingWorker<?, ?> worker) {
	    progressBarEncrypt.setVisible(false);
	    running = false;
	    btnEncrypt.setText("ENCRYPT");
	    
	    endTime = System.currentTimeMillis();
	    try {
	        worker.get();
	        lblStatus.setText("File Encrypted!");
	        
			DialogUtils.showMessageBox(
					null, 
					"File encrypted!", 
					"File encrypted!", 
					getOkMessage(), 
			        JOptionPane.INFORMATION_MESSAGE);
			
	    } catch (InterruptedException | ExecutionException e) {
			DialogUtils.showMessageBox(null, "Error during encryption", "Error during encryption!", 
			        e.getMessage(), 
			        JOptionPane.ERROR_MESSAGE);
	        lblStatus.setText("Encryption failed.");
	        e.printStackTrace();
	    }
	    btnEncrypt.setEnabled(true);
	}
	
	/**
	 * Builds the confirmation message
	 * 
	 * @return the message formatted correctly
	 */
	private String getOkMessage() {
        StringBuilder okMessage = new StringBuilder();
        okMessage.append("File Encrypted\n");
        okMessage.append("\n\nElapsed: " + TimeUtils.formatElapsedTime(startTime, endTime));
        okMessage.append("\n\n\nRecipients: \n");
        recipients.forEach(recipient -> {
        	okMessage.append("- " + X509Utils.getPrettySubject(recipient.getSubjectX500Principal().getEncoded()) + "\n");
        });
       
        return okMessage.toString();
	}

	/**
	 * Checks if all required inputs are ready for encryption.
	 * 
	 * @return true if all required inputs are present and valid, false otherwise
	 */
	private boolean allReady() {
	    return this.recipients.size() > 0 &&
	           this.plaintextFile != null &&
	           this.plaintextFile.exists() &&
	           !this.txtbOutputFile.getText().isEmpty();
	}
}
