package it.hackerinside.etk.GUI.forms;

import java.awt.EventQueue;

import javax.swing.JFrame;

import it.hackerinside.etk.GUI.ETKContext;

public class NewKeyPairForm {

	private JFrame frame;
	private ETKContext ctx;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					NewKeyPairForm window = new NewKeyPairForm();
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
	public NewKeyPairForm() {
		ctx = ETKContext.getInstance();
		initialize();
	}
	
	public void setVisible() {
		this.frame.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 557, 581);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
