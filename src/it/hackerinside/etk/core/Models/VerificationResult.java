package it.hackerinside.etk.core.Models;

import java.security.cert.X509Certificate;

/**
 * Represents the result of a digital signature verification process.
 *
 * @param validSignature           A boolean indicating whether the digital signature is valid or not.
 * @param hasSigningCertificateV2  A boolean indicating whether the signing certificate used is of version 2.
 * @param hasSigningTime           A boolean indicating whether a signing time is present in the signature.
 * @param signer                   The X509Certificate representing the signer’s certificate.
 * @author Francesco Valentini
 */
public record VerificationResult(
    boolean validSignature,
    boolean hasSigningCertificateV2,
    boolean hasSigningTime,
    X509Certificate signer
) {}
