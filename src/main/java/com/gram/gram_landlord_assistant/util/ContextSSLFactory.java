package com.gram.gram_landlord_assistant.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class ContextSSLFactory {
    private static final SSLContext sslContext_server;
    private static final SSLContext sslContext_client;
    private static final String SERVER_JKS = "tls/landlord.server.bks";
    private static final String CLIENT_JKS = "tls/landlord.client.bks";
    private static final String PASSWORD = "yangshu";

    static {
        SSLContext sslContext1 = null;
        SSLContext sslContext2 = null;
        try {
            sslContext1 = SSLContext.getInstance("TLS");
            sslContext2 = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            log.error(e.getMessage());
        }
        if(sslContext1 != null && sslContext2 != null) {
            try {
                if(getKeyManagersServer() != null && getTrustManagersServer() != null)
                    sslContext1.init(getKeyManagersServer(), getTrustManagersServer(), null);
                if(getKeyManagersClient() != null && getTrustManagersClient() != null)
                    sslContext2.init(getKeyManagersClient(), getTrustManagersClient(), null);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            sslContext1.createSSLEngine().getSupportedCipherSuites();
            sslContext2.createSSLEngine().getSupportedCipherSuites();
        }

        sslContext_server = sslContext1;
        sslContext_client = sslContext2;
    }

    public static SSLContext getServerSslContext() {
        return sslContext_server;
    }

    public static SSLContext getClientSslContext() {
        return sslContext_client;
    }

    private static TrustManager[] getTrustManagersServer() {
        return getTrustManagers(SERVER_JKS, PASSWORD);
    }

    private static TrustManager[] getTrustManagersClient() {
        return getTrustManagers(CLIENT_JKS, PASSWORD);
    }

    private static TrustManager[] getTrustManagers(String jksPath, String password) {
        FileInputStream is = null;
        KeyStore ks;
        TrustManagerFactory fac;
        TrustManager[] tms = null;
        try {
            fac = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            is = new FileInputStream((new ClassPathResource(jksPath)).getFile());
            ks = KeyStore.getInstance("BKS");
            ks.load(is, password.toCharArray());
            fac.init(ks);
            tms = fac.getTrustManagers();
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            if(is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
        return tms;
    }

    private static KeyManager[] getKeyManagersServer() {
        return getKeyManagers(SERVER_JKS, PASSWORD);
    }

    private static KeyManager[] getKeyManagersClient() {
        return getKeyManagers(CLIENT_JKS, PASSWORD);
    }

    private static KeyManager[] getKeyManagers(String jksPath, String password) {
        FileInputStream is = null;
        KeyStore ks;
        KeyManagerFactory fac;
        KeyManager[] kms = null;
        try {
            fac = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            is = new FileInputStream((new ClassPathResource(jksPath)).getFile());
            ks = KeyStore.getInstance("BKS");
            ks.load(is, password.toCharArray());
            fac.init(ks, password.toCharArray());
            kms = fac.getKeyManagers();
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            if(is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
        return kms;
    }

}
