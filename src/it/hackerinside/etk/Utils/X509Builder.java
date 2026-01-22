package it.hackerinside.etk.Utils;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.security.spec.ECGenParameterSpec;
import java.util.Calendar;
import java.util.Date;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

public class X509Builder {
	
    /**
     * Generate ECC KeyPairs
     * @param curve the desired ECC curve
     */
    public static KeyPair generateECKeyPair(String curve) throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC", "BC");
        kpg.initialize(new ECGenParameterSpec(curve), new SecureRandom());
        return kpg.generateKeyPair();
    }
    
    /**
     * Builds a self-signed X.509 certificate using the provided subject attributes and a default
     * {@link KeyUsage}.
     *
     * @param commonName  the Common Name (CN) for the certificate subject
     * @param countryCode the country code (C) for the certificate subject
     * @param state       the state or province (ST) for the certificate subject
     * @param expDays     the number of days from now until the certificate expires
     * @param pubk        the public key to embed in the certificate
     * @param privk       the private key used to sign the certificate
     * @return a generated self-signed {@link X509Certificate}
     * @throws Exception if certificate generation or signing fails
     */
    public static X509Certificate buildCertificate(
            String commonName,
            String countryCode,
            String state,
            int expDays,
            PublicKey pubk,
            PrivateKey privk) throws Exception {
    	
    	X500Name subject = new X500Name("CN=" + commonName + ", C=" + countryCode + ", ST=" + state);
    	KeyUsage usage = new KeyUsage(KeyUsage.digitalSignature | KeyUsage.nonRepudiation | KeyUsage.dataEncipherment);
    	return buildCertificate(subject, expDays, usage, pubk, privk);
    }
    
    /**
     * Builds a self-signed X.509 certificate using the provided subject attributes and explicit
     * {@link KeyUsage}.
     *
     * @param commonName  the Common Name (CN) for the certificate subject
     * @param countryCode the country code (C) for the certificate subject
     * @param state       the state or province (ST) for the certificate subject
     * @param expDays     the number of days from now until the certificate expires
     * @param usage       the {@link KeyUsage} extension to include in the certificate
     * @param pubk        the public key to embed in the certificate
     * @param privk       the private key used to sign the certificate
     * @return a generated self-signed {@link X509Certificate}
     * @throws Exception if certificate generation or signing fails
     */
    public static X509Certificate buildCertificate(
            String commonName,
            String countryCode,
            String state,
            int expDays,
            KeyUsage usage,
            PublicKey pubk,
            PrivateKey privk) throws Exception {
    	
    	X500Name subject = new X500Name("CN=" + commonName + ", C=" + countryCode + ", ST=" + state);
    	return buildCertificate(subject, expDays, usage, pubk, privk);
    }
    
    /**
     * Builds a self-signed X.509 v3 certificate for the given {@link X500Name} subject.
     * The certificate validity starts at the current time, expires after the specified
     * number of days, includes the provided {@link KeyUsage}, and is signed using
     * the supplied private key.
     *
     * @param subject the X.500 distinguished name used as both subject and issuer
     * @param expDays the number of days from now until the certificate expires
     * @param usage   the {@link KeyUsage} extension to include in the certificate
     * @param pubk    the public key to embed in the certificate
     * @param privk   the private key used to sign the certificate
     * @return a generated self-signed {@link X509Certificate}
     * @throws Exception if certificate generation or signing fails
     */
    public static X509Certificate buildCertificate(
    		X500Name subject,
            int expDays,
            KeyUsage usage,
            PublicKey pubk,
            PrivateKey privk) throws Exception {
    	
        Date notBefore = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(notBefore);
        cal.add(Calendar.DAY_OF_YEAR, expDays);
        Date notAfter = cal.getTime();

        BigInteger serial = new BigInteger(64, new SecureRandom()).abs();

        JcaX509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
            subject, serial, notBefore, notAfter, subject, pubk
        );

        certBuilder.addExtension(
            Extension.keyUsage,
            true,
            usage
        );
        
        certBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(false));


        ContentSigner signer = new JcaContentSignerBuilder("SHA384withECDSA")
            .setProvider("BC")
            .build(privk);

        return new JcaX509CertificateConverter()
            .setProvider("BC")
            .getCertificate(certBuilder.build(signer));
    }
}
