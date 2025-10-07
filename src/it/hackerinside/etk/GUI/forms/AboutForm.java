package it.hackerinside.etk.GUI.forms;

import java.awt.EventQueue;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import it.hackerinside.etk.GUI.ETKContext;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.Font;
import java.security.*;
import javax.crypto.*;

public class AboutForm {


    private JFrame frmHackerinsideEncryptionToolkit;
    private JTextArea debugText;
    private ETKContext ctx;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AboutForm window = new AboutForm();
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
	public AboutForm() {
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
        frmHackerinsideEncryptionToolkit = new JFrame("Java Crypto Info Viewer");
        frmHackerinsideEncryptionToolkit.setTitle("HackerInside Encryption Toolkit | About");
        frmHackerinsideEncryptionToolkit.setBounds(100, 100, 800, 700);

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(new Font("Tahoma", Font.PLAIN, 16));
        frmHackerinsideEncryptionToolkit.getContentPane().add(tabbedPane, BorderLayout.CENTER);

        // --- About tab ---
        JPanel aboutPanel = new JPanel(new BorderLayout());
        JEditorPane aboutText = new JEditorPane();
        aboutText.setContentType("text/html");
        aboutText.setText("<center>\r\n<h1>HackerInside Encryption Toolkit</h1>\r\n<h3>Francesco Valentini - 2025</h3>\r\n</center>\r\n<hr />\r\n<p><strong>HackerInside EncryptionToolkit</strong> is a software for encryption and digital signatures based on the <strong>CMS (Cryptographic Message Syntax)</strong> standard. <br/> It supports operations such as <strong>encryption, decryption, key management, and digital signatures</strong>, with integration for <strong>PKCS#11</strong> devices.</p>\r\n\r\n<br/><br/>\r\n\r\n<p><strong>Technologies Used</strong></p>\r\n<ul>\r\n  <li><strong>Cryptographic Library:</strong> BouncyCastle (https://www.bouncycastle.org/)</li>\r\n  <li><strong>Graphics Library:</strong>FlatLaf (https://www.formdev.com/flatlaf)</li>\r\n  <li><strong>Graphics Framework:</strong> Java Swing</li>\r\n</ul>\r\n");
        aboutText.setFont(new Font("Consolas", Font.PLAIN, 16));
        aboutText.setEditable(false);
        aboutPanel.add(new JScrollPane(aboutText), BorderLayout.CENTER);
        tabbedPane.addTab("About", aboutPanel);

        // --- Debug tab ---
        JPanel debugPanel = new JPanel(new BorderLayout());
        debugText = new JTextArea();
        debugText.setFont(new Font("Consolas", Font.PLAIN, 16));
        debugText.setEditable(false);
        JScrollPane scroll = new JScrollPane(debugText);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        debugPanel.add(scroll, BorderLayout.CENTER);
        tabbedPane.addTab("Debug", debugPanel);

        // Fill debug info
        debugText.setText(generateDebugInfo());

        frmHackerinsideEncryptionToolkit.setVisible(true);
    }

    // ================= INFO GENERATION =================

    private String generateDebugInfo() {
        StringBuilder info = new StringBuilder();

        info.append(getSystemInfo());
        info.append(swInfo());
        info.append(getProvidersInfo());
        info.append(getAlgorithmInfo());
        info.append(getKeyLengthInfo());
        info.append(getECCInfo());
        info.append(getHashingInfo());

        return info.toString();
    }

    // --- System Info ---
    private String getSystemInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== JAVA SYSTEM INFORMATION ===\n");
        sb.append("Java Version: ").append(System.getProperty("java.version")).append("\n");
        sb.append("Java Vendor: ").append(System.getProperty("java.vendor")).append("\n");
        sb.append("Java Home: ").append(System.getProperty("java.home")).append("\n");
        sb.append("OS: ").append(System.getProperty("os.name")).append(" ")
          .append(System.getProperty("os.version")).append(" (")
          .append(System.getProperty("os.arch")).append(")\n\n");
        return sb.toString();
    }
    
    private String swInfo() {
    	StringBuilder sb = new StringBuilder();
    	sb.append("=== ETK CONTEXT ===\n");
    	sb.append("Software Version: " + ctx.ETK_VERSION + "\n");
    	sb.append(ctx.toString());
    	sb.append("\n\n");
    	return sb.toString();
    }

    // --- Providers ---
    private String getProvidersInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== CRYPTOGRAPHIC PROVIDERS ===\n");
        for (Provider provider : Security.getProviders()) {
            sb.append(provider.getName())
              .append(" - ").append(provider.getInfo())
              .append("\n");
            if (provider.getName().toLowerCase().contains("bc")) {
                sb.append("  -> BouncyCastle detected!\n");
            }
        }
        sb.append("\n");
        return sb.toString();
    }

    // --- Algorithms ---
    private String getAlgorithmInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== AVAILABLE ALGORITHMS ===\n");
        for (Provider provider : Security.getProviders()) {
            sb.append("[").append(provider.getName()).append("]\n");
            for (Provider.Service service : provider.getServices()) {
                sb.append("  ").append(service.getType())
                  .append(": ").append(service.getAlgorithm()).append("\n");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    // --- Key Lengths ---
    private String getKeyLengthInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== MAXIMUM KEY LENGTHS ===\n");
        try {
            sb.append("AES  : ").append(formatKeyLength(Cipher.getMaxAllowedKeyLength("AES"))).append("\n");
            sb.append("RSA  : ").append(formatKeyLength(Cipher.getMaxAllowedKeyLength("RSA"))).append("\n");
            sb.append("DES  : ").append(formatKeyLength(Cipher.getMaxAllowedKeyLength("DES"))).append("\n");
            sb.append("ECIES: ").append(formatKeyLength(Cipher.getMaxAllowedKeyLength("AES"))).append("\n");



        } catch (Exception e) {
            sb.append("Error retrieving key length info: ").append(e.getMessage()).append("\n");
        }
        sb.append("\n");
        return sb.toString();
    }

    // --- ECC Info ---
    private String getECCInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ECC (Elliptic Curve Cryptography) SUPPORT ===\n");
        boolean eccFound = false;
        for (Provider provider : Security.getProviders()) {
            for (Provider.Service service : provider.getServices()) {
                String algo = service.getAlgorithm().toUpperCase();
                if (algo.contains("EC") || algo.contains("ECDH") || algo.contains("ECDSA") || algo.contains("ECIES")) {
                    eccFound = true;
                    sb.append(provider.getName())
                      .append(" -> ")
                      .append(service.getType())
                      .append(": ")
                      .append(service.getAlgorithm())
                      .append("\n");
                }
            }
        }
        if (!eccFound) {
            sb.append("No ECC algorithms found in registered providers.\n");
        } else {
            sb.append("\nECC algorithms detected successfully.\n");
        }
        sb.append("\n");
        return sb.toString();
    }
    
    private String formatKeyLength(int length) {
        if (length == Integer.MAX_VALUE) return "Unlimited (no restrictions)";
        return length + " bits";
    }


    // --- Hashing Info ---
    private String getHashingInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== HASHING (MESSAGE DIGEST) ALGORITHMS ===\n");

        for (Provider provider : Security.getProviders()) {
            sb.append("[").append(provider.getName()).append("]\n");
            for (Provider.Service service : provider.getServices()) {
                if (service.getType().equalsIgnoreCase("MessageDigest")) {
                    sb.append("  ").append(service.getAlgorithm()).append("\n");
                }
            }
            sb.append("\n");
        }
        sb.append("\n");
        return sb.toString();
    }

}
