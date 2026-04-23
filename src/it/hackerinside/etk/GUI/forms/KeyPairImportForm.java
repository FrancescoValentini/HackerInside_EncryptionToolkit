package it.hackerinside.etk.GUI.forms;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import it.hackerinside.etk.GUI.DialogUtils;
import it.hackerinside.etk.GUI.ETKContext;
import it.hackerinside.etk.GUI.FileDialogUtils;
import it.hackerinside.etk.GUI.Utils;
import it.hackerinside.etk.GUI.DTOs.CertificateTableModel;
import it.hackerinside.etk.GUI.DTOs.CertificateTableRow;
import it.hackerinside.etk.GUI.DTOs.KeysLocations;
import it.hackerinside.etk.core.Models.DefaultExtensions;
import it.hackerinside.etk.core.Services.KeysManagementService;
import it.hackerinside.etk.core.keystore.AbstractKeystore;
import it.hackerinside.etk.core.keystore.PKCS12Keystore;

import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KeyPairImportForm {

    private JFrame frame;
    private ETKContext ctx;
    private KeysManagementService kms;
    private File inputKeystoreFile;
    private AbstractKeystore inputKeystore;
    private boolean shouldShow = true;
    private Runnable callback;
    private JTable table;
    private CertificateTableModel tableModel;

    public KeyPairImportForm(File input) {
        initialize();
        this.inputKeystoreFile = input;
    }
    
	public void setCallback(Runnable r) {
		this.callback = r;
	}


    
    private void initialize() {
        ctx = ETKContext.getInstance();

        frame = new JFrame("Import KeyPair");
        frame.setBounds(100, 100, 700, 450);
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout(10, 10));

        // ===== TITLE =====
        JLabel titleLabel = new JLabel("Select the alias to import");
        titleLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        frame.getContentPane().add(titleLabel, BorderLayout.NORTH);

        // ===== TABLE =====
        tableModel = new CertificateTableModel();
        table = new JTable(tableModel);
        table.setFont(new Font("Consolas", Font.PLAIN, 15));
        table.setFillsViewportHeight(true);

        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JScrollPane scrollPane = new JScrollPane(
                table,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
        );

        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        // ===== BUTTON PANEL =====
        JPanel southContainer = new JPanel(new BorderLayout());

        // Info label
	     JLabel infoLabel = new JLabel(
	    		    "<html>You will be prompted to enter the password for the key in the source keystore.<br>" +
	    		    	    "The imported key pair will use the same password.</html>"
	     );
	     
	     infoLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
	     infoLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
	
	     // Button panel
	     JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	
	     JButton btnSelectAll = new JButton("Select All");
	     btnSelectAll.setFont(new Font("Tahoma", Font.PLAIN, 16));
	
	     JButton btnDeselectAll = new JButton("Deselect All");
	     btnDeselectAll.setFont(new Font("Tahoma", Font.PLAIN, 16));
	
	     JButton btnImport = new JButton("Import");
	     btnImport.setFont(new Font("Tahoma", Font.PLAIN, 16));


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
        
        btnSelectAll.addActionListener(e -> {
            if (table.getRowCount() > 0) {
                table.setRowSelectionInterval(0, table.getRowCount() - 1);
            }
        });

        btnDeselectAll.addActionListener(e -> table.clearSelection());

        btnImport.addActionListener(e -> {
            int[] selectedRows = table.getSelectedRows();
            List<String> aliasesToImport = new ArrayList<>();
            for (int row : selectedRows) {
                int modelRow = table.convertRowIndexToModel(row);

                CertificateTableRow data = tableModel.getRow(modelRow);
                aliasesToImport.add(data.keystoreAlias());
            }
            
            importKeyst(aliasesToImport);
        });

        buttonPanel.add(btnSelectAll);
        buttonPanel.add(btnDeselectAll);
        buttonPanel.add(btnImport);
        
        southContainer.add(infoLabel, BorderLayout.NORTH);
        southContainer.add(buttonPanel, BorderLayout.SOUTH);

        //frame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        frame.getContentPane().add(southContainer, BorderLayout.SOUTH);
        
        kms = new KeysManagementService(ctx);
        
        start();
    }
    
    private void start() {
    	if(inputKeystoreFile == null) {
    	    File sourceKeystore = FileDialogUtils.openFileDialog(
    		        null,
    		        "Import KeyPairs",
    		        ".",
    		        DefaultExtensions.CRYPTO_P12,
    		        DefaultExtensions.CRYPTO_PFX
    		    );
    	    if(sourceKeystore == null) {
    	    	closeForm();
    	    	return;
    	    }
    	    inputKeystoreFile= sourceKeystore;
    	}
    	
	    kms.setPwdProvider((alias) -> askUnlockPrivateKey(alias));
	    kms.setCertificateValidationProvider((crt) -> Utils.acceptX509Certificate(crt));

    	loadKeystore();
    	
    }
    
    public char[] askUnlockPrivateKey(String alias) {
        return DialogUtils.showPasswordInputBox(
            null,
            "Unlock Private key",
            "Private Key: " + alias,
            "Password:"
        );
    }
    
    private void loadKeystore() {
    	char[] pwd = askUnlockKeystore(inputKeystoreFile.getAbsolutePath());
    	try {
			if(pwd == null || pwd.length == 0) {
				closeForm();
				return;
			}
	    	inputKeystore = new PKCS12Keystore(inputKeystoreFile, pwd);
	    	
	    	inputKeystore.load(); // Loads the source keystore
	    	
	    	updateTable();
    	} catch (Exception e) {
			e.printStackTrace();
	    	DialogUtils.showMessageBox(null, "Error importing Keys!", "Error importing Keys!", 
	    			e.getMessage(), 
	    			JOptionPane.ERROR_MESSAGE);
	    	closeForm();
		}finally {
			if(pwd != null) Arrays.fill(pwd, (char)0x00);
    	}
    }
    
    private void importKeyst(List<String> aliasesToImport) {
    	try {
			kms.importKeyPair(inputKeystore, aliasesToImport);
			callback.run();
			closeForm();
			return;
		} catch (Exception e) {
			e.printStackTrace();
	    	DialogUtils.showMessageBox(null, "Error importing Keys!", "Error importing Keys!", 
	    			e.getMessage(), 
	    			JOptionPane.ERROR_MESSAGE);
		}
    }
    
    public char[] askUnlockKeystore(String path) {
        return DialogUtils.showPasswordInputBox(
            null,
            "Unlock source keystore",
            path,
            "Password:"
        );
    }
        
    
	private void updateTable() {
	    List<CertificateTableRow> rows = new ArrayList<>();
	    
	    try {
	        rows = kms.getKeystoreEntryDtos(inputKeystore, KeysLocations.PKCS12);
	    } catch (KeyStoreException e) {
	        System.err.println(e.getMessage());
	    }
	    
	    tableModel.setRows(rows);
	}
	
	/**
	 * Displays detailed certificate information in a separate form/dialog.
	 * 
	 * @param cert the X.509 certificate to display details for
	 */
	private void showCertificateInformation(X509Certificate cert) {
	    new CertificateDetailsForm(cert);
	}
	
	private void closeForm() {
	    shouldShow = false;
	    frame.dispose();
	}

	public void setVisible() {
	    if (shouldShow) {
	        this.frame.setVisible(true);
	    }
	}
}
