package it.hackerinside.etk.GUI;

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
}