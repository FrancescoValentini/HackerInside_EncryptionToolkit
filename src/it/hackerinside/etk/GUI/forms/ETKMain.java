package it.hackerinside.etk.GUI.forms;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.FlowLayout;
import javax.swing.JButton;
import java.awt.Font;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import it.hackerinside.etk.GUI.DialogUtils;
import it.hackerinside.etk.GUI.ETKContext;
import it.hackerinside.etk.GUI.FileDialogUtils;
import it.hackerinside.etk.GUI.DTOs.CertificateTableModel;
import it.hackerinside.etk.GUI.DTOs.CertificateTableRow;
import it.hackerinside.etk.GUI.DTOs.KeysLocations;
import it.hackerinside.etk.Utils.X509CertificateLoader;
import it.hackerinside.etk.core.Models.DefaultExtensions;
import it.hackerinside.etk.core.keystore.AbstractKeystore;
import it.hackerinside.etk.core.keystore.PKCS12Keystore;

import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.awt.event.ActionEvent;

public class ETKMain {

	private JFrame frmHackerinsideEncryptionToolkit;
	private static ETKContext ctx;
	private JTable table;
	private CertificateTableModel tableModel;
	private JButton btnDecrypt;
	private JButton btnSign;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		ctx = ETKContext.getInstance();
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ETKMain window = new ETKMain();
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
	public ETKMain() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
	    frmHackerinsideEncryptionToolkit = new JFrame();
	    frmHackerinsideEncryptionToolkit.setTitle("HackerInside Encryption Toolkit");
	    frmHackerinsideEncryptionToolkit.setBounds(100, 100, 921, 615);
	    frmHackerinsideEncryptionToolkit.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	    JPanel topBarPanel = new JPanel();
	    frmHackerinsideEncryptionToolkit.getContentPane().add(topBarPanel, BorderLayout.NORTH);
	    topBarPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 12));

	    btnSign = createSquareButton("SIGN");
	    JButton btnVerify = createSquareButton("VERIFY");
	    JButton btnEncrypt = createSquareButton("ENCRYPT");
	    btnDecrypt = createSquareButton("DECRYPT");

	    topBarPanel.add(btnSign);
	    topBarPanel.add(btnVerify);
	    topBarPanel.add(btnEncrypt);
	    topBarPanel.add(btnDecrypt);

	    JPanel panel = new JPanel();
	    frmHackerinsideEncryptionToolkit.getContentPane().add(panel, BorderLayout.CENTER);
	    panel.setLayout(new BorderLayout(0, 0));
	    
	    tableModel = new CertificateTableModel();
	    table = new JTable(tableModel);
	    table.setFont(new Font("Consolas", Font.PLAIN, 16));
	    panel.add(new JScrollPane(table), BorderLayout.CENTER);

	    JMenuBar menuBar = new JMenuBar();
	    menuBar.setFont(new Font("Segoe UI", Font.PLAIN, 16));
	    frmHackerinsideEncryptionToolkit.setJMenuBar(menuBar);

	    JMenu fileMenu = new JMenu("File");
	    fileMenu.setFont(new Font("Segoe UI", Font.PLAIN, 16));
	    menuBar.add(fileMenu);

	    JMenuItem settingsMenuItem = new JMenuItem("Settings");
	    fileMenu.add(settingsMenuItem);
	    
	    JMenu mnNewMenu = new JMenu("Certificates");
	    menuBar.add(mnNewMenu);
	    
	    JMenuItem menuItemKeystoreLogin = new JMenuItem("Keystore Login");

	    mnNewMenu.add(menuItemKeystoreLogin);
	    
	    JMenuItem menuItemImportKnownCert = new JMenuItem("Import certificate");
	    mnNewMenu.add(menuItemImportKnownCert);
	    
	    JMenuItem menuItemImportKeypair = new JMenuItem("Import KeyPair");
	    mnNewMenu.add(menuItemImportKeypair);

	    btnSign.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	            sign();
	        }
	    });

	    btnVerify.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	            verify();
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
	    
	    // Table row double click
	    table.addMouseListener(new MouseAdapter() {
	        @Override
	        public void mouseClicked(MouseEvent e) {
	            if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
	                int row = table.getSelectedRow();
	                int modelRow = table.convertRowIndexToModel(row);

	                CertificateTableRow selected = tableModel.getRow(modelRow);
	                if (selected != null) {
	                    showCertificateInformation(selected.original());
	                }
	            }
	        }
	    });
	    
	    menuItemKeystoreLogin.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		unlockKeystore();
	    		updateTable();
	    		btnDecrypt.setEnabled(true);
	    		btnSign.setEnabled(true);
	    	}
	    });
	    
	    menuItemImportKnownCert.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		importKnownCert();
	    	}
	    });
	    
	    settingsMenuItem.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		settings();
	    	}
	    });
	    
	    menuItemImportKeypair.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		importKeypair();
	    	}
	    });
	    
	 // table tablemenu
	    JPopupMenu tablePopup = new JPopupMenu();

	    JMenuItem miDelete = new JMenuItem("Delete");
	    miDelete.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            int viewRow = table.getSelectedRow();
	            if (viewRow == -1) {
	                DialogUtils.showMessageBox(null, "No selection", "No rows selected",
	                        "Please select a certificate first.", JOptionPane.WARNING_MESSAGE);
	                return;
	            }
	            int modelRow = table.convertRowIndexToModel(viewRow);
	            CertificateTableRow row = tableModel.getRow(modelRow);
	            if (row != null && row.original() != null) {
	                deleteCertificate(row);
	            }
	        }
	    });
	    tablePopup.add(miDelete);

	    JMenuItem miExport = new JMenuItem("Export");
	    miExport.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            int viewRow = table.getSelectedRow();
	            if (viewRow == -1) {
	                DialogUtils.showMessageBox(null, "No selection", "No rows selected",
	                        "Please select a certificate first.", JOptionPane.WARNING_MESSAGE);
	                return;
	            }
	            int modelRow = table.convertRowIndexToModel(viewRow);
	            CertificateTableRow row = tableModel.getRow(modelRow);
	            if (row != null && row.original() != null) {
	                exportCertificate(row);
	            }
	        }
	    });
	    tablePopup.add(miExport);

	    // Show popup menu on right click or platforms using isPopupTrigger
	    table.addMouseListener(new MouseAdapter() {
	        private void maybeShowPopup(MouseEvent e) {
	            if (e.isPopupTrigger()) {
	                int rowAtPoint = table.rowAtPoint(e.getPoint());
	                if (rowAtPoint != -1) {
	                    // select the row under the mouse if not already selected
	                    if (!table.isRowSelected(rowAtPoint)) {
	                        table.setRowSelectionInterval(rowAtPoint, rowAtPoint);
	                    }
	                } else {
	                    table.clearSelection();
	                }
	                tablePopup.show(e.getComponent(), e.getX(), e.getY());
	            }
	        }

	        @Override
	        public void mousePressed(MouseEvent e) {
	            maybeShowPopup(e);
	        }

	        @Override
	        public void mouseReleased(MouseEvent e) {
	            maybeShowPopup(e);
	        }
	    });

	    startProcedure();
	}


	/**
	 * Deletes a certificate from the appropriate keystore based on its location.
	 * PKCS11 certificates cannot be deleted and will show a warning message.
	 * 
	 * @param row the certificate table row containing the certificate to delete
	 */
	private void deleteCertificate(CertificateTableRow row) {
	    if (row.location() == KeysLocations.PKCS11) {
	        DialogUtils.showMessageBox(
	            null, 
	            "Operation not supported!", 
	            "Deleting certificates from PKCS11 devices is not supported!", 
	            "Deleting certificates from PKCS11 devices is not supported!", 
	            JOptionPane.WARNING_MESSAGE
	        );
	        return;
	    } else if (row.location() == KeysLocations.PKCS12) {
	        try {
	            ctx.getKeystore().deleteKeyOrCertificate(row.keystoreAlias());
	            ctx.getKeystore().save();
	        } catch (KeyStoreException e) {
	            e.printStackTrace();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    } else if (row.location() == KeysLocations.KNWOWN_CERTIFICATES) {
	        try {
	            ctx.getKnownCerts().deleteKeyOrCertificate(row.keystoreAlias());
	            ctx.getKnownCerts().save();
	        } catch (KeyStoreException e) {
	            e.printStackTrace();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	    updateTable();
	}

	/**
	 * Exports a certificate to a file in PEM format.
	 * Prompts the user to select a destination file and saves the certificate
	 * with proper PEM encoding (Base64 with headers and line breaks).
	 * 
	 * @param row the certificate table row containing the certificate to export
	 */
	protected void exportCertificate(CertificateTableRow row) {
	    X509Certificate crt = row.original();
	    File file = FileDialogUtils.saveFileDialog(
	        null,
	        "X.509 Certificate",
	        ".",
	        DefaultExtensions.CRYPTO_PEM
	    );
	    if (file != null) {
	        try (FileWriter fw = new FileWriter(file)) {
	            fw.write("-----BEGIN CERTIFICATE-----\n");
	            String encoded = Base64.getMimeEncoder(64, new byte[]{'\n'})
	                .encodeToString(crt.getEncoded());
	            fw.write(encoded);
	            fw.write("\n-----END CERTIFICATE-----\n");
	        } catch (IOException | java.security.cert.CertificateEncodingException e) {
	            e.printStackTrace();
	            DialogUtils.showMessageBox(
	                null, 
	                "Error while exporting certificate", 
	                "Error while exporting certificate", 
	                e.getMessage(), 
	                JOptionPane.ERROR_MESSAGE
	            );
	        }
	    }
	}

	/**
	 * Creates a square button with text below and an optional icon above.
	 * The button is configured with square dimensions and centered text layout.
	 * 
	 * @param text the text to display on the button
	 * @return a configured JButton with square dimensions and centered text layout
	 */
	private JButton createSquareButton(String text) {
	    JButton button = new JButton(text);
	    button.setPreferredSize(new Dimension(110, 110)); // Square dimensions
	    button.setFont(new Font("Arial", Font.PLAIN, 16)); // Subtle font
	    button.setFocusPainted(false);

	    // Configure for potential icon above + text below layout
	    button.setHorizontalTextPosition(SwingConstants.CENTER);
	    button.setVerticalTextPosition(SwingConstants.BOTTOM);

	    return button;
	}

	/**
	 * Initiates the main procedure by unlocking the keystore.
	 * This method starts the application workflow by attempting to unlock
	 * the keystore and then updating the certificate table.
	 */
	private void startProcedure() {
	    unlockKeystore();
	    updateTable();
	}

	/**
	 * Attempts to unlock the keystore by prompting for a password.
	 * Displays an error message if keystore loading fails and disables
	 * private key operations in case of failure.
	 */
	private void unlockKeystore() {
	    String password = DialogUtils.showInputBox(
	        null,
	        "Unlock Keystore",
	        ctx.getKeyStorePath(),
	        "Password:",
	        true
	    );
	    try {
	        ctx.loadKeystore(password);
	    } catch (Exception e) {
	        DialogUtils.showMessageBox(
	            null,
	            "Unable to load keystore!",
	            "Unable to unlock keystore; only Encryption and Digital Signature Verification are available.",
	            e.getMessage(),
	            JOptionPane.ERROR_MESSAGE
	        );
	        disablePrivateKeyOperations();
	    }
	}

	/**
	 * Updates the certificate table with current data from all available keystores.
	 * This method refreshes the table model by retrieving certificate information
	 * from both private keystores and known certificates keystore.
	 */
	private void updateTable() {
	    List<CertificateTableRow> rows = getTableRows();
	    tableModel.setRows(rows);
	}

	/**
	 * Retrieves certificate table rows from all available keystores.
	 * This method collects certificates from both the private keystore
	 * and the known certificates keystore, creating table rows for each certificate found.
	 * 
	 * @return a list of CertificateTableRow objects representing all available certificates
	 */
	private List<CertificateTableRow> getTableRows() {
	    List<CertificateTableRow> dtos = new ArrayList<>();

	    // --- Private keystore ---
	    try {
	        if (ctx.getKeystore() != null) {
	            List<String> privateAliases = Collections.list(ctx.getKeystore().listAliases());
	            for (String alias : privateAliases) {
	                X509Certificate crt = (X509Certificate) ctx.getKeystore().getCertificate(alias);
	                if (crt != null) {
	                    dtos.add(new CertificateTableRow(
	                        alias,
	                        ctx.usePKCS11() ? KeysLocations.PKCS11 : KeysLocations.PKCS12,
	                        crt
	                    ));
	                }
	            }
	        }
	    } catch (KeyStoreException e) {
	        System.err.println("Unable to access private keystore: " + e.getMessage());
	    }

	    // --- Known certificates keystore ---
	    try {
	        if (ctx.getKnownCerts() != null) {
	            List<String> knownAliases = Collections.list(ctx.getKnownCerts().listAliases());
	            for (String alias : knownAliases) {
	                X509Certificate crt = (X509Certificate) ctx.getKnownCerts().getCertificate(alias);
	                if (crt != null) {
	                    dtos.add(new CertificateTableRow(
	                        alias,
	                        KeysLocations.KNWOWN_CERTIFICATES,
	                        crt
	                    ));
	                }
	            }
	        }
	    } catch (KeyStoreException e) {
	        System.err.println("Unable to access known certificates keystore: " + e.getMessage());
	    }

	    return dtos;
	}

	/**
	 * Displays detailed certificate information in a separate form/dialog.
	 * 
	 * @param cert the X.509 certificate to display details for
	 */
	private void showCertificateInformation(X509Certificate cert) {
	    new CertificateDetailsForm(cert);
	}

	/**
	 * Disables private key operations by graying out relevant buttons.
	 * This is typically called when the keystore cannot be unlocked successfully.
	 */
	private void disablePrivateKeyOperations() {
	    btnSign.setEnabled(false);
	    btnDecrypt.setEnabled(false);
	}

	/**
	 * Imports a known certificate from a file into the known certificates keystore.
	 * Prompts the user to select a certificate file and provide an alias for the certificate.
	 * After successful import, updates the table and displays the certificate details.
	 */
	private void importKnownCert() {
	    File certFile = FileDialogUtils.openFileDialog(
	        null,
	        "Import certificate",
	        ".",
	        DefaultExtensions.CRYPTO_PEM,
	        DefaultExtensions.CRYPTO_CER,
	        DefaultExtensions.CRYPTO_CRT,
	        DefaultExtensions.CRYPTO_DER,
	        DefaultExtensions.STD_ANY
	    );
	    X509Certificate cert = null;
	    if (certFile != null) {
	        String alias = DialogUtils.showInputBox(null, "Certificate Alias", "Enter Certificate Alias", "", false);
	        try {
	            cert = X509CertificateLoader.loadFromFile(certFile);
	            ctx.getKnownCerts().addCertificate(alias, cert);
	            ctx.getKnownCerts().save();
	        } catch (CertificateException | IOException e) {
	            e.printStackTrace();
	            DialogUtils.showMessageBox(null, "Invalid certificate", "Invalid certificate!", 
	                e.getMessage(), 
	                JOptionPane.ERROR_MESSAGE);
	        } catch (KeyStoreException e) {
	            e.printStackTrace();
	            DialogUtils.showMessageBox(null, "Error importing certificate", "Error importing certificate!", 
	                e.getMessage(), 
	                JOptionPane.ERROR_MESSAGE);
	        } catch (Exception e) {

	            e.printStackTrace();
	        }
	    }
	    updateTable();
	    showCertificateInformation(cert);
	}	
	
	/**
	 * Imports key pairs from an external keystore file into the application's current keystore.
	 */
	private void importKeypair() {
		if(ctx.usePKCS11()) {
            DialogUtils.showMessageBox(null, "Error importing Keys!", "Importing keys into PKCS11 devices is not supported","", 
	                JOptionPane.ERROR_MESSAGE);
            return;
		}
	    File sourceKeystore = FileDialogUtils.openFileDialog(
		        null,
		        "Import KeyPairs",
		        ".",
		        DefaultExtensions.CRYPTO_PFX,
		        DefaultExtensions.CRYPTO_P12
		    );
	    
	    if(sourceKeystore != null) {
		    String password = DialogUtils.showInputBox(
			        null,
			        "Unlock Keystore",
			        sourceKeystore.getName(),
			        "Password:",
			        true
			    );
	    	AbstractKeystore src = new PKCS12Keystore(sourceKeystore, password.toCharArray());
	    	try {
	    		src.load();
				List<String> aliases = Collections.list(src.listAliases());
				for(String alias : aliases) {
				    String keyPwd = DialogUtils.showInputBox(
					        null,
					        "Unlock Private key",
					        alias,
					        "Password:",
					        true
					    );
				    
				    X509Certificate crt = src.getCertificate(alias);
				    PrivateKey key = src.getPrivateKey(alias, keyPwd.toCharArray());
				    ctx.getKeystore().addPrivateKey(alias, key, keyPwd.toCharArray(), new X509Certificate[] {crt});
				    ctx.getKeystore().save();
				}
				
			} catch (Exception e) {
				e.printStackTrace();
	            DialogUtils.showMessageBox(null, "Error importing Keys!", "Error importing Keys!", 
		                e.getMessage(), 
		                JOptionPane.ERROR_MESSAGE);
			}
	    }
	    updateTable();
	}
	/**
	 * Opens the digital signature form
	 */
	private void sign() {
	    SignForm frm = new SignForm();
	    frm.setVisible();
	}

	/**
	 * Opens the verification form
	 */
	private void verify() {
	    VerifyForm vfrm = new VerifyForm();
	    vfrm.setVisible();
	}

	/**
	 * Opens the encryption form
	 */
	private void encrypt() {
	    EncryptForm encfrm = new EncryptForm();
	    encfrm.setVisible();
	}

	/**
	 * Opens the decryption form
	 */
	private void decrypt() {
	    DecryptForm decfrm = new DecryptForm();
	    decfrm.setVisible();
	}
	
	/**
	 * Opens the settings form
	 */
	protected void settings() {
		SettingsForm settfrm = new SettingsForm();
		settfrm.setVisible();
		
	}
}
