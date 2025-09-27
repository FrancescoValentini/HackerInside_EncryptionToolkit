package it.hackerinside.etk.GUI.forms;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import it.hackerinside.etk.GUI.CertificateDetailsPanel;
import it.hackerinside.etk.GUI.DialogUtils;
import it.hackerinside.etk.GUI.ETKContext;
import it.hackerinside.etk.GUI.FileDialogUtils;
import it.hackerinside.etk.core.CAdES.CAdESUtils;
import it.hackerinside.etk.core.CAdES.CAdESVerifier;
import it.hackerinside.etk.core.Models.DefaultExtensions;
import it.hackerinside.etk.core.Models.EncodingOption;
import it.hackerinside.etk.core.Models.VerificationResult;
import it.hackerinside.etk.core.PEM.PEMUtils;

import javax.swing.JSplitPane;
import javax.swing.JPanel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JProgressBar;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Toolkit;

public class VerifyForm {

	private JFrame frmHackerinsideEncryptionToolkit;
	private JLabel lblStatus;
	private JProgressBar progressBar;
	private static ETKContext ctx;
	private File fileToVerify;
	private DefaultListModel<String> listModel = new DefaultListModel<>();
	private JList<String> verificationReport;
	private JSplitPane splitPane;
	private JPanel panel_1;
	private JButton btnExportContent;
	protected EncodingOption encoding;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VerifyForm window = new VerifyForm();
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
	public VerifyForm() {
		ctx = ETKContext.getInstance();
		initialize();
	}
	
	public void setVisible() {
		frmHackerinsideEncryptionToolkit.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
	    frmHackerinsideEncryptionToolkit = new JFrame();
	    frmHackerinsideEncryptionToolkit.setTitle("HackerInside Encryption Toolkit | Verify Digital Signature");
	    frmHackerinsideEncryptionToolkit.setIconImage(Toolkit.getDefaultToolkit().getImage(VerifyForm.class.getResource("/it/hackerinside/etk/GUI/icons/verify.png")));
	    frmHackerinsideEncryptionToolkit.setBounds(100, 100, 792, 567);
	    //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frmHackerinsideEncryptionToolkit.getContentPane().setLayout(new BorderLayout());

	    JLabel lblDigitalSignatureVerification = new JLabel("DIGITAL SIGNATURE VERIFICATION");
	    lblDigitalSignatureVerification.setHorizontalAlignment(SwingConstants.CENTER);
	    lblDigitalSignatureVerification.setFont(new Font("Tahoma", Font.BOLD, 27));
	    frmHackerinsideEncryptionToolkit.getContentPane().add(lblDigitalSignatureVerification, BorderLayout.NORTH);

	    splitPane = new JSplitPane();
	    splitPane.setResizeWeight(0.5);
	    frmHackerinsideEncryptionToolkit.getContentPane().add(splitPane, BorderLayout.CENTER);

	    JPanel panel = new JPanel();
	    splitPane.setLeftComponent(panel);
	    panel.setLayout(new BorderLayout(0, 0));

	    verificationReport = new JList<>(listModel);
	    verificationReport.setFont(new Font("Tahoma", Font.PLAIN, 16));
	    panel.add(verificationReport, BorderLayout.CENTER);
	    
	    panel_1 = new JPanel();
	    splitPane.setRightComponent(panel_1);

	    JPanel bottomPanel = new JPanel(null);
	    bottomPanel.setPreferredSize(new Dimension(100, 100));
	    frmHackerinsideEncryptionToolkit.getContentPane().add(bottomPanel, BorderLayout.SOUTH);

	    btnExportContent = new JButton("EXPORT CONTENT");

	    btnExportContent.setEnabled(false);
	    btnExportContent.setFont(new Font("Tahoma", Font.PLAIN, 14));
	    
	    lblStatus = new JLabel("New label");
	    lblStatus.setFont(new Font("Tahoma", Font.BOLD, 16));
	    
	    progressBar = new JProgressBar();
	    progressBar.setEnabled(false);
	    progressBar.setIndeterminate(true);
	    GroupLayout gl_bottomPanel = new GroupLayout(bottomPanel);
	    gl_bottomPanel.setHorizontalGroup(
	    	gl_bottomPanel.createParallelGroup(Alignment.LEADING)
	    		.addGroup(gl_bottomPanel.createSequentialGroup()
	    			.addGap(10)
	    			.addGroup(gl_bottomPanel.createParallelGroup(Alignment.LEADING)
	    				.addComponent(progressBar, GroupLayout.PREFERRED_SIZE, 380, GroupLayout.PREFERRED_SIZE)
	    				.addComponent(lblStatus, GroupLayout.PREFERRED_SIZE, 590, GroupLayout.PREFERRED_SIZE))
	    			.addPreferredGap(ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
	    			.addComponent(btnExportContent, GroupLayout.PREFERRED_SIZE, 156, GroupLayout.PREFERRED_SIZE)
	    			.addContainerGap())
	    );
	    gl_bottomPanel.setVerticalGroup(
	    	gl_bottomPanel.createParallelGroup(Alignment.LEADING)
	    		.addGroup(gl_bottomPanel.createSequentialGroup()
	    			.addGap(21)
	    			.addGroup(gl_bottomPanel.createParallelGroup(Alignment.TRAILING)
	    				.addComponent(btnExportContent, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
	    				.addGroup(gl_bottomPanel.createSequentialGroup()
	    					.addComponent(progressBar, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
	    					.addGap(11)
	    					.addComponent(lblStatus, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)))
	    			.addGap(11))
	    );
	    bottomPanel.setLayout(gl_bottomPanel);
	    
	    btnExportContent.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		extractContent();
	    	}
	    });
	    
	    if(this.fileToVerify == null) selectInputFile();
	   
	}
	
	/**
	 * Sets the status text and foreground color for the status label.
	 * 
	 * @param text the text to display in the status label
	 * @param foregroundColor the color to use for the status text
	 */
	private void setStatusText(String text, Color foregroundColor) {
	    lblStatus.setForeground(foregroundColor);
	    lblStatus.setText(text);
	}

	/**
	 * Opens a file dialog to allow the user to select an input file for verification.
	 * The selected file is stored in the fileToVerify field.
	 */
	private void selectInputFile() {
	    this.fileToVerify = FileDialogUtils.openFileDialog(
	            null,
	            "Select the file to verify",
	            ".",
	            DefaultExtensions.CRYPTO_P7M,
	            DefaultExtensions.CRYPTO_P7S
	    );
	    
	    if(this.fileToVerify == null) {
	        SwingUtilities.invokeLater(() -> {
	            frmHackerinsideEncryptionToolkit.dispose();
	        });
	    }else {
	    	 verifySignature();
	    }
	}

	/**
	 * Initiates the signature verification process for the selected file.
	 * Determines whether the signature is detached or enveloping and routes
	 * to the appropriate verification method.
	 * Uses SwingWorker to perform the operation in the background to avoid
	 * blocking the UI thread.
	 */
	private void verifySignature() {
	    startVerificationUI();

	    SwingWorker<Void, Void> worker = new SwingWorker<>() {
	        private boolean detached;

	        @Override
	        protected Void doInBackground() throws Exception {
	            encoding = PEMUtils.findFileEncoding(fileToVerify);
	            detached = CAdESUtils.isDetached(fileToVerify, encoding);
	            return null;
	        }

	        @Override
	        protected void done() {
	            try {
	                get(); // rethrow exceptions
	                if (detached) {
	                    verifyDetached(encoding);
	                } else {
	                    verifyEnveloping(encoding);
	                }
	            } catch (Exception e) {
	                e.printStackTrace();
	                setStatusText("Verification failed during setup.", Color.RED);
	            }
	        }
	    };

	    worker.execute();
	}

	/**
	 * Extracts the content from an enveloping signature file.
	 * Prompts the user to select an output location for the extracted content.
	 * Uses SwingWorker to perform the extraction in the background.
	 */
	private void extractContent() {
	    File defaultOutput = DefaultExtensions.removeExtension(fileToVerify, DefaultExtensions.CRYPTO_P7M);
	    File outputFile = FileDialogUtils.saveFileDialog(
	            null,
	            "Extract file",
	            defaultOutput.getAbsolutePath()
	    );

	    if (outputFile == null) return; // user canceled

	    progressBar.setVisible(true);
	    progressBar.setEnabled(true);

	    SwingWorker<Void, Void> worker = new SwingWorker<>() {
	        @Override
	        protected Void doInBackground() throws Exception {
	            new CAdESVerifier(encoding, false).extractContent(fileToVerify, outputFile);
	            return null;
	        }

	        @Override
	        protected void done() {
	            progressBar.setVisible(false);
	            progressBar.setEnabled(false);
	            try {
	                get();
	                DialogUtils.showMessageBox(
	                        null,
	                        "Extraction complete",
	                        "Extraction complete!",
	                        "The content was successfully extracted to:\n" + outputFile.getAbsolutePath(),
	                        JOptionPane.INFORMATION_MESSAGE
	                );
	            } catch (Exception e) {
	                e.printStackTrace();
	                DialogUtils.showMessageBox(
	                        null,
	                        "Extraction failed",
	                        "Error during extraction!",
	                        e.getMessage(),
	                        JOptionPane.ERROR_MESSAGE
	                );
	            }
	        }
	    };

	    worker.execute();
	}

	/**
	 * Verifies an enveloping signature using the specified encoding.
	 * Enables the export content button and initiates the verification process.
	 * 
	 * @param encoding the encoding option to use for verification
	 */
	private void verifyEnveloping(EncodingOption encoding) {
	    btnExportContent.setEnabled(true);
	    CAdESVerifier verifier = new CAdESVerifier(encoding, false);

	    runVerificationWorker(() -> verifier.verify(fileToVerify));
	}

	/**
	 * Verifies a detached signature using the specified encoding.
	 * Prompts the user to select the original data file that was signed.
	 * 
	 * @param encoding the encoding option to use for verification
	 */
	private void verifyDetached(EncodingOption encoding) {
	    File dataFile = FileDialogUtils.openFileDialog(
	            null,
	            "Select the data file",
	            "."
	    );

	    if (dataFile == null) return; // user canceled

	    CAdESVerifier verifier = new CAdESVerifier(encoding, true);
	    runVerificationWorker(() -> verifier.verifyDetached(fileToVerify, dataFile));
	}

	/**
	 * Creates and executes a SwingWorker to perform signature verification.
	 * 
	 * @param verificationTask a Callable that returns the verification result
	 */
	private void runVerificationWorker(Callable<VerificationResult> verificationTask) {
	    SwingWorker<VerificationResult, Void> worker = new SwingWorker<>() {
	        @Override
	        protected VerificationResult doInBackground() throws Exception {
	            return verificationTask.call();
	        }

	        @Override
	        protected void done() {
	            try {
	                VerificationResult result = get();
	                finishVerificationUI(this);
	            } catch (Exception e) {
	                e.printStackTrace();
	                finishVerificationUI(null);
	            }
	        }
	    };

	    worker.execute();
	}

	/**
	 * Checks if the specified certificate is trusted by comparing it against
	 * the application's keystore and known certificates.
	 * 
	 * @param cert the X509Certificate to check for trust
	 * @return true if the certificate is found in the keystore or known certificates,
	 *         false otherwise
	 */
	private boolean isTrusted(X509Certificate cert) {
	    try {
	        if (ctx.getKeystore() != null && ctx.getKeystore().contains(cert) != null) return true;
	        if (ctx.getKnownCerts() != null && ctx.getKnownCerts().contains(cert) != null) return true;
	    } catch (KeyStoreException e) {
	        e.printStackTrace();
	    }
	    return false;
	}

	/**
	 * Updates the UI to indicate that verification is in progress.
	 * Shows the progress bar and sets the status text.
	 */
	private void startVerificationUI() {
	    progressBar.setVisible(true);
	    progressBar.setEnabled(true);
	    setStatusText("Verifying...", Color.WHITE);
	    lblStatus.setVisible(true);
	}

	/**
	 * Finalizes the UI after verification is complete.
	 * Hides the progress bar and processes the verification result.
	 * 
	 * @param worker the SwingWorker that performed the verification, or null if verification failed
	 */
	private void finishVerificationUI(SwingWorker<?, ?> worker) {
	    progressBar.setVisible(false);
	    try {
	        if (worker == null) return;
	        Object result = worker.get();
	        if (result instanceof VerificationResult vr) {
	            showResult(vr);
	        }
	    } catch (InterruptedException | ExecutionException e) {
	        DialogUtils.showMessageBox(
	                null,
	                "Error during Verification",
	                "Error during Verification!",
	                e.getMessage(),
	                JOptionPane.ERROR_MESSAGE
	        );
	        setStatusText("Verification failed.", Color.RED);
	        e.printStackTrace();
	    }
	}

	/**
	 * Displays the verification result in the UI.
	 * Shows certificate details, signature validation status, signing time,
	 * SigningCertificateV2 attribute presence, and trust status.
	 * 
	 * @param result the VerificationResult to display
	 */
	private void showResult(VerificationResult result) {
	    // Display certificate info
	    CertificateDetailsPanel panel = new CertificateDetailsPanel();
	    panel.setCertificate(result.signer());
	    panel.hideContent(false);
	    splitPane.setRightComponent(panel);

	    listModel.clear();

	    // --- Signature validation ---
	    if (result.validSignature()) {
	        listModel.addElement("Intact signature!");
	        setStatusText("Valid CAdES Signature!", Color.GREEN);
	    } else {
	        listModel.addElement("CAUTION - Signature tampered!");
	        setStatusText("INVALID DIGITAL SIGNATURE", Color.RED);
	        return;
	    }

	    // --- Signing time ---
	    if (result.hasSigningTime()) {
	        listModel.addElement("Declared signing time: " + result.getSigningTime());
	    }
	    listModel.addElement(" ");

	    // --- SigningCertificateV2 attribute ---
	    if (result.hasSigningCertificateV2()) {
	        listModel.addElement("The signature has the SigningCertificateV2 attribute");
	    } else {
	        listModel.addElement("WARNING - Missing SigningCertificateV2!");
	        setStatusText("Verified OK, Missing SigningCertificateV2", Color.YELLOW);
	    }
	    listModel.addElement(" ");
	    listModel.addElement(" ");

	    // --- Trust check ---
	    if (isTrusted(result.signer())) {
	        listModel.addElement("Signer certificate is trusted!");
	    } else {
	        listModel.addElement("CAUTION - UNKNOWN SIGNER CERTIFICATE!");
	        setStatusText("Verified OK, UNKNOWN SIGNER CERTIFICATE", Color.ORANGE);
	    }
	}
}
