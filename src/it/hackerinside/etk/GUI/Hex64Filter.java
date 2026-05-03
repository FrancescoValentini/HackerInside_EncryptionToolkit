package it.hackerinside.etk.GUI;

import javax.swing.text.*;
import java.awt.Toolkit;

/**
 * A DocumentFilter that restricts input to hexadecimal characters only (0-9, A-F)
 * and limits the total length to 64 characters.
 * <p>
 * Features:
 * <ul>
 *     <li>Automatically converts input to uppercase</li>
 *     <li>Allows only hex characters (0-9, A-F)</li>
 *     <li>Enforces a maximum length of 64 characters</li>
 * </ul>
 */
public class Hex64Filter extends DocumentFilter {

    private static final int MAX_LENGTH = 64;
    private static final String HEX_REGEX = "[0-9A-F]*";

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
            throws BadLocationException {

        if (string == null) return;

        string = string.toUpperCase();

        if (isValid(fb, string, 0)) {
            super.insertString(fb, offset, string, attr);
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
            throws BadLocationException {

        if (text == null) return;

        text = text.toUpperCase();

        if (isValid(fb, text, length)) {
            super.replace(fb, offset, length, text, attrs);
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    @Override
    public void remove(FilterBypass fb, int offset, int length)
            throws BadLocationException {

        super.remove(fb, offset, length);
    }

    /**
     * Validates whether the insertion/replacement respects hex format and max length.
     */
    private boolean isValid(FilterBypass fb, String text, int lengthToReplace)
            throws BadLocationException {

        Document doc = fb.getDocument();
        int currentLength = doc.getLength();

        int newLength = currentLength - lengthToReplace + text.length();

        if (newLength > MAX_LENGTH) {
            return false;
        }

        return text.matches(HEX_REGEX);
    }
}