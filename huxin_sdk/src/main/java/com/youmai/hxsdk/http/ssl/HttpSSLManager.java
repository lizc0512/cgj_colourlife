package com.youmai.hxsdk.http.ssl;

import android.support.annotation.NonNull;

import com.youmai.hxsdk.config.AppConfig;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by fylder on 2017/5/9.
 */

public class HttpSSLManager {

    private static final String KEY_STORE_TYPE_P12 = "PKCS12";

    private static SSLSocketFactory mSslSocketFactory;

    public static SSLSocketFactory getSocketFactory() {

        if (mSslSocketFactory == null) {
            // 添加证书
            List<InputStream> certificates = new ArrayList<>();
            List<byte[]> certs_data = NetConfig.getCertificatesData();
            if (certs_data != null && !certs_data.isEmpty()) {
                for (byte[] bytes : certs_data) {
                    certificates.add(new ByteArrayInputStream(bytes));
                }
            }
            //key
            List<InputStream> keyCertificates = new ArrayList<>();
            List<byte[]> key_certs_data = NetConfig.getKeyCertificatesData();
            if (key_certs_data != null && !key_certs_data.isEmpty()) {
                for (byte[] bytes : key_certs_data) {
                    keyCertificates.add(new ByteArrayInputStream(bytes));
                }
            }
            if (key_certs_data.size() > 0) {
                mSslSocketFactory = getSocketFactory(certificates, keyCertificates.get(0), getKeyPass());
                return mSslSocketFactory;
            } else {
                return null;
            }
        } else {
            return mSslSocketFactory;
        }

    }

    /**
     * 单向认证
     * 添加cer证书
     */
    private static SSLSocketFactory getSocketFactoryCer(List<InputStream> certificates) {

        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            try {
                for (int i = 0, size = certificates.size(); i < size; ) {
                    InputStream certificate = certificates.get(i);
                    String certificateAlias = Integer.toString(i++);
                    keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));
                    if (certificate != null)
                        certificate.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 双向认证
     * <p>
     * tip:暂不支持导入多个客户端证书
     *
     * @param certificates 客户端信任的服务器端证书
     * @param key          服务器端需要验证的客户端证书，其实就是客户端的keystore  ps:.p12
     * @param keyPassword
     */
    private static SSLSocketFactory getSocketFactory(@NonNull List<InputStream> certificates, @NonNull InputStream key, @NonNull String keyPassword) {

        try {
            // 保存服务器端需要验证的客户端证书，其实就是客户端的keystore
            KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE_P12);
            // 保存客户端信任的服务器端证书
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore trustStore = KeyStore.getInstance(keyStoreType);
            trustStore.load(null);
            InputStream ksIn = key;
            try {
                //加载证书
                keyStore.load(ksIn, keyPassword.toCharArray());

                int i = 1;
                for (InputStream certificate : certificates) {
                    CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509", "BC");
                    trustStore.setCertificateEntry("ca" + i, certificateFactory.generateCertificate(certificate));
                    if (certificate != null) {
                        certificate.close();
                    }
                    i++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    ksIn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // 初始化SSLContext
            SSLContext sslContext = SSLContext.getInstance("TLS");
            // Create a TrustManager that trusts the CAs in our KeyStore
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("X509");
            keyManagerFactory.init(keyStore, keyPassword.toCharArray());
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
            return sslContext.getSocketFactory();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getKeyPass() {
        String pass;
        if (AppConfig.LAUNCH_MODE == 2) {
            pass = NetConfig.p12P;
        } else {
            pass = NetConfig.p12P_test;
        }
        return pass;
    }

}
