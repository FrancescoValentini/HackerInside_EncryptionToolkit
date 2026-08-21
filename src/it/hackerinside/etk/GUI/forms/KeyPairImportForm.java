package it.hackerinside.etk.GUI.forms;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import it.hackerinside.etk.GUI.ColumnVisibilityManager;
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

	// Certificate table
	private JTable table;
	private CertificateTableModel tableModel;
	private ColumnVisibilityManager columnsManager;

	// Alias list
	private JList<String> aliasList;
	private DefaultListModel<String> aliasListModel;

	// Tabs
	private JTabbedPane tabbedPane;
	public KeyPairImportForm(File input) {
		this.inputKeystoreFile = input;
		initialize();
	}

	public void setCallback(Runnable r) {
		this.callback = r;
	}

	private void initialize() {
		ctx = ETKContext.getInstance();
		frame = new JFrame("Import KeyPair");
		frame.setBounds(100, 100, 700, 450);

		// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(10, 10));
		JLabel titleLabel = new JLabel("Select the alias to import");

		titleLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));

		titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

		frame.getContentPane().add(titleLabel, BorderLayout.NORTH);
		tabbedPane = new JTabbedPane();
		tabbedPane.setFont(new Font("Tahoma", Font.PLAIN, 16));

		// TAB 1 - CERTIFICATES
		tableModel = new CertificateTableModel();
		table = new JTable(tableModel);
		table.setFont(new Font("Consolas", Font.PLAIN, 15));
		table.setFillsViewportHeight(true);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		columnsManager = new ColumnVisibilityManager(table);
		JScrollPane tableScrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		tableScrollPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
		JPanel certificatesPanel = new JPanel(new BorderLayout());
		certificatesPanel.add(tableScrollPane, BorderLayout.CENTER);
		tabbedPane.addTab("Certificates", certificatesPanel);


		// TAB 2 - ALIASES

		aliasListModel = new DefaultListModel<>();
		aliasList = new JList<>(aliasListModel);
		aliasList.setFont(new Font("Consolas", Font.PLAIN, 15));
		aliasList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		aliasList.setVisibleRowCount(15);
		JScrollPane aliasScrollPane = new JScrollPane(aliasList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		aliasScrollPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
		JPanel aliasesPanel = new JPanel(new BorderLayout());
		aliasesPanel.add(aliasScrollPane, BorderLayout.CENTER);
		tabbedPane.addTab("Symmetric Keys", aliasesPanel);
		frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);

		// DOUBLE CLICK - CERTIFICATE TABLE
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
					int row = table.getSelectedRow();
					int modelRow = table.convertRowIndexToModel(row);
					CertificateTableRow selected = tableModel.getRow(modelRow);
					if (selected != null) showCertificateInformation(selected.original());
				}
			}
		});

		JPanel southContainer = new JPanel(new BorderLayout());


		JLabel infoLabel = new JLabel(
				"<html>" + "You will be prompted to enter the password " + "for the key in the source keystore.<br>"
						+ "The imported key pair will use the same password." + "</html>");

		infoLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));

		infoLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

		// BUTTON PANEL

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton btnSelectAll = new JButton("Select All");
		btnSelectAll.setFont(new Font("Tahoma", Font.PLAIN, 16));
		JButton btnDeselectAll = new JButton("Deselect All");
		btnDeselectAll.setFont(new Font("Tahoma", Font.PLAIN, 16));
		JButton btnImport = new JButton("Import");
		btnImport.setFont(new Font("Tahoma", Font.PLAIN, 16));

		// SELECT ALL

		btnSelectAll.addActionListener(e -> {
			if (tabbedPane.getSelectedIndex() == 0) {
				// Certificates tab
				if (table.getRowCount() > 0) {
					table.setRowSelectionInterval(0, table.getRowCount() - 1);
				}
			} else { // Aliases tab
				if (aliasListModel.getSize() > 0) {
					aliasList.setSelectionInterval(0, aliasListModel.getSize() - 1);
				}
			}
		});

		// DESELECT ALL
		btnDeselectAll.addActionListener(e -> {
			if (tabbedPane.getSelectedIndex() == 0) {
				table.clearSelection();
			} else {
				aliasList.clearSelection();
			}
		});

		// IMPORT
		btnImport.addActionListener(e -> {
			List<String> aliasesToImport = new ArrayList<>();
			
			if (tabbedPane.getSelectedIndex() == 0) {
				int[] selectedRows = table.getSelectedRows();
				for (int row : selectedRows) {
					int modelRow = table.convertRowIndexToModel(row);
					CertificateTableRow data = tableModel.getRow(modelRow);
					if (data != null && data.keystoreAlias() != null) {
						aliasesToImport.add(data.keystoreAlias());
					}
				}
				if (aliasesToImport.isEmpty()) {
					DialogUtils.showMessageBox(null, "Import Keys", "Import Keys", "Please select at least one alias.",
							JOptionPane.WARNING_MESSAGE);
					return;
				}
				importKeyPairs(aliasesToImport);
			} else {
				List<String> symAliases = aliasList.getSelectedValuesList();
				if (symAliases.isEmpty()) {
					DialogUtils.showMessageBox(null, "Import Keys", "Import Keys", "Please select at least one alias.",
							JOptionPane.WARNING_MESSAGE);
					return;
				}
				importSymmetricKeys(symAliases);
			}
		});
		buttonPanel.add(btnSelectAll);
		buttonPanel.add(btnDeselectAll);
		buttonPanel.add(btnImport);
		southContainer.add(infoLabel, BorderLayout.NORTH);
		southContainer.add(buttonPanel, BorderLayout.SOUTH);
		frame.getContentPane().add(southContainer, BorderLayout.SOUTH);
		kms = new KeysManagementService(ctx);
		start();
	}

	private void updateTableColumns() {
		columnsManager.hideAll();
		columnsManager.showColumns(ctx.getVisibleColumns());
	}

	private void start() {
		updateTableColumns();
		if (inputKeystoreFile == null) {
			File sourceKeystore = FileDialogUtils.openFileDialog(null, "Import KeyPairs", ".",
					DefaultExtensions.CRYPTO_P12, DefaultExtensions.CRYPTO_PFX);

			if (sourceKeystore == null) {
				closeForm();
				return;
			}
			inputKeystoreFile = sourceKeystore;
		}

		kms.setCertificateValidationProvider((crt) -> Utils.acceptX509Certificate(crt));

		// Load source keystore
		loadKeystore();
	}

	public char[] askUnlockPrivateKey(String alias) {
		return DialogUtils.showPasswordInputBox(null, "Unlock Private key", "Private Key: " + alias, "Password:");
	}
	
	public char[] askUnlockSecretKey(String alias) {
		return DialogUtils.showPasswordInputBox(null, "Unlock Symmetric key", "Symmetric Key: " + alias, "Password:");
	}

	public char[] askUnlockKeystore(String path) {
		return DialogUtils.showPasswordInputBox(null, "Unlock source keystore", path, "Password:");
	}

	private void loadKeystore() {
		char[] pwd = askUnlockKeystore(inputKeystoreFile.getAbsolutePath());

		try {
			if (pwd == null || pwd.length == 0) {
				closeForm();
				return;
			}

			inputKeystore = new PKCS12Keystore(inputKeystoreFile, pwd);

			// Load source keystore
			inputKeystore.load();

			updateTable();
			loadKeysAliases();
		} catch (Exception e) {
			e.printStackTrace();
			String message = e.getMessage() != null ? e.getMessage() : "Unknown error";

			DialogUtils.showMessageBox(null, "Error importing Keys!", "Error importing Keys!", message,
					JOptionPane.ERROR_MESSAGE);

			closeForm();

		} finally {
			if (pwd != null)
				Arrays.fill(pwd, (char) 0x00);
		}
	}

	private void loadKeysAliases() {
		aliasListModel.removeAllElements();

		try {
			List<String> aliases = inputKeystore.listSymmetricKeyAliases();
			aliases.forEach(alias -> aliasListModel.addElement(alias));
		} catch (KeyStoreException e) {
			DialogUtils.showMessageBox(null, "Error loading Keys!", "Error loading Keys!", e.getMessage(),
					JOptionPane.ERROR_MESSAGE);
		}
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

	private void importKeyPairs(List<String> aliasesToImport) {
		kms.setPwdProvider((alias) -> askUnlockPrivateKey(alias));
		try {
			kms.importKeyPair(inputKeystore, aliasesToImport);
			if (callback != null)
				callback.run();
			DialogUtils.showMessageBox(null, 
					"Import completed successfully!", 
					"Imported asymmetric keys:",
					String.join(", ", aliasesToImport),
					JOptionPane.INFORMATION_MESSAGE);
			closeForm();

		} catch (Exception e) {
			e.printStackTrace();
			String message = e.getMessage() != null ? e.getMessage() : "Unknown error";

			DialogUtils.showMessageBox(null, "Error importing Keys!", "Error importing Keys!", message,
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void importSymmetricKeys(List<String> aliasesToImport) {
		kms.setPwdProvider((alias) -> askUnlockSecretKey(alias));
		try {
			kms.importSecretKeys(inputKeystore, aliasesToImport);
			if (callback != null)
				callback.run();
			DialogUtils.showMessageBox(null, 
					"Import completed successfully!", 
					"Imported symmetric keys:",
					String.join(", ", aliasesToImport),
					JOptionPane.INFORMATION_MESSAGE);
			closeForm();

		} catch (Exception e) {
			e.printStackTrace();
			String message = e.getMessage() != null ? e.getMessage() : "Unknown error";

			DialogUtils.showMessageBox(null, "Error importing Keys!", "Error importing Keys!", message,
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Displays detailed certificate information in a separate form/dialog.
	 *
	 * @param cert the X.509 certificate to display
	 */
	private void showCertificateInformation(X509Certificate cert) {
		new CertificateDetailsForm(cert);
	}

	private void closeForm() {
		shouldShow = false;
		frame.dispose();
	}

	public void setVisible() {
		if (shouldShow)
			frame.setVisible(true);
	}
}
