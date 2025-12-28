package it.hackerinside.etk.GUI;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import it.hackerinside.etk.core.Models.DefaultExtensions;

import java.awt.*;
import java.io.File;


/**
 * A utility class providing static methods for displaying file open and save dialogs.
 * 
 * @author Francesco Valentini
 */
public class FileDialogUtils {

    /**
     * Displays a file chooser dialog for opening a file.
     *
     * @param parent    the parent component (can be null)
     * @param title     the dialog title
     * @param path      the initial directory path
     * @param filters   array of DefaultExtensions to use as file filters
     * @return the selected File, or null if the operation was cancelled
     */
    public static File openFileDialog(
            Component parent,
            String title,
            String path,
            DefaultExtensions... filters
    ) {
        JFileChooser chooser = createFileChooser(title, path, filters);
        
        int result = chooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }
        return null;
    }

    /**
     * Displays a file chooser dialog for saving a file.
     * Automatically appends the correct file extension if not provided by the user.
     *
     * @param parent    the parent component (can be null)
     * @param title     the dialog title
     * @param path      the initial directory path
     * @param filters   array of DefaultExtensions to use as file filters
     * @return the selected File (with correct extension), or null if the operation was cancelled
     */
    public static File saveFileDialog(
            Component parent,
            String title,
            String path,
            DefaultExtensions... filters
    ) {
        JFileChooser chooser = createFileChooser(title, path, filters);
        
        int result = chooser.showSaveDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();

            // Get the extension chosen by the user
            FileFilter selectedFilter = chooser.getFileFilter();
            if (selectedFilter instanceof FileNameExtensionFilter) {
                FileNameExtensionFilter chosenFilter = (FileNameExtensionFilter) selectedFilter;
                String[] exts = chosenFilter.getExtensions();
                if (exts.length > 0) {
                    String ext = "." + exts[0];
                    if (!file.getName().toLowerCase().endsWith(ext.toLowerCase())) {
                        file = new File(file.getAbsolutePath() + ext);
                    }
                }
            }
            // else: user selected "All Files", do nothing


            return file;
        }
        return null;
    }

    /**
     * Creates and configures a JFileChooser with the specified parameters and filters.
     *
     * @param title     the dialog title
     * @param path      the initial directory path
     * @param filters   array of DefaultExtensions to use as file filters
     * @return a configured JFileChooser instance
     */
    private static JFileChooser createFileChooser(String title, String path, DefaultExtensions... filters) {
        File initial = path != null ? new File(path) : new File(".");
        JFileChooser chooser;

        if (initial.isDirectory()) {
            chooser = new JFileChooser(initial);
        } else {
            chooser = new JFileChooser(initial.getParentFile());
            chooser.setSelectedFile(initial); // <--- qui imposti il file proposto
        }

        chooser.setDialogTitle(title != null ? title : "Select file");

        FileNameExtensionFilter defaultFilter = null;
        if (filters != null && filters.length > 0) {
            for (DefaultExtensions ext : filters) {
                String extensionWithoutDot = ext.getExt().replace(".", "");
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        ext.getDescription() + " (*" + ext.getExt() + ")",
                        extensionWithoutDot
                );
                chooser.addChoosableFileFilter(filter);

                if (defaultFilter == null) {
                    defaultFilter = filter; // salva il primo filtro passato come default
                }
            }
            if (defaultFilter != null) {
                chooser.setFileFilter(defaultFilter); // imposta esplicitamente come filtro selezionato
            }
        }


        return chooser;
    }


    /**
     * Convenience method for displaying an open file dialog with a single file filter.
     *
     * @param parent    the parent component (can be null)
     * @param title     the dialog title
     * @param path      the initial directory path
     * @param filter    the DefaultExtensions to use as file filter
     * @return the selected File, or null if the operation was cancelled
     */
    public static File openFileDialog(Component parent, String title, String path, DefaultExtensions filter) {
        return openFileDialog(parent, title, path, new DefaultExtensions[]{filter});
    }

    /**
     * Convenience method for displaying a save file dialog with a single file filter.
     *
     * @param parent    the parent component (can be null)
     * @param title     the dialog title
     * @param path      the initial directory path
     * @param filter    the DefaultExtensions to use as file filter
     * @return the selected File (with correct extension), or null if the operation was cancelled
     */
    public static File saveFileDialog(Component parent, String title, String path, DefaultExtensions filter) {
        return saveFileDialog(parent, title, path, new DefaultExtensions[]{filter});
    }
    
    /**
     * Displays a file chooser dialog for opening a file or selecting a directory.
     *
     * @param parent    the parent component (can be null)
     * @param title     the dialog title
     * @param path      the initial directory path
     * @param filters   array of DefaultExtensions to use as file filters (optional)
     * @return the selected File or directory, or null if the operation was cancelled
     */
    public static File openFileOrDirectoryDialog(
            Component parent,
            String title,
            String path,
            DefaultExtensions... filters
    ) {
        JFileChooser chooser = createFileChooser(title, path, filters);

        // Allow both files and directories
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        int result = chooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }
        return null;
    }

    
    /**
     * Checks if a file already exists and displays an information message
     * @param f the file to check
     * @return true if the file does not exist or if the user want to overwrite it
     */
    public static boolean overwriteIfExists(File f) {
    	if(f.exists()) {
    		return DialogUtils.showConfirmBox(
    				null,
    				"File already exists!", 
    				"File already exists, do you want to overwrite it?", 
    		        "File: " + f.getAbsolutePath() + "\nSize: " + f.length(), 
    		        JOptionPane.WARNING_MESSAGE
    		);
    	}
		return true;
    }
}