package cn.linked.baselib.repository;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Arrays;
import java.util.Collection;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import cn.linked.baselib.LinkApplication;
import cn.linked.baselib.common.AppCookieJar;
import okhttp3.OkHttpClient;

/**
 * cer为公钥证书，xxx.bks 为自身cer证书秘钥存储仓库  xxxtruststore.bks 为受信任的证书公钥存储库
 * */
public class OkHttpClientManager {

    public static final String TAG = "OkHttpClientManager";

    private static OkHttpClient INSTANCE;

    public static OkHttpClient getOkHttpClient() {
        if(INSTANCE == null) {
            synchronized (OkHttpClientManager.class) {
                if(INSTANCE == null) {
                    INSTANCE = init();
                }
            }
        }
        return INSTANCE;
    }

    private static OkHttpClient init() {
        X509TrustManager trustManager = getTrustManager();
        SSLSocketFactory sslSocketFactory = getSSLSocketFactory(trustManager);
        return new OkHttpClient.Builder()
                .cookieJar(new AppCookieJar(LinkApplication.getInstance()))
                .hostnameVerifier((hostname, session) -> true)
                .sslSocketFactory(sslSocketFactory, trustManager)
                .build();
    }

    private static SSLSocketFactory getSSLSocketFactory(X509TrustManager trustManager) {
        SSLSocketFactory ssfFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{ trustManager }, null);
            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
            Log.e(TAG, "getSSLSocketFactory failed");
        }
        return ssfFactory;
    }

    private static X509TrustManager getTrustManager() {
        char[] password = "758481".toCharArray();
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(LinkApplication.getInstance().getAssets().open("clienttruststore.bks"), password);
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                Log.e(TAG, "Unexpected default trust managers:" + Arrays.toString(trustManagers));
                return null;
            }
            return (X509TrustManager) trustManagers[0];
        }catch (Exception e) {
            Log.e(TAG, "build client trust keystore manager failed!");
            return null;
        }
    }

}
