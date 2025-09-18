package it.hackerinside.etk.core.PEM;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import it.hackerinside.etk.core.Models.EncodingOption;

public class PEMUtils {
    /**
     * Determines whether a file is PEM-encoded or DER-encoded.
     *
     * @param file the file to check
     * @return "PEM" if the file contains PEM headers, "DER" if it appears to be binary DER
     * @throws IOException if an I/O error occurs while reading the file
     */
	public static EncodingOption findFileEncoding(File file) throws IOException {
		 try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file))) {
	            in.mark(1024); // mark for reset

	            byte[] buffer = new byte[8192];
	            int bytesRead = in.read(buffer);
	            in.reset();

	            if (bytesRead <= 0) {
	                throw new IOException("File is empty or unreadable");
	            }

	            String start = new String(buffer, 0, bytesRead).trim();

	            if (start.contains("-----BEGIN ")) {
	                return EncodingOption.ENCODING_PEM;
	            } else {
	            	return EncodingOption.ENCODING_DER;
	            }
	        }
	}
}
