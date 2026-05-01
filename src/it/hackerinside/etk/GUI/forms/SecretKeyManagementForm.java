package it.hackerinside.etk.GUI.forms;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import it.hackerinside.etk.GUI.DialogUtils;
import it.hackerinside.etk.GUI.ETKContext;
import it.hackerinside.etk.GUI.Hex64Filter;
import it.hackerinside.etk.core.Services.KeysManagementService;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import javax.swing.text.AbstractDocument;

import java.awt.event.ActionEvent;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;

public class SecretKeyManagementForm {

    private JFrame frmSymmetricKeyManagement;
    private JList<String> aliasList;
    private DefaultListModel<String> listModel;
    private JTextArea keyTextArea;
    private JTextField kcvField;
    private ETKContext ctx;
    private KeysManagementService kms;

   /* public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                SecretKeyManagementForm window = new SecretKeyManagementForm();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }*/

    public SecretKeyManagementForm() {
    	this.ctx = ETKContext.getInstance();
        initialize();
    }
    
    public void setVisible() {
    	this.frmSymmetricKeyManagement.setVisible(true);
    }

    private void initialize() {
        frmSymmetricKeyManagement = new JFrame("Secret Key Management");
        frmSymmetricKeyManagement.setTitle("Symmetric Key Management");
        frmSymmetricKeyManagement.setResizable(false);
        frmSymmetricKeyManagement.setBounds(100, 100, 709, 408);
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmSymmetricKeyManagement.getContentPane().setLayout(new BorderLayout(10, 10));

        // ===== LEFT PANEL =====
        listModel = new DefaultListModel<>();
        aliasList = new JList<>(listModel);
        aliasList.setFont(new Font("Tahoma", Font.PLAIN, 16));

        JScrollPane listScroll = new JScrollPane(aliasList);
        setTitledBorderFont(listScroll, "Aliases");
        listScroll.setPreferredSize(new Dimension(200, 0));

        // ===== RIGHT PANEL =====
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));

        keyTextArea = new JTextArea(8, 20);
        keyTextArea.setLineWrap(true);
        keyTextArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
        ((AbstractDocument) keyTextArea.getDocument())
        .setDocumentFilter(new Hex64Filter());

        JScrollPane textScroll = new JScrollPane(keyTextArea);
        setTitledBorderFont(textScroll, "Key (HEX)");

        JPanel aliasPanel = new JPanel(new BorderLayout(5, 5));
        kcvField = new JTextField();
        kcvField.setEditable(false);
        kcvField.setFont(new Font("Tahoma", Font.PLAIN, 16));

        
        setTitledBorderFont(aliasPanel, "KCV");
        aliasPanel.add(kcvField, BorderLayout.CENTER);
        rightPanel.add(textScroll, BorderLayout.CENTER);
        rightPanel.add(aliasPanel, BorderLayout.SOUTH);
        

        // ===== BUTTON PANEL =====
        JPanel buttonPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 15));
        buttonPanel.setPreferredSize(new Dimension(140, 0));

        JButton generateBtn = new JButton("RANDOM");
        JButton importBtn = new JButton("IMPORT");
        JButton exportBtn = new JButton("EXPORT");
        JButton deleteBtn = new JButton("DELETE");
        JButton renameBtn = new JButton("RENAME");

        Font btnFont = new Font("Tahoma", Font.PLAIN, 16);
        generateBtn.setFont(btnFont);
        importBtn.setFont(btnFont);
        exportBtn.setFont(btnFont);
        deleteBtn.setFont(btnFont);
        renameBtn.setFont(btnFont);

        buttonPanel.add(generateBtn);
        buttonPanel.add(importBtn);
        buttonPanel.add(exportBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(renameBtn);

        // ===== ACTIONS =====
        generateBtn.addActionListener(this::generateKey);
        importBtn.addActionListener(this::importKey);
        exportBtn.addActionListener(this::exportKey);
        deleteBtn.addActionListener(this::deleteKey);
        renameBtn.addActionListener(this::renameAlias);
        
        keyTextArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {

            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateKcvIfValid();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateKcvIfValid();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateKcvIfValid();
            }
        });

        // ===== LIST SELECTION =====
        aliasList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                kcvField.setText("");
                keyTextArea.setText("");
            }
        });

        // ===== LAYOUT =====
        frmSymmetricKeyManagement.getContentPane().add(listScroll, BorderLayout.WEST);
        frmSymmetricKeyManagement.getContentPane().add(rightPanel, BorderLayout.CENTER);
        frmSymmetricKeyManagement.getContentPane().add(buttonPanel, BorderLayout.EAST);
        
        start();
    }
    
    private void start() {
    	this.kms = new KeysManagementService(ctx);
    	loadKeysAliases();
    }
    
    private void loadKeysAliases() {
    	listModel.removeAllElements();
    	try {
			List<String> aliases = kms.getSecretKeys();
			aliases.forEach(alias -> listModel.addElement(alias));
		} catch (KeyStoreException e) {
			showError("Error loading symmetric keys","Error loading symmetric keys",e);
		}
    }

    // ===== UTILITY =====
    private void setTitledBorderFont(JComponent component, String title) {
        component.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                title,
                0,
                0,
                new Font("Tahoma", Font.BOLD, 14)
        ));
    }

    // ===== ACTION METHODS =====

    private void generateKey(ActionEvent e) {        
        try {
        	byte[] k = new byte[32]; // 256 bits
        	new SecureRandom().nextBytes(k);
        	
        	if(k != null) {
            	keyTextArea.setText(HexFormat.of().formatHex(k).toUpperCase());
    			String kcvValue = HexFormat.of().formatHex(kcv(k)).toUpperCase().substring(0, 6);
    			kcvField.setText(kcvValue);
        	}
        	
        }catch (UnsupportedOperationException e1) {
	        DialogUtils.showMessageBox(
		            null,
		            "Operation not supported!",
		            e1.getMessage(),
		            "",
		            JOptionPane.WARNING_MESSAGE
		        );
		} catch (Exception e1) {
			e1.printStackTrace();
            DialogUtils.showMessageBox(
            		null, 
            		"Error generating Keys!", 
            		"Error generating Keys!", 
	                e1.getMessage(), 
	                JOptionPane.ERROR_MESSAGE
	        );
		}
    }

    private void importKey(ActionEvent e) {
        String key = keyTextArea.getText();
        
		kms.setAliasProvider(() -> DialogUtils.showInputBox(null, "Secret Key Alias", "Secret Key Alias", ""));
        kms.setPwdProvider((x) -> askUnlockSecretKey(x));

        try {
        	validateHexKey(key);
    		boolean res = kms.importSecretKey(hexStringToByteArray(key));
    		if(res) {
    			String kcvValue = HexFormat.of().formatHex(kcv(hexStringToByteArray(key))).toUpperCase().substring(0, 6);
    			kcvField.setText(kcvValue);
    	        DialogUtils.showMessageBox(
    		            null,
    		            "Key successfully imported",
    		            "Key successfully imported, KCV: " + kcvValue,
    		            "",
    		            JOptionPane.INFORMATION_MESSAGE
    		        );
    	        
    	        loadKeysAliases();
    		}
	    	
    	}catch (UnsupportedOperationException e1) {
	        DialogUtils.showMessageBox(
		            null,
		            "Operation not supported!",
		            e1.getMessage(),
		            "",
		            JOptionPane.WARNING_MESSAGE
		        );
		} catch (Exception e1) {
			e1.printStackTrace();
            DialogUtils.showMessageBox(
            		null, 
            		"Error importing Keys!", 
            		"Error importing Keys!", 
	                e1.getMessage(), 
	                JOptionPane.ERROR_MESSAGE
	        );
		}
    }

    private void exportKey(ActionEvent e) {
        String selected = aliasList.getSelectedValue();
        kms.setPwdProvider((alias) -> askUnlockSecretKey(selected));
        
        try {
    	    byte[] k = kms.exportSecretKey(selected);
    	    if(k != null) {
    	    	keyTextArea.setText(HexFormat.of().formatHex(k).toUpperCase());
    	    	kcvField.setText(HexFormat.of().formatHex(kcv(k)).toUpperCase().substring(0, 6));
    	    }
    	    
    	    
        }catch (UnsupportedOperationException e1) {
	        DialogUtils.showMessageBox(
		            null,
		            "Operation not supported!",
		            e1.getMessage(),
		            "",
		            JOptionPane.WARNING_MESSAGE
		        );
		} catch (Exception e1) {
			e1.printStackTrace();
            DialogUtils.showMessageBox(
            		null, 
            		"Error exporting Keys!", 
            		"Error exporting Keys!", 
	                e1.getMessage(), 
	                JOptionPane.ERROR_MESSAGE
	        );
		}
    }
    

    private void deleteKey(ActionEvent e) {
        String selected = aliasList.getSelectedValue();

    	kms.setConfirmationProvider(() ->DialogUtils.showConfirmBox(
	            null, 
	            "DELETING SECRET KEY KEY!", 
	            "DELETING: " + selected, 
	            "You are about to delete a secret key; you will no longer be able to encrypt or decrypt with it.\r\n"
	            + "\r\n"
	            + "The deletion is irreversible; the key cannot be recovered.", 
	            JOptionPane.WARNING_MESSAGE
	        ));
    

        if (selected != null) {
            try {
            	
            	kms.deleteSecretKey(selected);
            	loadKeysAliases();
            	
            }catch(UnsupportedOperationException e1) {
    	        DialogUtils.showMessageBox(
    		            null,
    		            "Operation not supported!",
    		            e1.getMessage(),
    		            "",
    		            JOptionPane.WARNING_MESSAGE
    		        );
    		} catch (Exception e1) {
                e1.printStackTrace();
                DialogUtils.showMessageBox(
    	                null, 
    	                "Error while deleting secret key", 
    	                "Error while deleting secret key", 
    	                e1.getMessage(), 
    	                JOptionPane.ERROR_MESSAGE
    	            );
            }
        }
    }

    private void renameAlias(ActionEvent e) {
        String selected = aliasList.getSelectedValue();

        if (!DialogUtils.showConfirmBox(
                null,
                "Rename secret key alias",
                "Warning: this may affect automatic decryption",
                "You are about to rename a secret key alias.\n\n"
                + "After renaming, automatic key selection may no longer work "
                + "for data encrypted with the previous alias.\n\n"
                + "You can still decrypt the data manually by selecting the correct key.",
                JOptionPane.WARNING_MESSAGE
        )) return;

        if (selected != null) {
        	kms.setAliasProvider(() -> DialogUtils.showInputBox(null, "New Secret Key Alias", "New Secret Key Alias", ""));
            kms.setPwdProvider((x) -> askUnlockSecretKey(x));

            try {
            	
        		String res = kms.renameSecretKeyAlias(selected);
        		if(res != null) {
        	        DialogUtils.showMessageBox(
        		            null,
        		            "Key successfully renamed",
        		            "Key successfully renamed",
        		            selected + " --> " + res,
        		            JOptionPane.INFORMATION_MESSAGE
        		        );
        		}
    	    	
        	}catch (UnsupportedOperationException e1) {
    	        DialogUtils.showMessageBox(
    		            null,
    		            "Operation not supported!",
    		            e1.getMessage(),
    		            "",
    		            JOptionPane.WARNING_MESSAGE
    		        );
    		} catch (Exception e1) {
    			e1.printStackTrace();
                DialogUtils.showMessageBox(
                		null, 
                		"Error renaming Keys!", 
                		"Error renaming Keys!", 
    	                e1.getMessage(), 
    	                JOptionPane.ERROR_MESSAGE
    	        );
    		}
            loadKeysAliases();
        }
    }
    
	private void showError(String title, String message, Exception e) {
	    DialogUtils.showMessageBox(
	            null,
	            title,
	            message,
	            e.getMessage(),
	            JOptionPane.ERROR_MESSAGE
	    );
	    e.printStackTrace();
	}
	
    public char[] askUnlockSecretKey(String alias) {
        return DialogUtils.showPasswordInputBox(
            null,
            "Unlock Secret key",
            "Secret Key: " + alias,
            "Password:"
        );
    }
    
    private byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
    
    private byte[] kcv(byte[] keyBytes) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
		byte[] binaryZeroes = new byte[16];
		Arrays.fill(binaryZeroes, (byte) 01);
		final SecretKey key = new SecretKeySpec(keyBytes, "AES");
		final Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] cipherText = cipher.doFinal(binaryZeroes);
		return cipherText;
    }
    
    private void validateHexKey(String key) {
        if (key == null) throw new IllegalArgumentException("Key cannot be null");

        // 128-bit = 32 hex chars, 256-bit = 64 hex chars
        if (!key.matches("^(?:[0-9a-fA-F]{32}|[0-9a-fA-F]{64})$")) {
            throw new IllegalArgumentException(
                "Invalid key: must be a valid hex string of 128 or 256 bits (32 or 64 hex characters)"
            );
        }
    }
    
    private void updateKcvIfValid() {
        String hex = keyTextArea.getText();

        if (hex == null) {
            kcvField.setText("");
            return;
        }

        hex = hex.trim();

        if (hex.length() != 32 && hex.length() != 64) {
            kcvField.setText("");
            return;
        }

        try {
            byte[] keyBytes = hexStringToByteArray(hex);
            byte[] kcvBytes = kcv(keyBytes);

            String kcvValue = HexFormat.of()
                    .formatHex(kcvBytes)
                    .toUpperCase()
                    .substring(0, 6);

            kcvField.setText(kcvValue);

        } catch (Exception ex) {
            kcvField.setText("");
        }
    }
}