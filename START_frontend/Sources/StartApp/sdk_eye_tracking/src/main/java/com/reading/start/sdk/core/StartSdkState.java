package com.reading.start.sdk.core;

/**
 * Represent state of sdk in runtime.
 */
public class StartSdkState {
    private boolean mDevicePositionOk = false;

    private boolean mDeviceShakeOk = false;

    private boolean mLightEnvironmentOk = false;

    private boolean mLightCameraOk = false;

    private boolean mHeadDetectedOk = false;

    private boolean mHeadCloseOk = false;

    private boolean mHeadFarOk = false;

    private boolean mEyeDetectOk = false;

    public boolean isDevicePositionOk() {
        return mDevicePositionOk;
    }

    public void setDevicePositionOk(boolean devicePositionOk) {
        mDevicePositionOk = devicePositionOk;
    }

    public boolean isDeviceShakeOk() {
        return mDeviceShakeOk;
    }

    public void setDeviceShakeOk(boolean deviceShakeOk) {
        mDeviceShakeOk = deviceShakeOk;
    }

    public boolean isLightEnvironmentOk() {
        return mLightEnvironmentOk;
    }

    public void setLightEnvironmentOk(boolean lightEnvironmentOk) {
        mLightEnvironmentOk = lightEnvironmentOk;
    }

    public boolean isLightCameraOk() {
        return mLightCameraOk;
    }

    public void setLightCameraOk(boolean lightCameraOk) {
        mLightCameraOk = lightCameraOk;
    }

    public boolean isHeadDetectedOk() {
        return mHeadDetectedOk;
    }

    public void setHeadDetectedOk(boolean headDetectedOk) {
        mHeadDetectedOk = headDetectedOk;
    }

    public boolean isHeadCloseOk() {
        return mHeadCloseOk;
    }

    public void setHeadCloseOk(boolean headCloseOk) {
        mHeadCloseOk = headCloseOk;
    }

    public boolean isHeadFarOk() {
        return mHeadFarOk;
    }

    public void setHeadFarOk(boolean headFarOk) {
        mHeadFarOk = headFarOk;
    }

    public boolean isEyeDetectOk() {
        return mEyeDetectOk;
    }

    public void setEyeDetectOk(boolean eyeDetectOk) {
        mEyeDetectOk = eyeDetectOk;
    }

    public boolean isOk() {
        return isDevicePositionOk()
                && isDeviceShakeOk()
                && isEyeDetectOk()
                && isHeadCloseOk()
                && isHeadDetectedOk()
                && isHeadFarOk()
                && isLightCameraOk()
                && isLightEnvironmentOk();
    }

    public void copy(StartSdkState state) {
        if (state != null) {
            state.mDevicePositionOk = mDevicePositionOk;
            state.mDeviceShakeOk = mDeviceShakeOk;
            state.mLightEnvironmentOk = mLightEnvironmentOk;
            state.mLightCameraOk = mLightCameraOk;
            state.mHeadDetectedOk = mHeadDetectedOk;
            state.mHeadCloseOk = mHeadCloseOk;
            state.mHeadFarOk = mHeadFarOk;
            state.mEyeDetectOk = mEyeDetectOk;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StartSdkState) {
            StartSdkState state = (StartSdkState) obj;

            return state.mDevicePositionOk == mDevicePositionOk
                    && state.mDeviceShakeOk == mDeviceShakeOk
                    && state.mLightEnvironmentOk == mLightEnvironmentOk
                    && state.mLightCameraOk == mLightCameraOk
                    && state.mHeadDetectedOk == mHeadDetectedOk
                    && state.mHeadCloseOk == mHeadCloseOk
                    && state.mHeadFarOk == mHeadFarOk
                    && state.mEyeDetectOk == mEyeDetectOk;
        } else {
            return super.equals(obj);
        }
    }

    public void reset() {
        mDevicePositionOk = false;
        mDeviceShakeOk = false;
        mLightEnvironmentOk = false;
        mLightCameraOk = false;
        mHeadDetectedOk = false;
        mHeadCloseOk = false;
        mHeadFarOk = false;
        mEyeDetectOk = false;
    }
}
