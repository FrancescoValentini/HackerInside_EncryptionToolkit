package it.hackerinside.etk.GUI;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.FlowLayout;
import javax.swing.JButton;
import java.awt.Font;
import javax.swing.JSeparator;
import javax.swing.JTable;

public class ETKMain {

	private JFrame frmHackerinsideEncryptionToolkit;
	private static ETKContext ctx;
	private JTable table;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		ctx = ETKContext.getInstance();
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ETKMain window = new ETKMain();
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
	public ETKMain() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmHackerinsideEncryptionToolkit = new JFrame();
		frmHackerinsideEncryptionToolkit.setTitle("HackerInside Encryption Toolkit");
		frmHackerinsideEncryptionToolkit.setBounds(100, 100, 921, 615);
		frmHackerinsideEncryptionToolkit.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel topBarPanel = new JPanel();
		frmHackerinsideEncryptionToolkit.getContentPane().add(topBarPanel, BorderLayout.NORTH);
		topBarPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 8));
		
		JButton btnSign = new JButton("SIGN");
		btnSign.setFont(new Font("Arial", Font.BOLD, 37));
		topBarPanel.add(btnSign);
		
		JButton btnVerify = new JButton("VERIFY");
		btnVerify.setFont(new Font("Arial", Font.BOLD, 37));
		topBarPanel.add(btnVerify);
		
		JSeparator separator = new JSeparator();
		topBarPanel.add(separator);
		
		JButton btnEncrypt = new JButton("ENCRYPT");
		btnEncrypt.setFont(new Font("Arial", Font.BOLD, 37));
		topBarPanel.add(btnEncrypt);
		
		JButton btnDecrypt = new JButton("DECRYPT");
		btnDecrypt.setFont(new Font("Arial", Font.BOLD, 37));
		topBarPanel.add(btnDecrypt);
		
		JPanel panel = new JPanel();
		frmHackerinsideEncryptionToolkit.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));
		
		table = new JTable();
		panel.add(table, BorderLayout.CENTER);
		
		JMenuBar menuBar = new JMenuBar();
		frmHackerinsideEncryptionToolkit.setJMenuBar(menuBar);
		
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		
		JMenuItem mntmNewMenuItem = new JMenuItem("Settings");
		fileMenu.add(mntmNewMenuItem);
	}
	
	private void unlockKeystore() {
        String pwd = DialogUtils.showInputBox(
                null,
                "Unlock Keystore",
                ctx.getKeyStorePath(),
                "Password:",
                true
            );
        try {
			ctx.loadKeystore(pwd);
		} catch (Exception e) {
	        DialogUtils.showMessageBox(
	                null,
	                "Unable to load keystore!",
	                "Unable to unlock keystore; only Encryption and Digital Signature Verification are available.",
	                e.getMessage(),
	                JOptionPane.ERROR_MESSAGE
	        );
		}
	}
	
	

}
