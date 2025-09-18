package it.hackerinside.etk.TEST;

import java.io.File;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import it.hackerinside.etk.core.keystore.AbstractKeystore;
import it.hackerinside.etk.core.keystore.PKCS12Keystore;

public class KeystoreTest {
	File rsaKeystore = new File("RSATEST.pfx");
	File eccKeystore = new File("ECCTEST.pfx");
	char[] pwd = "123".toCharArray();
	String rsaAlias = "rsatest", eccAlias = "ecctest";
	AbstractKeystore pkcs12_rsa;
	AbstractKeystore pkcs12_ecc;
	/**
	 * @throws Exception 
	 * 
	 */
	public KeystoreTest() throws Exception {
		// 1) Opens the keystore
		pkcs12_rsa = new PKCS12Keystore(rsaKeystore,pwd); 
		pkcs12_ecc = new PKCS12Keystore(eccKeystore,pwd); 
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
