package it.hackerinside.etk.TEST;

import java.io.File;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import it.hackerinside.etk.core.CAdES.CAdESSigner;
import it.hackerinside.etk.core.Models.EncodingOption;
import it.hackerinside.etk.core.Models.HashAlgorithm;

public class SignatureTest {

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
		CAdESSigner rsaSigner = new CAdESSigner(rsaPriv, rsaCert, EncodingOption.ENCODING_DER, HashAlgorithm.SHA256, false);
		rsaSigner.sign(toSign, new File("rsa_signed.test"));
		
		
		System.out.println("ECC SIGNATURE");
		CAdESSigner eccSigner = new CAdESSigner(eccPriv, eccCert, EncodingOption.ENCODING_PEM, HashAlgorithm.SHA384, false);
		eccSigner.sign(toSign, new File("ecc_signed.test"));

		System.out.println("ECC SIGNATURE DETACHED");
		CAdESSigner eccSignerDetached = new CAdESSigner(eccPriv, eccCert, EncodingOption.ENCODING_PEM, HashAlgorithm.SHA384, true);
		eccSignerDetached.sign(toSign, new File("ecc_signed_detached.test"));
	}

}
