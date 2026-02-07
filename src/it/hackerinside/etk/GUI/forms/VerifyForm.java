package it.hackerinside.etk.GUI.forms;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStoreException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HexFormat;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.jcajce.util.MessageDigestUtils;

import com.formdev.flatlaf.FlatLaf;

import it.hackerinside.etk.GUI.CertificateDetailsPanel;
import it.hackerinside.etk.GUI.DialogUtils;
import it.hackerinside.etk.GUI.ETKContext;
import it.hackerinside.etk.GUI.FileDialogUtils;
import it.hackerinside.etk.Utils.X509TrustChainValidator;
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
import javax.swing.JScrollPane;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
	private String caCheckOutput = null;
    private boolean running = false;
    private SwingWorker<Void, Void> currentWorker;
	private CAdESVerifier verifier;

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
	    frmHackerinsideEncryptionToolkit.addWindowListener(new WindowAdapter() {
	    	@Override
	    	public void windowClosing(WindowEvent e) {
	    		abortVerification();
	    	}
	    });
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

	    JScrollPane scrollPane = new JScrollPane(
	            verificationReport,
	            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
	            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
	    );

	    panel.add(scrollPane, BorderLayout.CENTER);

	    
	    panel_1 = new JPanel();
	    splitPane.setRightComponent(panel_1);

	    JPanel bottomPanel = new JPanel(null);
	    bottomPanel.setPreferredSize(new Dimension(100, 100));
	    frmHackerinsideEncryptionToolkit.getContentPane().add(bottomPanel, BorderLayout.SOUTH);

	    btnExportContent = new JButton("EXPORT CONTENT");

	    btnExportContent.setEnabled(false);
	    btnExportContent.setFont(new Font("Tahoma", Font.PLAIN, 16));
	    
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
	    				.addComponent(lblStatus, GroupLayout.DEFAULT_SIZE, 533, Short.MAX_VALUE))
	    			.addPreferredGap(ComponentPlacement.UNRELATED)
	    			.addComponent(btnExportContent, GroupLayout.PREFERRED_SIZE, 213, GroupLayout.PREFERRED_SIZE)
	    			.addContainerGap())
	    );
	    gl_bottomPanel.setVerticalGroup(
	    	gl_bottomPanel.createParallelGroup(Alignment.LEADING)
	    		.addGroup(gl_bottomPanel.createSequentialGroup()
	    			.addGap(21)
	    			.addComponent(progressBar, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
	    			.addPreferredGap(ComponentPlacement.RELATED)
	    			.addComponent(lblStatus, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
	    			.addContainerGap())
	    		.addGroup(Alignment.TRAILING, gl_bottomPanel.createSequentialGroup()
	    			.addContainerGap(39, Short.MAX_VALUE)
	    			.addComponent(btnExportContent, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
	    			.addContainerGap())
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

	private Color getSuccessColor() {
	    return FlatLaf.isLafDark() ? new Color(80,200,120) : new Color(0,128,0);
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
	    running = true;
	    currentWorker = new SwingWorker<>() {
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
		            progressBar.setVisible(false);
		            progressBar.setEnabled(false);
	                DialogUtils.showMessageBox(
	                        null,
	                        "Verification failed",
	                        "Verification failed during setup.",
	                        e.getMessage(),
	                        JOptionPane.ERROR_MESSAGE
	                );
	            }
	        }
	    };

	    currentWorker.execute();
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
	            new CAdESVerifier(encoding, false,ctx.getBufferSize()).extractContent(fileToVerify, outputFile);
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
	    verifier = new CAdESVerifier(encoding, false,ctx.getBufferSize());

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

	    verifier = new CAdESVerifier(encoding, true,ctx.getBufferSize());
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
	                get();
	                finishVerificationUI(this);
	            } catch (Exception e) {
	            	finishVerificationUI(null);
	                Throwable cause = e;

	                if (e instanceof ExecutionException && e.getCause() != null) {
	                    cause = e.getCause();
	                }

	                // find root cause
	                while (cause.getCause() != null) {
	                    cause = cause.getCause();
	                }

	                DialogUtils.showMessageBox(
	                        null,
	                        "Verification failed",
	                        "Error during verification!",
	                        cause.getMessage(),
	                        JOptionPane.ERROR_MESSAGE
	                );

	                setStatusText("Verification failed: " + cause.getMessage() , Color.RED);
	                
	            }

	        }
	    };

	    worker.execute();
	}

	/**
	 * Checks if the specified certificate is trusted by comparing it against
	 * the application's keystore, known certificates and CA truststore
	 * 
	 * @param cert the X509Certificate to check for trust
	 * @return true if the certificate is trusted
	 */
	private boolean isTrusted(X509Certificate cert) {
	    try {
	        if (ctx.getKeystore() != null && ctx.getKeystore().contains(cert) != null) return true;
	        if (ctx.getKnownCerts() != null && ctx.getKnownCerts().contains(cert) != null) return true;
	        if (ctx.useTrustStore() && ctx.getTrustStore() != null) return checkCA(cert);
	    } catch (KeyStoreException e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	
	/**
	 * Check if the certificate is issued by a valid CA
	 * @param cert the X509Certificate to check for trust
	 * @return true if the certificate is issued by a valid CA
	 */
	private boolean checkCA(X509Certificate cert) {
		boolean valid = false;
		try {
			new X509TrustChainValidator(ctx.getTrustStore())
			.checkCertificate(cert);
			valid = true;
			caCheckOutput = "Certificate validated by certification authority!";
			
		} catch (CertificateException e) {
			e.printStackTrace();
			caCheckOutput = e.getMessage();
		} catch (CertPathValidatorException e) {
			e.printStackTrace();
			caCheckOutput = e.getMessage();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
			caCheckOutput = e.getMessage();
		}
		return valid;
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
	 * Check whether a certificate is valid at the date and time of signing.
	 * 
	 * 
	 * @param cert the X509Certificate to check
	 * @param signingTime Signature date and time, if null the current date and time is assumed
	 * @return Message with verification result or empty string
	 */
	private String checkCertTimeValidity(X509Certificate cert, Date signingTime) {
	    Date now = new Date();

	    if (signingTime == null) {
	        signingTime = now;
	    }

	    // Check at signing time
	    try {
	        cert.checkValidity(signingTime);
	    } catch (CertificateExpiredException e) {
	        return "Expired at signing";
	    } catch (CertificateNotYetValidException e) {
	        return "Not valid at signing";
	    }

	    // Check at current time
	    try {
	        cert.checkValidity(now);
	        return ""; // valid
	    } catch (CertificateExpiredException e) {
	        return "Expired now (valid at signing)";
	    } catch (CertificateNotYetValidException e) {
	        return "Not valid now (valid at signing)";
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
	        listModel.addElement("Digest Algorithm: " +  MessageDigestUtils.getDigestName(new ASN1ObjectIdentifier(result.digestAlgorithm())));
	        listModel.addElement("Digest: " + HexFormat.of().formatHex(result.contentDigest()));
	        listModel.addElement("Path: " + this.fileToVerify.getAbsolutePath());

	        listModel.addElement(" ");
	        setStatusText("Valid CAdES Signature!", getSuccessColor());
	    } else {
	        listModel.addElement("CAUTION - Signature tampered!");
	        setStatusText("INVALID DIGITAL SIGNATURE", Color.RED);
	        return;
	    }
	    
	    String timeValidity;
	    // --- Signing time ---
	    if (result.hasSigningTime()) {
	        listModel.addElement("Declared signing time: " + result.getSigningTime());
	        timeValidity = checkCertTimeValidity(result.signer(),result.getSigningTime());
	    }else {
	    	timeValidity = checkCertTimeValidity(result.signer(),null);
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
	    
	    if(caCheckOutput != null && !caCheckOutput.isEmpty()) {
	    	listModel.addElement(" ");
	    	listModel.addElement("CA: " + caCheckOutput);
	    }
	    
	    if(!timeValidity.isEmpty()) {
	    	listModel.addElement(" ");
	    	listModel.addElement("CAUTION: " + timeValidity);
	        setStatusText("Verified OK, " + timeValidity, Color.ORANGE);
	    }
	    
	    
	}
	
	private void abortVerification() {
		verifier.abort();
	    if (currentWorker != null && !currentWorker.isDone()) {
	        currentWorker.cancel(true);
	    }
	    running = false;
	}
}
