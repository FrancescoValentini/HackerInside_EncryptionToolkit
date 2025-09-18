package it.hackerinside.etk.TEST;

import java.io.File;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import it.hackerinside.etk.core.keystore.AbstractKeystore;
import it.hackerinside.etk.core.keystore.PKCS12Keystore;

public class EncryptTest {

	public static void main(String[] args) throws Exception {
		File rsaKeystore = new File("RSATEST.pfx");
		File eccKeystore = new File("ECCTEST.pfx");
		
		String rsaAlias = "rsatest", eccAlias = "ecctest";
		
		char[] pwd = "123".toCharArray();
		
		// 1) Opens the keystore
		AbstractKeystore pkcs12_rsa = new PKCS12Keystore(rsaKeystore,pwd); 
		AbstractKeystore pkcs12_ecc = new PKCS12Keystore(eccKeystore,pwd); 
		pkcs12_rsa.load();
		pkcs12_ecc.load();
		
		// 2) Print certificates informations
		Enumeration<String> rsaCerts = pkcs12_rsa.listAliases();
		Enumeration<String> eccCerts = pkcs12_ecc.listAliases();
		
		System.out.println("RSA ALIASES");
		rsaCerts.asIterator().forEachRemaining(id -> System.out.println("  - " + id));
		
		System.out.println("\nECC ALIASES");
		eccCerts.asIterator().forEachRemaining(id -> System.out.println("  - " + id));
		
		X509Certificate rsaCert = pkcs12_rsa.getCertificate(rsaAlias);
		X509Certificate eccCert = pkcs12_ecc.getCertificate(eccAlias);
		System.out.println("==== RSA CERTIFICATE ====");
		System.out.println(rsaCert);
		
		System.out.println("==== ECC CERTIFICATE ====");
		System.out.println(eccCert);
		
		
	}

}
