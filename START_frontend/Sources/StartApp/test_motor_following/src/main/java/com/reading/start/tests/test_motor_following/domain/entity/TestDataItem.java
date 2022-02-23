package com.reading.start.tests.test_motor_following.domain.entity;

import com.google.gson.annotations.SerializedName;

public class TestDataItem {
    public static final String FILED_BEE_X = "bee_x";
    public static final String FILED_BEE_Y = "bee_y";

    public static final String FILED_X = "touch_x";
    public static final String FILED_Y = "touch_y";
    public static final String FILED_TOUCH_PRESSURE = "touch_pressure";
    public static final String FILED_TOUCH_SIZE = "touch_size";
    public static final String FILED_TIME = "time";
    public static final String FILED_ACTION = "action";

    public static final String FILED_DEVICE_X = "device_x";
    public static final String FILED_DEVICE_Y = "device_y";
    public static final String FILED_DEVICE_Z = "device_z";

    @SerializedName(FILED_BEE_X)
    private int mBeeX;

    @SerializedName(FILED_BEE_Y)
    private int mBeeY;

    @SerializedName(FILED_X)
    private int mX;

    @SerializedName(FILED_Y)
    private int mY;

    @SerializedName(FILED_TOUCH_PRESSURE)
    private float mTouchPressure;

    @SerializedName(FILED_TOUCH_SIZE)
    private float mTouchSize;

    @SerializedName(FILED_TIME)
    private long mTime;

    @SerializedName(FILED_ACTION)
    private String mAction;

    @SerializedName(FILED_DEVICE_X)
    private float mDeviceX;

    @SerializedName(FILED_DEVICE_Y)
    private float mDeviceY;

    @SerializedName(FILED_DEVICE_Z)
    private float mDeviceZ;

    public TestDataItem(int beeX, int beeY, int x, int y, float touchPressure, float touchSize, long time, String action,
                        float deviceX, float deviceY, float deviceZ) {
        mBeeX = beeX;
        mBeeY = beeY;
        mX = x;
        mY = y;
        mTouchPressure = touchPressure;
        mTouchSize = touchSize;
        mTime = time;
        mAction = action;
        mDeviceX = deviceX;
        mDeviceY = deviceY;
        mDeviceZ = deviceZ;
    }

    public float getBeeX() {
        return mBeeX;
    }

    public void setBeeX(int beeX) {
        mBeeX = beeX;
    }

    public float getBeeY() {
        return mBeeY;
    }

    public void setBeeY(int beeY) {
        mBeeY = beeY;
    }

    public int getX() {
        return mX;
    }

    public void setX(int x) {
        mX = x;
    }

    public int getY() {
        return mY;
    }

    public void setY(int y) {
        mY = y;
    }

    public float getTouchPressure() {
        return mTouchPressure;
    }

    public void setTouchPressure(float touchPressure) {
        mTouchPressure = touchPressure;
    }

    public float getTouchSize() {
        return mTouchSize;
    }

    public void setTouchSize(float touchSize) {
        mTouchSize = touchSize;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public String getAction() {
        return mAction;
    }

    public void setAction(String action) {
        mAction = action;
    }

    public float getDeviceX() {
        return mDeviceX;
    }

    public void setDeviceX(float deviceX) {
        mDeviceX = deviceX;
    }

    public float getDeviceY() {
        return mDeviceY;
    }

    public void setDeviceY(float deviceY) {
        mDeviceY = deviceY;
    }

    public float getDeviceZ() {
        return mDeviceZ;
    }

    public void setDeviceZ(float deviceZ) {
        mDeviceZ = deviceZ;
    }
}
