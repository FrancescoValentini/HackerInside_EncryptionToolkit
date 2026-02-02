package it.hackerinside.etk.GUI.forms;

import javax.swing.JFrame;

import it.hackerinside.etk.GUI.DialogUtils;
import it.hackerinside.etk.GUI.ETKContext;
import it.hackerinside.etk.Utils.X509Builder;
import it.hackerinside.etk.Utils.X509PQCBuilder;
import it.hackerinside.etk.core.Models.PQCAlgorithms;

import javax.swing.JSpinner;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.util.Arrays;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.security.InvalidParameterException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBox;
import javax.swing.JList;

public class NewKeyPairForm {
	private class KeyUsageItem {

	    private final int bit;
	    private final String displayName;

	    public KeyUsageItem(int bit, String displayName) {
	        this.bit = bit;
	        this.displayName = displayName;
	    }

	    public int getBit() {
	        return bit;
	    }

	    @Override
	    public String toString() {
	        return displayName;
	    }
	}
	private JFrame frmNewKeypair;
	private ETKContext ctx;
	private JTextField txtbCountryCode;
	private JTextField txtbState;
	private JTextField txtbCommonName;
	private JComboBox<String> cmbAlgorithm;
	private JSpinner spinnerExpDays;
	private Runnable callback;
	private JCheckBox chckbPQC;
	private JCheckBox chckbX500;
	private JList<KeyUsageItem> listKeyUsage;
	private JLabel lblCommonName;
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
		frmNewKeypair.setBounds(100, 100, 651, 627);
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
		
		lblCommonName = new JLabel("Common Name:");
		lblCommonName.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblCommonName.setBounds(10, 14, 146, 17);
		panel.add(lblCommonName);
		
		JLabel lblCurve = new JLabel("CURVE:");
		lblCurve.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblCurve.setBounds(10, 158, 146, 17);
		panel.add(lblCurve);
		
		cmbAlgorithm = new JComboBox();
		cmbAlgorithm.setFont(new Font("Tahoma", Font.PLAIN, 14));
		cmbAlgorithm.setBounds(166, 155, 264, 22);
		panel.add(cmbAlgorithm);
		
		JLabel lblNewLabel_1_1 = new JLabel("<html>\r\n<p color=\"red\">\r\nA self-signed certificate is not signed by a recognized Certificate Authority (CA) and, therefore, has no legal value for trustworthy identification by third parties.\r\n</p>\r\n</html>");
		lblNewLabel_1_1.setVerticalAlignment(SwingConstants.TOP);
		lblNewLabel_1_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1_1.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblNewLabel_1_1.setBounds(32, 390, 570, 52);
		panel.add(lblNewLabel_1_1);
		
		JButton btnGenerateCertificate = new JButton("GENERATE");
		btnGenerateCertificate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				generateCertificate();
			}
		});
		btnGenerateCertificate.setFont(new Font("Tahoma", Font.PLAIN, 16));
		btnGenerateCertificate.setBounds(242, 464, 151, 52);
		panel.add(btnGenerateCertificate);
		
		chckbPQC = new JCheckBox("Post Quantum Cryptography");
		chckbPQC.setFont(new Font("Tahoma", Font.PLAIN, 11));
		chckbPQC.addItemListener(e -> {
				if ( e.getStateChange() == ItemEvent.SELECTED) {
					loadPqcAlgorithms();
					lblCurve.setText("Algorithm:");
					listKeyUsage.setEnabled(false);
				}else {
					loadECCurves();
					lblCurve.setText("CURVE:");
					listKeyUsage.setEnabled(true);
				}
			
		});
		chckbPQC.setBounds(436, 155, 185, 23);
		panel.add(chckbPQC);
		
		JLabel lblNewLabel = new JLabel("Custom X.500:");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel.setBounds(10, 186, 526, 23);
		panel.add(lblNewLabel);
		
		chckbX500 = new JCheckBox("Use a custom X.500 string (advanced)");
		chckbX500.setFont(new Font("Tahoma", Font.PLAIN, 11));
		chckbX500.setBounds(166, 186, 370, 23);
		chckbX500.addItemListener(e -> {
		    if (e.getStateChange() == ItemEvent.SELECTED ) {
		        lblCommonName.setText("X500 String:");
		        txtbCountryCode.setEnabled(false);
		        txtbState.setEnabled(false);
		        txtbCountryCode.setText("");
		        txtbState.setText("");
		    } else {
		        lblCommonName.setText("Common Name:");
		        txtbCountryCode.setEnabled(true);
		        txtbState.setEnabled(true);
		    }
		});

		panel.add(chckbX500);
		
		listKeyUsage = new JList();
		listKeyUsage.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		listKeyUsage.setSelectionModel(new DefaultListSelectionModel() {
		    @Override
		    public void setSelectionInterval(int index0, int index1) {
		        if (isSelectedIndex(index0)) {
		            removeSelectionInterval(index0, index1);
		        } else {
		            addSelectionInterval(index0, index1);
		        }
		    }
		});
		
		listKeyUsage.addMouseListener(new MouseAdapter() {
		    @Override
		    public void mousePressed(MouseEvent e) {
		        if (SwingUtilities.isRightMouseButton(e)) {
		            listKeyUsage.clearSelection();
		            loadKeyUsages(); // reset
		        }
		    }
		});


		listKeyUsage.setBounds(166, 236, 264, 105);

		panel.add(listKeyUsage);
		
		JLabel lblNewLabel_2 = new JLabel("Key Usage:");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel_2.setBounds(10, 231, 146, 23);
		panel.add(lblNewLabel_2);
		
		loadKeyUsages();
		loadECCurves();
	}
	
	private void loadPqcAlgorithms() {
		cmbAlgorithm.removeAllItems();
	    for (PQCAlgorithms alg : PQCAlgorithms.values()) {
	    	cmbAlgorithm.addItem(alg.bcName);
	    }
	}
	
	private void loadECCurves() {
		cmbAlgorithm.removeAllItems();
		cmbAlgorithm.setModel(
			    new DefaultComboBoxModel<>(new String[] {
			            "secp256r1",
			            "secp384r1",
			            "secp521r1",
			            "brainpoolP256r1",
			            "brainpoolP384r1",
			            "brainpoolP512r1"
			        }));
		cmbAlgorithm.setSelectedIndex(1);
	}
	
	private void loadKeyUsages() {
	    DefaultListModel<KeyUsageItem> model = new DefaultListModel<>();

	    model.addElement(new KeyUsageItem(KeyUsage.digitalSignature, "Digital Signature"));
	    model.addElement(new KeyUsageItem(KeyUsage.nonRepudiation, "Non Repudiation"));
	    model.addElement(new KeyUsageItem(KeyUsage.keyEncipherment, "Key Encipherment"));
	    model.addElement(new KeyUsageItem(KeyUsage.dataEncipherment, "Data Encipherment"));
	    model.addElement(new KeyUsageItem(KeyUsage.keyAgreement, "Key Agreement"));

	    listKeyUsage.setModel(model);
	    listKeyUsage.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

	    int defaultUsage =
	            KeyUsage.digitalSignature |
	            KeyUsage.nonRepudiation |
	            KeyUsage.dataEncipherment;

	    for (int i = 0; i < model.size(); i++) {
	        if ((defaultUsage & model.get(i).getBit()) != 0) {
	            listKeyUsage.addSelectionInterval(i, i);
	        }
	    }
	}

	
	/**
	 * Generate the certificate with the form data
	 */
	private void generateCertificate() {
		String alg = (String) cmbAlgorithm.getSelectedItem();
		
		KeyPair kp = null;
		PQCAlgorithms pqcAlgo;
		X509Certificate crt = null;
		
		boolean errors = false;
		
		try {

        	if(!chckbPQC.isSelected()) { // STANDARD
    			kp = X509Builder.generateECKeyPair(alg);
    			
        	}else { // PQC
        		pqcAlgo = PQCAlgorithms.fromString((String)cmbAlgorithm.getSelectedItem());
        		kp = X509PQCBuilder.generatePQCKeyPair(pqcAlgo);
        	}
        	
        	if(!chckbX500.isSelected()) {
        		crt = generateNormalCertificate(kp);
        	}else {
        		crt = generateCustomNameCertificate(kp);
        	}
        	
        	if(crt != null) saveToKeystore(kp.getPrivate(),crt);
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
	 * Generates a self-signed X.509 certificate using a custom X.500 distinguished name.
	 * Depending on user selections, this method creates either a standard or a PQC-based
	 * certificate with the appropriate key usage and algorithm.
	 *
	 * @param kp the key pair used to populate and/or sign the certificate
	 * @return the generated {@link X509Certificate}
	 * @throws Exception if validation fails or certificate generation encounters an error
	 */
	private X509Certificate generateCustomNameCertificate(KeyPair kp) throws Exception {
		String commonName = txtbCommonName.getText();
		int exp = (int) spinnerExpDays.getValue();
		if(chckbX500.isSelected()) {
        	if(commonName.isBlank()) throw new InvalidParameterException("Please fill in all the fields!");
		}
		X500Name name = generateX500Name();
	
    	if(!chckbPQC.isSelected()) { // STANDARD
    		KeyUsage ku  = getSelectedKeyUsage();
			return X509Builder.buildCertificate(name,exp,ku,kp.getPublic(), kp.getPrivate());
			
    	}else { // PQC
    		PQCAlgorithms pqcAlgo = PQCAlgorithms.fromString((String)cmbAlgorithm.getSelectedItem());

			return X509PQCBuilder.buildPQCCertificate(
					name, exp, kp.getPublic(), kp.getPrivate(),pqcAlgo);

    	}
		
	}
	
	/**
	 * Generates a self-signed X.509 certificate using individual subject fields
	 * (Common Name, State, Country). Depending on user selections, this method creates
	 * either a standard or a PQC-based certificate.
	 *
	 * @param kp the key pair used to populate and/or sign the certificate
	 * @return the generated {@link X509Certificate}
	 * @throws Exception if input validation fails or certificate generation encounters an error
	 */
	private X509Certificate generateNormalCertificate(KeyPair kp) throws Exception {
		String commonName = txtbCommonName.getText();
		String state = txtbState.getText();
		String country = txtbCountryCode.getText().toUpperCase();
		int exp = (int) spinnerExpDays.getValue();
		
    	if(commonName.isBlank() || country.isBlank() || state.isBlank()) throw new InvalidParameterException("Please fill in all the fields!");
		
		if(country.length() != 2) {
            throw new InvalidParameterException("Country code must have 2 letters!");
		}
		
    	if(!chckbPQC.isSelected()) { // STANDARD
    		KeyUsage ku  = getSelectedKeyUsage();
			return X509Builder.buildCertificate(commonName, country, state, exp, ku, kp.getPublic(), kp.getPrivate());
    		
			
    	}else { // PQC
    		PQCAlgorithms pqcAlgo = PQCAlgorithms.fromString((String)cmbAlgorithm.getSelectedItem());
			return X509PQCBuilder.buildPQCCertificate(
					commonName, country, state, exp, kp.getPublic(), kp.getPrivate(),pqcAlgo);
    	}
    	
	}
	
	/**
	 * Builds a {@link KeyUsage} object from the key usage values currently selected
	 * by the user.
	 *
	 * @return the constructed {@link KeyUsage} representing the selected usages
	 * @throws Exception if no key usage is selected or an error occurs during construction
	 */
	private KeyUsage getSelectedKeyUsage() throws Exception{
		int keyUsageBits = 0;
		if(listKeyUsage.getSelectedValuesList().size() == 0) throw new InvalidParameterException("You must select at least one key usage!");
		
		for (KeyUsageItem item : listKeyUsage.getSelectedValuesList()) {
		    keyUsageBits |= item.getBit();
		}

		return new KeyUsage(keyUsageBits);
	}
	
	/**
	 * Generates an {@link X500Name} from the user-provided distinguished name string.
	 * Displays an error dialog if the DN cannot be parsed.
	 *
	 * @return the parsed {@link X500Name}, or {@code null} if parsing fails
	 */
	private X500Name generateX500Name() {
	    String dn = txtbCommonName.getText();
	    
	    try {
	        return new X500Name(dn);
	    } catch (IllegalArgumentException e) {
            DialogUtils.showMessageBox(
                    null,
                    "X500 DN Error!",
                    "Error during X500Name parsing!",
                    e.getMessage()+ "\n\nExample: "+ "CN=DEMO, C=IT",
                    JOptionPane.ERROR_MESSAGE
                );
	    }
	    
	    return null;
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
			if(ctx.getKeystore().containsAlias(alias)) throw new Exception("Unable to save, alias is already in use!");
	        ctx.getKeystore().addPrivateKey(alias, priv, pwd, new X509Certificate[]{crt});
	        ctx.getKeystore().save();
		}finally {
			if(pwd != null) Arrays.fill(pwd, (char)0x00);
		}
	}
}
