
package it.hackerinside.etk.core.Models;

/**
 * Represents the encoding format options for cryptographic objects.
 * This enum provides support for PEM and DER encoding formats.
 * 
 * @author Francesco Valentini
 */
public enum EncodingOption {
    /**
     * Privacy Enhanced Mail (PEM) encoding format.
     * PEM uses Base64 encoding with header and footer lines.
     */
    ENCODING_PEM("PEM"),
    
    /**
     * Distinguished Encoding Rules (DER) encoding format.
     * DER is a binary encoding format following ASN.1 rules.
     */
    ENCODING_DER("DER");
    
    private final String value;
    
    private EncodingOption(String value) {
        this.value = value;
    }
    
    /**
     * Converts a string value to its corresponding EncodingOption enum constant.
     * 
     * @param value the string representation of the encoding option (case-insensitive)
     * @return the EncodingOption enum constant matching the provided value
     * @throws IllegalArgumentException if the provided value doesn't match any encoding option
     */
    public static EncodingOption fromString(String value) {
        for (EncodingOption option : EncodingOption.values()) {
            if (option.value.equalsIgnoreCase(value)) {
                return option;
            }
        }
        throw new IllegalArgumentException("Invalid Value: " + value);
    }
    
    /**
     * Returns the string representation of this encoding option.
     * 
     * @return the string value associated with this encoding option
     */
    @Override
    public String toString() {
        return value;
    }
}