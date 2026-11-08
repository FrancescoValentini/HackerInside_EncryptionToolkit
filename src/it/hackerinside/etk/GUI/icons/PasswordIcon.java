package it.hackerinside.etk.GUI.icons;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import javax.swing.Icon;
/**
 * An icon representing a password.
 */
public class PasswordIcon implements Icon {

    private final int size;

    /**
     * Creates a password icon.
     *
     * @param size the size of the icon
     */
    public PasswordIcon(int size) {
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

        // password dots
        g2.fill(new Ellipse2D.Float(3, 10, 4, 4));
        g2.fill(new Ellipse2D.Float(10, 10, 4, 4));
        g2.fill(new Ellipse2D.Float(17, 10, 4, 4));

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
