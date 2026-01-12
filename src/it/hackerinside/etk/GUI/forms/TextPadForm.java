package it.hackerinside.etk.GUI.forms;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Optional;
import java.util.Scanner;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.bouncycastle.util.Arrays;

import it.hackerinside.etk.GUI.DialogUtils;
import it.hackerinside.etk.GUI.ETKContext;
import it.hackerinside.etk.GUI.FileDialogUtils;
import it.hackerinside.etk.GUI.Utils;
import it.hackerinside.etk.GUI.DTOs.CertificateWrapper;
import it.hackerinside.etk.Utils.X509CertificateLoader;
import it.hackerinside.etk.core.Encryption.CMSCryptoUtils;
import it.hackerinside.etk.core.Encryption.CMSDecryptor;
import it.hackerinside.etk.core.Encryption.CMSEncryptor;
import it.hackerinside.etk.core.Models.DefaultExtensions;
import it.hackerinside.etk.core.Models.EncodingOption;
import it.hackerinside.etk.core.Models.RecipientIdentifier;
import it.hackerinside.etk.core.Models.SymmetricAlgorithms;

import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

public class TextPadForm {

	private JFrame frmEncryptedNote;
	private JTextField txtbRecipientFile;
	private ETKContext ctx;
	private JComboBox<SymmetricAlgorithms> cmbEncAlgorithm;
	private JComboBox<CertificateWrapper> cmbRecipientCert;
	private X509Certificate recipient;
	private JTextArea txtbData;
	private JButton btnDecrypt;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TextPadForm window = new TextPadForm();
					window.frmEncryptedNote.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public TextPadForm() {
		ctx = ETKContext.getInstance();
		initialize();
	}
	
	public void setVisible() {
		this.frmEncryptedNote.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmEncryptedNote = new JFrame();
		frmEncryptedNote.setTitle("HackerInside Encryption Toolkit | Notepad");
		frmEncryptedNote.setBounds(100, 100, 1048, 766);
		
		JPanel panel = new JPanel();

		frmEncryptedNote.getContentPane().add(panel, BorderLayout.CENTER);

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));

		txtbData = new JTextArea();
		txtbData.setLineWrap(true);
		txtbData.setToolTipText("");
		txtbData.setFont(new Font("Monospaced", Font.PLAIN, 14));


		JScrollPane scrollPane = new JScrollPane(txtbData, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		JButton btnEncrypt = new JButton("ENCRYPT");

		btnEncrypt.setFont(new Font("Tahoma", Font.BOLD, 13));

		btnDecrypt = new JButton("DECRYPT");

		btnDecrypt.setFont(new Font("Tahoma", Font.BOLD, 13));



		// TextArea prefs
		txtbData.setWrapStyleWord(true);
		txtbData.setLineWrap(true);

		JPopupMenu popupMenu = new JPopupMenu();
		

		popupMenu.setLayout(new GridLayout(2, 1, 0, 3)); 

		JButton btnOpenFile = new JButton("Open File");
		JButton btnSaveFile = new JButton("Save File");

		Dimension buttonSize = new Dimension(85, 30);
		btnOpenFile.setPreferredSize(buttonSize);
		btnSaveFile.setPreferredSize(buttonSize);

		popupMenu.add(btnOpenFile);
		popupMenu.add(btnSaveFile);
		
		addPopup(txtbData, popupMenu);

		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(10)
					.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
						.addComponent(scrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 1012, Short.MAX_VALUE)
						.addComponent(panel_1, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 1012, Short.MAX_VALUE))
					.addGap(10))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 742, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setFont(new Font("Tahoma", Font.PLAIN, 16));
		
		cmbEncAlgorithm = new JComboBox();
		cmbEncAlgorithm.setFont(new Font("Tahoma", Font.PLAIN, 16));
		
		JLabel lblEncryptionAlgorithm = new JLabel("Encryption Algorithm:");
		lblEncryptionAlgorithm.setFont(new Font("Tahoma", Font.PLAIN, 16));
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, 557, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addComponent(lblEncryptionAlgorithm, GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
						.addComponent(btnEncrypt, GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addComponent(cmbEncAlgorithm, Alignment.TRAILING, 0, 207, Short.MAX_VALUE)
						.addComponent(btnDecrypt, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addGap(6)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_panel_1.createSequentialGroup()
							.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
								.addComponent(btnDecrypt, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnEncrypt, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE))
							.addGap(11)
							.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
								.addComponent(cmbEncAlgorithm, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblEncryptionAlgorithm, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE))))
					.addContainerGap(20, Short.MAX_VALUE))
		);
		
		JPanel panel_1_1 = new JPanel();
		panel_1_1.setLayout(null);
		tabbedPane.addTab("Known Certificates", null, panel_1_1, null);
		
		cmbRecipientCert = new JComboBox();
		cmbRecipientCert.setFont(new Font("Tahoma", Font.PLAIN, 16));
		cmbRecipientCert.setBounds(10, 11, 414, 28);
		panel_1_1.add(cmbRecipientCert);
		
		JButton btnCertDetails = new JButton("DETAILS");


		btnCertDetails.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnCertDetails.setBounds(434, 11, 108, 28);
		panel_1_1.add(btnCertDetails);
		
		JPanel panel_2 = new JPanel();
		panel_2.setLayout(null);
		tabbedPane.addTab("File", null, panel_2, null);
		
		JButton btnOpenCertFile = new JButton("...");

		btnOpenCertFile.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnOpenCertFile.setBounds(481, 12, 61, 25);
		panel_2.add(btnOpenCertFile);
		
		txtbRecipientFile = new JTextField();
		txtbRecipientFile.setFont(new Font("Tahoma", Font.PLAIN, 16));
		txtbRecipientFile.setColumns(10);
		txtbRecipientFile.setBounds(10, 11, 448, 26);
		panel_2.add(txtbRecipientFile);
		panel_1.setLayout(gl_panel_1);
		panel.setLayout(gl_panel);
		
		scrollPane.addMouseWheelListener(new MouseWheelListener() { // Mouse wheel zoom
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (e.isControlDown()) { 
					txtbData.setFont(new java.awt.Font(txtbData.getFont().getFontName(), txtbData.getFont().getStyle(),
							e.getUnitsToScroll() > 0 ? txtbData.getFont().getSize() - 2 
									: txtbData.getFont().getSize() + 2));
				}
			}
		});
		
		// Open file button
		btnOpenFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File f = FileDialogUtils.openFileDialog(null, "Open Encrypted Text File", ".", 
			            DefaultExtensions.CRYPTO_P7E);
				
				if(f != null) {
					txtbData.setText(readTextFile(f));
				}

			}
		});
		
		// Save file button
		btnSaveFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File f = FileDialogUtils.saveFileDialog(null,
			            "Save encrypted text file",
			            java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ".txt",
			            DefaultExtensions.CRYPTO_P7E);
				
				if(f != null) {
					writeTextToFile(f,txtbData.getText().toString());
				}

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
		
		btnCertDetails.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showRecipientDetails();
			}
		});

		
		btnEncrypt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				encrypt();
			}
		});
		
		btnDecrypt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				decrypt();
			}
		});
		
		btnOpenCertFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadCertificateFromFile();
			}
		});
		
		start();
		
		
	}
	
	private void start() {
		loadAlgorithms();
		this.cmbEncAlgorithm.setSelectedItem(ctx.getCipher());
		populateKnowCerts(cmbRecipientCert);
		
		if(ctx.getKeystore() == null) {
			 btnDecrypt.setEnabled(false);
		}
	}
	
	/**
	 * Displays details of the currently selected recipient certificate.
	 */
	private void showRecipientDetails() {
	    if (this.recipient != null) {
	        new CertificateDetailsForm(this.recipient);
	    }
	}
	
	/**
	 * Populates a combo box with all available symmetric algorithms.
	 * 
	 */
	private void loadAlgorithms() {
	    cmbEncAlgorithm.removeAllItems();
	    for (SymmetricAlgorithms alg : SymmetricAlgorithms.values()) {
	    	cmbEncAlgorithm.addItem(alg);
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
	 * Encrypts the data in the text box using the selected symmetric algorithm and CMS encryption.
	 * The encrypted data is displayed in the same text box after successful encryption.
	 * If encryption fails, an error dialog is displayed and the original data remains unchanged.
	 */
	private void encrypt() {
		if(!Utils.acceptX509Certificate(recipient)) return;
		SymmetricAlgorithms cipher = (SymmetricAlgorithms) cmbEncAlgorithm.getSelectedItem();
		CMSEncryptor encryptor = new CMSEncryptor(cipher, EncodingOption.ENCODING_PEM, ctx.getBufferSize());
		encryptor.addRecipients(recipient);
		
		String text = txtbData.getText();
	    ByteArrayInputStream input = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
	    ByteArrayOutputStream output = new ByteArrayOutputStream();
	    boolean ok = true;
	    try {
			encryptor.encrypt(input, output);
		} catch (Exception e) {
			DialogUtils.showMessageBox(null, "Error during encryption", "Error during encryption!", 
			        e.getMessage(), 
			        JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			ok = false;
		}
	    
	    if(ok) txtbData.setText(new String(output.toByteArray()));
	}
	

	/**
	 * Extracts recipient identifiers from CMS encrypted data and attempts to find a matching
	 * private key alias in the keystore.
	 *
	 * @param data The input stream containing CMS encrypted data in PEM encoding
	 * @return An Optional containing the recipient alias if found, empty Optional otherwise
	 */
	private Optional<String> findRecipientAlias(ByteArrayInputStream data) {
        Collection<RecipientIdentifier> recipients;
        Optional<String> recipient = java.util.Optional.empty();
        try {
        	recipients = CMSCryptoUtils.extractRecipientIdentifiers(data, EncodingOption.ENCODING_PEM);
        	recipient = ctx.getKeystore().findAliasForRecipients(recipients);
		} catch (Exception e) {
            DialogUtils.showMessageBox(
                    null,
                    "Error",
                    "Error identifying key",
                    e.getMessage(),
                    JOptionPane.ERROR_MESSAGE
                );
			e.printStackTrace();
		}
        return recipient;
	}
	
	/**
	 * Retrieves the private key needed for decryption by first identifying the recipient
	 * from the encrypted data, then prompting the user for the key password.
	 *
	 * @return The private key for decryption, or null if no matching key is found or
	 *         password entry fails/cancels
	 */
	private PrivateKey getPrivateKey() {
		String text = txtbData.getText();
		ByteArrayInputStream input = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
		Optional<String> privateKeyAlias = findRecipientAlias(input);
		if(!privateKeyAlias.isPresent()) {
            DialogUtils.showMessageBox(
                    null,
                    "Private key not found!",
                    "No matching private key",
                    "No matching private key",
                    JOptionPane.WARNING_MESSAGE
                );
            return null;
		}
		
		return Utils.getPrivateKeyDialog(privateKeyAlias.get());
        
	}
	
	/**
	 * Decrypts the CMS encrypted data in the text box using the corresponding private key.
	 * The decrypted data is displayed in the same text box after successful decryption.
	 * If decryption fails, an error dialog is displayed and the encrypted data remains unchanged.
	 */
	private void decrypt() {
		String text = txtbData.getText();
	    ByteArrayInputStream input = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
	    ByteArrayOutputStream output = new ByteArrayOutputStream();
	    
        
        PrivateKey priv = getPrivateKey();
        if(priv == null) return;
        
        boolean ok = true;
		try {
			CMSDecryptor decryptor = new CMSDecryptor(priv, EncodingOption.ENCODING_PEM, ctx.getBufferSize());
			decryptor.decrypt(input, output);
		} catch (Exception e) {
	        DialogUtils.showMessageBox(
	                null,
	                "Error during decryption",
	                "Error during decryption!",
	                e.getMessage(),
	                JOptionPane.ERROR_MESSAGE
	        );
			e.printStackTrace();
			ok = false;
		}
	    if(ok) txtbData.setText(new String(output.toByteArray()));

		
	}

	
	/**
	 * Adds a popup menu trigger to the specified component.
	 *
	 * @param component The component to which the popup menu should be attached
	 * @param popup The popup menu to display when the component is right-clicked
	 */
	private void addPopup(Component component, final JPopupMenu popup) { 
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}

	/**
	 * Reads the entire content of a text file and returns it as a single string.
	 *
	 * @param f The file to read from
	 * @return The complete content of the file as a string, or empty string if file not found
	 */
	private String readTextFile(File f) {
		String data = "";
		try {
			Scanner s = new Scanner(f);
			while(s.hasNextLine()) {
				data = data + s.nextLine();
			}
			s.close();
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null,e.getMessage());
			e.printStackTrace();
		}
		return data;
	}


	/**
	 * Writes text data to a specified file.
	 *
	 * @param f The file to write to
	 * @param data The text data to write to the file
	 */
	private void writeTextToFile(File f,String data) {
		try {
			FileWriter myWriter = new FileWriter(f);
			myWriter.write(data);
			myWriter.close();

		} catch (IOException e) {
			JOptionPane.showMessageDialog(null,e.getMessage());
			e.printStackTrace();
		}
		return;
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
	    	txtbRecipientFile.setText(certFile.getAbsolutePath());
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
			DialogUtils.showMessageBox(null, "Invalid certificate", "Invalid certificate!", 
			        e.getMessage(), 
			        JOptionPane.ERROR_MESSAGE);
	        return null;
	    }
	}
}
