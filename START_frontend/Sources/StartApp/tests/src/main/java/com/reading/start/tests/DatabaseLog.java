package com.reading.start.tests;

import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * Represent helper file for save database log.
 */
public class DatabaseLog {
    private static final String TAG = DatabaseLog.class.getSimpleName();

    private static File mLogFile = null;

    private static void init() {
        try {
            String root = Environment.getExternalStorageDirectory().toString();
            File folder = new File(root + "/" + Constants.DATABASE_LOG_FOLDER);

            if (!folder.exists()) {
                folder.mkdir();
            }

            File logFile = new File(folder, Constants.DATABASE_LOG_FILE);

            if (logFile.exists()) {
                logFile.delete();
            }

            if (logFile.createNewFile()) {
                mLogFile = logFile;
            }
        } catch (Exception e) {
            TestLog.d(TAG, e);
        }
    }

    public static BufferedWriter getBufferedWriter() {
        BufferedWriter result = null;

        try {
            if (BuildConfig.LOG_DATABASE) {
                init();

                if (mLogFile != null && mLogFile.exists()) {
                    result = new BufferedWriter(new FileWriter(mLogFile, true));
                }
            }
        } catch (Exception e) {
            TestLog.d(TAG, e);
        }

        return result;
    }

    public static void writeDump() {
        try {
            if (BuildConfig.LOG_DATABASE) {
                init();

                if (mLogFile != null && mLogFile.exists()) {
                    BufferedWriter buf = new BufferedWriter(new FileWriter(mLogFile, true));
                    buf.append("-------------------------- SOCIAL WORKERS DUMP --------------------------");
                    buf.append("ID | SERVER ID");


                    buf.append("-------------------------- CHILD DUMP --------------------------");
                    buf.append("-------------------------- SURVEYS DUMP --------------------------");
                    buf.newLine();
                    buf.close();
                }
            }
        } catch (Exception e) {
            TestLog.d(TAG, e);
        }
    }
}
