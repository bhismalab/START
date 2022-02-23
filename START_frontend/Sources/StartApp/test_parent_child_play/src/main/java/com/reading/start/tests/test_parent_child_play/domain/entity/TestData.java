package com.reading.start.tests.test_parent_child_play.domain.entity;

import com.google.gson.annotations.SerializedName;

import java.util.Calendar;

public class TestData {
    public static final String FILED_START_TIME = "startTime";
    public static final String FILED_END_TIME = "endTime";
    public static final String FILED_FILE_PATH = "filePath";
    public static final String FILED_INTERRUPTED = "interrupted";
    public static final String FILED_SCREEN_WIDTH = "screenWidth";
    public static final String FILED_SCREEN_HEIGHT = "screenHeight";
    public static final String FILED_DEVICE_ID = "deviceId";
    public static final String FILED_X_DPI = "xdpi";
    public static final String FILED_Y_DPI = "ydpi";

    @SerializedName(FILED_START_TIME)
    private long mStartTime = 0;

    @SerializedName(FILED_END_TIME)
    private long mEndTime = 0;

    @SerializedName(FILED_FILE_PATH)
    private String mFilePath = "";

    @SerializedName(FILED_INTERRUPTED)
    private boolean mInterrupted = false;

    @SerializedName(FILED_SCREEN_WIDTH)
    private int mScreenWidth = 0;

    @SerializedName(FILED_SCREEN_HEIGHT)
    private int mScreenHeight = 0;

    @SerializedName(FILED_DEVICE_ID)
    private String mDeviceId = null;

    @SerializedName(FILED_X_DPI)
    private float mXDpi = 0;

    @SerializedName(FILED_Y_DPI)
    private float mYDpi = 0;

    public TestData(int screenWidth, int screenHeight, float xdpi, float ydpi) {
        mStartTime = Calendar.getInstance().getTimeInMillis();
        mScreenWidth = screenWidth;
        mScreenHeight = screenHeight;
        mXDpi = xdpi;
        mYDpi = ydpi;
    }

    public long getStartTime() {
        return mStartTime;
    }

    public void setStartTime(long startDate) {
        mStartTime = startDate;
    }

    public long getEndTime() {
        return mEndTime;
    }

    public void setEndTime(long endDate) {
        mEndTime = endDate;
    }

    public String getFilePath() {
        return mFilePath;
    }

    public void setFilePath(String filePath) {
        mFilePath = filePath;
    }

    public boolean isInterrupted() {
        return mInterrupted;
    }

    public void setInterrupted(boolean interrupted) {
        mInterrupted = interrupted;
    }

    public int getScreenWidth() {
        return mScreenWidth;
    }

    public void setScreenWidth(int screenWidth) {
        mScreenWidth = screenWidth;
    }

    public int getScreenHeight() {
        return mScreenHeight;
    }

    public void setScreenHeight(int screenHeight) {
        mScreenHeight = screenHeight;
    }

    public String getDeviceId() {
        return mDeviceId;
    }

    public void setDeviceId(String deviceId) {
        mDeviceId = deviceId;
    }

    public float getXDpi() {
        return mXDpi;
    }

    public void setXDpi(float xdpi) {
        mXDpi = xdpi;
    }

    public float getYDpi() {
        return mYDpi;
    }

    public void setYDpi(float ydpi) {
        mYDpi = ydpi;
    }
}
