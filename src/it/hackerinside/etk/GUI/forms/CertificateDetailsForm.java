package it.hackerinside.etk.GUI.forms;

import java.security.cert.X509Certificate;

import javax.swing.JFrame;
import javax.swing.UIManager;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;

import it.hackerinside.etk.GUI.CertificateDetailsPanel;
import it.hackerinside.etk.GUI.ETKContext;

public class CertificateDetailsForm {

	private JFrame frame;
	private X509Certificate c;
	private ETKContext ctx;

	/**
	 * Create the application.
	 */
	public CertificateDetailsForm(X509Certificate certificate) {
		this.ctx = ETKContext.getInstance();
		this.c = certificate;
		initialize();
		frame.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 522, 720);
		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		CertificateDetailsPanel  panel = new CertificateDetailsPanel();
		panel.setCertificate(c);
		panel.hideContent(false);
		frame.add(panel);
	}
}
