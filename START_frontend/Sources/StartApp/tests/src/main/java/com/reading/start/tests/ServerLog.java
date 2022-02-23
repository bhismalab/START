package com.reading.start.tests;

import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Represent helper file intended to collect all event related with network requests.
 */
public class ServerLog {
    private static final String TAG = ServerLog.class.getSimpleName();

    private static File mLogFile = null;

    private static SimpleDateFormat mTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private static void init() {
        try {
            String root = Environment.getExternalStorageDirectory().toString();
            File folder = new File(root + "/" + Constants.SERVER_LOG_FOLDER);

            if (!folder.exists()) {
                folder.mkdir();
            }

            File logFile = new File(folder, Constants.SERVER_LOG_FILE);

            if (!logFile.exists()) {
                if (logFile.createNewFile()) {
                    mLogFile = logFile;
                }
            } else {
                mLogFile = logFile;
            }

        } catch (Exception e) {
            TestLog.d(TAG, e);
        }
    }

    public static void log(String tag, String message) {
        try {
            if (BuildConfig.LOG_SERVER_REQUEST) {
                if (mLogFile == null || !mLogFile.exists()) {
                    init();
                }

                if (mLogFile != null && mLogFile.exists()) {
                    String time = mTimeFormat.format(Calendar.getInstance().getTime());
                    BufferedWriter buf = new BufferedWriter(new FileWriter(mLogFile, true));
                    buf.append(time + " : " + tag + " : " + message);
                    buf.newLine();
                    buf.close();
                }
            }
        } catch (Exception e) {
            TestLog.d(TAG, e);
        }
    }
}
