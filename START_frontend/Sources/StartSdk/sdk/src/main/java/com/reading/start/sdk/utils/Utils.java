package com.reading.start.sdk.utils;

import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;

import java.util.LinkedList;
import java.util.List;

public class Utils {
    private static final String TAG = Utils.class.getSimpleName();

    public static Point getAveragePoint(List<Point> items) {
        Point result = new Point(0, 0);

        if (items != null && items.size() > 0) {
            for (Point item : items) {
                result.x += item.x;
                result.y += item.y;
            }

            result.x = result.x / items.size();
            result.y = result.y / items.size();
        }

        return result;
    }

    public static Size getAverageSize(List<Size> items) {
        Size result = new Size(0, 0);

        if (items != null && items.size() > 0) {
            for (Size item : items) {
                result.width += item.width;
                result.height += item.height;
            }

            result.width = result.width / items.size();
            result.height = result.height / items.size();
        }

        return result;
    }

    public static Rect getAverageRect(LinkedList<Rect> items) {
        Rect result = new Rect(0, 0, 0, 0);

        if (items != null && items.size() > 0) {
            double w = 0;
            double h = 0;
            double x = 0;
            double y = 0;

            for (Rect item : items) {
                w += item.width;
                h += item.height;
                x += item.x;
                y += item.y;
            }

            result.x = (int) (x / items.size());
            result.y = (int) (y / items.size());
            result.width = (int) (w / items.size());
            result.height = (int) (h / items.size());
        }

        return result;
    }

    public static int getFillFaceAreaSize(int width, int height, int faceWidth, int faceHeight) {
        int result = 0;
        double full = width * height;
        double face = faceWidth * faceHeight;
        result = (int) (face / full * 100);
        return result;
    }
}
