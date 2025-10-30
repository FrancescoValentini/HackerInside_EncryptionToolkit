package it.hackerinside.etk.GUI;

import javax.swing.LookAndFeel;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;

/**
 * Enumeration of available UI themes using the FlatLaf Look and Feel library.
 * 
 * <p>Each theme includes a display name for user-facing selection and
 * the corresponding LookAndFeel instance for application.</p>
 * 
 * @author Francesco Valentini
 */
public enum UIThemes {
    
    /**
     * Light theme with FlatLaf's default light appearance.
     */
    FLATLAF_LIGHT("FlatLaf Light", new FlatLightLaf()),
    
    /**
     * Theme that mimics IntelliJ IDEA's light appearance.
     */
    FLATLAF_INTELLIJ("FlatLaf IntelliJ", new FlatIntelliJLaf()),
    
    /**
     * Dark theme with FlatLaf's default dark appearance.
     */
    FLATLAF_DARK("FlatLaf Dark", new FlatDarkLaf()),
    
    /**
     * Dark theme that mimics IntelliJ IDEA's Darcula appearance.
     */
    FLATLAF_DARCULA("FlatLaf Darcula", new FlatDarculaLaf()),
    
    /**
     * Light theme optimized for macOS with native-style appearance.
     */
    FLATLAF_MACOS_LIGHT("FlatLaf MacOS Light", new FlatMacLightLaf()),
    
    /**
     * Dark theme optimized for macOS with native-style appearance.
     */
    FLATLAF_MACOS_DARK("FlatLaf MacOS Dark", new FlatMacDarkLaf());
    
	// The display name of the theme
    private final String displayName;
    
    // The LookAndFeel instance associated with this theme.
    private final LookAndFeel lookAndFeel;
    
    /**
     * Constructs a new UI theme with the specified display name and LookAndFeel.
     * 
     * @param displayName the user-friendly name of the theme
     * @param lookAndFeel the LookAndFeel instance that implements this theme
     */
    private UIThemes(String displayName, LookAndFeel lookAndFeel) {
        this.displayName = displayName;
        this.lookAndFeel = lookAndFeel;
    }
    
    /**
     * Returns the LookAndFeel instance associated with this theme.
     * This instance can be used with {@code UIManager.setLookAndFeel()} 
     * to apply the theme to the application.
     * 
     * @return the LookAndFeel instance for this theme
     */
    public LookAndFeel getLookAndFeel() {
        return this.lookAndFeel;
    }
    
    /**
     * Converts a string value to its corresponding UIThemes enum constant.
     * 
     * @param value the string representation of the UIThemes option (case-insensitive)
     * @return the UIThemes enum constant matching the provided value
     * @throws IllegalArgumentException if the provided value doesn't match any encoding option
     */
    public static UIThemes fromString(String value) {
        for (UIThemes option : UIThemes.values()) {
            if (option.displayName.equalsIgnoreCase(value)) {
                return option;
            }
        }
        throw new IllegalArgumentException("Invalid Value: " + value);
    }
    
    /**
     * Returns the display name of this theme.
     * 
     * @return the display name of this theme
     */
    @Override
    public String toString() {
        return this.displayName;
    }
}