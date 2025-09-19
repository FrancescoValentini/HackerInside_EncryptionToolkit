package it.hackerinside.etk.GUI;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.JCheckBox;
import javax.swing.border.EtchedBorder;
import java.awt.Color;
import javax.swing.JProgressBar;

public class EncryptForm {

	private JFrame frmEncrypt;
	private JTextField txtbCertFile;
	private JTextField txtbOutputFile;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					EncryptForm window = new EncryptForm();
					window.frmEncrypt.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public EncryptForm() {
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
		
		
		frmEncrypt = new JFrame();
		frmEncrypt.setTitle("Encrypt");
		frmEncrypt.setBounds(100, 100, 593, 715);
		frmEncrypt.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JLabel lblNewLabel = new JLabel("ENCRYPT");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 27));
		frmEncrypt.getContentPane().add(lblNewLabel, BorderLayout.NORTH);
		
		JPanel panel = new JPanel();
		frmEncrypt.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		JLabel lblNewLabel_1 = new JLabel("Recipient Certificate");
		lblNewLabel_1.setBounds(10, 28, 151, 20);
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panel.add(lblNewLabel_1);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setFont(new Font("Tahoma", Font.PLAIN, 16));
		tabbedPane.setBounds(10, 59, 557, 84);
		panel.add(tabbedPane);
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Known Certificates", null, panel_1, null);
		panel_1.setLayout(null);
		
		JComboBox cmbRecipientCert = new JComboBox();
		cmbRecipientCert.setFont(new Font("Tahoma", Font.PLAIN, 16));
		cmbRecipientCert.setBounds(10, 11, 414, 28);
		panel_1.add(cmbRecipientCert);
		
		JButton btnCertDetails = new JButton("DETAILS");
		btnCertDetails.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnCertDetails.setBounds(434, 11, 108, 28);
		panel_1.add(btnCertDetails);
		
		JPanel panel_2 = new JPanel();
		tabbedPane.addTab("File", null, panel_2, null);
		panel_2.setLayout(null);
		
		JButton btnOpenCertFile = new JButton("...");
		btnOpenCertFile.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnOpenCertFile.setBounds(481, 12, 61, 25);
		panel_2.add(btnOpenCertFile);
		
		txtbCertFile = new JTextField();
		txtbCertFile.setFont(new Font("Tahoma", Font.PLAIN, 16));
		txtbCertFile.setColumns(10);
		txtbCertFile.setBounds(10, 11, 448, 26);
		panel_2.add(txtbCertFile);
		
		JLabel lblInputFile = new JLabel("Output File");
		lblInputFile.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblInputFile.setBounds(10, 154, 120, 20);
		panel.add(lblInputFile);
		
		txtbOutputFile = new JTextField();
		txtbOutputFile.setFont(new Font("Tahoma", Font.PLAIN, 16));
		txtbOutputFile.setColumns(10);
		txtbOutputFile.setBounds(10, 185, 448, 26);
		panel.add(txtbOutputFile);
		
		JButton btnOpenOutputFile = new JButton("...");
		btnOpenOutputFile.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnOpenOutputFile.setBounds(476, 185, 85, 25);
		panel.add(btnOpenOutputFile);
		
		JPanel panel_1_1 = new JPanel();
		panel_1_1.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panel_1_1.setLayout(null);
		panel_1_1.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "Encryption Settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1_1.setBounds(10, 233, 551, 68);
		panel.add(panel_1_1);
		
		JLabel lblEncryptionAlgorithm = new JLabel("Encryption Algorithm:");
		lblEncryptionAlgorithm.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblEncryptionAlgorithm.setBounds(10, 29, 173, 20);
		panel_1_1.add(lblEncryptionAlgorithm);
		
		JComboBox cmbEncAlgorithm = new JComboBox();
		cmbEncAlgorithm.setFont(new Font("Tahoma", Font.PLAIN, 16));
		cmbEncAlgorithm.setBounds(176, 25, 214, 28);
		panel_1_1.add(cmbEncAlgorithm);
		
		JCheckBox chckbPemOutput = new JCheckBox("PEM output");
		chckbPemOutput.setFont(new Font("Tahoma", Font.PLAIN, 18));
		chckbPemOutput.setBounds(396, 27, 133, 23);
		panel_1_1.add(chckbPemOutput);
		
		JProgressBar progressBarEncrypt = new JProgressBar();
		progressBarEncrypt.setIndeterminate(true);
		progressBarEncrypt.setFont(new Font("Tahoma", Font.PLAIN, 16));
		progressBarEncrypt.setEnabled(false);
		progressBarEncrypt.setBounds(141, 446, 295, 14);
		panel.add(progressBarEncrypt);
		
		JButton btnEncrypt = new JButton("ENCRYPT");
		btnEncrypt.setFont(new Font("Tahoma", Font.BOLD, 18));
		btnEncrypt.setBounds(219, 350, 138, 55);
		panel.add(btnEncrypt);
	}
}
