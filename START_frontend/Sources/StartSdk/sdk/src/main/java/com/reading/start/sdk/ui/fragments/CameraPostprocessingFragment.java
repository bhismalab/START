package com.reading.start.sdk.ui.fragments;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.reading.start.sdk.AppCore;
import com.reading.start.sdk.Constants;
import com.reading.start.sdk.R;
import com.reading.start.sdk.core.CalibrateType;
import com.reading.start.sdk.core.DetectMode;
import com.reading.start.sdk.core.Settings;
import com.reading.start.sdk.core.StartSdk;
import com.reading.start.sdk.core.StartSdkListener;
import com.reading.start.sdk.core.StartSdkState;
import com.reading.start.sdk.databinding.SdkFragmentCameraPostprocessingBinding;
import com.reading.start.sdk.general.SdkLog;

import org.opencv.core.Point;

import java.util.concurrent.atomic.AtomicBoolean;

public class CameraPostprocessingFragment extends BaseCameraFragment {
    private static final String TAG = CameraPostprocessingFragment.class.getSimpleName();

    private enum VideoType {
        None,
        Calibration,
        PostCalibration,
        Test
    }

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
                if (!mIsRecoding) {
                    showStateArea();
                } else {
                    hideAllArea();
                }
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
//            runOnUiThread(() -> {
//                mBinding.detectModeView.reset();
//                playRingtone();
//            });
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
            runOnUiThread(() -> mBinding.detectResult.setImageBitmap(bitmap));
        }

        @Override
        public void onVideoRecordingStart(StartSdk.VideoType type) {
        }

        @Override
        public void onVideoRecordingStop(StartSdk.VideoType type) {
            if (type == StartSdk.VideoType.Calibration) {
                StartSdk.getInstance().startVideoRecord(StartSdk.VideoType.PostCalibration);

                // stop post calibration
                mHandler.postDelayed(() -> StartSdk.getInstance().stopVideoRecord(), 5000);
            } else if (type == StartSdk.VideoType.PostCalibration) {
                StartSdk.getInstance().startVideoRecord(StartSdk.VideoType.Test);
            }
        }

        @Override
        public void onPostProcessingPostCalibrationStart() {
            showStateArea();
        }

        @Override
        public void onPostProcessingPostCalibrationEnd() {
        }

        @Override
        public void onPostProcessingTestStart() {
            showStateArea();
        }

        @Override
        public void onPostProcessingTestEnd() {
            mHandler.postDelayed(() -> showRecordingUI(), 1000);
        }
    };

    private SdkFragmentCameraPostprocessingBinding mBinding;

    private Ringtone mRingtone = null;

    private AtomicBoolean mStopPostprocessing = new AtomicBoolean();

    private boolean mIsRecoding = true;

    private Handler mHandler = new Handler();

    private VideoType mVideoType = VideoType.None;

    private boolean mIsStopManual = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.sdk_fragment_camera_postprocessing, container, false);
        setHasOptionsMenu(false);

        mBinding.buttonStartRecoding.setOnClickListener(v -> {
            mIsStopManual = false;
            mIsRecoding = true;
            mVideoType = VideoType.Calibration;
            startVideoRecoding(StartSdk.getInstance().getCalibrationVideoPath().getPath());
            mHandler.postDelayed(() -> stopVideoRecoding(), Constants.CALIBRATE_DELAY + 500);
            mBinding.buttonStartRecoding.setVisibility(View.GONE);
            mBinding.buttonStopRecoding.setVisibility(View.VISIBLE);
            mBinding.detectResultArea.setVisibility(View.GONE);
            mBinding.buttonStartDetect.setEnabled(false);
            mBinding.buttonStopDetect.setEnabled(false);
            mBinding.recordingState.setText(mVideoType.name());
            showCalibrateArea(CalibrateType.Center);
        });

        mBinding.buttonStopRecoding.setOnClickListener(v -> {
            mIsStopManual = true;
            stopVideoRecoding();
            mBinding.buttonStartRecoding.setVisibility(View.VISIBLE);
            mBinding.buttonStopRecoding.setVisibility(View.GONE);
            mBinding.detectResultArea.setVisibility(View.GONE);
            mBinding.buttonStartDetect.setEnabled(true);
            mBinding.buttonStopDetect.setEnabled(true);
            hideAllArea();
        });

        mBinding.buttonStartDetect.setOnClickListener(v -> {
            showDetectUI();
            mIsRecoding = false;
            mBinding.buttonStartRecoding.setEnabled(false);
            mBinding.buttonStopRecoding.setEnabled(false);
            mBinding.buttonStartDetect.setVisibility(View.GONE);
            mBinding.buttonStopDetect.setVisibility(View.VISIBLE);
            mBinding.detectResultArea.setVisibility(View.VISIBLE);

            mStopPostprocessing.set(false);

            Thread thread = new Thread(() -> {
                StartSdk.getInstance().processVideo(getCameraSettings(), mStopPostprocessing, mStartSdkListener);

                runOnUiThread(() -> {
                    mBinding.buttonStartRecoding.setEnabled(true);
                    mBinding.buttonStopRecoding.setEnabled(true);
                    mBinding.buttonStartDetect.setVisibility(View.VISIBLE);
                    mBinding.buttonStopDetect.setVisibility(View.GONE);
                });
            });
            thread.start();
        });

        mBinding.buttonStopDetect.setOnClickListener(v -> {
            if (!mStopPostprocessing.get()) {
                mStopPostprocessing.set(true);

                mBinding.buttonStartRecoding.setEnabled(true);
                mBinding.buttonStopRecoding.setEnabled(true);
                mBinding.buttonStartDetect.setVisibility(View.VISIBLE);
                mBinding.buttonStopDetect.setVisibility(View.GONE);
            }
        });

        mBinding.buttonSettings.setOnClickListener(v -> getMainActivity().openPostprocessingSettingsFragment());
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
    protected int getCameraIndex() {
        return getCameraSettings().getGeneral().getCameraIndex();
    }

    @Override
    protected Size getVideoSize() {
        return getCameraSettings().getGeneral().getCameraSize();
    }

    @Override
    public void onResume() {
        super.onResume();

        mHandler.postDelayed(() -> showRecordingUI(), 500);
        showDetectUI();
    }

    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onVideoStartRecoding() {
        if (mVideoType == VideoType.None) {
            mBinding.recordingState.setText("");
        } else {
            mBinding.recordingState.setText(mVideoType.name());
        }
    }

    @Override
    protected void onVideoStopRecoding() {
        hideAllArea();
        mHandler.postDelayed(() -> startCameraPreview(), 300);

        if (!mIsStopManual) {
            mHandler.postDelayed(() -> {
                if (mVideoType == VideoType.Calibration) {
                    mVideoType = VideoType.PostCalibration;
                    mHandler.postDelayed(() -> startVideoRecoding(StartSdk.getInstance().getPostCalibrationVideoPath().getPath()), 200);
                    mHandler.postDelayed(() -> stopVideoRecoding(), Constants.POST_CALIBRATE_DELAY + 200);
                } else if (mVideoType == VideoType.PostCalibration) {
                    mVideoType = VideoType.Test;
                    mHandler.postDelayed(() -> startVideoRecoding(StartSdk.getInstance().getTestVideoPath().getPath()), 200);
                } else {
                    mVideoType = VideoType.None;
                    mBinding.recordingState.setText("");
                }
            }, 300);
        } else {
            mVideoType = VideoType.None;
            mBinding.recordingState.setText("");
        }
    }

    private void showRecordingUI() {
        StartSdk.getInstance().stop();
        startCameraPreview();
        mBinding.texture.setVisibility(View.VISIBLE);
    }

    private void showDetectUI() {
        stopCameraPreview();
        mBinding.texture.setVisibility(View.GONE);
        StartSdk.getInstance().start(AppCore.getInstance(), mBinding.fdActivitySurfaceView,
                getCameraSettings(), mStartSdkListener);
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
        });
    }

    private void hideAllArea() {
        runOnUiThread(() -> {
            mBinding.cursorState.setVisibility(View.GONE);
            mBinding.cursorCalibration.setVisibility(View.GONE);
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

    public Settings getCameraSettings() {
        Settings settings = getMainActivity().getCameraPostProcessingSettings();
        return settings;
    }
}
