package it.hackerinside.etk.GUI.forms;

import java.awt.EventQueue;

import javax.swing.JFrame;

import it.hackerinside.etk.GUI.ETKContext;
import javax.swing.JSpinner;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.JButton;

public class NewKeyPairForm {

	private JFrame frmNewKeypair;
	private ETKContext ctx;
	private JTextField txtbCountryCode;
	private JTextField txtbState;
	private JTextField txtbCommonName;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					NewKeyPairForm window = new NewKeyPairForm();
					window.frmNewKeypair.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

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

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmNewKeypair = new JFrame();
		frmNewKeypair.setResizable(false);
		frmNewKeypair.setTitle("New Keypair");
		frmNewKeypair.setBounds(100, 100, 651, 445);
		frmNewKeypair.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmNewKeypair.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JLabel lblNewLabel_1 = new JLabel("NEW KEYPAIR");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 27));
		frmNewKeypair.getContentPane().add(lblNewLabel_1, BorderLayout.NORTH);
		
		JPanel panel = new JPanel();
		frmNewKeypair.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		JSpinner spinnerExpDays = new JSpinner();
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
		
		JComboBox cmbCurve = new JComboBox();
		cmbCurve.setModel(new DefaultComboBoxModel(new String[] {"secp256r1", "secp384r1", "secp521r1", "brainpoolP256r1", "brainpoolP384r1", "brainpoolP512r1"}));
		cmbCurve.setSelectedIndex(1);
		cmbCurve.setFont(new Font("Tahoma", Font.PLAIN, 14));
		cmbCurve.setBounds(166, 155, 169, 22);
		panel.add(cmbCurve);
		
		JLabel lblNewLabel_1_1 = new JLabel("<html>\r\n<p color=\"red\">\r\nA self-signed certificate is not signed by a recognized Certificate Authority (CA) and, therefore, has no legal value for trustworthy identification by third parties.\r\n</p>\r\n</html>");
		lblNewLabel_1_1.setVerticalAlignment(SwingConstants.TOP);
		lblNewLabel_1_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1_1.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblNewLabel_1_1.setBounds(32, 204, 570, 52);
		panel.add(lblNewLabel_1_1);
		
		JButton btnGenerateCertificate = new JButton("GENERATE");
		btnGenerateCertificate.setFont(new Font("Tahoma", Font.PLAIN, 16));
		btnGenerateCertificate.setBounds(242, 278, 151, 52);
		panel.add(btnGenerateCertificate);
	}
}
