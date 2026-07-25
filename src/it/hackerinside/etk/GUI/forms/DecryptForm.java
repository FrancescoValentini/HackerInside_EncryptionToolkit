package it.hackerinside.etk.GUI.forms;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.BorderLayout;
import java.awt.Font;
import java.security.Key;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import javax.swing.JPanel;
import javax.crypto.SecretKey;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JTextField;

import it.hackerinside.etk.GUI.DialogUtils;
import it.hackerinside.etk.GUI.ETKContext;
import it.hackerinside.etk.GUI.FileDialogUtils;
import it.hackerinside.etk.GUI.TimeUtils;
import it.hackerinside.etk.GUI.Utils;
import it.hackerinside.etk.GUI.DTOs.CertificateWrapper;
import it.hackerinside.etk.GUI.DTOs.SecretKeyWrapper;
import it.hackerinside.etk.core.Models.DefaultExtensions;
import it.hackerinside.etk.core.Models.IdentifiedRecipient;
import it.hackerinside.etk.core.Services.DecryptionService;
import it.hackerinside.etk.core.keystore.AbstractKeystore;

import javax.swing.JProgressBar;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.awt.event.ActionEvent;
import java.awt.Toolkit;
import javax.swing.JTabbedPane;

public class DecryptForm {

	private JFrame frmHackerinsideEncryptionToolkit;
	private JTextField txtbOutputFile;
	private JComboBox<CertificateWrapper> cmbPrivateKey;
	private JProgressBar progressBar;
	private static ETKContext ctx;
	private File fileToDecrypt;
	private JLabel lblStatus;
    private long startTime;
    private long endTime;
	private JButton btnDecrypt;
    private boolean running = false;
    private SwingWorker<Void, Void> currentWorker;
    private DecryptionService decryptionService;
    private JTabbedPane tabbedPane;
    private JPanel pnlPrivateKey;
    private JComboBox<SecretKeyWrapper> cmbKek;
    private JPanel pnlKek;
    private boolean hasPassword = false;
    

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
		frmHackerinsideEncryptionToolkit.addWindowListener(new WindowAdapter() {
	    	@Override
	    	public void windowClosing(WindowEvent e) {
	    		try {
	    			if(running && decryptionService != null) abortDecryption();
	    		}catch (Exception ex) {
	    			
	    		}
				
	    	}
	    });
		
		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JLabel lblNewLabel_1 = new JLabel("DECRYPT");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 27));
		frmHackerinsideEncryptionToolkit.getContentPane().add(lblNewLabel_1, BorderLayout.NORTH);
		
		JPanel panel = new JPanel();
		frmHackerinsideEncryptionToolkit.getContentPane().add(panel, BorderLayout.CENTER);
		
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
		
		btnDecrypt = new JButton("DECRYPT");
		btnDecrypt.setBounds(200, 236, 176, 55);

		btnDecrypt.setFont(new Font("Tahoma", Font.BOLD, 18));
		
		progressBar = new JProgressBar();
		progressBar.setBounds(106, 329, 364, 26);
		progressBar.setIndeterminate(true);
		progressBar.setEnabled(false);
		progressBar.setVisible(false);
		
		lblStatus = new JLabel("");
		lblStatus.setBounds(10, 392, 556, 31);
		lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
		lblStatus.setFont(new Font("Tahoma", Font.PLAIN, 16));
		
		panel.setLayout(null);
		panel.add(txtbOutputFile);
		panel.add(btnOpenOutputFile);
		panel.add(lblInputFile);
		panel.add(btnDecrypt);
		panel.add(progressBar);
		panel.add(lblStatus);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(10, 11, 556, 81);
		tabbedPane.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panel.add(tabbedPane);
		
		pnlPrivateKey = new JPanel();
		tabbedPane.addTab("Private Key", null, pnlPrivateKey, null);
		pnlPrivateKey.setLayout(null);
		
		cmbPrivateKey = new JComboBox();
		cmbPrivateKey.setBounds(10, 11, 426, 25);
		pnlPrivateKey.add(cmbPrivateKey);
		cmbPrivateKey.setFont(new Font("Tahoma", Font.PLAIN, 16));
		
		JButton btnCertDetails = new JButton("DETAILS");
		btnCertDetails.setBounds(446, 11, 95, 25);
		pnlPrivateKey.add(btnCertDetails);
		
				btnCertDetails.setFont(new Font("Tahoma", Font.PLAIN, 14));
				
				btnCertDetails.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						showRecipientDetails();
					}
				});
		
		pnlKek = new JPanel();
		tabbedPane.addTab("Symmetric", null, pnlKek, null);
		pnlKek.setLayout(null);
		
		cmbKek = new JComboBox();
		cmbKek.setFont(new Font("Tahoma", Font.PLAIN, 16));
		cmbKek.setBounds(10, 11, 531, 25);
		pnlKek.add(cmbKek);
		

		
		btnOpenOutputFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				outputSelection();
			}
		});
		
		btnDecrypt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {	
				if(!running) {
					decrypt();
				}else {
					if(DialogUtils.showConfirmBox(null,
							"Abort?", 
							"Are you sure you want to cancel the operation?", 
							"Press OK to abort decryption", 
							JOptionPane.QUESTION_MESSAGE)) {
						abortDecryption();
					}
					
				}
			}
		});
		
		this.decryptionService = new DecryptionService(ctx);
		
		populaterCerts(cmbPrivateKey);
		populateSecretKeys(cmbKek);
		
		if(!ctx.isLoggedIn()) tabbedPane.setVisible(false);
		
		if(this.fileToDecrypt == null) fileInitialization();
	}
	
	/**
	 * Populates a combo box with SecretKey
	 * Includes a null option for empty selection.
	 * 
	 * @param combo the combo box to populate with SecretKey wrappers
	 */
	private void populateSecretKeys(JComboBox<SecretKeyWrapper> combo) {
		combo.addItem(null);
		
		if(ctx.isLoggedIn()) {
			List<AbstractKeystore> keystores = Stream.of(
			        ctx.getKeystore()
			    )
			    .filter(Objects::nonNull)
			    .toList();
			
			Utils.populateSecretKeys(combo, keystores);
		}
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
		lblStatus.setText("Identifying recipients...");
		btnDecrypt.setEnabled(false);
	    SwingWorker<IdentifiedRecipient, Void> worker = new SwingWorker<>() {

	        @Override
	        protected IdentifiedRecipient doInBackground() throws Exception {
	        	
	        	if(ctx.isLoggedIn()) {
	        		return decryptionService.identifyRecipient(fileToDecrypt);
	        	}else {
	        		boolean hasPwd = decryptionService.hasPasswordRecipient(fileToDecrypt);
	        		return new IdentifiedRecipient(Optional.empty(),hasPwd);
	        	}
	        }

	        @Override
	        protected void done() {
	            try {
	            	IdentifiedRecipient rec = get();
	            	
	                Optional<String> aliasOpt = rec.keystoreAlias();
	                if (aliasOpt.isPresent()) {
	                    selectAlias(aliasOpt.get());
	                    lblStatus.setText("Found recipient key: " + aliasOpt.get());
	                    return;
	                } if(rec.hasPassword()) {
	                	hasPassword = true;
	                	lblStatus.setText("Found password recipient");
	                	return;
	                } else {
	                	if(ctx.isLoggedIn()) {
		                    DialogUtils.showMessageBox(
			                        null,
			                        "Private key not found!",
			                        "No matching private key",
			                        "Manually select the correct certificate.",
			                        JOptionPane.WARNING_MESSAGE
			                    );
		                    lblStatus.setText("Manually select the correct certificate.");
	                	}else {
		                    DialogUtils.showMessageBox(
			                        null,
			                        "Private key not found!",
			                        "You are not logged in, no matching private key",
			                        "You are not logged in, and no decryption key could be identified. \nThe encrypted message does not contain a password-based recipient, \ntherefore it cannot be decrypted using a password and decryption cannot proceed.",
			                        JOptionPane.WARNING_MESSAGE
			                    );
		                    lblStatus.setText("You are not logged in, no matching private key");
	                	}
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
	            }finally {
	            	btnDecrypt.setEnabled(true);
	            }
	        }
	    };
	    worker.execute();
	}
	
	private void selectAlias(String alias) {
	    if (selectAliasInPrivateCombo(alias)) {
	        tabbedPane.setSelectedComponent(pnlPrivateKey);
	        return;
	    }

	    if (selectAliasInSecretCombo(alias)) {
	        tabbedPane.setSelectedComponent(pnlKek);
	        return;
	    }

	    lblStatus.setText("Manually select the correct certificate.");
	}

	private boolean selectAliasInPrivateCombo(String alias) {
	    ComboBoxModel<CertificateWrapper> model = cmbPrivateKey.getModel();
	    for (int i = 0; i < model.getSize(); i++) {
	        CertificateWrapper cert = model.getElementAt(i);
	        if (cert != null && alias.equals(cert.getAlias())) {
	            cmbPrivateKey.setSelectedIndex(i);
	            tabbedPane.setSelectedComponent(pnlPrivateKey);
	            return true;
	        }
	    }
	    return false;
	}

	private boolean selectAliasInSecretCombo(String alias) {
	    ComboBoxModel<SecretKeyWrapper> model = cmbKek.getModel();
	    for (int i = 0; i < model.getSize(); i++) {
	        SecretKeyWrapper key = model.getElementAt(i);
	        if (key != null && alias.equals(key.getAlias())) {
	            cmbKek.setSelectedIndex(i);
	            tabbedPane.setSelectedComponent(pnlKek);
	            return true;
	        }
	    }
	    return false;
	}
	
	private String getSelectedAlias() {
	    if (tabbedPane.getSelectedComponent() == pnlPrivateKey) {
	        CertificateWrapper cert = (CertificateWrapper) cmbPrivateKey.getSelectedItem();
	        return cert != null ? cert.getAlias() : null;
	    } else if (tabbedPane.getSelectedComponent() == pnlKek) {
	        SecretKeyWrapper key = (SecretKeyWrapper) cmbKek.getSelectedItem();
	        return key != null ? key.getAlias() : null;
	    }
	    return null;
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
	
	private void populaterCerts(JComboBox<CertificateWrapper> combo) {
		if(ctx.isLoggedIn()) {
		    Utils.populateCerts(
			        combo,
			        List.of(ctx.getKeystore()),
			        cert -> {
			            String alg = cert.getPublicKey().getAlgorithm();

			            boolean isDSA = alg != null && alg.toUpperCase().contains("DSA");
			            boolean hideECC = ctx.usePKCS11() && !ctx.isPkcs11SignOnly()
			                              && alg != null && alg.toUpperCase().contains("EC");

			            return alg != null && !isDSA && !hideECC;
			        }
			    );
		}
	}
	
	private X509Certificate getCertificate() {
		try {
			return ctx.getKeystore().getCertificate(((CertificateWrapper) cmbPrivateKey.getSelectedItem()).getAlias());
		} catch (KeyStoreException e) {
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
	
	private Key getDecryptionKey() {
	    String alias = getSelectedAlias();
	    if (alias == null && !hasPassword) {
		    DialogUtils.showMessageBox(
		            null,
		            "Missing fields",
		            "No key selected.",
		            "",
		            JOptionPane.ERROR_MESSAGE
		    );
	    }
	    
	    // 1. PrivateKey
	    try {
	    	if(ctx.getKeystore().isPrivateKey(alias)) {
	    		PrivateKey priv = Utils.getPrivateKeyDialog(alias);
	    		if (priv != null) return priv;
	    	}
	    }catch(Exception e) {
	    	return null;
	    }
	    // 2. SecretKey
	    try {
	    	if(ctx.getKeystore().isSecretKeyEntry(alias)) {
			    SecretKey sk = Utils.getSecretKeyDialog(alias);
			    if (sk != null) return sk;
	    	}
	    }catch(Exception e) {
	    	return null;
	    }

	    return null;
	}
	
	private void decrypt() {
	    File output = new File(txtbOutputFile.getText());
	    if (!FileDialogUtils.overwriteIfExists(output)) return;
	    Key key = getDecryptionKey();
	    if(key == null && !hasPassword) return;
	    
	    startDecryptionUI();
	    running = true;
	    btnDecrypt.setText("ABORT");
	    
	    currentWorker = new SwingWorker<>() {
			@Override
			protected Void doInBackground() throws Exception {
		        startTime = System.currentTimeMillis();
		        if (key instanceof PrivateKey pk) {
			        decryptionService.decrypt(
			        		pk,
			                fileToDecrypt,
			                output
			            );
		        }else if (key instanceof SecretKey sk) {
			        decryptionService.decrypt(
			        		sk,
			                fileToDecrypt,
			                output
			            );
		        } else if(key == null && hasPassword) {
		        	char[] pwd = DialogUtils.showPasswordInputBox(
	                        null,
	                        "Password",
	                        "Decryption Password",
	                        "Password"
	                );
		        	if(pwd == null || pwd.length == 0) return null;
		        	startTime = System.currentTimeMillis(); // reset startTime
			        decryptionService.decrypt(
			        		pwd,
			                fileToDecrypt,
			                output
			            );
		        }
		        
		        else {
		            throw new IllegalStateException("Unsupported key type");
		        }


			    return null;
			}
	        @Override
	        protected void done() {
	            finishDecryptionUI(this);
	        }
	    };

	    currentWorker.execute();
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
		running = false;
	    progressBar.setVisible(false);
	    endTime = System.currentTimeMillis();

	    try {
	        if (worker == null) return;
	        Object result = worker.get();
	        lblStatus.setText("File Decrypted!");
			DialogUtils.showMessageBox(null, "File Decrypted!", "File Decrypted!", 
			        "File Decrypted!" +"\n\nSaved to: " 
			        		+ txtbOutputFile.getText() +
			        		"\n\nElapsed: " + TimeUtils.formatElapsedTime(startTime, endTime), 
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
	    btnDecrypt.setText("DECRYPT");
	}
	
	private void abortDecryption() {
		decryptionService.abort();
	    if (currentWorker != null && !currentWorker.isDone()) {
	        currentWorker.cancel(true);
	        lblStatus.setText("Decryption aborted.");
	    }
	    running = false;
	    btnDecrypt.setText("DECRYPT");
	    progressBar.setVisible(false);
	}
}
