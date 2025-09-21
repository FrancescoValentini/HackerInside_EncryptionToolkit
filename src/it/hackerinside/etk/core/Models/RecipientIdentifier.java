package it.hackerinside.etk.core.Models;

import java.math.BigInteger;

/**
 * Represents a recipient identifier extracted from CMS:
 * either a Subject Key Identifier (SKI) or Issuer + SerialNumber.
 */
public class RecipientIdentifier {
    public enum Type { SUBJECT_KEY_IDENTIFIER, ISSUER_SERIAL }

    private final Type type;
    // for SKI
    private final byte[] ski;
    // for Issuer+Serial
    private final byte[] issuerEncoded; // X500Name.getEncoded()
    private final BigInteger serial;

    public RecipientIdentifier(byte[] ski) {
        this.type = Type.SUBJECT_KEY_IDENTIFIER;
        this.ski = ski;
        this.issuerEncoded = null;
        this.serial = null;
    }

    public RecipientIdentifier(byte[] issuerEncoded, BigInteger serial) {
        this.type = Type.ISSUER_SERIAL;
        this.issuerEncoded = issuerEncoded;
        this.serial = serial;
        this.ski = null;
    }

    public Type getType() { return type; }
    public byte[] getSki() { return ski; }
    public byte[] getIssuerEncoded() { return issuerEncoded; }
    public BigInteger getSerial() { return serial; }

    /** Human readable key useful for logging / map keys. */
    public String toKeyString() {
        if (type == Type.SUBJECT_KEY_IDENTIFIER) {
            return "SKI:" + toHexString(ski);
        } else {
            return "ISSUER_SERIAL:" + toHexString(issuerEncoded) + ":" + serial.toString(16);
        }
    }

    @Override
    public String toString() {
        return toKeyString();
    }
    
    private String toHexString(byte[] data) {
        if (data == null) {
            return "";
        }
        
        StringBuilder hexString = new StringBuilder(data.length * 2);
        for (byte b : data) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }
}

