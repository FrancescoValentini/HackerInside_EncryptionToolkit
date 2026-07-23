package it.hackerinside.etk.GUI;

import java.util.Arrays;

public enum CertificateColumn {
    ALIAS("Alias"),
    COMMON_NAME("Common Name"),
    FINGERPRINT("Fingerprint"),
    LOCATION("Location"),
    ALGORITHM("Algorithm"),
    EXPIRATION_DATE("Expire");

    private final String title;

    CertificateColumn(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
    
    public static CertificateColumn fromString(String value) {
        if (value == null) {
            return null;
        }

        return CertificateColumn.valueOf(value.trim().toUpperCase());
    }

    public static CertificateColumn[] fromStringArray(String value) {
        if (value == null || value.isBlank()) {
            return new CertificateColumn[0];
        }

        return Arrays.stream(value.split(","))
                .map(CertificateColumn::fromString)
                .toArray(CertificateColumn[]::new);
    }
}