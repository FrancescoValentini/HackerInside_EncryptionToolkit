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
		frmHackerinsideEncryptionToolkit.setBounds(100, 100, 670, 299);
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
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
							.addComponent(lblKnownCertificatesPath)
							.addComponent(lblNewLabel))
						.addComponent(lblNewLabel_3, GroupLayout.PREFERRED_SIZE, 149, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(spnBufferSize, GroupLayout.PREFERRED_SIZE, 86, GroupLayout.PREFERRED_SIZE)
						.addComponent(chckbUsePem)
						.addGroup(gl_panel.createSequentialGroup()
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addComponent(txtbKnownCertsPath, GroupLayout.PREFERRED_SIZE, 401, GroupLayout.PREFERRED_SIZE)
								.addComponent(txtbKeyStorePath, GroupLayout.PREFERRED_SIZE, 401, GroupLayout.PREFERRED_SIZE))
							.addGap(10)
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addComponent(btnOpenKeystore)
								.addComponent(btnOpenKnownCerts))))
					.addContainerGap(162, Short.MAX_VALUE))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(txtbKeyStorePath, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel)
						.addComponent(btnOpenKeystore, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE))
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(12)
							.addComponent(lblKnownCertificatesPath, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(9)
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING, false)
								.addComponent(btnOpenKnownCerts, 0, 0, Short.MAX_VALUE)
								.addComponent(txtbKnownCertsPath, GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE))))
					.addGap(18)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(spnBufferSize, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel_3))
					.addGap(30)
					.addComponent(chckbUsePem)
					.addContainerGap(439, Short.MAX_VALUE))
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
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_1.createSequentialGroup()
							.addComponent(lblNewLabel_1)
							.addGap(18)
							.addComponent(cmbEncAlgPath, GroupLayout.PREFERRED_SIZE, 339, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_panel_1.createSequentialGroup()
							.addComponent(lblNewLabel_1_1, GroupLayout.PREFERRED_SIZE, 181, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addComponent(cmbHashAlgPath, GroupLayout.PREFERRED_SIZE, 339, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(249, Short.MAX_VALUE))
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel_1)
						.addComponent(cmbEncAlgPath, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel_1_1, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
						.addComponent(cmbHashAlgPath, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(515, Short.MAX_VALUE))
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
					.addComponent(lblConfigurationPath, GroupLayout.PREFERRED_SIZE, 124, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING)
						.addComponent(chckbUsePkcs11)
						.addGroup(gl_panel_2.createSequentialGroup()
							.addComponent(txtPkcs11ConfPath, GroupLayout.PREFERRED_SIZE, 376, GroupLayout.PREFERRED_SIZE)
							.addGap(10)
							.addComponent(btnOpenPKCS11Config, GroupLayout.PREFERRED_SIZE, 47, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(220, Short.MAX_VALUE))
		);
		gl_panel_2.setVerticalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_2.createSequentialGroup()
							.addContainerGap()
							.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING)
								.addComponent(txtPkcs11ConfPath, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnOpenPKCS11Config, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)))
						.addGroup(gl_panel_2.createSequentialGroup()
							.addGap(14)
							.addComponent(lblConfigurationPath, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)))
					.addGap(18)
					.addComponent(chckbUsePkcs11)
					.addContainerGap(512, Short.MAX_VALUE))
		);
		panel_2.setLayout(gl_panel_2);
		
		JPanel panel_3 = new JPanel();
		tabbedPane.addTab("Style", null, panel_3, null);
		panel_3.setLayout(null);
		
		cmbTheme = new JComboBox();
		cmbTheme.setBounds(109, 11, 339, 25);
		cmbTheme.setModel(new DefaultComboBoxModel(UIThemes.values()));
		cmbTheme.setFont(new Font("Tahoma", Font.PLAIN, 14));
		panel_3.add(cmbTheme);
		
		JLabel lblNewLabel_1_2 = new JLabel("Theme:");
		lblNewLabel_1_2.setBounds(10, 15, 89, 17);
		lblNewLabel_1_2.setFont(new Font("Tahoma", Font.PLAIN, 14));
		panel_3.add(lblNewLabel_1_2);
		
		JLabel lblNewLabel_2 = new JLabel("Restart the software to apply the theme");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_2.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblNewLabel_2.setBounds(151, 91, 339, 14);
		panel_3.add(lblNewLabel_2);
		
		
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
		chckbUsePkcs11.setSelected(ctx.usePKCS11());
		
		cmbEncAlgPath.setSelectedItem(ctx.getCipher());
		cmbHashAlgPath.setSelectedItem(ctx.getHashAlgorithm());
		cmbTheme.setSelectedItem(ctx.getTheme());
		spnBufferSize.setValue(ctx.getBufferSize());
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
		ctx.setTheme((UIThemes)cmbTheme.getSelectedItem());
		ctx.setBufferSize((int) spnBufferSize.getValue());
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
