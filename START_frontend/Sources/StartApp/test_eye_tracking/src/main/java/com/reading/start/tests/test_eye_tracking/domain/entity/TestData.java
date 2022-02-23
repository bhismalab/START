package com.reading.start.tests.test_eye_tracking.domain.entity;

import com.google.gson.annotations.SerializedName;
import com.reading.start.tests.test_eye_tracking.Constants;

public class TestData {
    public static final String FILED_TEST_LOOKING = "testLooking";
    public static final String FILED_TEST_ATTENTION = "testAttention";
    public static final String FILED_SCREEN_WIDTH = "screenWidth";
    public static final String FILED_SCREEN_HEIGHT = "screenHeight";
    public static final String FILED_DEVICE_ID = "deviceId";
    public static final String FILED_X_DPI = "xdpi";
    public static final String FILED_Y_DPI = "ydpi";

    @SerializedName(FILED_TEST_LOOKING)
    private TestDataTest mTestLooking;

    @SerializedName(FILED_TEST_ATTENTION)
    private TestDataTest mTestAttention;

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
        mTestLooking = new TestDataTest(Constants.TEST_NAME_LOOKING);
        mTestAttention = new TestDataTest(Constants.TEST_NAME_ATTENTION);
        mScreenWidth = screenWidth;
        mScreenHeight = screenHeight;
        mXDpi = xdpi;
        mYDpi = ydpi;
    }

    public TestDataTest getTestLooking() {
        return mTestLooking;
    }

    public TestDataTest getTestAttention() {
        return mTestAttention;
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
