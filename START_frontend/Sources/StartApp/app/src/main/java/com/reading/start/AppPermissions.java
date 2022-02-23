package com.reading.start;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.reading.start.general.TLog;

/**
 * Intended for check application permission.
 */
public class AppPermissions {
    private static final String TAG = AppPermissions.class.getSimpleName();

    /**
     * List of required permissions.
     */
    private static String[] mPermissionList = new String[]
            {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_PHONE_STATE
            };

    /**
     * Return list of required permissions.
     */
    public static String[] getPermissionList() {
        return mPermissionList;
    }

    /**
     * Check if permissions was granted.
     */
    public static boolean checkPermissionGranted(Context context) {
        boolean result = false;

        try {
            // need check permission for api level 23 and above
            if (Build.VERSION.SDK_INT >= 23) {

                result = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
            } else {
                result = true;
            }
        } catch (Exception e) {
            TLog.e(TAG, "checkPermissionGranted", e);
        }

        return result;
    }

    /**
     * Show dialog for allow permission.
     */
    public static boolean shouldShowRequestPermissionRationale(Activity context) {
        boolean result = false;

        try {
            if (Build.VERSION.SDK_INT >= 23) {

                result = ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                        || ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        || ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.CAMERA)
                        || ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.ACCESS_FINE_LOCATION)
                        || ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                        || ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.RECORD_AUDIO)
                        || ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.READ_PHONE_STATE);
            }
        } catch (Exception e) {
            TLog.e(TAG, "shouldShowRequestPermissionRationale", e);
        }

        return result;
    }
}
