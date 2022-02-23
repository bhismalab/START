package com.reading.start.sdk.general;

import android.util.Log;

import com.reading.start.sdk.BuildConfig;

public class SdkLog {
    private static final boolean DEBUG = BuildConfig.DEBUG;

    public static void i(String tag, String message, Throwable throwable) {
        if (DEBUG) {
            Log.i(tag, message, throwable);
            //Crashlytics.log(Log.INFO, tag, message);
        }
    }

    public static void i(String tag, String message) {
        i(tag, message, null);
    }

    public static void e(String tag, String message, Throwable throwable) {
        if (DEBUG) {
            Log.e(tag, message, throwable);
        }

//        Crashlytics.log(Log.ERROR, tag, message);

//        if (Fabric.isInitialized() && throwable != null) {
//            Crashlytics.logException(throwable);
//        }
    }

    public static void e(String tag, String message) {
        e(tag, message, null);
    }

    public static void e(String tag, Throwable throwable) {
        e(tag, null, throwable);
    }

    public static void d(String tag, String message, Throwable throwable) {
        if (DEBUG) {
            Log.d(tag, message, throwable);
//            Crashlytics.log(Log.DEBUG, tag, message);
        }
    }

    public static void d(String tag, String message) {
        d(tag, message, null);
    }

    public static void v(String tag, String message, Throwable throwable) {
        if (DEBUG) {
            Log.v(tag, message, throwable);
//            Crashlytics.log(Log.VERBOSE, tag, message);
        }
    }

    public static void v(String tag, String message) {
        v(tag, message, null);
    }

    public static void w(String tag, String message, Throwable throwable) {
        if (DEBUG) {
            Log.w(tag, message, throwable);
//            Crashlytics.log(Log.WARN, tag, message);
        }
    }

    public static void w(String tag, String message) {
        w(tag, message, null);
    }
}

