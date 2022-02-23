package com.reading.start.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.DisplayMetrics;
import android.util.Size;
import android.view.WindowManager;

import com.reading.start.general.TLog;

/**
 * Contains different helper method related to whole app and device.
 */
public class Utility {
    private static final String TAG = Utility.class.getSimpleName();

    /**
     * Get display size.
     */
    public static Size getDisplaySize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        return new Size(dm.widthPixels, dm.heightPixels);
    }

    /**
     * Initiate restart of the application.
     */
    public static void doRestart(Context c) {
        try {
            // check if the context is given
            if (c != null) {
                // fetch the packagemanager so we can get the default launch activity
                // (you can replace this intent with any other activity if you want
                PackageManager pm = c.getPackageManager();
                // check if we got the PackageManager
                if (pm != null) {
                    // create the intent with the default start activity for your
                    // application
                    Intent mStartActivity = pm.getLaunchIntentForPackage(c
                            .getPackageName());
                    if (mStartActivity != null) {
                        mStartActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        // create a pending intent so the application is restarted after
                        // System.exit(0) was called.
                        // We use an AlarmManager to call this intent in 100ms
                        int mPendingIntentId = 223344;
                        PendingIntent mPendingIntent = PendingIntent.getActivity(c,
                                mPendingIntentId, mStartActivity,
                                PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager mgr = (AlarmManager) c
                                .getSystemService(Context.ALARM_SERVICE);
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100,
                                mPendingIntent);
                        // kill the application
                        System.exit(0);
                    } else {
                        TLog.e(TAG, "Was not able to restart application, mStartActivity null");
                    }
                } else {
                    TLog.e(TAG, "Was not able to restart application, PM null");
                }
            } else {
                TLog.e(TAG, "Was not able to restart application, Context null");
            }
        } catch (Exception ex) {
            TLog.e(TAG, "Was not able to restart application", ex);
        }
    }

    /**
     * Return value that indicates whether device is narrow.
     */
    public static boolean isNarrow(Context context) {
        boolean result = true;

        try {
            Size size = getDisplaySize(context);
            double factor = (double) size.getWidth() / (double) size.getHeight();

            if (factor < 1.5) {
                result = false;
            }
        } catch (Exception e) {
            TLog.e(TAG, e);
        }

        return result;
    }
}
