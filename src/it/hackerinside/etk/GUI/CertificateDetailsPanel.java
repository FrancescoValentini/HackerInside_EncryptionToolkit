package it.hackerinside.etk.GUI;

import java.awt.BorderLayout;
import java.awt.Component;
import java.security.MessageDigest;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;

import it.hackerinside.etk.Utils.X509CertificateExporter;
import it.hackerinside.etk.Utils.X509Utils;

import java.awt.Font;


/**
 * A panel for displaying X.509 certificate details in a user-friendly interface.
 * The panel consists of a split pane with a key-value table at the top and a text area
 * at the bottom that shows detailed values when table rows are selected.
 * 
 * @author Francesco Valentini
 */
public class CertificateDetailsPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private JTable table;
    private JTextArea textArea;
    private DefaultTableModel tableModel;
    private JSplitPane splitPane;
    private JScrollPane scrollText;

    /**
     * Constructs a new CertificateDetailsPanel with default UI components.
     * Initializes a split pane containing a table for key-value pairs and
     * a text area for detailed value display.
     */
    public CertificateDetailsPanel() {
        setLayout(new BorderLayout());

        // Key-value table
        tableModel = new DefaultTableModel(new Object[]{"Field", "Value"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // read-only
            }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Tahoma", Font.PLAIN, 14));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollTable = new JScrollPane(table);

        // TextArea at the bottom
        textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        
        scrollText = new JScrollPane(textArea);
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollTable, scrollText);

        splitPane.setResizeWeight(0.5); // initial proportion

        add(splitPane, BorderLayout.CENTER);

        // When selecting a row, show the value in the textArea
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String value = (String) tableModel.getValueAt(row, 1);
                textArea.setText(value);
            }
        });
    }

    /**
     * Populates the panel with details from the provided X.509 certificate.
     * Clears any existing data before displaying the new certificate information.
     * 
     * @param cert the X.509 certificate to display, or null to clear the panel
     */
    public void setCertificate(X509Certificate cert) {
        tableModel.setRowCount(0); // clear
        textArea.setText("");
        if (cert == null) return;
        
        String prettySubj = X509Utils.getPrettySubject(cert.getSubjectX500Principal().getEncoded());

        // Common Name
        String cn = extractCN(cert.getSubjectX500Principal().getName());

        addRow("Common name", cn);
        addRow("Subject", prettySubj);
        addRow("Fingerprint (SHA-256)", getFingerprint(cert));
        addRow("SKI (SHA-1)", getSKI(cert));
        addRow("Status", checkCertificateValidity(cert));
        addRow("Serial Number", cert.getSerialNumber().toString(16));
        addRow("Issuer", X509Utils.getPrettySubject(cert.getIssuerX500Principal().getEncoded()));
        addRow("Valid from", cert.getNotBefore().toString());
        addRow("Valid to", cert.getNotAfter().toString());
        addRow("Key usage", cert.getKeyUsage() != null ? keyUsageToString(cert.getKeyUsage()) : "N/A");
        addRow("Signature Algorithm", cert.getSigAlgName());
        addRow("Public Key Algorithm", cert.getPublicKey().getAlgorithm());
        addRow("Version", String.valueOf(cert.getVersion()));
        try {
			addRow("PEM Certificate",X509CertificateExporter.exportCertificateToString(cert));
	        addRow("PEM Public Key",X509CertificateExporter.exportPublicKeyToString(cert));

		} catch (CertificateEncodingException e) {
			e.printStackTrace();
		}
        
        
        autoResizeColumnWidths();
    }
    

    /**
     * Adds a new row to the certificate details table.
     * 
     * @param field the field name (left column)
     * @param value the field value (right column)
     */
    private void addRow(String field, String value) {
        tableModel.addRow(new Object[]{field, value});
    }

    /**
     * Extracts the Common Name (CN) from a Distinguished Name (DN) string.
     * 
     * @param dn the Distinguished Name string to parse
     * @return the Common Name value, or an empty string if not found
     */
    private String extractCN(String dn) {
        for (String part : dn.split(",")) {
            if (part.trim().startsWith("CN=")) {
                return part.trim().substring(3);
            }
        }
        return "";
    }
    
    /**
     * Checks the validity status of a certificate.
     * 
     * @param cert the certificate to validate
     * @return a string describing the certificate's validity status
     */
    private String checkCertificateValidity(X509Certificate cert) {
        try {
            cert.checkValidity();
            return "Valid";
        } catch (java.security.cert.CertificateExpiredException e) {
            return "EXPIRED (expired on " + cert.getNotAfter() + ")";
        } catch (java.security.cert.CertificateNotYetValidException e) {
            return "NOT YET VALID (valid from " + cert.getNotBefore() + ")";
        }
    }

    /**
     * Calculates the SHA-256 fingerprint of a certificate.
     * 
     * @param cert the certificate to fingerprint
     * @return the SHA-256 fingerprint as a hexadecimal string, or an error message if calculation fails
     */
    private String getFingerprint(X509Certificate cert) {
        try {
            byte[] encoded = cert.getEncoded();
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(encoded);

            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02X", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return "Error calculating fingerprint";
        }
    }
    
    /**
     * RFC 3280 type 1 SKI
     * @param cert
     * @return RFC 3280 type 1 SKI
     */
    private String getSKI(X509Certificate cert) {
		try {
			return bytesToHex(new JcaX509ExtensionUtils()
					.createSubjectKeyIdentifier(cert.getPublicKey())
					.getKeyIdentifier());
		} catch (Exception e) {
			return null;
		}
    }

    /**
     * Converts a key usage boolean array to a human-readable string.
     * 
     * @param keyUsage the key usage array from a certificate
     * @return a comma-separated string of key usage descriptions
     */
    private String keyUsageToString(boolean[] keyUsage) {
        String[] usages = {
                "Digital Signature", "Non Repudiation", "Key Encipherment", "Data Encipherment",
                "Key Agreement", "Key Cert Sign", "CRL Sign", "Encipher Only", "Decipher Only"
        };
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keyUsage.length && i < usages.length; i++) {
            if (keyUsage[i]) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(usages[i]);
            }
        }
        return sb.toString();
    }
    
    /**
     * Automatically adjusts column widths based on content.
     * Ensures all content is visible while maintaining reasonable column sizes.
     */
    private void autoResizeColumnWidths() {
        final TableColumnModel columnModel = table.getColumnModel();
        for (int column = 0; column < table.getColumnCount(); column++) {
            int width = 50; // minimum width
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer renderer = table.getCellRenderer(row, column);
                Component comp = table.prepareRenderer(renderer, row, column);
                width = Math.max(comp.getPreferredSize().width + 10, width);
            }
            if (width > 400) width = 400; // maximum limit for readability
            columnModel.getColumn(column).setPreferredWidth(width);
        }
    }
    
    /**
     * Shows or hides the text area component at the bottom of the split pane.
     * 
     * @param hide if true, hides the text area; if false, shows the text area
     */
    public void hideContent(boolean hide) {
        if (hide) {
            // hide the bottom component
            splitPane.setBottomComponent(null);
        } else {
            // restore the textArea inside the scroll pane
            splitPane.setBottomComponent(scrollText);
        }
        revalidate();
        repaint();
    }
    
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}