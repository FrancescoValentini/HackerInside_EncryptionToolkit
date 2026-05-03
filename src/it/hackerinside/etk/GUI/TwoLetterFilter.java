package it.hackerinside.etk.GUI;

import javax.swing.text.*;

/**
 * A {@link DocumentFilter} that restricts input to a maximum of two uppercase letters (A-Z).
 * <p>
 * This filter automatically converts all inserted or replaced text to uppercase
 * and ensures that:
 * <ul>
 *     <li>The total length does not exceed 2 characters</li>
 *     <li>Only alphabetic characters (A-Z) are allowed</li>
 * </ul>
 * <p>
 * Any input that violates these constraints is ignored.
 */
public class TwoLetterFilter extends DocumentFilter {

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
            throws BadLocationException {

        if (string == null) return;

        string = string.toUpperCase();

        if (isValid(fb, string, 0)) {
            super.insertString(fb, offset, string, attr);
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
            throws BadLocationException {

        if (text == null) return;

        text = text.toUpperCase();

        if (isValid(fb, text, length)) {
            super.replace(fb, offset, length, text, attrs);
        }
    }

    /**
     * Validates whether the given text can be inserted or replace existing content.
     *
     * @param fb the filter bypass providing access to the document
     * @param text the text to validate
     * @param lengthToReplace the number of characters that will be replaced
     * @return {@code true} if the operation is valid, {@code false} otherwise
     * @throws BadLocationException if document access fails
     */
    private boolean isValid(FilterBypass fb, String text, int lengthToReplace) throws BadLocationException {
        Document doc = fb.getDocument();
        int currentLength = doc.getLength();
        
        if (currentLength - lengthToReplace + text.length() > 2) {
            return false;
        }

        return text.matches("[A-Z]*");
    }
}