package it.hackerinside.etk.GUI.forms;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import javax.swing.JTabbedPane;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListModel;

import it.hackerinside.etk.GUI.CertificateColumn;
import it.hackerinside.etk.GUI.DialogUtils;
import it.hackerinside.etk.GUI.ETKContext;
import it.hackerinside.etk.GUI.FileDialogUtils;
import it.hackerinside.etk.core.Models.DefaultExtensions;
import it.hackerinside.etk.core.Models.HashAlgorithm;
import it.hackerinside.etk.core.Models.SymmetricAlgorithms;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.stream.Collectors;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;

import it.hackerinside.etk.GUI.UIThemes;
import it.hackerinside.etk.GUI.Utils;
import it.hackerinside.etk.GUI.DTOs.CertificateTableRow;
import it.hackerinside.etk.Utils.X509CertificateLoader;
import it.hackerinside.etk.Utils.X509Utils;

import javax.swing.SwingConstants;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;
import java.awt.Color;

public class SettingsForm {

	private JFrame frmHackerinsideEncryptionToolkit;
	private JTextField txtbKeyStorePath;
	private JTextField txtbKnownCertsPath;
	private JTextField txtPkcs11ConfPath;
	private ETKContext ctx;
	private JCheckBox chckbUsePem;
	private JCheckBox chckbUsePkcs11;
	private JComboBox<HashAlgorithm> cmbHashAlgPath;
	private JComboBox<SymmetricAlgorithms> cmbEncAlgPath;
	private JComboBox<UIThemes> cmbTheme;
	private JSpinner spnBufferSize;
	private JCheckBox chckbPasswordCache;
	private JSpinner spnCacheTimeout;
	private JCheckBox chckbxSKI;
	private JCheckBox chckbHideInvalidCerts;
	private JTextField txtbTrustStorePath;
	private JList<CertificateTableRow> caList;
	private JCheckBox chckbxUseTruststore;
    DefaultListModel<CertificateTableRow> listModel = new DefaultListModel<>();
	private JPanel panel_3_1;
	private JCheckBox chckbRSAOAEP;
	private JCheckBox chckbPKCS11SignOnly;
	private JCheckBox chckbValKeyUsages;
	private JList<CertificateColumn> listboxCertificateTableColumns;
	private Runnable callback;
	private JTextField txtbKeystoreServer;
	private JTextField txtbRemoteUser;
	private JPasswordField txtbRemotePwd;
	private JCheckBox chckbUseRemoteKeystore;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SettingsForm window = new SettingsForm();
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
	public SettingsForm() {
		ctx = ETKContext.getInstance();
		initialize();
	}
	
	public void setVisible() {
		this.frmHackerinsideEncryptionToolkit.setVisible(true);
	}
	
	public void setCallback(Runnable r) {
		this.callback = r;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmHackerinsideEncryptionToolkit = new JFrame();
		frmHackerinsideEncryptionToolkit.setResizable(false);
		frmHackerinsideEncryptionToolkit.setTitle("HackerInside Encryption Toolkit | Settings");
		frmHackerinsideEncryptionToolkit.setBounds(100, 100, 879, 450);
		//frmHackerinsideEncryptionToolkit.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		tabbedPane.setFont(new Font("Tahoma", Font.PLAIN, 16));
		frmHackerinsideEncryptionToolkit.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("General", null, panel, null);
		
		JLabel lblNewLabel = new JLabel("Keystore path:");
		lblNewLabel.setBounds(68, 15, 119, 17);
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		txtbKeyStorePath = new JTextField();
		txtbKeyStorePath.setBounds(197, 11, 381, 25);
		txtbKeyStorePath.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtbKeyStorePath.setColumns(10);
		
		JButton btnOpenKeystore = new JButton("...");
		btnOpenKeystore.setBounds(588, 11, 48, 25);
		btnOpenKeystore.setFont(new Font("Tahoma", Font.PLAIN, 16));
		
		txtbKnownCertsPath = new JTextField();
		txtbKnownCertsPath.setBounds(197, 45, 381, 25);
		txtbKnownCertsPath.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtbKnownCertsPath.setColumns(10);
		
		JLabel lblKnownCertificatesPath = new JLabel("Known Certificates path:");
		lblKnownCertificatesPath.setBounds(10, 48, 183, 17);
		lblKnownCertificatesPath.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		JButton btnOpenKnownCerts = new JButton("...");
		btnOpenKnownCerts.setBounds(588, 45, 48, 25);
		btnOpenKnownCerts.setFont(new Font("Tahoma", Font.PLAIN, 16));
		
		chckbUsePem = new JCheckBox("PEM Encoding");
		chckbUsePem.setBounds(10, 158, 125, 29);
		chckbUsePem.setFont(new Font("Tahoma", Font.PLAIN, 16));
		
		JLabel lblNewLabel_3 = new JLabel("Buffer size (bytes):");
		lblNewLabel_3.setBounds(10, 82, 149, 17);
		lblNewLabel_3.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		spnBufferSize = new JSpinner();
		spnBufferSize.setBounds(197, 78, 86, 25);
		spnBufferSize.setModel(new SpinnerNumberModel(Integer.valueOf(8192), Integer.valueOf(1024), null, Integer.valueOf(1024)));
		spnBufferSize.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		chckbPasswordCache = new JCheckBox("Enable Password Cache");
		chckbPasswordCache.setBounds(301, 111, 191, 29);

		chckbPasswordCache.setFont(new Font("Tahoma", Font.PLAIN, 16));
		
		JLabel lblNewLabel_3_1 = new JLabel("Password cache timeout (s):");
		lblNewLabel_3_1.setBounds(10, 117, 183, 17);
		lblNewLabel_3_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		spnCacheTimeout = new JSpinner();
		spnCacheTimeout.setBounds(197, 113, 86, 25);
		spnCacheTimeout.setModel(new SpinnerNumberModel(0, 0, 120, 1));
		spnCacheTimeout.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		chckbxSKI = new JCheckBox("Use SKI during encryption");
		chckbxSKI.setBounds(10, 190, 255, 29);
		chckbxSKI.setToolTipText("Uses the Subject Key Identifier (SKI) to identify the certificate instead of the issuer name and serial number.");
		chckbxSKI.setFont(new Font("Tahoma", Font.PLAIN, 16));
		
		chckbHideInvalidCerts = new JCheckBox("Hide invalid certificates");
		chckbHideInvalidCerts.setBounds(301, 158, 490, 29);
		chckbHideInvalidCerts.setFont(new Font("Tahoma", Font.PLAIN, 16));
		
		chckbPKCS11SignOnly = new JCheckBox("PKCS#11 sign-only mode");
		chckbPKCS11SignOnly.setBounds(301, 190, 490, 29);

		chckbPKCS11SignOnly.setToolTipText("Prevents decryption issues with PKCS#11 tokens that do not support decryption.");
		chckbPKCS11SignOnly.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panel.setLayout(null);
		
		chckbValKeyUsages = new JCheckBox("Validate Key Usages");
		chckbValKeyUsages.setBounds(10, 222, 215, 29);
		chckbValKeyUsages.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panel.add(chckbValKeyUsages);
		panel.add(chckbPKCS11SignOnly);
		panel.add(chckbHideInvalidCerts);
		panel.add(chckbUsePem);
		panel.add(chckbxSKI);
		panel.add(lblNewLabel);
		panel.add(lblKnownCertificatesPath);
		panel.add(lblNewLabel_3);
		panel.add(lblNewLabel_3_1);
		panel.add(txtbKeyStorePath);
		panel.add(txtbKnownCertsPath);
		panel.add(btnOpenKeystore);
		panel.add(btnOpenKnownCerts);
		panel.add(spnBufferSize);
		panel.add(spnCacheTimeout);
		panel.add(chckbPasswordCache);
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Algorithms", null, panel_1, null);
		
		JLabel lblNewLabel_1 = new JLabel("Default Encryption Algorithm:");
		lblNewLabel_1.setBounds(10, 15, 226, 17);
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		JLabel lblNewLabel_1_1 = new JLabel("Default Hash Algorithm:");
		lblNewLabel_1_1.setBounds(10, 51, 212, 17);
		lblNewLabel_1_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		cmbEncAlgPath = new JComboBox();
		cmbEncAlgPath.setBounds(240, 11, 339, 25);
		cmbEncAlgPath.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		cmbHashAlgPath = new JComboBox();
		cmbHashAlgPath.setBounds(240, 47, 339, 25);
		cmbHashAlgPath.setFont(new Font("Tahoma", Font.PLAIN, 14));
		panel_1.setLayout(null);
		
		chckbRSAOAEP = new JCheckBox("Use RSA OAEP");
		chckbRSAOAEP.setBounds(10, 79, 317, 25);
		chckbRSAOAEP.setFont(new Font("Tahoma", Font.PLAIN, 14));
		panel_1.add(chckbRSAOAEP);
		panel_1.add(lblNewLabel_1);
		panel_1.add(cmbEncAlgPath);
		panel_1.add(lblNewLabel_1_1);
		panel_1.add(cmbHashAlgPath);
		
		JPanel panel_2 = new JPanel();
		tabbedPane.addTab("PKCS#11", null, panel_2, null);
		
		txtPkcs11ConfPath = new JTextField();
		txtPkcs11ConfPath.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtPkcs11ConfPath.setColumns(10);
		
		JLabel lblConfigurationPath = new JLabel("Configuration path:");
		lblConfigurationPath.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		JButton btnOpenPKCS11Config = new JButton("...");
		btnOpenPKCS11Config.setFont(new Font("Tahoma", Font.PLAIN, 16));
		
		chckbUsePkcs11 = new JCheckBox("USE PKCS#11");
		chckbUsePkcs11.setFont(new Font("Tahoma", Font.PLAIN, 16));
		GroupLayout gl_panel_2 = new GroupLayout(panel_2);
		gl_panel_2.setHorizontalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblConfigurationPath, GroupLayout.PREFERRED_SIZE, 163, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING)
						.addComponent(chckbUsePkcs11)
						.addGroup(gl_panel_2.createSequentialGroup()
							.addComponent(txtPkcs11ConfPath, GroupLayout.PREFERRED_SIZE, 373, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnOpenPKCS11Config, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		gl_panel_2.setVerticalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_2.createParallelGroup(Alignment.TRAILING)
						.addComponent(lblConfigurationPath, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING)
							.addComponent(txtPkcs11ConfPath, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
							.addComponent(btnOpenPKCS11Config, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)))
					.addGap(2)
					.addComponent(chckbUsePkcs11)
					.addContainerGap(541, Short.MAX_VALUE))
		);
		panel_2.setLayout(gl_panel_2);
		
		JPanel panel_3 = new JPanel();
		tabbedPane.addTab("Style", null, panel_3, null);
		
		cmbTheme = new JComboBox();
		cmbTheme.setBounds(109, 11, 339, 25);
		cmbTheme.setModel(new DefaultComboBoxModel(UIThemes.values()));
		cmbTheme.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		JLabel lblNewLabel_1_2 = new JLabel("Theme:");
		lblNewLabel_1_2.setBounds(10, 15, 89, 17);
		lblNewLabel_1_2.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		JLabel lblNewLabel_2 = new JLabel("Restart the software to apply the theme");
		lblNewLabel_2.setBounds(10, 282, 515, 25);
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_2.setFont(new Font("Tahoma", Font.BOLD, 16));
		panel_3.setLayout(null);
		panel_3.add(lblNewLabel_1_2);
		panel_3.add(cmbTheme);
		panel_3.add(lblNewLabel_2);
		
		listboxCertificateTableColumns = new JList<>();
		listboxCertificateTableColumns.setFont(new Font("Tahoma", Font.PLAIN, 16));
		listboxCertificateTableColumns.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		listboxCertificateTableColumns.setSelectionModel(new DefaultListSelectionModel() {
		    @Override
		    public void setSelectionInterval(int index0, int index1) {
		        if (isSelectedIndex(index0)) {
		            super.removeSelectionInterval(index0, index1);
		        } else {
		            super.addSelectionInterval(index0, index1);
		        }
		    }
		});

		JScrollPane scrollPane1 = new JScrollPane(listboxCertificateTableColumns);
		scrollPane1.setBounds(109, 94, 339, 177);
		scrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		panel_3.add(scrollPane1);



		
		JLabel lblNewLabel_1_2_1 = new JLabel("Certificate table columns:");
		lblNewLabel_1_2_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel_1_2_1.setBounds(109, 68, 339, 17);
		panel_3.add(lblNewLabel_1_2_1);
		
		JPanel panel_4 = new JPanel();
		tabbedPane.addTab("TrustStore", null, panel_4, null);
		panel_4.setLayout(null);
		
		chckbxUseTruststore = new JCheckBox("Use TrustStore");
		chckbxUseTruststore.setSelected(false);
		chckbxUseTruststore.setFont(new Font("Tahoma", Font.PLAIN, 16));
		chckbxUseTruststore.setBounds(183, 38, 373, 29);
		panel_4.add(chckbxUseTruststore);
		
		txtbTrustStorePath = new JTextField();
		txtbTrustStorePath.setText("");
		txtbTrustStorePath.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtbTrustStorePath.setColumns(10);
		txtbTrustStorePath.setBounds(183, 11, 373, 25);
		panel_4.add(txtbTrustStorePath);
		
		JButton btnOpenTrustStore = new JButton("...");
		btnOpenTrustStore.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openPKCS12(txtbTrustStorePath);
			}
		});
		btnOpenTrustStore.setFont(new Font("Tahoma", Font.PLAIN, 16));
		btnOpenTrustStore.setBounds(562, 11, 45, 25);
		panel_4.add(btnOpenTrustStore);
		
		JLabel lblTruststorePath = new JLabel("TrustStore Path:");
		lblTruststorePath.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblTruststorePath.setBounds(10, 11, 163, 25);
		panel_4.add(lblTruststorePath);
		
		panel_3_1 = new JPanel();
		panel_3_1.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "Certification Authorities", TitledBorder.LEADING, TitledBorder.TOP, null));
		panel_3_1.setBounds(10, 74, 625, 195);
		panel_4.add(panel_3_1);
		panel_3_1.setLayout(new BorderLayout(0, 0));
		
		caList = new JList<>(listModel);
		caList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollPane = new JScrollPane(caList);
		panel_3_1.add(scrollPane, BorderLayout.CENTER);

		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new GridLayout(3, 1, 5, 5));
		panel_3_1.add(buttonsPanel, BorderLayout.EAST);
		
		JButton btnAddCertFile = new JButton("+");

		btnAddCertFile.setFont(new Font("Tahoma", Font.BOLD, 16));
		
		JButton btnRemoveCert = new JButton("-");
		btnRemoveCert.setFont(new Font("Tahoma", Font.BOLD, 16));
		


		JButton btnCertInfo = new JButton("INFO");
		btnCertInfo.setFont(new Font("Tahoma", Font.BOLD, 16));
	
		buttonsPanel.add(btnAddCertFile);
		buttonsPanel.add(btnRemoveCert);
		buttonsPanel.add(btnCertInfo);
		
		JPanel panel_6 = new JPanel();
		tabbedPane.addTab("Remote Keystore", null, panel_6, null);
		panel_6.setLayout(null);
		
		chckbUseRemoteKeystore = new JCheckBox("USE REMOTE KEYSTORE");
		chckbUseRemoteKeystore.setSelected(false);
		chckbUseRemoteKeystore.setFont(new Font("Tahoma", Font.PLAIN, 16));
		chckbUseRemoteKeystore.setBounds(152, 127, 229, 29);
		panel_6.add(chckbUseRemoteKeystore);
		
		txtbKeystoreServer = new JTextField();
		txtbKeystoreServer.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtbKeystoreServer.setColumns(10);
		txtbKeystoreServer.setBounds(152, 11, 404, 25);
		panel_6.add(txtbKeystoreServer);
		
		JLabel lblServer = new JLabel("Server:");
		lblServer.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblServer.setBounds(10, 11, 123, 25);
		panel_6.add(lblServer);
		
		txtbRemoteUser = new JTextField();
		txtbRemoteUser.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtbRemoteUser.setColumns(10);
		txtbRemoteUser.setBounds(152, 45, 404, 25);
		panel_6.add(txtbRemoteUser);
		
		JLabel lblUsername = new JLabel("Username:");
		lblUsername.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblUsername.setBounds(10, 45, 123, 25);
		panel_6.add(lblUsername);
		
		txtbRemotePwd = new JPasswordField();
		txtbRemotePwd.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtbRemotePwd.setColumns(10);
		txtbRemotePwd.setBounds(152, 81, 404, 25);
		panel_6.add(txtbRemotePwd);
		
		JLabel lblServer_1_1 = new JLabel("Password:");
		lblServer_1_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblServer_1_1.setBounds(10, 81, 123, 25);
		panel_6.add(lblServer_1_1);
		
		JPanel panel_5 = new JPanel();
		tabbedPane.addTab("Preferences", null, panel_5, null);
		panel_5.setLayout(null);
		
		JButton btnExportPreferences = new JButton("EXPORT PREFERENCES");

		btnExportPreferences.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnExportPreferences.setBounds(112, 11, 200, 39);
		panel_5.add(btnExportPreferences);
		
		JButton btnImportPreferences = new JButton("IMPORT PREFERENCES");
		btnImportPreferences.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnImportPreferences.setBounds(322, 11, 200, 39);
		panel_5.add(btnImportPreferences);
		
		
		btnRemoveCert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CertificateTableRow selected = caList.getSelectedValue();
				if(selected != null) {
					try {
						ctx.getTrustStore().deleteKeyOrCertificate(selected.keystoreAlias());
						ctx.getTrustStore().save();
					} catch (Exception e1) {
						e1.printStackTrace();
			            DialogUtils.showMessageBox(
			            		null, 
			            		"Error deleting certificate", 
			            		"Error deleting certificate!", 
			                e1.getMessage(), 
			                JOptionPane.ERROR_MESSAGE);
					}
					refreshCaList();
				}
			}
		});
		btnAddCertFile.addActionListener(e -> {
		    X509Certificate cert = loadCertificateFromFile();
		    if (cert != null) {
		        addTrustStoreCertificate(cert);
		    }
		});

		btnImportPreferences.addActionListener(e -> importPreferences());
		btnExportPreferences.addActionListener(e -> exportPreferences());

		btnCertInfo.addActionListener(e -> {
		    CertificateTableRow selected = caList.getSelectedValue();
		    if (selected != null) {
		        showCertificateInfo(selected.original());
		    }
		});

		frmHackerinsideEncryptionToolkit.addWindowListener(new WindowAdapter() {
		    @Override
		    public void windowClosing(WindowEvent e) {
		        save();
		    }
		});

		btnOpenKnownCerts.addActionListener(e -> openPKCS12(txtbKnownCertsPath));
		btnOpenKeystore.addActionListener(e -> openPKCS12(txtbKeyStorePath));
		btnOpenPKCS11Config.addActionListener(e -> openPKCS11Config());

		chckbPasswordCache.addItemListener(e ->
		    handleCache(chckbPasswordCache.isSelected())
		);

		
		chckbxUseTruststore.addItemListener(new ItemListener() {
		    @Override
		    public void itemStateChanged(ItemEvent e) {
		        if (e.getStateChange() == ItemEvent.SELECTED) {
		            try {
		                ctx.initOrLoadTrustStore();
		                refreshCaList();
		            } catch (Exception ex) {
		                ex.printStackTrace();
		            }
		        }
		    }
		});
		
		chckbPKCS11SignOnly.addChangeListener(e -> {
		    boolean signOnly = chckbPKCS11SignOnly.isSelected();

		    if (signOnly) {
		        chckbRSAOAEP.setEnabled(true);
		        chckbRSAOAEP.setSelected(true);
		    } else {
		        chckbRSAOAEP.setSelected(false);
		        chckbRSAOAEP.setEnabled(false);
		    }
		});
		
		start();
	}
	


	/**
	 * Method executed when the form opens
	 */
	private void start() {
		loadVisibleColumns();
		loadEncAlgos();
		loadHashAlgo();
		loadSettings();
		refreshCaList();
	}
	
	
	private void loadVisibleColumns() {
	    DefaultListModel<CertificateColumn> model = new DefaultListModel<>();

	    for (CertificateColumn column : CertificateColumn.values()) {
	        model.addElement(column);
	    }
	    

	    listboxCertificateTableColumns.setModel(model);
	}

	
	/**
	 * Refreshes the list of CA certificates from the trust store.
	 * Clears the current list model and reloads all stored certificates
	 * if the trust store is enabled.
	 */

	private void refreshCaList() {
		if(!ctx.useTrustStore()) return;
		try {
			listModel.clear();
			List<String> aliases = Collections.list(ctx.getTrustStore().listAliases());
			for(String alias : aliases) {
				X509Certificate cert = ctx.getTrustStore().getCertificate(alias);
				listModel.addElement(new CertificateTableRow(alias, null, cert));
			}

		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Displays detailed information for the given X.509 certificate.
	 *
	 * @param crt the certificate to display; ignored if {@code null}
	 */

	private void showCertificateInfo(X509Certificate crt) {
	    if (crt != null) {
	        new CertificateDetailsForm(crt);
	    }
	}
	
	/**
	 * Adds an X.509 CA certificate to the trust store.
	 * Prompts the user for an alias, validates that the certificate
	 * is a CA certificate, and persists it to the trust store.
	 *
	 * @param cert the certificate to add to the trust store
	 */

	private void addTrustStoreCertificate(X509Certificate cert) {
		String alias;
		String cn = X509Utils.extractCN(cert.getSubjectX500Principal().getName());
		
		if(cn != null && !cn.isEmpty()) {
			alias = cn;
		}else {
			alias = DialogUtils.showInputBox(null, "Certificate Alias", "Enter Certificate Alias", "");
		}
		
        if(alias == null || alias.isEmpty()) return;
        
        try {
        	
            if (cert.getBasicConstraints() < 0) {
                throw new Exception("The selected certificate is not a CA certificate.");
            }
        	
            if(Utils.acceptX509Certificate(cert)) {
                ctx.getTrustStore().addCertificate(alias, cert);
                ctx.getTrustStore().save();
                refreshCaList();
            }
        }catch (Exception e) {
            e.printStackTrace();
            DialogUtils.showMessageBox(
            		null, 
            		"Error importing certificate", 
            		"Error importing certificate!", 
                e.getMessage(), 
                JOptionPane.ERROR_MESSAGE);
        }
	}
	
	/**
	 * Loads a CA certificate from a file selected via file dialog.
	 */
	private X509Certificate loadCertificateFromFile() {
	    File certFile = FileDialogUtils.openFileDialog(
	            null,
	            "Select CA certificate",
	            ".",
	            DefaultExtensions.CRYPTO_PEM,
	            DefaultExtensions.CRYPTO_CER,
	            DefaultExtensions.CRYPTO_CRT,
	            DefaultExtensions.CRYPTO_DER
	    );

	    if (certFile != null) {
		    try {
		        return X509CertificateLoader.loadFromFile(certFile);
		    } catch (CertificateException | IOException e) {
		        e.printStackTrace();
				DialogUtils.showMessageBox(
						null, 
						"Invalid certificate", 
						"Invalid certificate!", 
				        e.getMessage(), 
				        JOptionPane.ERROR_MESSAGE);
		        return null;
		    }
	    }
	    return null;
	}
	
	/**
	 * Loads the currently used settings
	 */
	private void loadSettings() {
		
		txtbKeyStorePath.setText(ctx.getKeyStorePath());
		txtbKnownCertsPath.setText(ctx.getKnownCertsPath());
		txtPkcs11ConfPath.setText(ctx.getPkcs11Driver());
		txtbTrustStorePath.setText(ctx.getTrustStorePath());
		
		chckbUsePem.setSelected(ctx.usePEM());
		chckbxSKI.setSelected(ctx.useSKI());
		chckbUsePkcs11.setSelected(ctx.usePKCS11());
		chckbPasswordCache.setSelected(ctx.getUseCacheEntryPasswords());
		chckbHideInvalidCerts.setSelected(ctx.hideInvalidCerts());
		chckbxUseTruststore.setSelected(ctx.useTrustStore());
		chckbPKCS11SignOnly.setSelected(ctx.isPkcs11SignOnly());
		chckbValKeyUsages.setSelected(ctx.validateKeyUsages());
		
		cmbEncAlgPath.setSelectedItem(ctx.getCipher());
		cmbHashAlgPath.setSelectedItem(ctx.getHashAlgorithm());
		cmbTheme.setSelectedItem(ctx.getTheme());
		spnBufferSize.setValue(ctx.getBufferSize());
		spnCacheTimeout.setValue(ctx.getCacheEntryTimeout());
		
		txtbKeystoreServer.setText(ctx.getRemoteKeystoreUrl());
		txtbRemoteUser.setText(ctx.getRemoteKeystoreUser());
		txtbRemotePwd.setText(ctx.getRemoteKeystorePwd());
		chckbUseRemoteKeystore.setSelected(ctx.isUseRemoteKeystore());

		
		chckbPKCS11SignOnly.setSelected(ctx.isPkcs11SignOnly());
		if (ctx.isPkcs11SignOnly()) {
		    chckbRSAOAEP.setEnabled(true);
		    if(ctx.useRsaOaep()) chckbRSAOAEP.setSelected(true);
		    else chckbRSAOAEP.setSelected(false);
		} else {
		    chckbRSAOAEP.setSelected(false);
		    chckbRSAOAEP.setEnabled(false);
		}
		selectVisibleColumns();
	}
	
	
	/**
	 * Save settings automatically when closing the form
	 */
	private void save() {
		if(!checkSettings()) return;
		ctx.setKeyStorePath(txtbKeyStorePath.getText());
		ctx.setKnownCertsPath(txtbKnownCertsPath.getText());
		ctx.setTrustStorePath(txtbTrustStorePath.getText());
		ctx.setPkcs11Driver(txtPkcs11ConfPath.getText());
		ctx.setCipher(((SymmetricAlgorithms) cmbEncAlgPath.getSelectedItem()));
		ctx.setHashAlgorithm((HashAlgorithm) cmbHashAlgPath.getSelectedItem());
		ctx.setUsePkcs11(chckbUsePkcs11.isSelected());
		ctx.setUsePEM(chckbUsePem.isSelected());
		ctx.setUseSKI(chckbxSKI.isSelected());
		ctx.setTheme((UIThemes)cmbTheme.getSelectedItem());
		ctx.setBufferSize((int) spnBufferSize.getValue());
		ctx.setUseCacheEntryPassword(chckbPasswordCache.isSelected());
		ctx.setCacheEntryTimeout((int) spnCacheTimeout.getValue());
		ctx.setHideInvalidCerts(chckbHideInvalidCerts.isSelected());
		ctx.setUseTrustStore(chckbxUseTruststore.isSelected());
		ctx.setUseRsaOaep(chckbRSAOAEP.isSelected());
		ctx.setPkcs11SignOnly(chckbPKCS11SignOnly.isSelected());
		ctx.setValidateKeyUsages(chckbValKeyUsages.isSelected());
		
		ctx.setRemoteKeystoreUrl(txtbKeystoreServer.getText());
		ctx.setRemoteKeystoreUser(txtbRemoteUser.getText());
		ctx.setRemoteKeystorePwd(new String(txtbRemotePwd.getPassword()));
		ctx.setUseRemoteKeystore(chckbUseRemoteKeystore.isSelected());
		
		String selected = listboxCertificateTableColumns
		        .getSelectedValuesList()
		        .stream()
		        .map(Enum::toString)
		        .collect(Collectors.joining(","));
		
		ctx.setVisibleColumns(selected);
		
		if(callback != null) callback.run();

	}
	
	private void selectVisibleColumns() {
	    String visible = ctx.getVisibleColumns();

	    Set<CertificateColumn> selected = Arrays.stream(visible.split(","))
	            .map(String::trim)
	            .map(CertificateColumn::valueOf)
	            .collect(Collectors.toSet());

	    ListModel<CertificateColumn> model = listboxCertificateTableColumns.getModel();

	    listboxCertificateTableColumns.setSelectionMode(
	            ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
	    );

	    for (int i = 0; i < model.getSize(); i++) {
	        CertificateColumn column = model.getElementAt(i);

	        if (selected.contains(column)) {
	            listboxCertificateTableColumns.getSelectionModel()
	                    .addSelectionInterval(i, i);
	        }
	    }
	}


	
	private boolean checkSettings() {
		if(!chckbPKCS11SignOnly.isSelected() && (chckbRSAOAEP.isSelected() || ctx.useRsaOaep())) {
			return DialogUtils.showConfirmBox(
						null, 
						"BE CAREFUL", 
						"You have enabled PKCS11 also for encrypt and decrypt operations!", 
						"The software cannot decrypt documents encrypted with RSA-OAEP.\r\n"
						+ "\r\n"
						+ "RSA-OAEP has been automatically disabled.\r\n"
						+ "\r\n"
						+ "Press OK to confirm the changes; CANCEL to exit without saving.",
						 0
					);
		}
		return true;
	}
	
	/**
	 * Initializes or destroys the cache based on user choice
	 * @param selected
	 */
	private void handleCache(boolean selected) {
		if(selected) ctx.initCache();
		else ctx.destroyCache();
	}
	
	/**
	 * Populates a combo box with all available symmetric algorithms.
	 */
	private void loadEncAlgos() {
		cmbEncAlgPath.removeAllItems();
	    for (SymmetricAlgorithms alg : SymmetricAlgorithms.values()) {
	    	cmbEncAlgPath.addItem(alg);
	    }
	}
	
	/**
	 * Populates a combo box with all available hash algorithms.
	 */
	private void loadHashAlgo() {
		cmbHashAlgPath.removeAllItems();
	    for (HashAlgorithm alg : HashAlgorithm.values()) {
	    	cmbHashAlgPath.addItem(alg);
	    }
	}
	
	/**
	 * Dialog for selecting pkcs12 files
	 * @param txtField the text field where the path is written
	 */
	private void openPKCS12(JTextField txtField) {
	    File file = FileDialogUtils.openFileDialog(
	            null,
	            "Select PKCS12 Container",
	            ".",
	            DefaultExtensions.CRYPTO_P12,
	            DefaultExtensions.CRYPTO_PFX
	    );

	    if (file != null && file.exists() && !file.isDirectory()) {
	    	txtField.setText(file.getAbsolutePath());
	    }
	}
	
	/**
	 * Dialog for selecting the pkcs11 configuration file
	 */
	private void openPKCS11Config() {
	    File file = FileDialogUtils.openFileDialog(
	            null,
	            "Select PKCS11 Config file",
	            "."
	    );

	    if (file != null && file.exists() && !file.isDirectory()) {
	    	txtPkcs11ConfPath.setText(file.getAbsolutePath());
	    }
	}
	
	private void exportPreferences() {
		String fileName = "ETKPreferences_" + ETKContext.ETK_VERSION + ".xml";
		File out = FileDialogUtils.saveFileDialog(
				null, 
				"Export Preferences", 
				fileName, 
				DefaultExtensions.STD_XML
				);
		
		if(out == null) return;
		if(FileDialogUtils.overwriteIfExists(out)) {
			try {
				ctx.exportPreferences(out);
	            DialogUtils.showMessageBox(
	            		null, 
	            		"Preferences exported!", 
	            		"Preferences exported!", 
	                null,
	                JOptionPane.INFORMATION_MESSAGE);
			} catch (IOException | BackingStoreException e1) {
	            DialogUtils.showMessageBox(
	            		null, 
	            		"Error exporting preferences!", 
	            		"Error exporting preferences!", 
	                e1.getMessage(), 
	                JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	private void importPreferences() {
		String fileName = "ETKPreferences_" + ETKContext.ETK_VERSION + ".xml";
		File in = FileDialogUtils.openFileDialog(
				null, 
				"Import Preferences", 
				fileName, 
				DefaultExtensions.STD_XML
				);
		if(in == null) return;
		try {
			ctx.importPreferences(in);
            DialogUtils.showMessageBox(
            		null, 
            		"Preferences loaded!", 
            		"Preferences loaded!", 
            		"Restart the software to ensure that the preferences are applied correctly.",
                JOptionPane.INFORMATION_MESSAGE);
            loadSettings();
		} catch (IOException | InvalidPreferencesFormatException e1) {
            DialogUtils.showMessageBox(
            		null, 
            		"Error importing preferences!", 
            		"Error importing preferences!", 
                e1.getMessage(), 
                JOptionPane.ERROR_MESSAGE);
		}
		
	}
}
