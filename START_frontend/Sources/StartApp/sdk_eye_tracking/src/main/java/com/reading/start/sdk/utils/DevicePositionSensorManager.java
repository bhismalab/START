package com.reading.start.sdk.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Display;
import android.view.WindowManager;

import com.reading.start.sdk.Constants;
import com.reading.start.tests.TestLog;

/**
 * Helper class to fetch data from device position sensors.
 */
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

    private int mDisplayRotation = 0;

    public DevicePositionSensorManager(Context context) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mLightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        mDisplayRotation = display.getRotation();
    }

    public void enable() {
        if (mLightSensor != null) {
            mSensorManager.registerListener(this, mLightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            TestLog.w(TAG, "Accelerometer sensor in not supported");
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
        float[] localValues = adjustAccelOrientation(mDisplayRotation, event.values);

        if ((curTime - mLastUpdate) > 100) {
            long diffTime = (curTime - mLastUpdate);
            mLastUpdate = curTime;
            float speed = Math.abs(localValues[0] + localValues[1] + localValues[2] - mAxisX - mAxisY - mAxisZ) / diffTime * 10000;

            //TestLog.d(TAG, "speed: " + speed);
            boolean localShake;

            localShake = !(speed > Constants.SHAKE_THRESHOLD);

            if (mEnvironmentChangedListener != null) {
                mEnvironmentChangedListener.onShakeChanged(localShake);
            }
        }

        mAxisX = localValues[0];
        mAxisY = localValues[1];
        mAxisZ = localValues[2];

        boolean localPosition = Math.abs(mAxisX) < Constants.DEVICE_POSITION_X_THRESHOLD;

        if (mEnvironmentChangedListener != null) {
            mEnvironmentChangedListener.onPositionChanged(localPosition);
        }

        //TestLog.d(TAG, "mAxisX: " + mAxisX + ", mAxisY: " + mAxisY + ", mAxisZ: " + mAxisZ);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public static float[] adjustAccelOrientation(int displayRotation, float[] eventValues) {
        float[] adjustedValues = new float[3];

        final int axisSwap[][] = {
                {1, -1, 0, 1},     // ROTATION_0
                {-1, -1, 1, 0},     // ROTATION_90
                {-1, 1, 0, 1},     // ROTATION_180
                {1, 1, 1, 0}}; // ROTATION_270

        final int[] as = axisSwap[displayRotation];
        adjustedValues[0] = (float) as[0] * eventValues[as[2]];
        adjustedValues[1] = (float) as[1] * eventValues[as[3]];
        adjustedValues[2] = eventValues[2];

        return adjustedValues;
    }
}