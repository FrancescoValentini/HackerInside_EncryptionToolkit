package it.hackerinside.etk.GUI.DTOs;

import java.security.KeyStoreException;
import java.security.cert.X509Certificate;

import it.hackerinside.etk.core.keystore.AbstractKeystore;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

public class CertificateWrapper {
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final String alias;
    private final AbstractKeystore keystore;

    // lazily loaded cache
    private boolean loaded = false;
    private String commonName = "";
    private Date notAfter = null;

    public CertificateWrapper(String alias, AbstractKeystore keystore) {
        this.alias = alias;
        this.keystore = keystore;
    }

    @Override
    public String toString() {
        return alias;
    }

    public String getAlias() { return alias; }
    public AbstractKeystore getKeystore() { return keystore; }

    /** CN of the certificate subject, or "-" if unavailable. */
    public String getCommonName() {
        load();
        return commonName;
    }

    /** Expiration date formatted as yyyy-MM-dd, or "-" if unavailable. */
    public String getExpiration() {
        load();
        if (notAfter == null) return "-";
        return notAfter.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().format(DATE_FMT);
    }

    public boolean isExpired() {
        load();
        return notAfter != null && notAfter.before(new Date());
    }

    private synchronized void load() {
        if (loaded) return;
        loaded = true; // set first so a failure doesn't retry on every repaint

        commonName = "-";
        try {
            X509Certificate cert = getCertificate();
            if (cert == null) return;

            notAfter = cert.getNotAfter();
            commonName = extractCn(cert.getSubjectX500Principal().getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String extractCn(String dn) {
        try {
            for (Rdn rdn : new LdapName(dn).getRdns()) {
                if ("CN".equalsIgnoreCase(rdn.getType())) {
                    return rdn.getValue().toString();
                }
            }
        } catch (InvalidNameException ignored) {
        }
        return dn; // fallback: full DN
    }

    private X509Certificate getCertificate() throws KeyStoreException {
        return this.keystore.getCertificate(this.alias);
    }
    
    public boolean hasPrivateKey()  {
    	try {
			return this.keystore.isPrivateKey(alias);
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return false;
    }
}