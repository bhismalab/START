package com.reading.start.sdk.utils;

import com.reading.start.sdk.general.SdkLog;

import org.opencv.core.Point;
import org.opencv.core.Rect;

import java.util.LinkedList;
import java.util.List;

/**
 * Utils for working with different math data.
 */
public class Utils {
    private static final String TAG = Utils.class.getSimpleName();

    /**
     * Gets average point
     */
    public static Point getAveragePoint(final List<Point> items) {
        Point result = new Point(0, 0);

        try {
            if (items != null && items.size() > 0) {
                for (Point item : items) {
                    result.x += item.x;
                    result.y += item.y;
                }

                result.x = result.x / items.size();
                result.y = result.y / items.size();
            }
        } catch (Exception e) {
            SdkLog.e(TAG, e);
        }

        return result;
    }

    /**
     * Gets average rect
     */
    public static Rect getAverageRect(final LinkedList<Rect> items) {
        Rect result = new Rect(0, 0, 0, 0);

        try {
            if (items != null && items.size() > 0) {
                double w = 0;
                double h = 0;
                double x = 0;
                double y = 0;

                for (Rect item : items) {
                    if (item != null) {
                        w += item.width;
                        h += item.height;
                        x += item.x;
                        y += item.y;
                    }
                }

                result.x = (int) (x / items.size());
                result.y = (int) (y / items.size());
                result.width = (int) (w / items.size());
                result.height = (int) (h / items.size());
            }
        } catch (Exception e) {
            SdkLog.e(TAG, e);
        }

        return result;
    }

    /**
     * Gets size of face area relative frame size
     */
    public static int getFillFaceAreaSize(int width, int height, int faceWidth, int faceHeight) {
        int result = 0;
        double full = width * height;
        double face = faceWidth * faceHeight;
        result = (int) (face / full * 100);
        return result;
    }
}
