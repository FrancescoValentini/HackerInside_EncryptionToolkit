package it.hackerinside.etk.GUI.forms;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JComboBox;
import java.awt.Font;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.concurrent.ExecutionException;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import it.hackerinside.etk.GUI.DialogUtils;
import it.hackerinside.etk.GUI.ETKContext;
import it.hackerinside.etk.GUI.FileDialogUtils;
import it.hackerinside.etk.GUI.TimeUtils;
import it.hackerinside.etk.core.CAdES.CAdESSigner;
import it.hackerinside.etk.core.Models.DefaultExtensions;
import it.hackerinside.etk.core.Models.EncodingOption;
import it.hackerinside.etk.core.Models.HashAlgorithm;
import javax.swing.JCheckBox;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.JProgressBar;
import java.awt.event.ActionListener;
import java.io.File;
import java.awt.event.ActionEvent;
import java.awt.Toolkit;



public class SignForm {

	private JFrame frmSign;
	private JTextField txtbOutputFile;
	private JComboBox<HashAlgorithm> cmbAlgorithm;
	private JComboBox<String> cmbSignerCert;
	private JCheckBox chckbPem;
	private JCheckBox chckbDetachedSignature;
    private long startTime;
    private long endTime;
	
	private static ETKContext ctx;
	private File fileToSign;
	private JLabel lblStatus;
	private JProgressBar progressSignature;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SignForm window = new SignForm();
					window.frmSign.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public SignForm() {
		ctx = ETKContext.getInstance();
		initialize();
	}
	
	public void setVisible() {
		frmSign.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmSign = new JFrame();
		frmSign.setIconImage(Toolkit.getDefaultToolkit().getImage(SignForm.class.getResource("/it/hackerinside/etk/GUI/icons/sign.png")));
		frmSign.setResizable(false);
		frmSign.setTitle("SIGN");
		frmSign.setBounds(100, 100, 593, 715);
		//frmSign.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		frmSign.getContentPane().add(panel, BorderLayout.CENTER);
		
		cmbSignerCert = new JComboBox();
		cmbSignerCert.setFont(new Font("Tahoma", Font.PLAIN, 16));
		
		JLabel lblNewLabel = new JLabel("Signer Certificate");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
		
		JButton btnCertDetails = new JButton("DETAILS");

		btnCertDetails.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		JLabel lblInputFile = new JLabel("Output File");
		lblInputFile.setFont(new Font("Tahoma", Font.PLAIN, 16));
		
		txtbOutputFile = new JTextField();
		txtbOutputFile.setFont(new Font("Tahoma", Font.PLAIN, 16));
		txtbOutputFile.setColumns(10);
		
		JButton btnOpenOutputFile = new JButton("...");
		btnOpenOutputFile.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Signature Settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JButton btnSign = new JButton("SIGN");
		btnSign.setFont(new Font("Tahoma", Font.BOLD, 18));
		
		progressSignature = new JProgressBar();
		progressSignature.setEnabled(false);
		progressSignature.setFont(new Font("Tahoma", Font.PLAIN, 16));
		progressSignature.setIndeterminate(true);
		progressSignature.setVisible(false);
		
		lblStatus = new JLabel("");
		lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
		lblStatus.setFont(new Font("Tahoma", Font.PLAIN, 16));
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addContainerGap()
							.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
								.addComponent(panel_1, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 551, Short.MAX_VALUE)
								.addComponent(lblNewLabel, Alignment.LEADING)
								.addComponent(lblInputFile, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE)
								.addGroup(gl_panel.createSequentialGroup()
									.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
										.addComponent(txtbOutputFile, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 448, Short.MAX_VALUE)
										.addComponent(cmbSignerCert, 0, 448, Short.MAX_VALUE))
									.addGap(18)
									.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
										.addComponent(btnOpenOutputFile, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE)
										.addComponent(btnCertDetails)))))
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(245)
							.addComponent(btnSign, GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE)
							.addGap(235)))
					.addContainerGap())
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(138)
					.addComponent(progressSignature, GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE)
					.addGap(138))
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(200)
					.addComponent(lblStatus, GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
					.addGap(199))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(23)
					.addComponent(lblNewLabel)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(cmbSignerCert, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnCertDetails))
					.addGap(29)
					.addComponent(lblInputFile, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(txtbOutputFile, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnOpenOutputFile, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
					.addGap(42)
					.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 111, GroupLayout.PREFERRED_SIZE)
					.addGap(34)
					.addComponent(btnSign, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE)
					.addGap(41)
					.addComponent(progressSignature, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(72)
					.addComponent(lblStatus)
					.addContainerGap(106, Short.MAX_VALUE))
		);
		panel_1.setLayout(null);
		
		JLabel lblDigestAlgorithm = new JLabel("Digest Algorithm:");
		lblDigestAlgorithm.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblDigestAlgorithm.setBounds(10, 29, 153, 20);
		panel_1.add(lblDigestAlgorithm);
		
		cmbAlgorithm = new JComboBox();
		cmbAlgorithm.setFont(new Font("Tahoma", Font.PLAIN, 16));
		cmbAlgorithm.setBounds(152, 25, 174, 28);
		panel_1.add(cmbAlgorithm);
		
		chckbDetachedSignature = new JCheckBox("Detached Signature");
		chckbDetachedSignature.setFont(new Font("Tahoma", Font.PLAIN, 18));
		chckbDetachedSignature.setBounds(10, 67, 187, 23);
		panel_1.add(chckbDetachedSignature);
		
		chckbPem = new JCheckBox("PEM output");
		chckbPem.setFont(new Font("Tahoma", Font.PLAIN, 18));
		chckbPem.setBounds(210, 67, 141, 23);
		panel_1.add(chckbPem);
		panel.setLayout(gl_panel);
		
		JLabel lblNewLabel_1 = new JLabel("SIGN");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 27));
		frmSign.getContentPane().add(lblNewLabel_1, BorderLayout.NORTH);
		
		
		btnCertDetails.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showRecipientDetails();
			}
		});
		
		btnOpenOutputFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				outputFileSelection();
			}
		});
		
		btnSign.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sign();
			}
		});
		
		chckbDetachedSignature.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createOutputFilePath();
			}
		});
		
		populateHashAlgorithms(cmbAlgorithm);
		populateSignerCerts(cmbSignerCert);
		
		cmbAlgorithm.setSelectedItem(ctx.getHashAlgorithm());
		chckbPem.setSelected(ctx.usePEM());
		
		if(this.fileToSign == null) fileInitialization();
	}
	
	/**
	 * Populates a combo box with all available hash algorithms.
	 * 
	 * @param combo the combo box to populate with hash algorithm values
	 */
	private void populateHashAlgorithms(JComboBox<HashAlgorithm> combo) {
	    combo.removeAllItems();
	    for (HashAlgorithm alg : HashAlgorithm.values()) {
	        combo.addItem(alg);
	    }
	}
	
	/**
	 * Populates a combo box with all available signer certificates.
	 * 
	 * @param combo the combo box to populate with certificates
	 */
	private void populateSignerCerts(JComboBox<String> combo) {
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
			return ctx.getKeystore().getCertificate((String) cmbSignerCert.getSelectedItem());
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private PrivateKey getPrivateKey() {
		String alias = (String) cmbSignerCert.getSelectedItem();
		
		String password = DialogUtils.showInputBox(null, "Unlock Private key", "Password for " + alias, 
		        "Password:", true);
		PrivateKey k = null;
		try {
			k = ctx.getKeystore().getPrivateKey(alias, password.toCharArray()); 
		} catch (UnrecoverableKeyException | NoSuchAlgorithmException | KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return k;
	}
	
	/**
	 * Displays details of the currently selected signer certificate.
	 */
	private void showRecipientDetails() {
		new CertificateDetailsForm(getCertificate());
	}
	
	/**
	 * Sets the output file and updates the corresponding text field.
	 * 
	 * @param file the output file to set
	 */
	private void setOutputFile(File file) {
	    txtbOutputFile.setText(file.getAbsolutePath());
	}

	/**
	 * Creates a default output file path by applying the cryptographic extension to the input file.
	 */
	private void createOutputFilePath() {
		File file;
		if(chckbDetachedSignature.isSelected()) {
			file = DefaultExtensions.applyExtension(this.fileToSign, DefaultExtensions.CRYPTO_P7S);
	    	
		}else {
			file = DefaultExtensions.applyExtension(this.fileToSign, DefaultExtensions.CRYPTO_P7M);

		}
		setOutputFile(file);
	}
	
	/**
	 * Opens a file dialog to select an output file for the signed result.
	 */
	private void outputFileSelection() {
		File file;
		if(chckbDetachedSignature.isSelected()) {
		    file = FileDialogUtils.saveFileDialog(
		            null,
		            "Signature file",
		            this.fileToSign != null ? this.fileToSign.getAbsolutePath() : ".",
		            DefaultExtensions.CRYPTO_P7S
		    );
	    	
		}else {
		    file = FileDialogUtils.saveFileDialog(
		            null,
		            "Signed file",
		            this.fileToSign != null ? this.fileToSign.getAbsolutePath() : ".",
		            DefaultExtensions.CRYPTO_P7M
		    );

		}
		
	    if (file != null) {
	        setOutputFile(file);
	    }
	}
	
	/**
	 * Initializes the file selection process
	 * Selects an input file and creates a default output path.
	 */
	private void fileInitialization() {
	    this.fileToSign = selectInputFile();
	    if (this.fileToSign != null) {
	        createOutputFilePath();
	    }else {
	        SwingUtilities.invokeLater(() -> {
	            frmSign.dispose();
	        });
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
	            "Select the file to sign",
	            "."
	    );
	}
	
	private void sign() {
	    if (!allReady()) {
	        lblStatus.setText("Missing required inputs.");
	        return;
	    }
	    
	    EncodingOption encoding = chckbPem.isSelected()
	            ? EncodingOption.ENCODING_PEM
	            : EncodingOption.ENCODING_DER;
	    
	    HashAlgorithm hash = (HashAlgorithm) cmbAlgorithm.getSelectedItem();
	    
	    boolean detached = chckbDetachedSignature.isSelected();
	    File signedFile = new File(txtbOutputFile.getText());
	    PrivateKey priv = getPrivateKey();
	    X509Certificate signerCert = getCertificate();
	    
	    startSignatureUI();
	    
	    CAdESSigner signer = new CAdESSigner(priv, signerCert, encoding, hash, detached,ctx.getBufferSize());
	    
	    SwingWorker<Void, Void> worker = new SwingWorker<>() {
	        @Override
	        protected Void doInBackground() throws Exception {
	            startTime = System.currentTimeMillis();
	        	signer.sign(fileToSign, signedFile);
	            return null;
	        }

	        @Override
	        protected void done() {
	        	finishSignatureUI(this);
	        }
	    };

	    worker.execute();
	    
	}
	
	/**
	 * Updates the UI to indicate that signature is in progress.
	 */
	private void startSignatureUI() {
		progressSignature.setVisible(true);
		progressSignature.setEnabled(true);
	    lblStatus.setText("Signing...");
	    lblStatus.setVisible(true);
	}

	/**
	 * Finalizes the UI after signature completes or fails.
	 * 
	 * @param worker the SwingWorker that performed the encryption
	 */
	private void finishSignatureUI(SwingWorker<?, ?> worker) {
	    progressSignature.setVisible(false);
	    endTime = System.currentTimeMillis();
	    try {
	        worker.get();
	        lblStatus.setText("File Signed!");
			DialogUtils.showMessageBox(null, "File signed!", "File signed!", 
			        "File signed\n\nSaved to: " 
			        		+ this.fileToSign.getAbsolutePath().toString() +
			        		"\n\nElapsed: " + TimeUtils.formatElapsedTime(startTime, endTime), 
			        JOptionPane.INFORMATION_MESSAGE);
	    } catch (InterruptedException | ExecutionException e) {
			DialogUtils.showMessageBox(null, "Error during digital signature", "Error during digital signature!", 
			        e.getMessage(), 
			        JOptionPane.ERROR_MESSAGE);
	        lblStatus.setText("Digital Signature failed");
	        e.printStackTrace();
	    }
	}

	/**
	 * Checks if all required inputs are ready.
	 * 
	 * @return true if all required inputs are present and valid, false otherwise
	 */
	private boolean allReady() {
	    return this.fileToSign != null &&
	           this.fileToSign.exists() &&
	           !this.txtbOutputFile.getText().isEmpty();
	}
	
}
