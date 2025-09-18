package it.hackerinside.etk.TEST;

import java.io.File;
import java.security.Security;
import java.security.cert.X509Certificate;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import it.hackerinside.etk.core.Encryption.CMSEncryptor;
import it.hackerinside.etk.core.Models.EncodingOption;
import it.hackerinside.etk.core.Models.SymmetricAlgorithms;

public class EncryptTest {
	static {
	    Security.addProvider(new BouncyCastleProvider());
	}
	public static void main(String[] args) throws Exception {

		KeystoreTest ks = new KeystoreTest();
		File toEncrypt = new File("file.test");
		String rsaAlias = "rsatest", eccAlias = "ecctest";
		
		// Load certificates
		X509Certificate rsaCert = ks.pkcs12_rsa.getCertificate(rsaAlias);
		X509Certificate eccCert = ks.pkcs12_ecc.getCertificate(eccAlias);
		
		// Encrypt 
		System.out.println("RSA ENCRYPT");
		CMSEncryptor rsaEncryptor = new CMSEncryptor(rsaCert, SymmetricAlgorithms.AES_256_CBC, EncodingOption.ENCODING_DER);
		rsaEncryptor.encrypt(toEncrypt, new File("enc_rsa.test"));
		
		System.out.println("ECC ENCRYPT");
		CMSEncryptor eccEncryptor = new CMSEncryptor(eccCert, SymmetricAlgorithms.AES_256_CBC, EncodingOption.ENCODING_PEM);
		eccEncryptor.encrypt(toEncrypt, new File("enc_ecc.test"));
	}

}
