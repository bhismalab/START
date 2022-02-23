package com.reading.start.sdk.ui.fragments;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.reading.start.sdk.AppCore;
import com.reading.start.sdk.Constants;
import com.reading.start.sdk.R;
import com.reading.start.sdk.core.CalibrateType;
import com.reading.start.sdk.core.DetectMode;
import com.reading.start.sdk.core.StartSdk;
import com.reading.start.sdk.core.StartSdkListener;
import com.reading.start.sdk.core.StartSdkState;
import com.reading.start.sdk.databinding.SdkFragmentCameraBinding;
import com.reading.start.sdk.general.SdkLog;

import org.opencv.core.Point;

public class CameraFragment extends BaseFragment {
    private static final String TAG = CameraFragment.class.getSimpleName();

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
                runOnUiThread(() -> Toast.makeText(AppCore.getInstance(), "Calibration not success. Please try again.", Toast.LENGTH_LONG).show());
            }
        }

        @Override
        public void onCalibrateStopped(long timeStamp) {
            hideAllArea();
        }

        @Override
        public void onPositionUpdated(Point right, Point left, long timeStamp) {
            updateUiInfo(right, left);
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
            runOnUiThread(() -> stopRingtone());
        }

        @Override
        public void onFaceLost(long timeStamp) {
            runOnUiThread(() -> {
                mBinding.detectModeView.reset();
                stopRingtone();
            });
        }

        @Override
        public void onRightEyeDetected(long timeStamp) {
            runOnUiThread(() -> stopRingtone());
        }

        @Override
        public void onRightEyeLost(long timeStamp) {
            runOnUiThread(() -> {
                mBinding.detectModeView.reset();
                stopRingtone();
            });
        }

        @Override
        public void onLeftEyeDetected(long timeStamp) {
            runOnUiThread(() -> stopRingtone());
        }

        @Override
        public void onLeftEyeLost(long timeStamp) {
            runOnUiThread(() -> {
                mBinding.detectModeView.reset();
                stopRingtone();
            });
        }

        @Override
        public void onGazeOutside(long timeStamp) {
            runOnUiThread(() -> {
                mBinding.detectModeView.reset();
                playRingtone();
            });
        }

        @Override
        public void onGazeDetected(long timeStamp) {
            runOnUiThread(() -> {
                mBinding.detectModeView.reset();
                stopRingtone();
            });
        }

        @Override
        public void onStartSdkStateChanged(final StartSdkState state, long timeStamp) {
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

    private SdkFragmentCameraBinding mBinding;

    private Ringtone mRingtone = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.sdk_fragment_camera, container, false);
        setHasOptionsMenu(false);

        mBinding.buttonAction.setOnClickListener(v -> StartSdk.getInstance().calibrate());

        mBinding.buttonHideCalibration.setOnClickListener(v -> StartSdk.getInstance().stopCalibrate());

        mBinding.buttonSettings.setOnClickListener(v -> getMainActivity().openSettingsFragment());

        hideAllArea();
        return mBinding.getRoot();
    }

    @Override
    public void onPause() {
        stopRingtone();
        StartSdk.getInstance().stop();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        StartSdk.getInstance().start(AppCore.getInstance(), mBinding.fdActivitySurfaceView,
                getMainActivity().getCameraSettings(), mStartSdkListener);
    }

    public void onDestroy() {
        super.onDestroy();
    }

    private void updateUiInfo(final Point offsetRight, final Point offsetLeft) {
        runOnUiThread(() -> {
            float x = 0;
            float y = 0;

            if (offsetRight != null) {
                x += offsetRight.x;
                y += offsetRight.y;
            }

            if (offsetLeft != null) {
                x += offsetLeft.x;
                y += offsetLeft.y;
            }

            final String text = "offsetX: " + Constants.DECIMAL_FORMAT.format(x) + "\n" + "offsetY: " + Constants.DECIMAL_FORMAT.format(y);
            mBinding.calibrateSize.setText(text);
        });
    }

    private void showStateArea() {
        runOnUiThread(() -> {
            mBinding.cursorState.setVisibility(View.VISIBLE);
            mBinding.cursorCalibration.setVisibility(View.GONE);
            mBinding.buttonHideCalibration.setVisibility(View.GONE);
            mBinding.buttonAction.setVisibility(View.VISIBLE);
        });
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
            mBinding.buttonHideCalibration.setVisibility(View.VISIBLE);
            mBinding.buttonAction.setVisibility(View.INVISIBLE);
        });
    }

    private void hideAllArea() {
        runOnUiThread(() -> {
            mBinding.cursorState.setVisibility(View.GONE);
            mBinding.cursorCalibration.setVisibility(View.GONE);
            mBinding.buttonHideCalibration.setVisibility(View.GONE);
            mBinding.buttonAction.setVisibility(View.VISIBLE);
        });
    }

    private synchronized void playRingtone() {
        try {
            if (mRingtone == null) {
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                mRingtone = RingtoneManager.getRingtone(AppCore.getInstance(), notification);
            }

            if (!mRingtone.isPlaying()) {
                mRingtone.play();
            }
        } catch (Exception e) {
            SdkLog.e(TAG, e);
        }
    }

    private synchronized void stopRingtone() {
        try {
            if (mRingtone != null && mRingtone.isPlaying()) {
                mRingtone.stop();
                mRingtone = null;
            }
        } catch (Exception e) {
            SdkLog.e(TAG, e);
        }
    }
}
