package com.reading.start.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.reading.start.AppCore;
import com.reading.start.Constants;
import com.reading.start.general.TLog;
import com.reading.start.tests.Server;

import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;

/**
 * Helper class for working with network.
 */
public class NetworkHelper {
    private static final String TAG = NetworkHelper.class.getSimpleName();

    private static Context m_context = null;

    public static void initialize(Context context) {
        m_context = context;
    }

    /**
     * Check if exist internet connection.
     */
    public static boolean checkInternetConnection(boolean wifiOnly) {
        boolean haveConnection = false;

        try {
            if (!wifiOnly || (wifiOnly && isWiFiAvailable())) {
                ConnectivityManager cm = (ConnectivityManager) m_context.getSystemService(Context.CONNECTIVITY_SERVICE);
                boolean mobileNetworkAvailable = false;
                boolean wifiNetworkAvailable = false;
                boolean bluetoothNetworkAvailable = false;

                // Iterate over available network connections to make sure we have
                // access
                // to Internet.
                for (NetworkInfo networkInfo : cm.getAllNetworkInfo()) {
                    if (networkInfo.isConnected()) {
                        if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                            mobileNetworkAvailable = true;
                            continue;
                        }
                        if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                            wifiNetworkAvailable = true;
                        }

                        if (networkInfo.getType() == ConnectivityManager.TYPE_BLUETOOTH) {
                            bluetoothNetworkAvailable = true;
                        }
                    }
                }

                haveConnection = mobileNetworkAvailable || wifiNetworkAvailable || bluetoothNetworkAvailable;
            }
        } catch (Exception e) {
            TLog.e(TAG, "checkInternetConnection", e);
        }

        return haveConnection;
    }

    /**
     * Check if WiFi available.
     */
    public static boolean isWiFiAvailable() {
        boolean result = false;

        ConnectivityManager connManager = (ConnectivityManager) m_context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {
            result = true;
        }

        return result;
    }

    /**
     * Check if internet connection is active.
     */
    public static boolean hasActiveInternetConnection() {
        if (checkInternetConnection(false)) {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://google.com").openConnection());
                urlc.setConnectTimeout(Constants.NETWORK_CHECK_TIMEOUT);
                urlc.connect();

                if (urlc.getResponseCode() != 200) {
                    return false;
                }

                URL urlServer = new URL(Server.getServer(AppCore.getInstance()));
                SocketAddress address = new InetSocketAddress(urlServer.getHost(), urlServer.getPort() == -1 ? 80 : urlServer.getPort());
                Socket sock = new Socket();
                int timeoutMs = Constants.NETWORK_CHECK_TIMEOUT;
                sock.connect(address, timeoutMs);
                return sock.isConnected();
            } catch (Exception e) {
                TLog.d(TAG, "Error checking internet connection", e);
            }
        } else {
            TLog.d(TAG, "No network available!");
        }

        return false;
    }
}
