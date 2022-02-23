package com.reading.start.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Base64;

import com.reading.start.AppCore;
import com.reading.start.Constants;
import com.reading.start.general.TLog;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.concurrent.CountDownLatch;

/**
 * Helper class for working with bitmaps.
 */
public class BitmapUtils {
    private static final String TAG = BitmapUtils.class.getSimpleName();

    /**
     * Convert bitmap to base64
     */
    public static String bitmapToBase64(Bitmap bitmap) {
        String result = null;

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            result = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
        } catch (Exception e) {
            TLog.e(TAG, e);
        }

        return result;
    }

    /**
     * Convert file content bo base64
     */
    public static String fileToBase64(File file) {
        String result = null;

        try {
            if (file != null && file.exists()) {
                final StringBuilder builder = new StringBuilder();
                CountDownLatch doneSignal = new CountDownLatch(1);

                Picasso.with(AppCore.getInstance()).load(file).resize(0, Constants.PHOTO_HEIGHT).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        builder.append(bitmapToBase64(bitmap));
                        doneSignal.countDown();
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        doneSignal.countDown();
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                    }
                });

                doneSignal.await();
                result = builder.toString();
            }
        } catch (Exception e) {
            TLog.e(TAG, e);
        }

        return result;
    }

    /**
     * Create bitmap from base64.
     */
    public static Bitmap bitmapFromBase64(String base64Str) throws IllegalArgumentException {
        Bitmap result = null;

        try {
            if (base64Str != null && base64Str.length() > 0) {
                byte[] decodedBytes = Base64.decode(base64Str.substring(base64Str.indexOf(",") + 1), Base64.DEFAULT);
                result = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            }
        } catch (Exception e) {
            TLog.e(TAG, e);
        }

        return result;
    }
}
