package com.reading.start.sdk.utils;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;

/**
 * Native wrapper. Uses for detect pupil on the image.
 */
public class PupilDetectHelper {
    private static final String TAG = PupilDetectHelper.class.getSimpleName();

    static {
        System.loadLibrary("eye-sdk-lib");
    }

    /**
     * Detect pupil in selected area.
     *
     * @param map     target image
     * @param eyeArea ROI are
     * @return center of pupil
     */
    public static Point detectEyeCenter(Mat map, Rect eyeArea, int pupilSize) {
        double[] arr = detectEyeCenter(map.getNativeObjAddr(), eyeArea.x, eyeArea.y, eyeArea.width, eyeArea.height, pupilSize);

        Point result = new Point(arr[0], arr[1]);
        result.x = result.x + eyeArea.x;
        result.y = result.y + eyeArea.y;

        return result;
    }

    /**
     * Calculate image brightness.
     */
    public static int calculateBrightness(Mat map) {
        int result = calculateBrightness(map.getNativeObjAddr());
        return result;
    }

    public static native double[] detectEyeCenter(long map, int x, int y, int w, int h, int f);

    public static native int calculateBrightness(long mat);
}
