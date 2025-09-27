package it.hackerinside.etk.GUI.forms;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.BorderLayout;
import java.awt.Font;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.JPanel;
import javax.swing.ComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JTextField;

import it.hackerinside.etk.GUI.DialogUtils;
import it.hackerinside.etk.GUI.ETKContext;
import it.hackerinside.etk.GUI.FileDialogUtils;
import it.hackerinside.etk.core.Encryption.CMSCryptoUtils;
import it.hackerinside.etk.core.Encryption.CMSDecryptor;
import it.hackerinside.etk.core.Models.DefaultExtensions;
import it.hackerinside.etk.core.Models.EncodingOption;
import it.hackerinside.etk.core.Models.RecipientIdentifier;
import it.hackerinside.etk.core.PEM.PEMUtils;

import javax.swing.JProgressBar;
import java.awt.event.ActionListener;
import java.io.File;
import java.awt.event.ActionEvent;
import java.awt.Toolkit;

public class DecryptForm {

	private JFrame frmHackerinsideEncryptionToolkit;
	private JTextField txtbOutputFile;
	private JComboBox cmbPrivateKey;
	private JProgressBar progressBar;
	private static ETKContext ctx;
	private File fileToDecrypt;
	private JLabel lblStatus;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DecryptForm window = new DecryptForm();
					window.frmHackerinsideEncryptionToolkit.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public DecryptForm() {
		ctx = ETKContext.getInstance();
		initialize();
	}
	
	public void setVisible() {
		this.frmHackerinsideEncryptionToolkit.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmHackerinsideEncryptionToolkit = new JFrame();
		frmHackerinsideEncryptionToolkit.setIconImage(Toolkit.getDefaultToolkit().getImage(DecryptForm.class.getResource("/it/hackerinside/etk/GUI/icons/decrypt.png")));
		frmHackerinsideEncryptionToolkit.setTitle("HackerInside Encryption Toolkit | Decrypt");
		frmHackerinsideEncryptionToolkit.setResizable(false);
		frmHackerinsideEncryptionToolkit.setBounds(100, 100, 593, 550);
		
		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JLabel lblNewLabel_1 = new JLabel("DECRYPT");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 27));
		frmHackerinsideEncryptionToolkit.getContentPane().add(lblNewLabel_1, BorderLayout.NORTH);
		
		JPanel panel = new JPanel();
		frmHackerinsideEncryptionToolkit.getContentPane().add(panel, BorderLayout.CENTER);
		
		JLabel lblNewLabel = new JLabel("Private Key");
		lblNewLabel.setBounds(10, 11, 120, 20);
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
		
		cmbPrivateKey = new JComboBox();
		cmbPrivateKey.setBounds(10, 42, 451, 25);
		cmbPrivateKey.setFont(new Font("Tahoma", Font.PLAIN, 16));
		
		JButton btnCertDetails = new JButton("DETAILS");
		btnCertDetails.setBounds(471, 42, 95, 25);

		btnCertDetails.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		JButton btnOpenOutputFile = new JButton("...");
		btnOpenOutputFile.setBounds(471, 148, 95, 25);

		btnOpenOutputFile.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		txtbOutputFile = new JTextField();
		txtbOutputFile.setBounds(10, 147, 451, 26);
		txtbOutputFile.setFont(new Font("Tahoma", Font.PLAIN, 16));
		txtbOutputFile.setColumns(10);
		
		JLabel lblInputFile = new JLabel("Output File");
		lblInputFile.setBounds(10, 116, 120, 20);
		lblInputFile.setFont(new Font("Tahoma", Font.PLAIN, 16));
		
		JButton btnDecrypt = new JButton("DECRYPT");
		btnDecrypt.setBounds(200, 236, 176, 55);

		btnDecrypt.setFont(new Font("Tahoma", Font.BOLD, 18));
		
		progressBar = new JProgressBar();
		progressBar.setBounds(106, 329, 364, 26);
		progressBar.setIndeterminate(true);
		progressBar.setEnabled(false);
		progressBar.setVisible(false);
		
		lblStatus = new JLabel("");
		lblStatus.setBounds(162, 392, 253, 31);
		lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
		lblStatus.setFont(new Font("Tahoma", Font.PLAIN, 16));
		
		btnCertDetails.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showRecipientDetails();
			}
		});
		
		btnOpenOutputFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				outputSelection();
			}
		});
		
		btnDecrypt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				decrypt();
			}
		});
		
		populaterCerts(cmbPrivateKey);
		panel.setLayout(null);
		panel.add(lblNewLabel);
		panel.add(txtbOutputFile);
		panel.add(cmbPrivateKey);
		panel.add(btnCertDetails);
		panel.add(btnOpenOutputFile);
		panel.add(lblInputFile);
		panel.add(btnDecrypt);
		panel.add(progressBar);
		panel.add(lblStatus);
		if(this.fileToDecrypt == null) fileInitialization();
	}
	
	
	/**
	 * Initializes the file selection process
	 * Selects an input file and creates a default output path.
	 */
	private void fileInitialization() {
	    this.fileToDecrypt = selectInputFile();
	    if (this.fileToDecrypt != null) {
	        createOutputFilePath();
	        populaterCerts(cmbPrivateKey);
	        identifyRecipientKeyAsync();
	    }else {
	        SwingUtilities.invokeLater(() -> {
	            frmHackerinsideEncryptionToolkit.dispose();
	        });
	    }
	}
	
	private void identifyRecipientKeyAsync() {
	    SwingWorker<Optional<String>, Void> worker = new SwingWorker<>() {
	        private EncodingOption encoding;
	        private Collection<RecipientIdentifier> recipients;

	        @Override
	        protected Optional<String> doInBackground() throws Exception {
	            encoding = PEMUtils.findFileEncoding(fileToDecrypt);
	            recipients = CMSCryptoUtils.extractRecipientIdentifiers(fileToDecrypt, encoding);
	            return ctx.getKeystore().findAliasForRecipients(recipients);
	        }

	        @Override
	        protected void done() {
	            try {
	                Optional<String> aliasOpt = get();
	                if (aliasOpt.isPresent()) {
	                    selectAliasInCombo(aliasOpt.get());
	                    lblStatus.setText("Found private key: " + aliasOpt.get());
	                } else {
	                    DialogUtils.showMessageBox(
	                        null,
	                        "Private key not found!",
	                        "No matching private key",
	                        "Manually select the correct certificate.",
	                        JOptionPane.WARNING_MESSAGE
	                    );
	                    lblStatus.setText("Manually select the correct certificate.");
	                }
	            } catch (Exception e) {
	                DialogUtils.showMessageBox(
	                    null,
	                    "Error",
	                    "Error identifying key",
	                    e.getMessage(),
	                    JOptionPane.ERROR_MESSAGE
	                );
	                lblStatus.setText("Error identifying key");
	                e.printStackTrace();
	            }
	        }
	    };
	    worker.execute();
	}

	private void selectAliasInCombo(String alias) {
	    ComboBoxModel<String> model = cmbPrivateKey.getModel();
	    for (int i = 0; i < model.getSize(); i++) {
	        if (alias.equals(model.getElementAt(i))) {
	            cmbPrivateKey.setSelectedIndex(i);
	            return;
	        }
	    }
	}

	/**
	 * Opens a file dialog to select an input file
	 * 
	 * @return the selected file, or null if no file was selected
	 */
	private File selectInputFile() {
	    return FileDialogUtils.openFileDialog(
	            null,
	            "Select the file to decrypt",
	            ".",
	            DefaultExtensions.CRYPTO_P7E
	    );
	}
	
	/**
	 * Creates a default output file path by removing the cryptographic extension to the input file.
	 */
	private void createOutputFilePath() {
		File file = DefaultExtensions.removeExtension(fileToDecrypt, DefaultExtensions.CRYPTO_P7E);
		setOutputFile(file);
	}
	
	private void outputSelection() {
	    File defaultOutput = DefaultExtensions.removeExtension(fileToDecrypt, DefaultExtensions.CRYPTO_P7E);
	    File outputFile = FileDialogUtils.saveFileDialog(
	            null,
	            "Decrypted file",
	            defaultOutput.getAbsolutePath()
	    );
		setOutputFile(outputFile);

	    
	}
	
	/**
	 * Sets the output file and updates the corresponding text field.
	 * 
	 * @param file the output file to set
	 */
	private void setOutputFile(File file) {
	    txtbOutputFile.setText(file.getAbsolutePath());
	}
	
	private void populaterCerts(JComboBox<String> combo) {
	    combo.removeAllItems();
	    Enumeration<String> aliases = null;
		try {
			aliases = ctx.getKeystore().listAliases();
			aliases.asIterator().forEachRemaining(x -> combo.addItem(x));
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
	}
	
	private X509Certificate getCertificate() {
		try {
			return ctx.getKeystore().getCertificate((String) cmbPrivateKey.getSelectedItem());
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Displays details of the currently selected certificate.
	 */
	private void showRecipientDetails() {
		new CertificateDetailsForm(getCertificate());
	}
	
	private void decrypt() {
	    File output = new File(txtbOutputFile.getText());
	    startDecryptionUI();

	    SwingWorker<Void, Void> worker = new SwingWorker<>() {
	        @Override
	        protected Void doInBackground() throws Exception {
	            String alias = (String) cmbPrivateKey.getSelectedItem();
	            if (alias == null) {
	                throw new IllegalStateException("No certificates selected.");
	            }

	            String pwd = DialogUtils.showInputBox(
	                null,
	                "Unlock Private key",
	                "Password for " + alias,
	                "Password:",
	                true
	            );
	            PrivateKey priv = ctx.getKeystore().getPrivateKey(alias, pwd.toCharArray());
	            EncodingOption encoding = PEMUtils.findFileEncoding(fileToDecrypt);

	            CMSDecryptor decryptor = new CMSDecryptor(priv, encoding);
	            decryptor.decrypt(fileToDecrypt, output);
	            return null;
	        }

	        @Override
	        protected void done() {
	            finishDecryptionUI(this);
	        }
	    };

	    worker.execute();
	}
	/**
	 * Updates the UI to indicate that decryption is in progress.
	 * Shows the progress bar and sets the status text.
	 */
	private void startDecryptionUI() {
	    progressBar.setVisible(true);
	    progressBar.setEnabled(true);
	    lblStatus.setText("Decrypting...");
	    lblStatus.setVisible(true);
	}
	

	private void finishDecryptionUI(SwingWorker<?, ?> worker) {
	    progressBar.setVisible(false);
	    try {
	        if (worker == null) return;
	        Object result = worker.get();
	        lblStatus.setText("File Decrypted!");
			DialogUtils.showMessageBox(null, "File Decrypted!", "File Decrypted!", 
			        "File Decrypted!" +"\n\nSaved to: " 
			        		+ txtbOutputFile.getText() , 
			        JOptionPane.INFORMATION_MESSAGE);
	    } catch (InterruptedException | ExecutionException e) {
	        DialogUtils.showMessageBox(
	                null,
	                "Error during decryption",
	                "Error during decryption!",
	                e.getMessage(),
	                JOptionPane.ERROR_MESSAGE
	        );
	        lblStatus.setText("Decryption failed");
	        e.printStackTrace();
	    }
	}
}
