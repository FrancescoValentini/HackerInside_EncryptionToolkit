package it.hackerinside.etk.core.Models;

import java.math.BigInteger;

/**
 * Represents a recipient identifier extracted from CMS:
 * either a Subject Key Identifier (SKI) or Issuer + SerialNumber.
 */
public class RecipientIdentifier {
    public enum Type { SUBJECT_KEY_IDENTIFIER, ISSUER_SERIAL, KEK_KEY_ID }

    private final Type type;

    // SKI o KEM Key ID
    private final byte[] keyIdentifier;

    // Issuer+Serial
    private final byte[] issuerEncoded;
    private final BigInteger serial;

    // SKI
    public static RecipientIdentifier fromSki(byte[] ski) {
        return new RecipientIdentifier(Type.SUBJECT_KEY_IDENTIFIER, ski, null, null);
    }

    // KEM Key ID
    public static RecipientIdentifier fromKekKeyId(byte[] kemKeyId) {
        return new RecipientIdentifier(Type.KEK_KEY_ID, kemKeyId, null, null);
    }

    // Issuer + Serial
    public static RecipientIdentifier fromIssuerSerial(byte[] issuerEncoded, BigInteger serial) {
        return new RecipientIdentifier(Type.ISSUER_SERIAL, null, issuerEncoded, serial);
    }

    private RecipientIdentifier(Type type, byte[] keyIdentifier, byte[] issuerEncoded, BigInteger serial) {
        this.type = type;
        this.keyIdentifier = keyIdentifier;
        this.issuerEncoded = issuerEncoded;
        this.serial = serial;
    }

    public Type getType() { return type; }

    public byte[] getKeyIdentifier() { return keyIdentifier; }
    
    public String getKeyIdentifierAsHexString() { return toHexString(keyIdentifier); }

    public byte[] getIssuerEncoded() { return issuerEncoded; }

    public BigInteger getSerial() { return serial; }

    public String toKeyString() {
        switch (type) {
            case SUBJECT_KEY_IDENTIFIER:
                return "SKI:" + toHexString(keyIdentifier);
            case KEK_KEY_ID:
                return "KEK:" + toHexString(keyIdentifier);
            case ISSUER_SERIAL:
                return "ISSUER_SERIAL:" + toHexString(issuerEncoded) + ":" + serial.toString(16);
            default:
                throw new IllegalStateException("Unknown type");
        }
    }

    @Override
    public String toString() {
        return toKeyString();
    }

    private String toHexString(byte[] data) {
        if (data == null) return "";

        StringBuilder hexString = new StringBuilder(data.length * 2);
        for (byte b : data) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

	public byte[] getSki() {
		return getKeyIdentifier();
	}
}

