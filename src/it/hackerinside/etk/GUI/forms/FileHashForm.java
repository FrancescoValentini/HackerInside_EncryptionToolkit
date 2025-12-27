package it.hackerinside.etk.GUI.forms;

import java.awt.EventQueue;

import javax.swing.JFrame;

import it.hackerinside.etk.GUI.DialogUtils;
import it.hackerinside.etk.GUI.ETKContext;
import it.hackerinside.etk.GUI.FileDialogUtils;
import it.hackerinside.etk.GUI.TimeUtils;
import it.hackerinside.etk.core.FilesChecksum.FilesChecksum;
import it.hackerinside.etk.core.Models.DefaultExtensions;
import it.hackerinside.etk.core.Models.FileChecksumDTO;
import it.hackerinside.etk.core.Models.HashAlgorithm;

import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;
import java.awt.Color;
import javax.swing.JComboBox;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class FileHashForm {

	private JFrame frmHackerinsideEncryptionToolkit;
	private static ETKContext ctx;
	private JComboBox<HashAlgorithm> cmbHashAlgo;
	JComboBox<HashAlgorithm> cmbHashAlgo_1;
	private JProgressBar progressBar;
	private JTextArea txtbVerifyResult;
	private JTextField txtbChecksumFileInput;
	
	private JList<File> filesList;
	private DefaultListModel<File> listModel = new DefaultListModel<>();
	private List<File> files;
	private File checksumFile = null;
	HashMap<FileChecksumDTO, Boolean> verificationResult;
	
    private long startTime;
    private long endTime;
    

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FileHashForm window = new FileHashForm();
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
	public FileHashForm() {
		ctx = ETKContext.getInstance();
		initialize();
	}
	
	public void setVisible() {
		frmHackerinsideEncryptionToolkit.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		files = new ArrayList<>();
		frmHackerinsideEncryptionToolkit = new JFrame();
		frmHackerinsideEncryptionToolkit.setResizable(false);
		frmHackerinsideEncryptionToolkit.setBounds(100, 100, 612, 438);
		//frmHackerinsideEncryptionToolkit.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmHackerinsideEncryptionToolkit.setTitle("HackerInside Encryption Toolkit | Files Checksum");
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setFont(new Font("Tahoma", Font.PLAIN, 14));
		frmHackerinsideEncryptionToolkit.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("Calculate", null, panel, null);
		panel.setLayout(null);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "Files & Folders", TitledBorder.LEADING, TitledBorder.TOP, null));
		panel_3.setBounds(10, 11, 571, 127);
		panel.add(panel_3);
		panel_3.setLayout(new BorderLayout(0, 0));

		filesList = new JList<>(listModel);
		filesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollPane = new JScrollPane(filesList);
		panel_3.add(scrollPane, BorderLayout.CENTER);

		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new GridLayout(3, 1, 5, 5));
		panel_3.add(buttonsPanel, BorderLayout.EAST);
		
		JButton btnAddFile = new JButton("+");

		btnAddFile.setFont(new Font("Tahoma", Font.BOLD, 20));
		JButton btnRemoveFile = new JButton("-");

		btnRemoveFile.setFont(new Font("Tahoma", Font.BOLD, 20));
		buttonsPanel.add(btnAddFile);
		buttonsPanel.add(btnRemoveFile);
		
		JLabel lblInputFile_1_1 = new JLabel("Hashing algorithm:");
		lblInputFile_1_1.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblInputFile_1_1.setBounds(10, 149, 160, 20);
		panel.add(lblInputFile_1_1);
		
		cmbHashAlgo = new JComboBox();
		cmbHashAlgo.setFont(new Font("Tahoma", Font.PLAIN, 16));
		cmbHashAlgo.setBounds(162, 149, 200, 22);
		panel.add(cmbHashAlgo);
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Verify", null, panel_1, null);
		panel_1.setLayout(null);
		
		txtbChecksumFileInput = new JTextField();
		txtbChecksumFileInput.setFont(new Font("Tahoma", Font.PLAIN, 16));
		txtbChecksumFileInput.setColumns(10);
		txtbChecksumFileInput.setBounds(10, 42, 476, 26);
		panel_1.add(txtbChecksumFileInput);
		
		JLabel lblChecksumFile = new JLabel("Checksum file");
		lblChecksumFile.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblChecksumFile.setBounds(10, 11, 571, 20);
		panel_1.add(lblChecksumFile);
		
		JButton btnOpenChecksumFile = new JButton("...");

		btnOpenChecksumFile.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnOpenChecksumFile.setBounds(496, 42, 85, 26);
		panel_1.add(btnOpenChecksumFile);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 110, 571, 250);
		panel_1.add(scrollPane_1);
		
		txtbVerifyResult = new JTextArea();
		txtbVerifyResult.setEditable(false);
		txtbVerifyResult.setFont(new Font("Monospaced", Font.PLAIN, 15));
		scrollPane_1.setViewportView(txtbVerifyResult);
		
		JLabel lblInputFile_1_1_1 = new JLabel("Hashing algorithm:");
		lblInputFile_1_1_1.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblInputFile_1_1_1.setBounds(10, 79, 160, 20);
		panel_1.add(lblInputFile_1_1_1);
		
		 cmbHashAlgo_1 = new JComboBox();
		cmbHashAlgo_1.setFont(new Font("Tahoma", Font.PLAIN, 16));
		cmbHashAlgo_1.setBounds(162, 79, 200, 22);
		panel_1.add(cmbHashAlgo_1);
		
		
		JButton btnCalculate = new JButton("CALCULATE");

		btnCalculate.setFont(new Font("Tahoma", Font.BOLD, 18));
		btnCalculate.setBounds(226, 285, 138, 55);
		panel.add(btnCalculate);
		
		progressBar = new JProgressBar();
		progressBar.setEnabled(false);
		progressBar.setVisible(false);
		progressBar.setIndeterminate(true);
		progressBar.setBounds(120, 224, 350, 20);
		panel.add(progressBar);
		
		
		btnAddFile.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {addFile();}});
		btnRemoveFile.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {removeFile();}});
		btnOpenChecksumFile.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {openChecksumFile();}});
		btnCalculate.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {calculate();}});
		
		populateHashAlgorithms(cmbHashAlgo);
		populateHashAlgorithms(cmbHashAlgo_1);
	}
	
	/**
	 * Populates a combo box with all available hash algorithms.
	 * 
	 * @param combo the combo box to populate with hash algorithm values
	 */
	private void populateHashAlgorithms(JComboBox<HashAlgorithm> combo) {
	    combo.removeAllItems();
	    for (HashAlgorithm alg : HashAlgorithm.values()) {
	        combo.addItem(alg);
	    }
	    combo.setSelectedItem(ctx.getHashAlgorithm());
	}
	
	/**
	 * Removes the path from the list
	 */
	private void removeFile() {
		File selected = filesList.getSelectedValue();
		if(selected != null) {
			files.remove(selected);
			listModel.removeElement(selected);
		}
	}
	
	/**
	 * Allows you to select a path and adds it to the list
	 */
	private void addFile() {
		File f = FileDialogUtils.openFileOrDirectoryDialog(null, "File/folder to hash", null);
		if(f != null) {
			files.add(f);
			listModel.addElement(f);
		}
	}
	/**
	 * File checksum verification
	 */
	private void openChecksumFile() {
		File f = FileDialogUtils.openFileDialog(null, "Checksum file", null);
		if(f != null) {
			checksumFile = f;
			txtbChecksumFileInput.setText(f.getAbsolutePath());
			verify();
		}
	}
	
	/**
	 * Show progress bar while calculating checksums
	 */
	private void startHashUi() {
		progressBar.setVisible(true);
		progressBar.setEnabled(true);
	}
	
	/**
	 * Method invoked when checksum calculation is finished
	 * @param worker
	 */
	private void finishHashUi(SwingWorker<?, ?> worker) {
		progressBar.setVisible(false);
	    endTime = System.currentTimeMillis();
	    try {
	        worker.get();
	        
			DialogUtils.showMessageBox(
					null, 
					"Checksum calculation completed!", 
					"Checksum calculation completed!", 
					getOkMessage(), 
			        JOptionPane.INFORMATION_MESSAGE);
			
	    } catch (InterruptedException | ExecutionException e) {
			DialogUtils.showMessageBox(null, "Error while calculating checksums", "Error while calculating checksums!", 
			        e.getMessage(), 
			        JOptionPane.ERROR_MESSAGE);
	        e.printStackTrace();
	    }
	    this.checksumFile = null;
	}
	/**
	 * Generates the message showing the summary of the files included in the calculation
	 * @return
	 */
	private String getOkMessage() {
		StringBuilder sb = new StringBuilder();
		sb.append("Output:");
		sb.append(this.checksumFile);
		sb.append("\n\nElapsed: " + TimeUtils.formatElapsedTime(startTime, endTime));

		sb.append("\n\nPaths:\n");
		for(File f : files) {
			sb.append("- " + f.getAbsoluteFile() + "\n");
		}
		return sb.toString();
	}

	/**
	 * Calculate file checksums
	 */
	private void calculate() {
		if(files.isEmpty()) {
			return;
		}
		
		checksumFile = FileDialogUtils.saveFileDialog(null, "Checksum File", ".", DefaultExtensions.STD_TXT);
		if(!FileDialogUtils.overwriteIfExists(checksumFile)) return;
		if(checksumFile == null) return;
		
		startHashUi();
		
		HashAlgorithm alg = (HashAlgorithm) cmbHashAlgo.getSelectedItem();
		FilesChecksum checksum = new FilesChecksum(alg, ctx.getBufferSize());
		
		SwingWorker<Void, Void> worker = new SwingWorker<>() {
	        @Override
	        protected Void doInBackground() throws Exception {
	        	startTime = System.currentTimeMillis();
	        	checksum.calculate(files, checksumFile);
	            return null;
	        }

	        @Override
	        protected void done() {
	        	finishHashUi(this);
	        }
	    };

	    worker.execute();
	}
	/**
	 * Verify file checksums
	 */
	private void verify() {
		if(this.checksumFile == null) {
			return;
		}
		
		HashAlgorithm alg = (HashAlgorithm) cmbHashAlgo_1.getSelectedItem();
		FilesChecksum checksum = new FilesChecksum(alg, ctx.getBufferSize());
		txtbVerifyResult.setText("Calculating...");
		SwingWorker<Void, Void> worker = new SwingWorker<>() {
	        @Override
	        protected Void doInBackground() throws Exception {
	        	startTime = System.currentTimeMillis();
	        	verificationResult = checksum.verify(checksumFile);
	            return null;
	        }

	        @Override
	        protected void done() {
	        	finishHashVerify(this);
	        }
	    };

	    worker.execute();
	}
	/**
	 * Method invoked at the end of the verification
	 * @param worker
	 */
	private void finishHashVerify(SwingWorker<?, ?> worker) {
	    endTime = System.currentTimeMillis();
	    try {
	        worker.get();
	        
	        if(buildVerificationResult()) {
				DialogUtils.showMessageBox(null, 
						"INVALID CHECKSUMS!", 
						"At least one file failed the checksum verification!", 
				        "At least one file failed the checksum verification!\n\n"
						+ "This may indicate a missing or corrupt file, please check the full report in detail for more information.", 
				        JOptionPane.WARNING_MESSAGE);
	        }
	    } catch (InterruptedException | ExecutionException e) {
			DialogUtils.showMessageBox(null, "Error while calculating checksums", "Error while calculating checksums!", 
			        e.getMessage(), 
			        JOptionPane.ERROR_MESSAGE);
	        e.printStackTrace();
	    }
	    this.checksumFile = null;
	}
	
	/**
	 * Generates a summary of the checksum verification outcome
	 */
	private boolean buildVerificationResult() {
		StringBuilder sb = new StringBuilder();
		boolean hasInvalidChecksums = false;
		sb.append("Elapsed time: " + TimeUtils.formatElapsedTime(startTime, endTime) + "\n");
		for(FileChecksumDTO f : verificationResult.keySet()) {
			boolean result = verificationResult.get(f);
			if(result) {
				sb.append("OK : " + f.filePath() + "\n");
			}else {
				sb.append("FAILED : " + f.filePath() + "\n");
				hasInvalidChecksums = true;
			}
		}
		txtbVerifyResult.setText(sb.toString());
		return hasInvalidChecksums;
	}
}
