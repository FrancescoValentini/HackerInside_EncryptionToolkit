package it.hackerinside.etk.GUI.icons;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.Icon;
/**
 * An icon representing a certificate, optionally decorated with a key icon.
 */
public class CertificateIcon implements Icon {

    private final int size;
    private final boolean keyPair;

    /**
     * Creates a certificate icon without a key icon.
     *
     * @param size the size of the icon
     */
    public CertificateIcon(int size) {
        this(size, false);
    }

    /**
     * Creates a certificate icon with an optional key icon.
     *
     * @param size the size of the icon
     * @param keyPair {@code true} to add a key icon to the certificate icon;
     *                {@code false} to display only the certificate icon
     */
    public CertificateIcon(int size, boolean keyPair) {
        this.size = size;
        this.keyPair = keyPair;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.setColor(c.getForeground());
        g2.translate(x, y);

        float s = size / 24f;
        g2.scale(s, s);
        g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // page
        g2.draw(new RoundRectangle2D.Float(2, 3, 20, 14, 3, 3));

        // text lines
        g2.draw(new Line2D.Float(6, 7.5f, 18, 7.5f));
        if (!keyPair) {
            g2.draw(new Line2D.Float(6, 11, 13, 11));
        } else {
            // key (replaces the short text line): head, shaft, tooth
            g2.setStroke(new BasicStroke(1.3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(new Ellipse2D.Float(5, 10.5f, 4, 4));
            g2.draw(new Line2D.Float(9, 12.5f, 14, 12.5f));
            g2.draw(new Line2D.Float(12, 12.5f, 12, 14.5f));
            g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        }

        // seal
        g2.fill(new Ellipse2D.Float(14.5f, 11.5f, 6, 6));

        // ribbon tails
        Path2D ribbon = new Path2D.Float();
        ribbon.moveTo(16, 17);
        ribbon.lineTo(15, 22);
        ribbon.lineTo(17.5, 20.5);
        ribbon.lineTo(20, 22);
        ribbon.lineTo(19, 17);
        g2.fill(ribbon);

        g2.dispose();
    }

    @Override
    public int getIconWidth() { return size; }

    @Override
    public int getIconHeight() { return size; }
}
