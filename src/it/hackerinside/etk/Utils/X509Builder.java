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
     * Builds an X509 certificate with the provided subject details, validity, and keys.
     */
    public static X509Certificate buildCertificate(
            String commonName,
            String countryCode,
            String state,
            int expDays,
            PublicKey pubk,
            PrivateKey privk) throws Exception {

        X500Name subject = new X500Name("CN=" + commonName + ", C=" + countryCode + ", ST=" + state);

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
            new KeyUsage(KeyUsage.digitalSignature | KeyUsage.nonRepudiation | KeyUsage.dataEncipherment)
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
