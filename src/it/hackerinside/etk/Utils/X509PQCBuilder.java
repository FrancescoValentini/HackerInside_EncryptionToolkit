package it.hackerinside.etk.Utils;

import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import it.hackerinside.etk.core.Models.PQCAlgorithms;

public class X509PQCBuilder {
    /**
     * Generate a Post-Quantum Cryptography (PQC) signing KeyPair.
     *
     * @param algorithm The PQC algorithm to be used for key generation. This should be one of the supported PQC algorithms: {@link PQCAlgorithms}
     * @return A KeyPair consisting of the public and private keys generated using the specified PQC algorithm.
     * @throws Exception If any errors occur during key generation, such as invalid algorithm or parameters.
     */
    public static KeyPair generatePQCKeyPair(PQCAlgorithms algorithm) throws Exception {
        KeyPairGenerator kpg =
                KeyPairGenerator.getInstance(algorithm.keyPairAlgorithm, "BC");

        kpg.initialize(
        		algorithm.params,
                new SecureRandom()
        );

        return kpg.generateKeyPair();
    }

    /**
     * Builds a PQC-only X509 signing certificate (self-signed) based on the provided information and the PQC algorithm.
     * The generated certificate can be used for cryptographic purposes such as digital signatures or encryption,
     * depending on the algorithm.
     *
     * @param commonName The common name (CN) for the subject of the certificate.
     * @param countryCode The country code (C) for the subject of the certificate.
     * @param state The state (ST) for the subject of the certificate.
     * @param expDays The number of days until the certificate expires.
     * @param pubk The public key associated with the certificate.
     * @param privk The private key used to sign the certificate.
     * @param algorithm The PQC algorithm used to generate the keys and define the cryptographic properties of the certificate.
     * @return The X509 certificate representing the PQC-only self-signed certificate.
     * @throws Exception If any errors occur during certificate creation, such as invalid key pair or parameters.
     */
    public static X509Certificate buildPQCCertificate(
            String commonName,
            String countryCode,
            String state,
            int expDays,
            PublicKey pubk,
            PrivateKey privk,
            PQCAlgorithms algorithm) throws Exception {

        X500Name subject = new X500Name(
                "CN=" + commonName +
                ", C=" + countryCode +
                ", ST=" + state
        );

        Date notBefore = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(notBefore);
        cal.add(Calendar.DAY_OF_YEAR, expDays);
        Date notAfter = cal.getTime();

        BigInteger serial = new BigInteger(64, new SecureRandom()).abs();

        JcaX509v3CertificateBuilder certBuilder =
                new JcaX509v3CertificateBuilder(
                        subject,
                        serial,
                        notBefore,
                        notAfter,
                        subject,
                        pubk          
                );
        
        KeyUsage usage = algorithm.canSign
        	    ? new KeyUsage(KeyUsage.digitalSignature | KeyUsage.nonRepudiation)
        	    : new KeyUsage(KeyUsage.keyEncipherment | KeyUsage.dataEncipherment | KeyUsage.keyAgreement);

        	certBuilder.addExtension(Extension.keyUsage, true, usage);

        ContentSigner signer =
                new JcaContentSignerBuilder(algorithm.bcName)
                        .setProvider("BC")
                        .build(privk);

        X509CertificateHolder holder = certBuilder.build(signer);

        return new JcaX509CertificateConverter()
                .setProvider("BC")
                .getCertificate(holder);
    }
}
