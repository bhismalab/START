package com.reading.start.tests;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Represent helper methods for download file and images by link.
 */
public class DownloadHelper {
    private static final String TAG = DownloadHelper.class.getSimpleName();

    public static Bitmap downloadBitmap(String url) {
        Bitmap result = null;

        try {
            int index = url.lastIndexOf("/");
            String baseUrl = url.substring(0, index + 1);
            String requestUrl = url.substring(index + 1, url.length());
            OkHttpClient okHttpClient = getUnsafeOkHttpClient();

            Retrofit retrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(baseUrl)
                    .build();

            DownloadAPI api = retrofit.create(DownloadAPI.class);
            Call<ResponseBody> call = api.downloadFile(requestUrl);
            Response<ResponseBody> response = call.execute();

            if (response != null && response.isSuccessful()) {
                result = writeResponseBodyToDisk(response.body());
            }
        } catch (Exception e) {
            TestLog.e(TAG, "downloadFile", e);
        }

        return result;
    }

    public static boolean downloadFile(String url, File file) {
        boolean result = false;

        try {
            int index = url.lastIndexOf("/");
            String baseUrl = url.substring(0, index + 1);
            String requestUrl = url.substring(index + 1, url.length());
            OkHttpClient okHttpClient = getUnsafeOkHttpClient();

            Retrofit retrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(baseUrl)
                    .build();

            DownloadAPI api = retrofit.create(DownloadAPI.class);
            Call<ResponseBody> call = api.downloadFile(requestUrl);
            Response<ResponseBody> response = call.execute();

            if (response != null && response.isSuccessful()) {
                result = writeResponseBodyToDisk(response.body(), file);
            }
        } catch (Exception e) {
            TestLog.e(TAG, "downloadFile", e);
        }

        return result;
    }

    private static boolean writeResponseBodyToDisk(ResponseBody body, File file) {
        try {
            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(file);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    TestLog.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    private static Bitmap writeResponseBodyToDisk(ResponseBody body) {
        try {
            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                inputStream = body.byteStream();
                return BitmapFactory.decodeStream(inputStream);
            } catch (Exception e) {
                return null;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return null;
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

    private interface DownloadAPI {
        @Streaming
        @GET()
        Call<ResponseBody> downloadFile(@Url String url);
    }
}
