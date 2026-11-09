package it.hackerinside.etk.GUI.Renderer;

import java.awt.Color;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.UIManager;

import it.hackerinside.etk.GUI.DTOs.CertificateWrapper;
import it.hackerinside.etk.GUI.icons.CertificateIcon;

public class ComboBoxCertificateRenderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = 1L;

    private static final CertificateIcon CERT_ICON          = new CertificateIcon(24);
    private static final CertificateIcon CERT_ICON_SMALL    = new CertificateIcon(16);
   

    @Override
    public Component getListCellRendererComponent(
            JList<?> list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {

        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        setIconTextGap(10);

        if (value instanceof CertificateWrapper) {
            CertificateWrapper w = (CertificateWrapper) value;

            if (index == -1) {
                // closed combo box: alias only, smaller icon
            	setIcon(CERT_ICON_SMALL);
                setText(w.getAlias());
            } else {
                setIcon(CERT_ICON);

                Color fg = isSelected ? list.getSelectionForeground() : list.getForeground();
                Color dim = isSelected ? fg : UIManager.getColor("Label.disabledForeground");
                if (dim == null) dim = fg;

                String expires = w.getExpiration() + (w.isExpired() ? " (expired)" : "");

                setText("<html><b>" + escape(w.getAlias()) + "</b><br>"
                        + "<font size='-1' color='" + hex(dim) + "'>"
                        + "CN: " + escape(w.getCommonName()) + "<br>"
                        + "Expires: " + escape(expires)
                        + "</font></html>");
            }
        }
        else {
            setIcon(null);
            setText(value == null ? " " : value.toString());
        }

        setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        return this;
    }

    private static String hex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }

    private static String escape(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}