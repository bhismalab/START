package com.reading.start.tests.test_motor_following.domain.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Calendar;

public class TestData {
    public static final String FILED_START_TIME = "startTime";
    public static final String FILED_END_TIME = "endTime";
    public static final String FILED_ITEMS = "attempts";
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

    @SerializedName(FILED_ITEMS)
    private ArrayList<TestDataAttempt> mAttempts = null;

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
        mAttempts = new ArrayList<>();
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

    public ArrayList<TestDataAttempt> getAttempts() {
        return mAttempts;
    }

    public void setAttempts(ArrayList<TestDataAttempt> items) {
        mAttempts = items;
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
