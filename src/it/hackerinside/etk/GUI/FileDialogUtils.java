package it.hackerinside.etk.GUI;

import javax.swing.*;
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
            FileNameExtensionFilter chosenFilter = (FileNameExtensionFilter) chooser.getFileFilter();
            String[] exts = chosenFilter.getExtensions();
            if (exts.length > 0) {
                String ext = "." + exts[0];
                if (!file.getName().toLowerCase().endsWith(ext.toLowerCase())) {
                    file = new File(file.getAbsolutePath() + ext);
                }
            }

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
        JFileChooser chooser = new JFileChooser(path != null ? path : ".");
        chooser.setDialogTitle(title != null ? title : "Select file");

        // Add all filters
        if (filters != null && filters.length > 0) {
            for (DefaultExtensions ext : filters) {
                String extensionWithoutDot = ext.getExt().replace(".", "");
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        ext.getDescription() + " (*" + ext.getExt() + ")",
                        extensionWithoutDot
                );
                chooser.addChoosableFileFilter(filter);
            }
            // Set default to the first filter
            chooser.setFileFilter(chooser.getChoosableFileFilters()[0]);
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
}