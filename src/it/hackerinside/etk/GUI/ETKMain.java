package it.hackerinside.etk.GUI;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.FlowLayout;
import javax.swing.JButton;
import java.awt.Font;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

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
	    topBarPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 12));

	    JButton btnSign = createSquareButton("SIGN");
	    JButton btnVerify = createSquareButton("VERIFY");
	    JButton btnEncrypt = createSquareButton("ENCRYPT");
	    JButton btnDecrypt = createSquareButton("DECRYPT");

	    topBarPanel.add(btnSign);
	    topBarPanel.add(btnVerify);
	    topBarPanel.add(btnEncrypt);
	    topBarPanel.add(btnDecrypt);

	    JPanel panel = new JPanel();
	    frmHackerinsideEncryptionToolkit.getContentPane().add(panel, BorderLayout.CENTER);
	    panel.setLayout(new BorderLayout(0, 0));

	    table = new JTable();
	    panel.add(new JScrollPane(table), BorderLayout.CENTER);

	    JMenuBar menuBar = new JMenuBar();
	    frmHackerinsideEncryptionToolkit.setJMenuBar(menuBar);

	    JMenu fileMenu = new JMenu("File");
	    menuBar.add(fileMenu);

	    JMenuItem mntmNewMenuItem = new JMenuItem("Settings");
	    fileMenu.add(mntmNewMenuItem);

	    btnSign.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	            sign();
	        }
	    });

	    btnVerify.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	            verify();
	        }
	    });

	    btnEncrypt.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	            encrypt();
	        }
	    });

	    btnDecrypt.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	            decrypt();
	        }
	    });

	    startProcedure();
	}
	/**
	 * Creates a square button with text below and an optional icon above.
	 * 
	 * @param text the text to display on the button
	 * @return a configured JButton with square dimensions and centered text layout
	 */
	private JButton createSquareButton(String text) {
	    JButton button = new JButton(text);
	    button.setPreferredSize(new Dimension(110, 110)); // Square dimensions
	    button.setFont(new Font("Arial", Font.PLAIN, 16)); // Subtle font
	    button.setFocusPainted(false);

	    // Configure for potential icon above + text below layout
	    button.setHorizontalTextPosition(SwingConstants.CENTER);
	    button.setVerticalTextPosition(SwingConstants.BOTTOM);

	    return button;
	}

	/**
	 * Initiates the main procedure by unlocking the keystore.
	 */
	private void startProcedure() {
	    unlockKeystore();
	}

	/**
	 * Attempts to unlock the keystore by prompting for a password.
	 * Displays an error message if keystore loading fails.
	 */
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

	/**
	 * Opens the digital signature form
	 */
	private void sign() {
	    SignForm frm = new SignForm();
	    frm.setVisible();
	}

	/**
	 * Opens the verification form
	 */
	private void verify() {
	    VerifyForm vfrm = new VerifyForm();
	    vfrm.setVisible();
	}

	/**
	 * Opens the encryption form
	 */
	private void encrypt() {
	    EncryptForm encfrm = new EncryptForm();
	    encfrm.setVisible();
	}

	/**
	 * Opens the decryption form
	 */
	private void decrypt() {
	    DecryptForm decfrm = new DecryptForm();
	    decfrm.setVisible();
	}
	
}
