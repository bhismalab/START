package com.reading.start.tests;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Save and provide link to server.
 */
public class Server {
    private static final String TAG = Server.class.getSimpleName();

    private static final String s_preference_server = "server";
    private static final String s_preference_server_default = BuildConfig.SERVER;

    /**
     * Get server link.
     */
    public static String getServer(Context context) {
        String result = null;

        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            result = prefs.getString(s_preference_server, s_preference_server_default);
        } catch (Exception e) {
            TestLog.e(TAG, e);
        }

        return result;
    }

    /**
     * Gets retrofit instance.
     */
    public static Retrofit getRetrofit(String server) {
        OkHttpClient okHttpClient = getUnsafeOkHttpClient();
        return new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(server)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    /**
     * Set server link.
     */
    public static void setServer(Context context, String value) {
        setStringPreference(context, s_preference_server, value);
    }

    private static void setStringPreference(Context context, String key, String value) {
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(key, value);
            editor.commit();
        } catch (Exception e) {
            TestLog.e(TAG, "Unable set Boolean preferences: " + key, e);
        }
    }

    private static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier((hostname, session) -> true);
            builder.readTimeout(Constants.OK_HTTP_CLIENT_TIMEOUT, TimeUnit.SECONDS);
            builder.writeTimeout(Constants.OK_HTTP_CLIENT_TIMEOUT, TimeUnit.SECONDS);
            builder.connectTimeout(Constants.OK_HTTP_CLIENT_TIMEOUT, TimeUnit.SECONDS);
            builder.socketFactory(new RestrictedSocketFactory(Constants.OK_HTTP_CLIENT_BUFFER_SIZE)); // Fix accordingly this link: https://stackoverflow.com/questions/42224186/what-is-wrong-with-upload-speed-of-some-devices-with-multipart-okhttp3-retrofit

            OkHttpClient okHttpClient = builder.build();
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
