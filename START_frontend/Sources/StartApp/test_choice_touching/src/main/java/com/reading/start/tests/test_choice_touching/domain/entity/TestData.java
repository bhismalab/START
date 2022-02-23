package com.reading.start.tests.test_choice_touching.domain.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Calendar;

public class TestData {
    public static final String FILED_START_TIME = "startTime";
    public static final String FILED_END_TIME = "endTime";
    public static final String FILED_RED_CLICK_COUNT = "redClickCount";
    public static final String FILED_GREEN_CLICK_COUNT = "greenClickCount";
    public static final String FILED_ITEMS = "items";
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

    @SerializedName(FILED_RED_CLICK_COUNT)
    private int mRedClickCount = 0;

    @SerializedName(FILED_GREEN_CLICK_COUNT)
    private int mGreenClickCount = 0;

    @SerializedName(FILED_ITEMS)
    private ArrayList<TestDataItem> mItems = null;

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
        mItems = new ArrayList<>();
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

    public int getRedClickCount() {
        return mRedClickCount;
    }

    public void setRedClickCount(int count) {
        mRedClickCount = count;
    }

    public int getGreenClickCount() {
        return mGreenClickCount;
    }

    public void setGreenClickCount(int count) {
        mGreenClickCount = count;
    }

    public ArrayList<TestDataItem> getItems() {
        return mItems;
    }

    public void setItems(ArrayList<TestDataItem> items) {
        mItems = items;
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
