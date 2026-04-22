package it.hackerinside.etk.core.Services;

import java.security.cert.X509Certificate;

@FunctionalInterface
public interface CertificateValidationProvider {
    boolean acceptX509Certificate(X509Certificate crt);
}
