package it.hackerinside.etk.Utils;

import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jcajce.spec.MLDSAParameterSpec;
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
     * Builds a PQC-only self-signed X.509 certificate using the provided subject attributes
     * and post-quantum cryptography (PQC) algorithm. The key usage is derived automatically
     * from the capabilities of the selected algorithm.
     *
     * @param commonName  the Common Name (CN) for the certificate subject
     * @param countryCode the country code (C) for the certificate subject
     * @param state       the state or province (ST) for the certificate subject
     * @param expDays     the number of days from now until the certificate expires
     * @param pubk        the public key to embed in the certificate
     * @param privk       the private key used to sign the certificate
     * @param algorithm  the PQC algorithm defining signing or key encapsulation behavior
     * @return a generated self-signed PQC {@link X509Certificate}
     * @throws Exception if certificate generation or signing fails
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
    	
        KeyUsage usage = algorithm.canSign
                ? new KeyUsage(KeyUsage.digitalSignature | KeyUsage.nonRepudiation)
                : new KeyUsage(KeyUsage.keyEncipherment | KeyUsage.keyAgreement);
        
        return buildPQCCertificate(subject,expDays, pubk,privk,usage,algorithm);
    }
    
    /**
     * Builds a PQC-only self-signed X.509 certificate for the given {@link X500Name} subject.
     * The key usage is automatically determined based on the capabilities of the provided
     * PQC algorithm.
     *
     * @param subject    the X.500 distinguished name used as both subject and issuer
     * @param expDays    the number of days from now until the certificate expires
     * @param pubk       the public key to embed in the certificate
     * @param privk      the private key used to sign the certificate
     * @param algorithm  the PQC algorithm defining signing or key encapsulation behavior
     * @return a generated self-signed PQC {@link X509Certificate}
     * @throws Exception if certificate generation or signing fails
     */
    public static X509Certificate buildPQCCertificate(
    		X500Name subject,
            int expDays,
            PublicKey pubk,
            PrivateKey privk,
            PQCAlgorithms algorithm) throws Exception {

    	
        KeyUsage usage = algorithm.canSign
                ? new KeyUsage(KeyUsage.digitalSignature | KeyUsage.nonRepudiation)
                : new KeyUsage(KeyUsage.keyEncipherment | KeyUsage.keyAgreement);
        
        return buildPQCCertificate(subject,expDays, pubk,privk,usage,algorithm);
    }
    
    /**
     * Builds a PQC-only self-signed X.509 v3 certificate for the given {@link X500Name} subject.
     * The certificate validity starts at the current time, expires after the specified number
     * of days, includes the provided {@link KeyUsage}, and is signed according to the selected
     * PQC algorithm.
     *
     * <p>If the algorithm supports signing, the provided private key is used directly.
     * If the algorithm is a KEM and cannot sign, a temporary PQC signing key is generated
     * solely for certificate signing while preserving the original public key.</p>
     *
     * @param subject    the X.500 distinguished name used as both subject and issuer
     * @param expDays    the number of days from now until the certificate expires
     * @param pubk       the public key to embed in the certificate
     * @param privk      the private key used for signing when supported by the algorithm
     * @param usage      the {@link KeyUsage} extension to include in the certificate
     * @param algorithm  the PQC algorithm defining signing or key encapsulation behavior
     * @return a generated self-signed PQC {@link X509Certificate}
     * @throws Exception if certificate generation or signing fails
     */
    public static X509Certificate buildPQCCertificate(
    		X500Name subject,
            int expDays,
            PublicKey pubk,
            PrivateKey privk,
            KeyUsage usage,
            PQCAlgorithms algorithm) throws Exception {

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

        certBuilder.addExtension(Extension.keyUsage, true, usage);
        certBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(false));

        ContentSigner signer;
        if (algorithm.canSign) {
        	// The algorithm can sign (ML-DSA, SLH-DSA)
            signer = new JcaContentSignerBuilder(algorithm.bcName)
                    .setProvider("BC")
                    .build(privk);
        } else {
            // IF IT IS A KEM (ML-KEM): It cannot sign itself.
            // The certificate will contain the original ML-KEM public key.
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("ML-DSA", "BC");
            kpg.initialize(MLDSAParameterSpec.ml_dsa_65, new SecureRandom());
            KeyPair tempSigPair = kpg.generateKeyPair();

            signer = new JcaContentSignerBuilder("ML-DSA-65")
                    .setProvider("BC")
                    .build(tempSigPair.getPrivate());
        }

        X509CertificateHolder holder = certBuilder.build(signer);

        return new JcaX509CertificateConverter()
                .setProvider("BC")
                .getCertificate(holder);
    }
}
