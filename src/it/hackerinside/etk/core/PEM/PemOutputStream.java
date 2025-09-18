package it.hackerinside.etk.core.PEM;

import java.io.*;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;

/**
 * An output stream that encodes written data into PEM format.
 * This class acts as a wrapper around an underlying OutputStream, collecting
 * the written data and encoding it as a PEM object when the stream is closed.
 * 
 * <p>Usage example:
 * <pre>
 * try (PemOutputStream pemOut = new PemOutputStream(outputStream, "CERTIFICATE")) {
 *     pemOut.write(certificateData);
 * } // Automatically encodes and writes PEM data on close
 * </pre>
 * 
 * @see org.bouncycastle.util.io.pem.PemWriter
 * @see org.bouncycastle.util.io.pem.PemObject
 * @author Francesco Valentini
 */
public class PemOutputStream extends OutputStream {

    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private final OutputStream underlying;
    private final String type;

    /**
     * Constructs a PemOutputStream that encapsulates PEM output.
     * 
     * @param underlying the OutputStream to write the final PEM data to
     * @param type       the type of PEM object (e.g., "CMS", "CERTIFICATE", "PRIVATE KEY")
     */
    public PemOutputStream(OutputStream underlying, String type) {
        this.underlying = underlying;
        this.type = type;
    }

    /**
     * Writes a single byte to the internal buffer.
     * The byte will be included in the PEM object when the stream is closed.
     *
     * @param b the byte to be written
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void write(int b) throws IOException {
        buffer.write(b);
    }

    /**
     * Writes a portion of a byte array to the internal buffer.
     * The bytes will be included in the PEM object when the stream is closed.
     *
     * @param b   the data to be written
     * @param off the start offset in the data
     * @param len the number of bytes to write
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        buffer.write(b, off, len);
    }

    /**
     * Closes the stream and encodes all written data as a PEM object.
     * This method writes the collected data as a PEM-encoded object to the
     * underlying output stream using the specified type.
     *
     * @throws IOException if an I/O error occurs during PEM encoding or writing
     */
    @Override
    public void close() throws IOException {
        try (PemWriter pemWriter = new PemWriter(new OutputStreamWriter(underlying))) {
            PemObject pemObject = new PemObject(type, buffer.toByteArray());
            pemWriter.writeObject(pemObject);
            pemWriter.flush();
        }
    }
}