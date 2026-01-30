package it.hackerinside.etk.GUI.forms;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JTabbedPane;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

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
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.DefaultComboBoxModel;
import it.hackerinside.etk.GUI.UIThemes;
import javax.swing.SwingConstants;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

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

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmHackerinsideEncryptionToolkit = new JFrame();
		frmHackerinsideEncryptionToolkit.setResizable(false);
		frmHackerinsideEncryptionToolkit.setTitle("HackerInside Encryption Toolkit | Settings");
		frmHackerinsideEncryptionToolkit.setBounds(100, 100, 670, 400);
		//frmHackerinsideEncryptionToolkit.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setFont(new Font("Tahoma", Font.PLAIN, 16));
		frmHackerinsideEncryptionToolkit.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("General", null, panel, null);
		
		JLabel lblNewLabel = new JLabel("Keystore path:");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		txtbKeyStorePath = new JTextField();
		txtbKeyStorePath.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtbKeyStorePath.setColumns(10);
		
		JButton btnOpenKeystore = new JButton("...");
		btnOpenKeystore.setFont(new Font("Tahoma", Font.PLAIN, 16));
		
		txtbKnownCertsPath = new JTextField();
		txtbKnownCertsPath.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtbKnownCertsPath.setColumns(10);
		
		JLabel lblKnownCertificatesPath = new JLabel("Known Certificates path:");
		lblKnownCertificatesPath.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		JButton btnOpenKnownCerts = new JButton("...");
		btnOpenKnownCerts.setFont(new Font("Tahoma", Font.PLAIN, 16));
		
		chckbUsePem = new JCheckBox("PEM Encoding");
		chckbUsePem.setFont(new Font("Tahoma", Font.PLAIN, 16));
		
		JLabel lblNewLabel_3 = new JLabel("Buffer size (bytes):");
		lblNewLabel_3.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		spnBufferSize = new JSpinner();
		spnBufferSize.setModel(new SpinnerNumberModel(Integer.valueOf(8192), Integer.valueOf(1024), null, Integer.valueOf(1024)));
		spnBufferSize.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		chckbPasswordCache = new JCheckBox("Enable Password Cache");

		chckbPasswordCache.setFont(new Font("Tahoma", Font.PLAIN, 16));
		
		JLabel lblNewLabel_3_1 = new JLabel("Password cache timeout (s):");
		lblNewLabel_3_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		spnCacheTimeout = new JSpinner();
		spnCacheTimeout.setModel(new SpinnerNumberModel(0, 0, 120, 1));
		spnCacheTimeout.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		chckbxSKI = new JCheckBox("Use SKI during encryption");
		chckbxSKI.setFont(new Font("Tahoma", Font.PLAIN, 16));
		
		chckbHideInvalidCerts = new JCheckBox("Hide invalid certificates");
		chckbHideInvalidCerts.setFont(new Font("Tahoma", Font.PLAIN, 16));
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING, false)
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(58)
							.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 119, GroupLayout.PREFERRED_SIZE))
						.addComponent(lblKnownCertificatesPath, GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
						.addComponent(lblNewLabel_3, GroupLayout.PREFERRED_SIZE, 149, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel_3_1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(chckbHideInvalidCerts)
						.addComponent(chckbxSKI)
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(spnCacheTimeout, GroupLayout.PREFERRED_SIZE, 86, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addComponent(chckbPasswordCache))
						.addGroup(gl_panel.createSequentialGroup()
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addComponent(txtbKeyStorePath, GroupLayout.PREFERRED_SIZE, 381, GroupLayout.PREFERRED_SIZE)
								.addComponent(txtbKnownCertsPath, GroupLayout.PREFERRED_SIZE, 381, GroupLayout.PREFERRED_SIZE))
							.addGap(10)
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING, false)
								.addComponent(btnOpenKeystore, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(btnOpenKnownCerts, GroupLayout.DEFAULT_SIZE, 48, Short.MAX_VALUE)))
						.addComponent(spnBufferSize, GroupLayout.PREFERRED_SIZE, 86, GroupLayout.PREFERRED_SIZE)
						.addComponent(chckbUsePem, GroupLayout.PREFERRED_SIZE, 147, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(161, Short.MAX_VALUE))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(txtbKeyStorePath, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel)
						.addComponent(btnOpenKeystore, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(9)
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING, false)
								.addComponent(txtbKnownCertsPath, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnOpenKnownCerts, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)))
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(12)
							.addComponent(lblKnownCertificatesPath, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)))
					.addGap(8)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel_3)
						.addComponent(spnBufferSize, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
					.addGap(8)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel_3_1, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
						.addComponent(spnCacheTimeout, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
						.addComponent(chckbPasswordCache))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(chckbUsePem)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(chckbxSKI)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(chckbHideInvalidCerts)
					.addContainerGap(387, Short.MAX_VALUE))
		);
		panel.setLayout(gl_panel);
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Algorithms", null, panel_1, null);
		
		JLabel lblNewLabel_1 = new JLabel("Default Encryption Algorithm:");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		JLabel lblNewLabel_1_1 = new JLabel("Default Hash Algorithm:");
		lblNewLabel_1_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		cmbEncAlgPath = new JComboBox();
		cmbEncAlgPath.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		cmbHashAlgPath = new JComboBox();
		cmbHashAlgPath.setFont(new Font("Tahoma", Font.PLAIN, 14));
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.TRAILING, false)
						.addGroup(gl_panel_1.createSequentialGroup()
							.addComponent(lblNewLabel_1, GroupLayout.PREFERRED_SIZE, 226, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(cmbEncAlgPath, GroupLayout.PREFERRED_SIZE, 339, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_panel_1.createSequentialGroup()
							.addComponent(lblNewLabel_1_1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addGap(18)
							.addComponent(cmbHashAlgPath, GroupLayout.PREFERRED_SIZE, 339, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel_1)
						.addComponent(cmbEncAlgPath, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(11)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel_1_1, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
						.addComponent(cmbHashAlgPath, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(534, Short.MAX_VALUE))
		);
		panel_1.setLayout(gl_panel_1);
		
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
		cmbTheme.setModel(new DefaultComboBoxModel(UIThemes.values()));
		cmbTheme.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		JLabel lblNewLabel_1_2 = new JLabel("Theme:");
		lblNewLabel_1_2.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		JLabel lblNewLabel_2 = new JLabel("Restart the software to apply the theme");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_2.setFont(new Font("Tahoma", Font.BOLD, 16));
		GroupLayout gl_panel_3 = new GroupLayout(panel_3);
		gl_panel_3.setHorizontalGroup(
			gl_panel_3.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_3.createSequentialGroup()
					.addGroup(gl_panel_3.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_3.createSequentialGroup()
							.addGap(10)
							.addComponent(lblNewLabel_1_2, GroupLayout.PREFERRED_SIZE, 89, GroupLayout.PREFERRED_SIZE)
							.addGap(10)
							.addComponent(cmbTheme, GroupLayout.PREFERRED_SIZE, 339, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_panel_3.createSequentialGroup()
							.addContainerGap()
							.addComponent(lblNewLabel_2, GroupLayout.PREFERRED_SIZE, 515, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		gl_panel_3.setVerticalGroup(
			gl_panel_3.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_3.createSequentialGroup()
					.addGap(11)
					.addGroup(gl_panel_3.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_3.createSequentialGroup()
							.addGap(4)
							.addComponent(lblNewLabel_1_2))
						.addComponent(cmbTheme, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
					.addGap(55)
					.addComponent(lblNewLabel_2, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
					.addGap(495))
		);
		panel_3.setLayout(gl_panel_3);
		
		
		frmHackerinsideEncryptionToolkit.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				save();
			}
		});
		
		btnOpenKnownCerts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openPKCS12(txtbKnownCertsPath);
			}
		});
		
		btnOpenKeystore.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openPKCS12(txtbKeyStorePath);
			}
		});
		
		btnOpenPKCS11Config.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openPKCS11Config();
			}
		});
		
		chckbPasswordCache.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				handleCache(chckbPasswordCache.isSelected());
			}
		});
		
		start();
	}
	


	/**
	 * Method executed when the form opens
	 */
	private void start() {
		loadEncAlgos();
		loadHashAlgo();
		loadSettings();
	}
	
	/**
	 * Loads the currently used settings
	 */
	private void loadSettings() {
		txtbKeyStorePath.setText(ctx.getKeyStorePath());
		txtbKnownCertsPath.setText(ctx.getKnownCertsPath());
		txtPkcs11ConfPath.setText(ctx.getPkcs11Driver());
		
		chckbUsePem.setSelected(ctx.usePEM());
		chckbxSKI.setSelected(ctx.useSKI());
		chckbUsePkcs11.setSelected(ctx.usePKCS11());
		chckbPasswordCache.setSelected(ctx.getUseCacheEntryPasswords());
		chckbHideInvalidCerts.setSelected(ctx.hideInvalidCerts());
		
		cmbEncAlgPath.setSelectedItem(ctx.getCipher());
		cmbHashAlgPath.setSelectedItem(ctx.getHashAlgorithm());
		cmbTheme.setSelectedItem(ctx.getTheme());
		spnBufferSize.setValue(ctx.getBufferSize());
		spnCacheTimeout.setValue(ctx.getCacheEntryTimeout());
		
	}
	
	
	/**
	 * Save settings automatically when closing the form
	 */
	private void save() {
		ctx.setKeyStorePath(txtbKeyStorePath.getText());
		ctx.setKnownCertsPath(txtbKnownCertsPath.getText());
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
}
