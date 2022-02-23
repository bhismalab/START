package com.reading.start.tests.test_eye_tracking.domain.entity;

public class CalibrationCheckItem {
    private int mDetectPosition = -1;

    private int mImagePosition = -1;

    public CalibrationCheckItem(int detectPosition, int imagePosition) {
        mDetectPosition = detectPosition;
        mImagePosition = imagePosition;
    }

    public int getDetectPosition() {
        return mDetectPosition;
    }

    public int getImagePosition() {
        return mImagePosition;
    }

    public boolean isValid() {
        return mDetectPosition == mImagePosition && mDetectPosition != -1;
    }
}
