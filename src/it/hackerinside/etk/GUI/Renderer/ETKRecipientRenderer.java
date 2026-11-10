package it.hackerinside.etk.GUI.Renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.UIManager;

import it.hackerinside.etk.GUI.DTOs.CertificateTableRow;
import it.hackerinside.etk.GUI.DTOs.PasswordTableRow;
import it.hackerinside.etk.GUI.DTOs.SecretKeyTableRow;
import it.hackerinside.etk.GUI.icons.CertificateIcon;
import it.hackerinside.etk.GUI.icons.KeyIcon;
import it.hackerinside.etk.GUI.icons.PasswordIcon;

public class ETKRecipientRenderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = 1L;

    private static final CertificateIcon 	CERT_ICON          		= new CertificateIcon(24);
    private static final KeyIcon 			SYMMETRIC_KEY_ICON    	= new KeyIcon(24);
    private static final PasswordIcon 		PASSWORD_ICON    		= new PasswordIcon(24);

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index,boolean isSelected, boolean cellHasFocus) {

        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        setIconTextGap(10);

        if (value instanceof CertificateTableRow row) {
            renderCertificate(list, row, isSelected);
        } else if (value instanceof SecretKeyTableRow row) {
            renderSecretKey(row);
        } else if (value instanceof PasswordTableRow) {
            renderPassword();
        } else {
            renderFallback(value);
        }

        setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        return this;
    }
    
    private void renderCertificate(JList<?> list, CertificateTableRow row, boolean isSelected) {
        setIcon(CERT_ICON);
        Color fg = isSelected
                ? list.getSelectionForeground()
                : list.getForeground();

        Color dim = isSelected
                ? fg
                : UIManager.getColor("Label.disabledForeground");

        if (dim == null) dim = fg;

        setText("<html><b>" + escape(row.keystoreAlias()) + "</b><br>"
                + "<font size='-1' color='" + hex(dim) + "'>"
                + "CN: " + escape(row.CommonName()) + "<br>"
                + "Fingerprint: " + row.truncatedFingerprint()
                + "</font></html>");
    }

    private void renderSecretKey(SecretKeyTableRow row) {
        setIcon(SYMMETRIC_KEY_ICON);
        setText("<html><b>" + escape(row.keystoreAlias()) + "</b></html>");
    }

    private void renderPassword() {
        setIcon(PASSWORD_ICON);
        setText("<html><b>Password Recipient</b></html>");
    }

    private void renderFallback(Object value) {
        setIcon(null);
        setText(value == null ? " " : value.toString());
    }

    private static String hex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }

    private static String escape(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}