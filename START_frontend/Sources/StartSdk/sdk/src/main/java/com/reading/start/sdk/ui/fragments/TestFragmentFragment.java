package com.reading.start.sdk.ui.fragments;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.reading.start.sdk.AppCore;
import com.reading.start.sdk.R;
import com.reading.start.sdk.core.CalibrateType;
import com.reading.start.sdk.core.DetectMode;
import com.reading.start.sdk.core.Settings;
import com.reading.start.sdk.core.StartSdk;
import com.reading.start.sdk.core.StartSdkListener;
import com.reading.start.sdk.core.StartSdkState;
import com.reading.start.sdk.databinding.SdkFragmentTestBinding;

import org.opencv.core.Point;

public class TestFragmentFragment extends BaseFragment {
    private static final String TAG = TestFragmentFragment.class.getSimpleName();

    private StartSdkListener mStartSdkListener = new StartSdkListener() {
        @Override
        public void onCalibrateStart(long timeStamp) {
        }

        @Override
        public void onCalibrateChanged(CalibrateType type, long timeStamp) {
            showCalibrateArea(type);
        }

        @Override
        public void onCalibrateCompleted(boolean success, long timeStamp) {
            if (success) {
                showStateArea();
            } else {
                hideAllArea();

                runOnUiThread(() -> Toast.makeText(AppCore.getInstance(), "Calibration not success. Please not move a head and try again.", Toast.LENGTH_LONG).show());
            }
        }

        @Override
        public void onCalibrateStopped(long timeStamp) {
            hideAllArea();
        }

        @Override
        public void onPositionUpdated(Point right, Point left, long timeStamp) {
        }

        @Override
        public void onDetectedSelectedArea(final DetectMode mode, final int col, final int row, long timeStamp) {
            runOnUiThread(() -> {
                mBinding.detectModeView.setDetectMode(mode);

                if (col >= 0 && row >= 0) {
                    mBinding.detectModeView.setPosition(col, row);
                }
            });
        }

        @Override
        public void onFaceDetected(long timeStamp) {
        }

        @Override
        public void onFaceLost(long timeStamp) {
        }

        @Override
        public void onRightEyeDetected(long timeStamp) {
        }

        @Override
        public void onRightEyeLost(long timeStamp) {
        }

        @Override
        public void onLeftEyeDetected(long timeStamp) {
        }

        @Override
        public void onLeftEyeLost(long timeStamp) {
        }

        @Override
        public void onGazeOutside(long timeStamp) {
        }

        @Override
        public void onGazeDetected(long timeStamp) {
        }

        @Override
        public void onStartSdkStateChanged(final StartSdkState state, long timeStamp) {
            runOnUiThread(() -> {
                if (state != null && !isDetached() && isAdded()) {
                    if (state.isDevicePositionOk()) {
                        mBinding.devicePositionValue.setText(getString(R.string.test_ok));
                        mBinding.devicePositionValue.setTextColor(getResources().getColor(R.color.colorOK));
                    } else {
                        mBinding.devicePositionValue.setText(getString(R.string.test_fail));
                        mBinding.devicePositionValue.setTextColor(getResources().getColor(R.color.colorFail));
                    }

                    if (state.isDeviceShakeOk()) {
                        mBinding.deviceJoltingValue.setText(getString(R.string.test_ok));
                        mBinding.deviceJoltingValue.setTextColor(getResources().getColor(R.color.colorOK));
                    } else {
                        mBinding.deviceJoltingValue.setText(getString(R.string.test_fail));
                        mBinding.deviceJoltingValue.setTextColor(getResources().getColor(R.color.colorFail));
                    }

                    if (state.isLightEnvironmentOk()) {
                        mBinding.lightEnvironmentValue.setText(getString(R.string.test_ok));
                        mBinding.lightEnvironmentValue.setTextColor(getResources().getColor(R.color.colorOK));
                    } else {
                        mBinding.lightEnvironmentValue.setText(getString(R.string.test_fail));
                        mBinding.lightEnvironmentValue.setTextColor(getResources().getColor(R.color.colorFail));
                    }

                    if (state.isLightCameraOk()) {
                        mBinding.lightCameraValue.setText(getString(R.string.test_ok));
                        mBinding.lightCameraValue.setTextColor(getResources().getColor(R.color.colorOK));
                    } else {
                        mBinding.lightCameraValue.setText(getString(R.string.test_fail));
                        mBinding.lightCameraValue.setTextColor(getResources().getColor(R.color.colorFail));
                    }

                    if (state.isHeadDetectedOk()) {
                        mBinding.headDetectValue.setText(getString(R.string.test_ok));
                        mBinding.headDetectValue.setTextColor(getResources().getColor(R.color.colorOK));
                    } else {
                        mBinding.headDetectValue.setText(getString(R.string.test_fail));
                        mBinding.headDetectValue.setTextColor(getResources().getColor(R.color.colorFail));
                    }

                    if (state.isHeadCloseOk() && state.isHeadFarOk()) {
                        mBinding.headDistanceValue.setText(getString(R.string.test_ok));
                        mBinding.headDistanceValue.setTextColor(getResources().getColor(R.color.colorOK));
                    } else {
                        mBinding.headDistanceValue.setText(getString(R.string.test_fail));
                        mBinding.headDistanceValue.setTextColor(getResources().getColor(R.color.colorFail));
                    }

                    if (state.isEyeDetectOk()) {
                        mBinding.eyeDetectValue.setText(getString(R.string.test_ok));
                        mBinding.eyeDetectValue.setTextColor(getResources().getColor(R.color.colorOK));
                    } else {
                        mBinding.eyeDetectValue.setText(getString(R.string.test_fail));
                        mBinding.eyeDetectValue.setTextColor(getResources().getColor(R.color.colorFail));
                    }

                    String message = null;

                    if (state.isOk()) {
                        mBinding.startTest.setEnabled(true);
                        message = getString(R.string.test_ok);
                    } else {
                        mBinding.startTest.setEnabled(false);

                        if (!state.isDevicePositionOk()) {
                            message = getString(R.string.test_fail_message_device_position);
                        } else if (!state.isDeviceShakeOk()) {
                            message = getString(R.string.test_fail_message_device_shake);
                        } else if (!state.isLightEnvironmentOk()) {
                            message = getString(R.string.test_fail_message_light);
                        } else if (!state.isLightCameraOk()) {
                            message = getString(R.string.test_fail_message_light_camera);
                        } else if (!state.isHeadDetectedOk()) {
                            message = getString(R.string.test_fail_message_head_detect);
                        } else if (!state.isHeadFarOk()) {
                            message = getString(R.string.test_fail_message_head_far);
                        } else if (!state.isHeadCloseOk()) {
                            message = getString(R.string.test_fail_message_head_close);
                        } else if (!state.isEyeDetectOk()) {
                            message = getString(R.string.test_fail_message_eye_detect);
                        }
                    }

                    if (message != null) {
                        mBinding.status.setText(message);
                    }
                }
            });
        }

        @Override
        public void onFrameProcessed(DetectMode mode, int col, int row, boolean isEyeDetected,
                                     boolean gazeOutside, long timeStamp) {
            // no need to implement
        }

        @Override
        public void onFramePostProcessed(Bitmap bitmap, long timeStamp) {
        }

        @Override
        public void onVideoRecordingStart(StartSdk.VideoType type) {
            // no need to implement
        }

        @Override
        public void onVideoRecordingStop(StartSdk.VideoType type) {
            // no need to implement
        }

        @Override
        public void onPostProcessingPostCalibrationStart() {

        }

        @Override
        public void onPostProcessingPostCalibrationEnd() {

        }

        @Override
        public void onPostProcessingTestStart() {

        }

        @Override
        public void onPostProcessingTestEnd() {

        }
    };

    private SdkFragmentTestBinding mBinding;

    private Settings mSettings = null;

    public void setSettings(Settings settings) {
        mSettings = settings;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.sdk_fragment_test, container, false);
        setHasOptionsMenu(false);

        mBinding.startTest.setOnClickListener(v -> {
            mBinding.settingsScreen.setVisibility(View.INVISIBLE);
            mBinding.testScreen.setVisibility(View.VISIBLE);
            StartSdk.getInstance().calibrate();
        });

        mBinding.stopTest.setOnClickListener(v -> {
            mBinding.settingsScreen.setVisibility(View.VISIBLE);
            mBinding.testScreen.setVisibility(View.INVISIBLE);
        });

        hideAllArea();
        return mBinding.getRoot();
    }

    @Override
    public void onPause() {
        StartSdk.getInstance().stop();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        StartSdk.getInstance().start(AppCore.getInstance(), mBinding.fdActivitySurfaceView, mSettings, mStartSdkListener);
    }

    public void onDestroy() {
        super.onDestroy();
    }

    private void showCalibrateArea(final CalibrateType type) {
        runOnUiThread(() -> {
            if (type != null) {
                switch (type) {
                    case Center: {
                        mBinding.calibrationCenter.setVisibility(View.VISIBLE);
                        mBinding.calibrationTopLeft.setVisibility(View.INVISIBLE);
                        mBinding.calibrationTopRight.setVisibility(View.INVISIBLE);
                        mBinding.calibrationBottomLeft.setVisibility(View.INVISIBLE);
                        mBinding.calibrationBottomRight.setVisibility(View.INVISIBLE);
                        break;
                    }
                    case TopLeft: {
                        mBinding.calibrationCenter.setVisibility(View.INVISIBLE);
                        mBinding.calibrationTopLeft.setVisibility(View.VISIBLE);
                        mBinding.calibrationTopRight.setVisibility(View.INVISIBLE);
                        mBinding.calibrationBottomLeft.setVisibility(View.INVISIBLE);
                        mBinding.calibrationBottomRight.setVisibility(View.INVISIBLE);
                        break;
                    }
                    case TopRight: {
                        mBinding.calibrationCenter.setVisibility(View.INVISIBLE);
                        mBinding.calibrationTopLeft.setVisibility(View.INVISIBLE);
                        mBinding.calibrationTopRight.setVisibility(View.VISIBLE);
                        mBinding.calibrationBottomLeft.setVisibility(View.INVISIBLE);
                        mBinding.calibrationBottomRight.setVisibility(View.INVISIBLE);
                        break;
                    }
                    case BottomLeft: {
                        mBinding.calibrationCenter.setVisibility(View.INVISIBLE);
                        mBinding.calibrationTopLeft.setVisibility(View.INVISIBLE);
                        mBinding.calibrationTopRight.setVisibility(View.INVISIBLE);
                        mBinding.calibrationBottomLeft.setVisibility(View.VISIBLE);
                        mBinding.calibrationBottomRight.setVisibility(View.INVISIBLE);
                        break;
                    }
                    case BottomRight: {
                        mBinding.calibrationCenter.setVisibility(View.INVISIBLE);
                        mBinding.calibrationTopLeft.setVisibility(View.INVISIBLE);
                        mBinding.calibrationTopRight.setVisibility(View.INVISIBLE);
                        mBinding.calibrationBottomLeft.setVisibility(View.INVISIBLE);
                        mBinding.calibrationBottomRight.setVisibility(View.VISIBLE);
                        break;
                    }
                    default: {
                        mBinding.calibrationCenter.setVisibility(View.INVISIBLE);
                        mBinding.calibrationTopLeft.setVisibility(View.INVISIBLE);
                        mBinding.calibrationTopRight.setVisibility(View.INVISIBLE);
                        mBinding.calibrationBottomLeft.setVisibility(View.INVISIBLE);
                        mBinding.calibrationBottomRight.setVisibility(View.INVISIBLE);
                        break;
                    }
                }
            } else {
                mBinding.calibrationCenter.setVisibility(View.INVISIBLE);
                mBinding.calibrationTopLeft.setVisibility(View.INVISIBLE);
                mBinding.calibrationTopRight.setVisibility(View.INVISIBLE);
                mBinding.calibrationBottomLeft.setVisibility(View.INVISIBLE);
                mBinding.calibrationBottomRight.setVisibility(View.INVISIBLE);
            }

            mBinding.cursorState.setVisibility(View.GONE);
            mBinding.cursorCalibration.setVisibility(View.VISIBLE);
        });
    }

    private void hideAllArea() {
        runOnUiThread(() -> {
            mBinding.cursorState.setVisibility(View.GONE);
            mBinding.cursorCalibration.setVisibility(View.GONE);
        });
    }

    private void showStateArea() {
        runOnUiThread(() -> {
            mBinding.cursorState.setVisibility(View.VISIBLE);
            mBinding.cursorCalibration.setVisibility(View.GONE);
        });
    }
}
