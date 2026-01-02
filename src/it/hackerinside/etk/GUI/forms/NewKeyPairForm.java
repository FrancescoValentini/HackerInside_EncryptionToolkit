package it.hackerinside.etk.GUI.forms;

import javax.swing.JFrame;

import it.hackerinside.etk.GUI.DialogUtils;
import it.hackerinside.etk.GUI.ETKContext;
import it.hackerinside.etk.Utils.X509Builder;
import it.hackerinside.etk.Utils.X509PQCBuilder;
import it.hackerinside.etk.core.Models.HashAlgorithm;
import it.hackerinside.etk.core.Models.PQCAlgorithms;

import javax.swing.JSpinner;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.bouncycastle.util.Arrays;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.security.InvalidParameterException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBox;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class NewKeyPairForm {

	private JFrame frmNewKeypair;
	private ETKContext ctx;
	private JTextField txtbCountryCode;
	private JTextField txtbState;
	private JTextField txtbCommonName;
	private JComboBox cmbAlgorithm;
	private JSpinner spinnerExpDays;
	private Runnable callback;
	private JCheckBox chckbPQC;

	/**
	 * Create the application.
	 */
	public NewKeyPairForm() {
		ctx = ETKContext.getInstance();
		initialize();
	}
	
	public void setVisible() {
		this.frmNewKeypair.setVisible(true);
	}
	
	public void setCallback(Runnable r) {
		this.callback = r;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmNewKeypair = new JFrame();
		frmNewKeypair.setResizable(false);
		frmNewKeypair.setTitle("New Keypair");
		frmNewKeypair.setBounds(100, 100, 651, 445);
		frmNewKeypair.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JLabel lblNewLabel_1 = new JLabel("NEW KEYPAIR");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 27));
		frmNewKeypair.getContentPane().add(lblNewLabel_1, BorderLayout.NORTH);
		
		JPanel panel = new JPanel();
		frmNewKeypair.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		spinnerExpDays = new JSpinner();
		spinnerExpDays.setModel(new SpinnerNumberModel(Integer.valueOf(1095), Integer.valueOf(1), null, Integer.valueOf(1)));
		spinnerExpDays.setFont(new Font("Tahoma", Font.PLAIN, 16));
		spinnerExpDays.setEnabled(true);
		spinnerExpDays.setBounds(166, 116, 85, 23);
		panel.add(spinnerExpDays);
		
		JLabel lblExpire = new JLabel("Expire (Days):");
		lblExpire.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblExpire.setBounds(10, 118, 146, 17);
		panel.add(lblExpire);
		
		txtbCountryCode = new JTextField();
		txtbCountryCode.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtbCountryCode.setEnabled(true);
		txtbCountryCode.setColumns(10);
		txtbCountryCode.setBounds(166, 79, 85, 23);
		panel.add(txtbCountryCode);
		
		JLabel lblCountryCode = new JLabel("Country Code:");
		lblCountryCode.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblCountryCode.setBounds(10, 82, 146, 17);
		panel.add(lblCountryCode);
		
		JLabel lblStateName = new JLabel("State Name:");
		lblStateName.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblStateName.setBounds(10, 48, 146, 17);
		panel.add(lblStateName);
		
		txtbState = new JTextField();
		txtbState.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtbState.setEnabled(true);
		txtbState.setColumns(10);
		txtbState.setBounds(166, 45, 455, 23);
		panel.add(txtbState);
		
		txtbCommonName = new JTextField();
		txtbCommonName.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtbCommonName.setEnabled(true);
		txtbCommonName.setColumns(10);
		txtbCommonName.setBounds(166, 11, 455, 23);
		panel.add(txtbCommonName);
		
		JLabel lblCommonName = new JLabel("Common Name:");
		lblCommonName.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblCommonName.setBounds(10, 14, 146, 17);
		panel.add(lblCommonName);
		
		JLabel lblCurve = new JLabel("CURVE:");
		lblCurve.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblCurve.setBounds(10, 158, 146, 17);
		panel.add(lblCurve);
		
		cmbAlgorithm = new JComboBox();
		cmbAlgorithm.setModel(new DefaultComboBoxModel(new String[] {"secp256r1", "secp384r1", "secp521r1"}));
		cmbAlgorithm.setSelectedIndex(1);
		cmbAlgorithm.setFont(new Font("Tahoma", Font.PLAIN, 14));
		cmbAlgorithm.setBounds(166, 155, 264, 22);
		panel.add(cmbAlgorithm);
		
		JLabel lblNewLabel_1_1 = new JLabel("<html>\r\n<p color=\"red\">\r\nA self-signed certificate is not signed by a recognized Certificate Authority (CA) and, therefore, has no legal value for trustworthy identification by third parties.\r\n</p>\r\n</html>");
		lblNewLabel_1_1.setVerticalAlignment(SwingConstants.TOP);
		lblNewLabel_1_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1_1.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblNewLabel_1_1.setBounds(32, 204, 570, 52);
		panel.add(lblNewLabel_1_1);
		
		JButton btnGenerateCertificate = new JButton("GENERATE");
		btnGenerateCertificate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				generateCertificate();
			}
		});
		btnGenerateCertificate.setFont(new Font("Tahoma", Font.PLAIN, 16));
		btnGenerateCertificate.setBounds(242, 278, 151, 52);
		panel.add(btnGenerateCertificate);
		
		chckbPQC = new JCheckBox("Post Quantum Cryptography");
		chckbPQC.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if(chckbPQC.isSelected()) {
					loadPqcAlgorithms();
					lblCurve.setText("Algorithm:");
				}else {
					loadECCurves();
					lblCurve.setText("Curve:");
				}
			}
		});
		chckbPQC.setBounds(436, 155, 185, 23);
		panel.add(chckbPQC);
	}
	
	private void loadPqcAlgorithms() {
		cmbAlgorithm.removeAllItems();
	    for (PQCAlgorithms alg : PQCAlgorithms.values()) {
	    	cmbAlgorithm.addItem(alg.bcName);
	    }
	}
	
	private void loadECCurves() {
		cmbAlgorithm.removeAllItems();
		cmbAlgorithm.setModel(new DefaultComboBoxModel(new String[] {"secp256r1", "secp384r1", "secp521r1", "SLH-DSA-SHAKE-128s"}));

	}
	
	/**
	 * Generate the certificate with the form data
	 */
	private void generateCertificate() {
		String commonName = txtbCommonName.getText();
		String state = txtbState.getText();
		String country = txtbCountryCode.getText().toUpperCase();
		int exp = (int) spinnerExpDays.getValue();
		String alg = (String) cmbAlgorithm.getSelectedItem();
		
		if(country.length() != 2) {
            DialogUtils.showMessageBox(
                    null,
                    "Invalid parameters!",
                    "Invalid country code!",
                    "Country code must have 2 letters!",
                    JOptionPane.ERROR_MESSAGE
                );
            return;
		}
		
		KeyPair kp = null;
		PQCAlgorithms pqcAlgo;
		X509Certificate crt = null;
		
		boolean errors = false;
		
		try {
        	if(commonName.isBlank() || country.isBlank() || state.isBlank()) throw new InvalidParameterException("Please fill in all the fields!");
        	
        	if(!chckbPQC.isSelected()) { // STANDARD
    			kp = X509Builder.generateECKeyPair(alg);
    			crt = X509Builder.buildCertificate(commonName, country, state, exp, kp.getPublic(), kp.getPrivate());
    			
        	}else { // PQC
        		pqcAlgo = PQCAlgorithms.fromString((String)cmbAlgorithm.getSelectedItem());
        		kp = X509PQCBuilder.generatePQCKeyPair(pqcAlgo);
    			crt = X509PQCBuilder.buildPQCCertificate(
    					commonName, country, state, exp, kp.getPublic(), kp.getPrivate(),pqcAlgo);

        	}

        	saveToKeystore(kp.getPrivate(),crt);
		} catch (Exception e) {
			e.printStackTrace();
            DialogUtils.showMessageBox(
                    null,
                    "Error during certificate generation!",
                    "Error during certificate generation!",
                    e.getMessage(),
                    JOptionPane.ERROR_MESSAGE
                );
            errors = true;
		}
		
		if(!errors) {
			callback.run();
            DialogUtils.showMessageBox(
                    null,
                    "Certificate generated successfully!",
                    "Certificate generated successfully!",
                    "",
                    JOptionPane.INFORMATION_MESSAGE
                );
		}
		
		
	}
	
	/**
	 * Save the key pair in the keystore
	 * 
	 * @param priv the private key
	 * @param crt the x509 certificate
	 * @throws Exception
	 */
	private void saveToKeystore(PrivateKey priv, X509Certificate crt) throws Exception {
		String alias = DialogUtils.showInputBox(
	            null,
	            "Private key alias",
	            "Private key alias",
	            "Alias:"
	        );
		
		char[] pwd = DialogUtils.showPasswordInputBox(
	            null,
	            "Private key password",
	            alias,
	            "Password:"
	        );
		
		try {
	        ctx.getKeystore().addPrivateKey(alias, priv, pwd, new X509Certificate[]{crt});
	        ctx.getKeystore().save();
		}finally {
			if(pwd != null) Arrays.fill(pwd, (char)0x00);
		}
	}
}
