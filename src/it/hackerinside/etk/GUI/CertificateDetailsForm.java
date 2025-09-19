package it.hackerinside.etk.GUI;

import java.awt.EventQueue;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.swing.JFrame;
import javax.swing.UIManager;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;

public class CertificateDetailsForm {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CertificateDetailsForm window = new CertificateDetailsForm();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public CertificateDetailsForm() {
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
		frame = new JFrame();
		frame.setBounds(100, 100, 522, 720);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		X509Certificate c = null;
		try {
			c = load();
		}catch(Exception e) {}
		
		CertificateDetailsPanel  panel = new CertificateDetailsPanel();
		panel.setCertificate(c);
		panel.hideContent(false);
		frame.add(panel);
		
		
	}
	
	private X509Certificate load() throws CertificateException, IOException {
		CertificateFactory fact = CertificateFactory.getInstance("X.509");
		FileInputStream is = new FileInputStream (new File("ECCTEST.pem"));
		X509Certificate cer = (X509Certificate) fact.generateCertificate(is);
		PublicKey key = cer.getPublicKey();
		is.close();
		return cer;
	}

}
