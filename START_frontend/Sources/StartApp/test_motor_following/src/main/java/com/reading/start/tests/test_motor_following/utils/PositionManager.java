package com.reading.start.tests.test_motor_following.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.reading.start.tests.TestLog;

public class PositionManager implements SensorEventListener {
    private static final String TAG = PositionManager.class.getSimpleName();

    public interface PositionManagerListener {
        void onPositionChanged(float x, float y, float z);
    }

    private final SensorManager mSensorManager;

    private final Sensor mSensor;

    private PositionManagerListener mPositionManagerListener;

    private float mAxisX;
    private float mAxisY;
    private float mAxisZ;

    public PositionManager(Context context) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public void enable() {
        if (mSensor != null) {
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            TestLog.w(TAG, "Accelerometer sensor in not supported");
        }
    }

    public void disable() {
        mSensorManager.unregisterListener(this);
    }

    public void setPositionManagerListener(PositionManagerListener listener) {
        mPositionManagerListener = listener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        mAxisX = event.values[0];
        mAxisY = event.values[1];
        mAxisZ = event.values[2];

        if (mPositionManagerListener != null) {
            mPositionManagerListener.onPositionChanged(mAxisX, mAxisY, mAxisZ);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public float getX() {
        return mAxisX;
    }

    public float getY() {
        return mAxisY;
    }

    public float getZ() {
        return mAxisZ;
    }
}