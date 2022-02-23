package com.reading.start.tests.test_eye_tracking.domain.entity;

import com.google.gson.annotations.SerializedName;

public class TestDataEyeTracking {
    public static final String FILED_GAZE_X = "gazeX";
    public static final String FILED_GAZE_Y = "gazeY";
    public static final String FILED_TIME = "time";
    public static final String FILED_VALID = "gazeDetect";
    public static final String FILED_GAZE_OUT = "gazeOut";

    @SerializedName(FILED_GAZE_X)
    private float mX;

    @SerializedName(FILED_GAZE_Y)
    private float mY;

    @SerializedName(FILED_TIME)
    private long mTime;

    @SerializedName(FILED_VALID)
    private int mValid;

    @SerializedName(FILED_GAZE_OUT)
    private int mGazeOut;

    public TestDataEyeTracking(float x, float y, long time, int valid, int gazeOut) {
        mX = x;
        mY = y;
        mTime = time;
        mValid = valid;
        mGazeOut = gazeOut;
    }

    public float getX() {
        return mX;
    }

    public void setX(float x) {
        mX = x;
    }

    public float getY() {
        return mY;
    }

    public void setY(float y) {
        mY = y;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public int isValid() {
        return mValid;
    }

    public void setValid(int valid) {
        mValid = valid;
    }

    public int getGazeOut() {
        return mGazeOut;
    }

    public void setGazeOut(int gazeOut) {
        mGazeOut = gazeOut;
    }
}
