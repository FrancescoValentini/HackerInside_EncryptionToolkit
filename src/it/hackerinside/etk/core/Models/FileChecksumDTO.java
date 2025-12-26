package it.hackerinside.etk.core.Models;

import java.io.File;

/**
 * Class that represents the checksum result of a file
 * @author Francesco Valentini
 */
public record FileChecksumDTO(File filePath, String checksum) {
	
	/**
	 * Class that represents the checksum result of a file
	 * @param filePath
	 * @param checksumBytes
	 */
    public FileChecksumDTO(File filePath, byte[] checksumBytes) {
        this(filePath, toHex(checksumBytes));
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}