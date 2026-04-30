package it.hackerinside.etk.GUI.forms;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;

import it.hackerinside.etk.GUI.DialogUtils;
import it.hackerinside.etk.GUI.ETKContext;
import it.hackerinside.etk.GUI.Utils;
import it.hackerinside.etk.core.Services.KeysManagementService;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.security.KeyStoreException;
import java.util.HexFormat;
import java.util.List;

public class SecretKeyManagementForm {

    private JFrame frame;
    private JList<String> aliasList;
    private DefaultListModel<String> listModel;
    private JTextArea keyTextArea;
    private JTextField aliasField;
    private ETKContext ctx;
    private KeysManagementService kms;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                SecretKeyManagementForm window = new SecretKeyManagementForm();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public SecretKeyManagementForm() {
    	this.ctx = ETKContext.getInstance();
        initialize();
    }
    
    public void setVisible() {
    	this.frame.setVisible(true);
    }

    private void initialize() {
        frame = new JFrame("Secret Key Management");
        frame.setResizable(false);
        frame.setBounds(100, 100, 709, 408);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout(10, 10));

        // ===== LEFT PANEL =====
        listModel = new DefaultListModel<>();
        aliasList = new JList<>(listModel);
        aliasList.setFont(new Font("Tahoma", Font.PLAIN, 16));

        JScrollPane listScroll = new JScrollPane(aliasList);
        setTitledBorderFont(listScroll, "Alias");
        listScroll.setPreferredSize(new Dimension(200, 0));

        // ===== RIGHT PANEL =====
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));

        keyTextArea = new JTextArea(8, 20);
        keyTextArea.setLineWrap(true);
        keyTextArea.setFont(new Font("Monospaced", Font.PLAIN, 16));

        JScrollPane textScroll = new JScrollPane(keyTextArea);
        setTitledBorderFont(textScroll, "Key (HEX)");

        JPanel aliasPanel = new JPanel(new BorderLayout(5, 5));
        aliasField = new JTextField();
        aliasField.setFont(new Font("Tahoma", Font.PLAIN, 16));

        
        setTitledBorderFont(aliasPanel, "Alias");
        aliasPanel.add(aliasField, BorderLayout.CENTER);
        JPanel kcvPanel = new JPanel(new BorderLayout(5, 5));

        JLabel kcvLabel = new JLabel("KCV:");
        kcvLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));

        JTextField kcvField = new JTextField();
        kcvField.setFont(new Font("Tahoma", Font.PLAIN, 16));
        kcvField.setEditable(false);
        kcvField.setFocusable(false);

        kcvPanel.add(kcvLabel, BorderLayout.WEST);
        kcvPanel.add(kcvField, BorderLayout.CENTER);
        

        rightPanel.add(aliasPanel, BorderLayout.NORTH);
        rightPanel.add(textScroll, BorderLayout.CENTER);
        

        // ===== BUTTON PANEL =====
        JPanel buttonPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 15));
        buttonPanel.setPreferredSize(new Dimension(140, 0));

        JButton generateBtn = new JButton("NEW");
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

        // ===== LIST SELECTION =====
        aliasList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                aliasField.setText(aliasList.getSelectedValue());
            }
        });

        // ===== LAYOUT =====
        frame.getContentPane().add(listScroll, BorderLayout.WEST);
        frame.getContentPane().add(rightPanel, BorderLayout.CENTER);
        frame.getContentPane().add(buttonPanel, BorderLayout.EAST);
        
        start();
    }
    
    private void start() {
    	this.kms = new KeysManagementService(ctx);
    	loadKeysAliases();
    }
    
    private void loadKeysAliases() {
    	aliasList.removeAll();
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
        String alias = aliasField.getText();
		kms.setAliasProvider(() -> DialogUtils.showInputBox(null, "Secret Key Alias", "Secret Key Alias", ""));

        kms.setPwdProvider((x) -> askUnlockSecretKey(x));
        
        try {
        	byte[] k = kms.generateAndStoreSecretKey();
        	if(k != null) {
            	keyTextArea.setText(HexFormat.of().formatHex(k).toUpperCase());
            	
    	        DialogUtils.showMessageBox(
    		            null,
    		            "Key successfully generated",
    		            "The Secret Key has been generated and saved successfully!",
    		            "",
    		            JOptionPane.INFORMATION_MESSAGE
    		        );
        	}
        	loadKeysAliases();
        	
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
        
        if (!alias.isEmpty()) {
            listModel.addElement(alias);
        }
    }

    private void importKey(ActionEvent e) {
        String alias = aliasField.getText();
        String key = keyTextArea.getText();

        System.out.println("Import key for alias: " + alias + " value: " + key);

        if (!alias.isEmpty()) {
            listModel.addElement(alias);
        }
    }

    private void exportKey(ActionEvent e) {
        String selected = aliasList.getSelectedValue();
        kms.setPwdProvider((alias) -> askUnlockSecretKey(selected));
        
        try {
    	    byte[] k = kms.exportSecretKey(selected);
    	    if(k != null) keyTextArea.setText(HexFormat.of().formatHex(k).toUpperCase());
    	    
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
        String newAlias = aliasField.getText();

        System.out.println("Rename alias: " + selected + " -> " + newAlias);

        if (selected != null && !newAlias.isEmpty()) {
            int index = aliasList.getSelectedIndex();
            listModel.set(index, newAlias);
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
    
    
	
}