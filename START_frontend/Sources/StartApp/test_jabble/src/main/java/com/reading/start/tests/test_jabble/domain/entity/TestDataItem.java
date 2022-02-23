package com.reading.start.tests.test_jabble.domain.entity;

import com.google.gson.annotations.SerializedName;

public class TestDataItem {

    public static final String FILED_BUBBLE_1_X = "bubble_1_x";
    public static final String FILED_BUBBLE_1_Y = "bubble_1_y";

    public static final String FILED_BUBBLE_2_X = "bubble_2_x";
    public static final String FILED_BUBBLE_2_Y = "bubble_2_y";

    public static final String FILED_BUBBLE_3_X = "bubble_3_x";
    public static final String FILED_BUBBLE_3_Y = "bubble_3_y";

    public static final String FILED_BUBBLE_4_X = "bubble_4_x";
    public static final String FILED_BUBBLE_4_Y = "bubble_4_y";

    public static final String FILED_BUBBLE_5_X = "bubble_5_x";
    public static final String FILED_BUBBLE_5_Y = "bubble_5_y";

    public static final String FILED_BUBBLE_6_X = "bubble_6_x";
    public static final String FILED_BUBBLE_6_Y = "bubble_6_y";

    public static final String FILED_X = "touch_x";
    public static final String FILED_Y = "touch_y";
    public static final String FILED_TOUCH_PRESSURE = "touch_pressure";
    public static final String FILED_TOUCH_SIZE = "touch_size";
    public static final String FILED_TIME = "time";
    public static final String FILED_BUBBLE = "bubble";

    public static final String FILED_DEVICE_X = "device_x";
    public static final String FILED_DEVICE_Y = "device_y";
    public static final String FILED_DEVICE_Z = "device_z";

    @SerializedName(FILED_BUBBLE_1_X)
    private float mBubble1X;

    @SerializedName(FILED_BUBBLE_1_Y)
    private float mBubble1Y;

    @SerializedName(FILED_BUBBLE_2_X)
    private float mBubble2X;

    @SerializedName(FILED_BUBBLE_2_Y)
    private float mBubble2Y;

    @SerializedName(FILED_BUBBLE_3_X)
    private float mBubble3X;

    @SerializedName(FILED_BUBBLE_3_Y)
    private float mBubble3Y;

    @SerializedName(FILED_BUBBLE_4_X)
    private float mBubble4X;

    @SerializedName(FILED_BUBBLE_4_Y)
    private float mBubble4Y;

    @SerializedName(FILED_BUBBLE_5_X)
    private float mBubble5X;

    @SerializedName(FILED_BUBBLE_5_Y)
    private float mBubble5Y;

    @SerializedName(FILED_BUBBLE_6_X)
    private float mBubble6X;

    @SerializedName(FILED_BUBBLE_6_Y)
    private float mBubble6Y;

    @SerializedName(FILED_X)
    private float mX;

    @SerializedName(FILED_Y)
    private float mY;

    @SerializedName(FILED_TOUCH_PRESSURE)
    private float mTouchPressure;

    @SerializedName(FILED_TOUCH_SIZE)
    private float mTouchSize;

    @SerializedName(FILED_TIME)
    private long mTime;

    @SerializedName(FILED_BUBBLE)
    private String mBubble;

    @SerializedName(FILED_DEVICE_X)
    private float mDeviceX;

    @SerializedName(FILED_DEVICE_Y)
    private float mDeviceY;

    @SerializedName(FILED_DEVICE_Z)
    private float mDeviceZ;

    public TestDataItem(float bubble1X, float bubble1Y, float bubble2X, float bubble2Y,
                        float bubble3X, float bubble3Y, float bubble4X, float bubble4Y,
                        float bubble5X, float bubble5Y, float bubble6X, float bubble6Y,
                        float x, float y, float touchPressure, float touchSize, long time, String bubble,
                        float deviceX, float deviceY, float deviceZ) {
        mBubble1X = bubble1X;
        mBubble1Y = bubble1Y;
        mBubble2X = bubble2X;
        mBubble2Y = bubble2Y;
        mBubble3X = bubble3X;
        mBubble3Y = bubble3Y;
        mBubble4X = bubble4X;
        mBubble4Y = bubble4Y;
        mBubble5X = bubble5X;
        mBubble5Y = bubble5Y;
        mBubble6X = bubble6X;
        mBubble6Y = bubble6Y;
        mX = x;
        mY = y;
        mTouchPressure = touchPressure;
        mTouchSize = touchSize;
        mTime = time;
        mBubble = bubble;
        mDeviceX = deviceX;
        mDeviceY = deviceY;
        mDeviceZ = deviceZ;
    }

    public float getBubble1X() {
        return mBubble1X;
    }

    public void setBubble1X(float bubble1X) {
        mBubble1X = bubble1X;
    }

    public float getBubble1Y() {
        return mBubble1Y;
    }

    public void setBubble1Y(float bubble1Y) {
        mBubble1Y = bubble1Y;
    }

    public float getBubble2X() {
        return mBubble2X;
    }

    public void setBubble2X(float bubble2X) {
        mBubble2X = bubble2X;
    }

    public float getBubble2Y() {
        return mBubble2Y;
    }

    public void setBubble2Y(float bubble2Y) {
        mBubble2Y = bubble2Y;
    }

    public float getBubble3X() {
        return mBubble3X;
    }

    public void setBubble3X(float bubble3X) {
        mBubble3X = bubble3X;
    }

    public float getBubble3Y() {
        return mBubble3Y;
    }

    public void setBubble3Y(float bubble3Y) {
        mBubble3Y = bubble3Y;
    }

    public float getBubble4X() {
        return mBubble4X;
    }

    public void setBubble4X(float bubble4X) {
        mBubble4X = bubble4X;
    }

    public float getBubble4Y() {
        return mBubble4Y;
    }

    public void setBubble4Y(float bubble4Y) {
        mBubble4Y = bubble4Y;
    }

    public float getBubble5X() {
        return mBubble5X;
    }

    public void setBubble5X(float bubble5X) {
        mBubble5X = bubble5X;
    }

    public float getBubble5Y() {
        return mBubble5Y;
    }

    public void setBubble5Y(float bubble5Y) {
        mBubble5Y = bubble5Y;
    }

    public float getBubble6X() {
        return mBubble6X;
    }

    public void setBubble6X(float bubble6X) {
        mBubble6X = bubble6X;
    }

    public float getBubble6Y() {
        return mBubble6Y;
    }

    public void setBubble6Y(float bubble6Y) {
        mBubble6Y = bubble6Y;
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

    public String getBubble() {
        return mBubble;
    }

    public void setBubble(String bubble) {
        mBubble = bubble;
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
