package com.reading.start.sdk.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.reading.start.sdk.Constants;
import com.reading.start.tests.TestLog;

/**
 * Helper class to fetch info about environment lighting.
 */
public class LightSensorManager implements SensorEventListener {
    private static final String TAG = LightSensorManager.class.getSimpleName();

    private enum Environment {
        LightOk,
        LightBad
    }

    public interface EnvironmentChangedListener {
        void onLightOk();

        void onLightBad();
    }

    private final SensorManager mSensorManager;

    private final Sensor mLightSensor;

    private EnvironmentChangedListener mEnvironmentChangedListener;

    private Environment mCurrentEnvironment;

    public LightSensorManager(Context context) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mLightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    }

    public void enable() {
        if (mLightSensor != null) {
            mSensorManager.registerListener(this, mLightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            TestLog.w(TAG, "Light sensor in not supported");
        }
    }

    public void disable() {
        mSensorManager.unregisterListener(this);
    }

    public EnvironmentChangedListener getEnvironmentChangedListener() {
        return mEnvironmentChangedListener;
    }

    public void setEnvironmentChangedListener(EnvironmentChangedListener environmentChangedListener) {
        mEnvironmentChangedListener = environmentChangedListener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float luxLevel = event.values[0];
        //TestLog.e(TAG, "luxLevel: " + luxLevel);

        if (luxLevel > Constants.LIGHT_THRESHOLD_LUX) {
            mCurrentEnvironment = Environment.LightOk;
        } else {
            mCurrentEnvironment = Environment.LightBad;
        }

        callListener(mCurrentEnvironment);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void callListener(Environment environment) {
        if (mEnvironmentChangedListener == null || environment == null) {
            return;
        }
        switch (environment) {
            case LightOk:
                mEnvironmentChangedListener.onLightOk();
                break;
            case LightBad:
                mEnvironmentChangedListener.onLightBad();
                break;
        }
    }
}