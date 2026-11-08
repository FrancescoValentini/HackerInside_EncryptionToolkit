package it.hackerinside.etk.GUI.icons;
import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import javax.swing.Icon;
/**
 * An icon representing a key.
 */
public class KeyIcon implements Icon {

    private final int size;

    /**
     * Creates a key icon.
     *
     * @param size the size of the icon
     */
    public KeyIcon(int size) {
        this.size = size;
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

        // key head
        g2.draw(new Ellipse2D.Float(3, 8, 7, 7));

        // key shaft
        g2.draw(new Line2D.Float(10, 11.5f, 21, 11.5f));

        // key teeth
        g2.draw(new Line2D.Float(17, 11.5f, 17, 15));
        g2.draw(new Line2D.Float(20, 11.5f, 20, 14));

        g2.dispose();
    }

    @Override
    public int getIconWidth() {
        return size;
    }

    @Override
    public int getIconHeight() {
        return size;
    }
}
