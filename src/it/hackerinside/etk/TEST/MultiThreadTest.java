package it.hackerinside.etk.TEST;

import java.io.File;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import it.hackerinside.etk.core.CAdES.CAdESSigner;
import it.hackerinside.etk.core.Models.EncodingOption;
import it.hackerinside.etk.core.Models.HashAlgorithm;

public class MultiThreadTest {
    public static void main(String[] args) throws Exception {
        KeystoreTest ks = new KeystoreTest();
        File toSign = new File("bigfile.test");
        String rsaAlias = "rsatest";

        X509Certificate rsaCert = ks.pkcs12_rsa.getCertificate(rsaAlias);
        PrivateKey rsaPriv = ks.pkcs12_rsa.getPrivateKey(rsaAlias, "123".toCharArray());

        CAdESSigner rsaSigner = new CAdESSigner(
            rsaPriv,
            rsaCert,
            EncodingOption.ENCODING_DER,
            HashAlgorithm.SHA256,
            false,8192
        );

        ExecutorService executor = Executors.newSingleThreadExecutor();

        Future<?> future = executor.submit(() -> {
            try {
                rsaSigner.sign(toSign, new File("rsa_signed.test"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        while (!future.isDone()) {
            System.out.println("Signing...");
            Thread.sleep(500);
        }

        future.get();
        System.out.println("File signed!");

        executor.shutdown();
    }
}