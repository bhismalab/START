package com.reading.start.tests.test_motor_following.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.reading.start.tests.TestLog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class BitmapUtils {
    private static final String TAG = BitmapUtils.class.getSimpleName();

    public static String bitmapToBase64(Bitmap bitmap) {
        String result = null;

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            result = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
        } catch (Exception e) {
            TestLog.e(TAG, e);
        }

        return result;
    }

    public static Bitmap bitmapFromBase64(String base64Str) throws IllegalArgumentException {
        Bitmap result = null;

        try {
            if (base64Str != null && base64Str.length() > 0) {
                byte[] decodedBytes = Base64.decode(base64Str.substring(base64Str.indexOf(",") + 1), Base64.DEFAULT);
                result = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            }
        } catch (Exception e) {
            TestLog.e(TAG, e);
        }

        return result;
    }

    public static File bitmapToFile(Bitmap bitmap, String fileName, File path) {
        File result = null;

        try {
            result = new File(path, fileName);
            result.createNewFile();

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            byte[] bitmapData = bos.toByteArray();

            FileOutputStream fos = new FileOutputStream(result);
            fos.write(bitmapData);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            TestLog.e(TAG, e);
        }

        return result;
    }
}
