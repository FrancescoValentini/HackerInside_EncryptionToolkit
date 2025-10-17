package it.hackerinside.etk.TEST;

import java.io.File;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import it.hackerinside.etk.core.CAdES.CAdESSigner;
import it.hackerinside.etk.core.CAdES.CAdESUtils;
import it.hackerinside.etk.core.CAdES.CAdESVerifier;
import it.hackerinside.etk.core.Models.EncodingOption;
import it.hackerinside.etk.core.Models.HashAlgorithm;
import it.hackerinside.etk.core.Models.VerificationResult;

public class SignatureTest {
    static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }
	public static void main(String[] args) throws Exception {
		
		KeystoreTest ks = new KeystoreTest();
		File toSign = new File("file.test");
		String rsaAlias = "rsatest", eccAlias = "ecctest";
		
		// Load certificates
		X509Certificate rsaCert = ks.pkcs12_rsa.getCertificate(rsaAlias);
		X509Certificate eccCert = ks.pkcs12_ecc.getCertificate(eccAlias);
		
		PrivateKey rsaPriv = ks.pkcs12_rsa.getPrivateKey(rsaAlias, "123".toCharArray());
		PrivateKey eccPriv = ks.pkcs12_ecc.getPrivateKey(eccAlias, "123".toCharArray());
		
		System.out.println("RSA SIGNATURE");
		CAdESSigner rsaSigner = new CAdESSigner(rsaPriv, rsaCert, EncodingOption.ENCODING_DER, HashAlgorithm.SHA256, false,8192);
		rsaSigner.sign(toSign, new File("rsa_signed.test"));
		
		
		System.out.println("ECC SIGNATURE");
		CAdESSigner eccSigner = new CAdESSigner(eccPriv, eccCert, EncodingOption.ENCODING_PEM, HashAlgorithm.SHA384, false,8192);
		eccSigner.sign(toSign, new File("ecc_signed.test"));

		System.out.println("ECC SIGNATURE DETACHED");
		CAdESSigner eccSignerDetached = new CAdESSigner(eccPriv, eccCert, EncodingOption.ENCODING_PEM, HashAlgorithm.SHA384, true,8192);
		eccSignerDetached.sign(toSign, new File("ecc_signed_detached.test"));
		
		System.out.println(CAdESUtils.isDetached(new File("ecc_signed_detached.test"), EncodingOption.ENCODING_PEM));
		System.out.println(CAdESUtils.isDetached(new File("rsa_signed.test"), EncodingOption.ENCODING_DER));
		
		// Verify TEST
		
		CAdESVerifier verifier = new CAdESVerifier(EncodingOption.ENCODING_PEM, false,8192);
		VerificationResult result = verifier.verify(new File("ecc_signed.test"));
		verifier.extractContent(new File("ecc_signed.test"), new File("ecc_extracted.test"));
		System.out.println(result);
		System.out.println(result.getSigningTime());
	}

}
