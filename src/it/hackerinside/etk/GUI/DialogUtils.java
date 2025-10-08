package it.hackerinside.etk.GUI;

import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DialogUtils {

	/**
	 * Displays a message box with a title, header message, and detailed content.
	 * The dialog features a bold header and a scrollable text area for long messages.
	 * 
	 * @param parent      the parent component for the dialog; can be null
	 * @param title       the title of the dialog window
	 * @param header      the main header message (displayed in bold)
	 * @param message     the detailed descriptive text (can be long)
	 * @param messageType the type of message (JOptionPane.INFORMATION_MESSAGE, 
	 *                    WARNING_MESSAGE, or ERROR_MESSAGE)
	 */
	public static void showMessageBox(Component parent, String title, String header, String message, int messageType) {
	    JLabel headerLabel = new JLabel(header);
	    headerLabel.putClientProperty(FlatClientProperties.STYLE, "font: +2 bold");

	    JTextArea textArea = new JTextArea(message);
	    textArea.setEditable(false);
	    textArea.setLineWrap(true);
	    textArea.setWrapStyleWord(true);
	    JScrollPane scrollPane = new JScrollPane(textArea);
	    scrollPane.setPreferredSize(new Dimension(400, 150));

	    JPanel panel = new JPanel(new BorderLayout(10, 10));
	    panel.setBorder(new EmptyBorder(10, 10, 10, 10));
	    panel.add(headerLabel, BorderLayout.NORTH);
	    panel.add(scrollPane, BorderLayout.CENTER);

        JOptionPane optionPane = new JOptionPane(panel, messageType, JOptionPane.DEFAULT_OPTION);
        JDialog dialog = optionPane.createDialog(parent, title);
        dialog.setAlwaysOnTop(true); 
        dialog.setVisible(true);
	}

	/**
	 * Displays an input dialog with a title, header message, and input field.
	 * The dialog features a bold header and can display either a text field or password field.
	 * 
	 * @param parent   the parent component for the dialog; can be null
	 * @param title    the title of the dialog window
	 * @param header   the main header message (displayed in bold)
	 * @param label    the descriptive text next to the input field
	 * @param password true to display a password field, false for a regular text field
	 * @return the text entered by the user, or null if the user cancels the operation
	 */
	public static String showInputBox(Component parent, String title, String header, String label, boolean password) {
	    JLabel headerLabel = new JLabel(header);
	    headerLabel.putClientProperty(FlatClientProperties.STYLE, "font: +2 bold");

	    JLabel fieldLabel = new JLabel(label);
	    JComponent inputField = password ? new JPasswordField(20) : new JTextField(20);

	    JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
	    inputPanel.add(fieldLabel, BorderLayout.WEST);
	    inputPanel.add(inputField, BorderLayout.CENTER);

	    JPanel panel = new JPanel(new BorderLayout(10, 10));
	    panel.setBorder(new EmptyBorder(10, 10, 10, 10));
	    panel.add(headerLabel, BorderLayout.NORTH);
	    panel.add(inputPanel, BorderLayout.CENTER);

        JOptionPane optionPane = new JOptionPane(panel, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
        JDialog dialog = optionPane.createDialog(parent, title);
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);

        Object selectedValue = optionPane.getValue();
        int result = (selectedValue instanceof Integer) ? (Integer) selectedValue : JOptionPane.CLOSED_OPTION;

        if (result == JOptionPane.OK_OPTION) {
            if (password) {
                return new String(((JPasswordField) inputField).getPassword());
            } else {
                return ((JTextField) inputField).getText();
            }
        }
        return null;
	}
}
