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

        showDialog(parent, title, panel);
	}
	
    /**
     * Displays a confirmation dialog with a title, header message, and detailed content.
     * The dialog features a bold header and a scrollable text area for long messages.
     * Returns true if the user clicks OK, false if the user clicks Cancel.
     * 
     * @param parent      the parent component for the dialog; can be null
     * @param title       the title of the dialog window
     * @param header      the main header message (displayed in bold)
     * @param message     the detailed descriptive text (can be long)
     * @param messageType the type of message (JOptionPane.INFORMATION_MESSAGE, 
     *                    WARNING_MESSAGE, or ERROR_MESSAGE)
     * @return            true if OK is clicked, false if Cancel is clicked
     */
    public static boolean showConfirmBox(Component parent, String title, String header, String message, int messageType) {
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

        // Create options for the confirmation dialog
        Object[] options = {"OK", "Cancel"};
        int choice = JOptionPane.showOptionDialog(
            parent, 
            panel, 
            title, 
            JOptionPane.DEFAULT_OPTION, 
            messageType, 
            null, 
            options, 
            options[0]  // Default to "OK"
        );

        // Return true if OK was clicked, false if Cancel was clicked
        return choice == 0;
    }
	
	/**
	 * Creates a JPanel that contains a header label, a label for the input field, and the input field itself.
	 * The layout of the panel consists of a BorderLayout, with the header at the top, 
	 * and the label/input field pair in the center.
	 * 
	 * @param header the header text to be displayed at the top of the panel
	 * @param label the label text for the input field
	 * @param inputField the JComponent representing the input field (e.g., JTextField, JComboBox)
	 * @return a JComponent containing the header, label, and input field arranged in a BorderLayout
	 */
	private static JComponent createInputPanel(String header, String label, JComponent inputField) {
	    // Create the header label with bold font style
	    JLabel headerLabel = new JLabel(header);
	    headerLabel.putClientProperty(FlatClientProperties.STYLE, "font: +2 bold");

	    // Create the label for the input field
	    JLabel fieldLabel = new JLabel(label);

	    // Create the panel that will hold the label and input field in a BorderLayout
	    JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
	    inputPanel.add(fieldLabel, BorderLayout.WEST);
	    inputPanel.add(inputField, BorderLayout.CENTER);

	    // Create the main panel that holds the header and the input panel
	    JPanel panel = new JPanel(new BorderLayout(10, 10));
	    panel.setBorder(new EmptyBorder(10, 10, 10, 10));
	    panel.add(headerLabel, BorderLayout.NORTH);
	    panel.add(inputPanel, BorderLayout.CENTER);

	    return panel;
	}
    
	/**
	 * Displays a dialog with the given title and content panel, allowing the user to either 
	 * confirm or cancel. The dialog will display the given component (such as a form or input field) 
	 * inside a JOptionPane and returns an integer representing the user's choice.
	 * 
	 * @param parent the parent component to which the dialog is attached
	 * @param title the title of the dialog window
	 * @param panel the JComponent (typically a form or input) to be displayed inside the dialog
	 * @return an integer representing the user's choice: 
	 *         JOptionPane.OK_OPTION for confirmation, 
	 *         JOptionPane.CANCEL_OPTION for cancellation,
	 *         or JOptionPane.CLOSED_OPTION if the dialog was closed without a selection
	 */
	private static int showDialog(Component parent, String title, JComponent panel) {
	    // Create an option pane to display the dialog with a question message type
	    JOptionPane optionPane = new JOptionPane(panel, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
	    
	    // Create a dialog window with the provided title
	    JDialog dialog = optionPane.createDialog(parent, title);
	    
	    // Ensure the dialog stays on top of other windows
	    dialog.setAlwaysOnTop(true);
	    
	    // Make the dialog visible to the user
	    dialog.setVisible(true);

	    // Get the user's selection (OK or Cancel) and return the corresponding value
	    Object selectedValue = optionPane.getValue();
	    return (selectedValue instanceof Integer) ? (Integer) selectedValue : JOptionPane.CLOSED_OPTION;
	}
	
	/**
	 * Displays an input dialog with a title, header message, and input field.
	 * 
	 * @param parent   the parent component for the dialog; can be null
	 * @param title    the title of the dialog window
	 * @param header   the main header message (displayed in bold)
	 * @param label    the descriptive text next to the input field
	 * @return the text entered by the user, or null if the user cancels the operation
	 */
    public static String showInputBox(Component parent, String title, String header, String label) {
        JTextField textField = new JTextField(20);
        JComponent panel = createInputPanel(header, label, textField);

        int result = showDialog(parent, title, panel);
        if (result == JOptionPane.OK_OPTION) {
            return textField.getText();
        }
        return null;
    }

	/**
	 * Displays a password input dialog with a title, header message, and input field.
	 * 
	 * @param parent   the parent component for the dialog; can be null
	 * @param title    the title of the dialog window
	 * @param header   the main header message (displayed in bold)
	 * @param label    the descriptive text next to the input field
	 * @return the password entered by the user, or null if the user cancels the operation
	 */
    public static char[] showPasswordInputBox(Component parent, String title, String header, String label) {
        JPasswordField passwordField = new JPasswordField(20);
        JComponent panel = createInputPanel(header, label, passwordField);

        int result = showDialog(parent, title, panel);
        if (result == JOptionPane.OK_OPTION) {
            return passwordField.getPassword(); 
        }
        return null;
    }
	
	/**
	 * Displays a large text input dialog with a title, header, and scrollable text area.
	 * The dialog can be used both for entering and viewing large text, depending on the
	 * editable flag. It shows OK/Cancel buttons and returns the resulting text if confirmed.
	 *
	 * @param parent   the parent component for the dialog; can be null
	 * @param title    the title of the dialog window
	 * @param header   the main header message (displayed in bold)
	 * @param text     the initial text to display in the text area (can be null or empty)
	 * @param editable true if the user can edit the text; false for read-only
	 * @return the resulting text entered by the user, or null if canceled
	 */
	public static String showLargeInputBox(Component parent, String title, String header, String text, boolean editable) {
	    JLabel headerLabel = new JLabel(header);
	    headerLabel.putClientProperty(FlatClientProperties.STYLE, "font: +2 bold");

	    JTextArea textArea = new JTextArea(text != null ? text : "");
	    textArea.setEditable(editable);
	    textArea.setLineWrap(true);
	    textArea.setWrapStyleWord(true);
	    textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

	    JScrollPane scrollPane = new JScrollPane(textArea);
	    scrollPane.setPreferredSize(new Dimension(550, 300));

	    JPanel panel = new JPanel(new BorderLayout(10, 10));
	    panel.setBorder(new EmptyBorder(10, 10, 10, 10));
	    panel.add(headerLabel, BorderLayout.NORTH);
	    panel.add(scrollPane, BorderLayout.CENTER);

	    JOptionPane optionPane = new JOptionPane(
	        panel,
	        JOptionPane.QUESTION_MESSAGE,
	        JOptionPane.OK_CANCEL_OPTION
	    );

	    JDialog dialog = optionPane.createDialog(parent, title);
	    dialog.setAlwaysOnTop(true);
	    dialog.setVisible(true);

	    Object selectedValue = optionPane.getValue();
	    int result = (selectedValue instanceof Integer) ? (Integer) selectedValue : JOptionPane.CLOSED_OPTION;

	    if (result == JOptionPane.OK_OPTION) {
	        return textArea.getText();
	    }
	    return null;
	}

}
