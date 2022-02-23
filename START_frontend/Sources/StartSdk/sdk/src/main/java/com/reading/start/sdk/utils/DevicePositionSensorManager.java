package com.reading.start.sdk.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.reading.start.sdk.Constants;
import com.reading.start.sdk.general.SdkLog;

public class DevicePositionSensorManager implements SensorEventListener {
    private static final String TAG = DevicePositionSensorManager.class.getSimpleName();

    public interface EnvironmentChangedListener {
        void onPositionChanged(boolean ok);

        void onShakeChanged(boolean ok);
    }

    private final SensorManager mSensorManager;

    private final Sensor mLightSensor;

    private EnvironmentChangedListener mEnvironmentChangedListener;

    private float mAxisX;
    private float mAxisY;
    private float mAxisZ;

    private long mLastUpdate;

    public DevicePositionSensorManager(Context context) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mLightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public void enable() {
        if (mLightSensor != null) {
            mSensorManager.registerListener(this, mLightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            SdkLog.w(TAG, "Accelerometer sensor in not supported");
        }
    }

    public void disable() {
        mSensorManager.unregisterListener(this);
    }

    public void setEnvironmentChangedListener(EnvironmentChangedListener environmentChangedListener) {
        mEnvironmentChangedListener = environmentChangedListener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long curTime = System.currentTimeMillis();

        if ((curTime - mLastUpdate) > 100) {
            long diffTime = (curTime - mLastUpdate);
            mLastUpdate = curTime;
            float speed = Math.abs(event.values[0] + event.values[1] + event.values[2] - mAxisX - mAxisY - mAxisZ) / diffTime * 10000;

            //SdkLog.d(TAG, "speed: " + speed);
            boolean localShake;

            if (speed > Constants.SHAKE_THRESHOLD) {
                localShake = false;
            } else {
                localShake = true;
            }

            if (mEnvironmentChangedListener != null) {
                mEnvironmentChangedListener.onShakeChanged(localShake);
            }
        }

        mAxisX = event.values[0];
        mAxisY = event.values[1];
        mAxisZ = event.values[2];

        boolean localPosition = Constants.USE_Y_FOR_POSITION ?
                Math.abs(mAxisY) < Constants.DEVICE_POSITION_Y_THRESHOLD
                : Math.abs(mAxisX) < Constants.DEVICE_POSITION_X_THRESHOLD;

        if (mEnvironmentChangedListener != null) {
            mEnvironmentChangedListener.onPositionChanged(localPosition);
        }

        //SdkLog.d(TAG, "mAxisX: " + mAxisX + ", mAxisY: " + mAxisY + ", mAxisZ: " + mAxisZ);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}