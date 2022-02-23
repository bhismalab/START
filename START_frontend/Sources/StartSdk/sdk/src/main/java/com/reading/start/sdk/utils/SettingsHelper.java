package com.reading.start.sdk.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.reading.start.sdk.AppCore;
import com.reading.start.sdk.Constants;
import com.reading.start.sdk.core.Settings;

import java.io.File;

public class SettingsHelper {
    private static final String TAG = SettingsHelper.class.getSimpleName();

    public static Settings loadSetting() {
        Settings result = null;
        File dir = AppCore.getInstance().getFilesDir();
        File file = new File(dir.getPath() + File.pathSeparator + Constants.SETTINGS_FILE_NAME);

        try {
            if (file.exists()) {
                Gson gson = new Gson();
                String json = SDCardUtils.readFileToString(file);
                result = gson.fromJson(json, Settings.class);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        return result;
    }

    public static Settings loadPostprocessingSetting() {
        Settings result = null;
        File dir = AppCore.getInstance().getFilesDir();
        File file = new File(dir.getPath() + File.pathSeparator + Constants.POSTPROCESSING_SETTINGS_FILE_NAME);

        try {
            if (file.exists()) {
                Gson gson = new Gson();
                String json = SDCardUtils.readFileToString(file);
                result = gson.fromJson(json, Settings.class);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        return result;
    }

    public static boolean saveSetting(Settings settings) {
        boolean result = false;

        File dir = AppCore.getInstance().getFilesDir();
        File file = new File(dir.getPath() + File.pathSeparator + Constants.SETTINGS_FILE_NAME);

        try {
            Gson gson = new Gson();
            String json = gson.toJson(settings);
            result = SDCardUtils.writeStringToFile(file, json);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        return result;
    }

    public static boolean savePostprocessingSetting(Settings settings) {
        boolean result = false;

        File dir = AppCore.getInstance().getFilesDir();
        File file = new File(dir.getPath() + File.pathSeparator + Constants.POSTPROCESSING_SETTINGS_FILE_NAME);

        try {
            Gson gson = new Gson();
            String json = gson.toJson(settings);
            result = SDCardUtils.writeStringToFile(file, json);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        return result;
    }
}
