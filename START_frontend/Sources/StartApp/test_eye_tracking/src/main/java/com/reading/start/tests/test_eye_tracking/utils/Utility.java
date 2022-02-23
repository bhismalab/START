package com.reading.start.tests.test_eye_tracking.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Size;
import android.view.WindowManager;

import com.reading.start.tests.TestLog;

public class Utility {
    private static final String TAG = Utility.class.getSimpleName();

    public static Size getDisplaySize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        return new Size(dm.widthPixels, dm.heightPixels);
    }

    @SuppressLint("MissingPermission")
    public static String getDeviceId(Context context) {
        String result = "";

        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            if (tm != null) {
                result = tm.getDeviceId();
            }

            if (result == null || result.isEmpty()) {
                result = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            }
        } catch (Exception e) {
            TestLog.e(TAG, e);
        }

        return result;
    }

    public static float[] getXYDpi(Context context) {
        float[] result = new float[2];
        result[0] = 0;
        result[1] = 0;

        try {
            if (context != null) {
                DisplayMetrics dm = context.getResources().getDisplayMetrics();

                if (dm != null) {
                    result[0] = dm.xdpi;
                    result[1] = dm.ydpi;
                }
            }
        } catch (Exception e) {
            TestLog.e(TAG, e);
        }

        return result;
    }
}
