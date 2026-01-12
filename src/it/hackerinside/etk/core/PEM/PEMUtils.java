package it.hackerinside.etk.core.PEM;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

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

	        in.mark(2048);

	        int firstByte = in.read();
	        if (firstByte == -1) {
	            throw new IOException("File is empty or unreadable");
	        }

	        if ((firstByte & 0xFF) == 0x30) {
	            return EncodingOption.ENCODING_DER;
	        }

	        in.reset();

	        ByteArrayOutputStream lineBuffer = new ByteArrayOutputStream();
	        int b;
	        while ((b = in.read()) != -1) {
	            if (b == '\n' || b == '\r') {
	                break;
	            }
	            lineBuffer.write(b);
	            if (lineBuffer.size() > 1024) {
	                break;
	            }
	        }

	        if (lineBuffer.size() > 0) {
	            String firstLine = lineBuffer
	                    .toString(StandardCharsets.US_ASCII)
	                    .trim();

	            if (firstLine.startsWith("-----BEGIN ")) {
	                return EncodingOption.ENCODING_PEM;
	            }
	        }

	        throw new IOException("Unknown file format.");
	    }
	}

}
