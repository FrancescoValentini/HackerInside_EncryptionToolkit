package it.hackerinside.etk.GUI;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JComboBox;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;

import javax.swing.JCheckBox;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.JSeparator;
import javax.swing.JProgressBar;



public class SignForm {

	private JFrame frmSign;
	private JTextField txtbOutputFile;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SignForm window = new SignForm();
					window.frmSign.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public SignForm() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		try {
		    UIManager.setLookAndFeel( new FlatMacDarkLaf() );
		} catch( Exception ex ) {
		    System.err.println( "Failed to initialize LaF" );
		}
		
		frmSign = new JFrame();
		frmSign.setTitle("SIGN");
		frmSign.setBounds(100, 100, 585, 668);
		frmSign.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		frmSign.getContentPane().add(panel, BorderLayout.CENTER);
		
		JComboBox cmbSignerCert = new JComboBox();
		cmbSignerCert.setFont(new Font("Tahoma", Font.PLAIN, 16));
		
		JLabel lblNewLabel = new JLabel("Signer Certificate");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
		
		JButton btnCertDetails = new JButton("DETAILS");
		btnCertDetails.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		JLabel lblInputFile = new JLabel("Output File");
		lblInputFile.setFont(new Font("Tahoma", Font.PLAIN, 16));
		
		txtbOutputFile = new JTextField();
		txtbOutputFile.setFont(new Font("Tahoma", Font.PLAIN, 16));
		txtbOutputFile.setColumns(10);
		
		JButton btnOpenOutputFile = new JButton("...");
		btnOpenOutputFile.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Signature Settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JButton btnNewButton = new JButton("SIGN");
		btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 18));
		
		JProgressBar progressBar = new JProgressBar();
		progressBar.setEnabled(false);
		progressBar.setFont(new Font("Tahoma", Font.PLAIN, 16));
		progressBar.setIndeterminate(true);
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addContainerGap()
							.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
								.addComponent(panel_1, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 549, Short.MAX_VALUE)
								.addComponent(lblNewLabel, Alignment.LEADING)
								.addComponent(lblInputFile, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE)
								.addGroup(gl_panel.createSequentialGroup()
									.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
										.addComponent(txtbOutputFile, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 446, Short.MAX_VALUE)
										.addComponent(cmbSignerCert, 0, 446, Short.MAX_VALUE))
									.addGap(18)
									.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
										.addComponent(btnOpenOutputFile, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE)
										.addComponent(btnCertDetails)))))
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(245)
							.addComponent(btnNewButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addGap(235)))
					.addContainerGap())
				.addGroup(Alignment.LEADING, gl_panel.createSequentialGroup()
					.addGap(138)
					.addComponent(progressBar, GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)
					.addGap(138))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(23)
					.addComponent(lblNewLabel)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(cmbSignerCert, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnCertDetails))
					.addGap(29)
					.addComponent(lblInputFile, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(txtbOutputFile, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnOpenOutputFile, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
					.addGap(42)
					.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 111, GroupLayout.PREFERRED_SIZE)
					.addGap(34)
					.addComponent(btnNewButton, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE)
					.addGap(41)
					.addComponent(progressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(192, Short.MAX_VALUE))
		);
		panel_1.setLayout(null);
		
		JLabel lblDigestAlgorithm = new JLabel("Digest Algorithm:");
		lblDigestAlgorithm.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblDigestAlgorithm.setBounds(10, 29, 153, 20);
		panel_1.add(lblDigestAlgorithm);
		
		JComboBox cmbSignerCert_1 = new JComboBox();
		cmbSignerCert_1.setFont(new Font("Tahoma", Font.PLAIN, 16));
		cmbSignerCert_1.setBounds(152, 25, 174, 28);
		panel_1.add(cmbSignerCert_1);
		
		JCheckBox chckbxNewCheckBox = new JCheckBox("Detached Signature");
		chckbxNewCheckBox.setFont(new Font("Tahoma", Font.PLAIN, 18));
		chckbxNewCheckBox.setBounds(10, 67, 187, 23);
		panel_1.add(chckbxNewCheckBox);
		
		JCheckBox chckbxNewCheckBox_1 = new JCheckBox("PEM output");
		chckbxNewCheckBox_1.setFont(new Font("Tahoma", Font.PLAIN, 18));
		chckbxNewCheckBox_1.setBounds(210, 67, 141, 23);
		panel_1.add(chckbxNewCheckBox_1);
		panel.setLayout(gl_panel);
		
		JLabel lblNewLabel_1 = new JLabel("SIGN");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 27));
		frmSign.getContentPane().add(lblNewLabel_1, BorderLayout.NORTH);
	}
}
