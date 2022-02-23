package com.reading.start.tests.test_eye_tracking.ui.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.reading.start.sdk.core.CalibrateType;
import com.reading.start.sdk.core.DetectMode;
import com.reading.start.sdk.core.Settings;
import com.reading.start.sdk.core.SettingsFactory;
import com.reading.start.sdk.core.StartSdk;
import com.reading.start.sdk.core.StartSdkListener;
import com.reading.start.sdk.core.StartSdkState;
import com.reading.start.tests.TestLog;
import com.reading.start.tests.TestsProvider;
import com.reading.start.tests.test_eye_tracking.BuildConfig;
import com.reading.start.tests.test_eye_tracking.Constants;
import com.reading.start.tests.test_eye_tracking.EyeTrackingTest;
import com.reading.start.tests.test_eye_tracking.R;
import com.reading.start.tests.test_eye_tracking.data.DataProvider;
import com.reading.start.tests.test_eye_tracking.databinding.TestEyeTrackingFragmentTestLookingBinding;
import com.reading.start.tests.test_eye_tracking.domain.entity.CalibrationCheckItem;
import com.reading.start.tests.test_eye_tracking.domain.entity.EyeTrackingTestContentPairs;
import com.reading.start.tests.test_eye_tracking.domain.entity.EyeTrackingTestSurveyResult;
import com.reading.start.tests.test_eye_tracking.domain.entity.TestDataEyeTracking;
import com.reading.start.tests.test_eye_tracking.domain.entity.TestDataStep;
import com.reading.start.tests.test_eye_tracking.domain.entity.TestDataStimulus;
import com.reading.start.tests.test_eye_tracking.domain.entity.TestDataTest;
import com.reading.start.tests.test_eye_tracking.ui.activities.MainActivity;
import com.reading.start.tests.test_eye_tracking.ui.dialogs.DialogCompleteTest;
import com.reading.start.tests.test_eye_tracking.ui.views.CompleteTriggerView;

import org.opencv.core.Point;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import io.realm.Realm;

public class EyeTrackingLookingTestFragment extends BaseCameraFragment {
    private static final String TAG = EyeTrackingLookingTestFragment.class.getSimpleName();

    private enum VideoType {
        None,
        Calibration,
        PostCalibration,
        Test
    }

    private StartSdkListener mStartSdkListener = new StartSdkListener() {
        @Override
        public void onCalibrateStart(long timeStamp) {
            mIsCalibrating = true;
        }

        @Override
        public void onCalibrateChanged(CalibrateType type, long timeStamp) {
            mIsCalibrating = true;
            showCalibrateArea(type);
            startChangeCalibrationImages();
        }

        @Override
        public void onCalibrateCompleted(boolean success, long timeStamp) {
            if (success) {
                runOnUiThread(() -> showCalibrationCheckArea());
            } else {
                showSettingsArea();
                runOnUiThread(() -> Toast.makeText(getActivity(), getString(R.string.sdk_test_calibration_not_success), Toast.LENGTH_LONG).show());
            }

            mIsCalibrating = false;
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
                if (BuildConfig.SKIP_SDK_STATE_CHECK) {
                    if (state != null) {
                        state.setLightEnvironmentOk(true);
                        state.setLightCameraOk(true);
                        state.setDeviceShakeOk(true);
                        state.setDevicePositionOk(true);
                        state.setHeadDetectedOk(true);
                        state.setHeadCloseOk(true);
                        state.setHeadFarOk(true);
                        state.setEyeDetectOk(true);
                    }
                }

                if (state != null && !isDetached() && isAdded()) {
                    if (state.isDevicePositionOk()) {
                        mBinding.devicePositionValue.setText(getString(R.string.sdk_test_ok));
                        mBinding.devicePositionValue.setTextColor(getResources().getColor(R.color.colorOK));
                    } else {
                        mBinding.devicePositionValue.setText(getString(R.string.sdk_test_fail));
                        mBinding.devicePositionValue.setTextColor(getResources().getColor(R.color.colorFail));
                    }

                    if (state.isDeviceShakeOk()) {
                        mBinding.deviceJoltingValue.setText(getString(R.string.sdk_test_ok));
                        mBinding.deviceJoltingValue.setTextColor(getResources().getColor(R.color.colorOK));
                    } else {
                        mBinding.deviceJoltingValue.setText(getString(R.string.sdk_test_fail));
                        mBinding.deviceJoltingValue.setTextColor(getResources().getColor(R.color.colorFail));
                    }

                    if (state.isLightEnvironmentOk()) {
                        mBinding.lightEnvironmentValue.setText(getString(R.string.sdk_test_ok));
                        mBinding.lightEnvironmentValue.setTextColor(getResources().getColor(R.color.colorOK));
                    } else {
                        mBinding.lightEnvironmentValue.setText(getString(R.string.sdk_test_fail));
                        mBinding.lightEnvironmentValue.setTextColor(getResources().getColor(R.color.colorFail));
                    }

                    if (state.isLightCameraOk()) {
                        mBinding.lightCameraValue.setText(getString(R.string.sdk_test_ok));
                        mBinding.lightCameraValue.setTextColor(getResources().getColor(R.color.colorOK));
                    } else {
                        mBinding.lightCameraValue.setText(getString(R.string.sdk_test_fail));
                        mBinding.lightCameraValue.setTextColor(getResources().getColor(R.color.colorFail));
                    }

                    if (state.isHeadDetectedOk()) {
                        mBinding.headDetectValue.setText(getString(R.string.sdk_test_ok));
                        mBinding.headDetectValue.setTextColor(getResources().getColor(R.color.colorOK));
                    } else {
                        mBinding.headDetectValue.setText(getString(R.string.sdk_test_fail));
                        mBinding.headDetectValue.setTextColor(getResources().getColor(R.color.colorFail));
                    }

                    if (state.isHeadCloseOk() && state.isHeadFarOk()) {
                        mBinding.headDistanceValue.setText(getString(R.string.sdk_test_ok));
                        mBinding.headDistanceValue.setTextColor(getResources().getColor(R.color.colorOK));
                    } else {
                        mBinding.headDistanceValue.setText(getString(R.string.sdk_test_fail));
                        mBinding.headDistanceValue.setTextColor(getResources().getColor(R.color.colorFail));
                    }

                    if (state.isEyeDetectOk()) {
                        mBinding.eyeDetectValue.setText(getString(R.string.sdk_test_ok));
                        mBinding.eyeDetectValue.setTextColor(getResources().getColor(R.color.colorOK));
                    } else {
                        mBinding.eyeDetectValue.setText(getString(R.string.sdk_test_fail));
                        mBinding.eyeDetectValue.setTextColor(getResources().getColor(R.color.colorFail));
                    }

                    String message = null;

                    if (state.isOk()) {
                        mBinding.startTest.setEnabled(true);
                        message = getString(R.string.test_eye_tracking_status_ok);
                    } else {
                        mBinding.startTest.setEnabled(false);

                        if (!state.isDevicePositionOk()) {
                            message = getString(R.string.sdk_test_fail_message_device_position);
                        } else if (!state.isDeviceShakeOk()) {
                            message = getString(R.string.sdk_test_fail_message_device_shake);
                        } else if (!state.isLightEnvironmentOk()) {
                            message = getString(R.string.sdk_test_fail_message_light);
                        } else if (!state.isLightCameraOk()) {
                            message = getString(R.string.sdk_test_fail_message_light_camera);
                        } else if (!state.isHeadDetectedOk()) {
                            message = getString(R.string.sdk_test_fail_message_head_detect);
                        } else if (!state.isHeadFarOk()) {
                            message = getString(R.string.sdk_test_fail_message_head_far);
                        } else if (!state.isHeadCloseOk()) {
                            message = getString(R.string.sdk_test_fail_message_head_close);
                        } else if (!state.isEyeDetectOk()) {
                            message = getString(R.string.sdk_test_fail_message_eye_detect);
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
            //TestLog.d(TAG, "onFrameProcessed, isValid: " + isValid);

            if (mIsCalibrationCheck) {
                int imagePosition = mBinding.calibrationCheckLeft.getVisibility() == View.VISIBLE ? 0 : 1;
                mCalibrationCheckItems.add(new CalibrationCheckItem(col, imagePosition));
            } else if (mCurrentTestDataStep != null && mCurrentTestDataStep.getItems() != null) {
                float x = col;
                float y = row;
                long time = Calendar.getInstance().getTimeInMillis();
                String stimulusAppear = mCurrentTestDataStep.getName();

                TestDataEyeTracking eyeTrackingItem = new TestDataEyeTracking(x, y, time, isEyeDetected ? 1 : 0, gazeOutside ? 1 : 0);
                TestDataStimulus stimulusItem = new TestDataStimulus(time, stimulusAppear, "");
                mTestData.getItemsEyeTracking().add(eyeTrackingItem);
                mCurrentTestDataStep.getItems().add(stimulusItem);
            }
        }

        @Override
        public void onFramePostProcessed(Bitmap bitmap, long timeStamp) {
        }

        @Override
        public void onVideoRecordingStart(StartSdk.VideoType type) {
        }

        @Override
        public void onVideoRecordingStop(StartSdk.VideoType type) {
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

    private static final int POST_CALIBRATION_STEP_DELAY = 2000;

    private TestEyeTrackingFragmentTestLookingBinding mBinding;

    private boolean mInProgress = false;

    private boolean mIsStop = false;

    private Handler mHandler = new Handler();

    private TestTimer mTestTimer = null;

    private boolean mSaving = false;

    private TestDataTest mTestData = null;

    private String mNextVideoAbsolutePath;

    private Settings mSettings = null;

    private int mAttempt = 0;

    private TestDataStep mCurrentTestDataStep;

    private TestDataStimulus mCurrentStimulusItem;

    private ImageView mCurrentCalibrationView = null;

    private int mCalibrationImageIndex = 0;

    private boolean mIsCalibrating = false;

    private boolean mIsCalibrationCheck = false;

    private int mCalibrationCheckSteps = 0;

    private ArrayList<CalibrationCheckItem> mCalibrationCheckItems = new ArrayList<>();

    private VideoType mVideoType = VideoType.None;

    private boolean mIsSkipped = false;

    private boolean mIsBack = false;

    private boolean mIsStarted = false;

    private Random mRandom = new Random();

    private ArrayList<Integer> mPlayedVideo = new ArrayList<>();

    public void setSettings(Settings settings) {
        mSettings = settings;
    }

    public EyeTrackingLookingTestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.test_eye_tracking_fragment_test_looking, container, false);

        TextView title = mBinding.actionBar.findViewById(R.id.text_actionbar);
        title.setText(R.string.test_eye_tracking_assessment_actionbar_1);

        mBinding.actionBar.findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        mBinding.actionBar.findViewById(R.id.next_button).setVisibility(View.GONE);
        mBinding.actionBar.findViewById(R.id.home_button).setVisibility(View.GONE);
        mBinding.actionBar.findViewById(R.id.save_button).setVisibility(View.GONE);
        mBinding.actionBar.findViewById(R.id.skip_button).setVisibility(View.VISIBLE);

        mBinding.actionBar.findViewById(R.id.back_button).setOnClickListener(v -> {
            mIsBack = true;
            final MainActivity activity = getMainActivity();

            if (activity != null) {
                activity.onBackPressedSupper();
            }
        });

        mBinding.actionBar.findViewById(R.id.skip_button).setOnClickListener(v -> {
            mIsSkipped = true;
            mInProgress = true;
            showEndTestDialog(true);
        });

        mBinding.startTest.setOnClickListener(v -> {
            mIsStarted = true;
            mBinding.settingsScreen.setVisibility(View.INVISIBLE);
            mBinding.testScreen.setVisibility(View.VISIBLE);
            StartSdk.getInstance().stop();
            mBinding.texture.setVisibility(View.VISIBLE);
            startCamera();
        });

        mBinding.completeTriggerView.setListener(new CompleteTriggerView.CompleteTriggerViewListener() {
            @Override
            public void onCompleteTrigger() {
                mIsSkipped = true;
                mTestData.setInterrupted(true);
                onTestCompleted(false);
            }

            @Override
            public void onTouchChanged(float x, float y, float touchPressure, float touchSize, int touchCount) {
            }

            @Override
            public boolean onCountFingerChanged(int count) {
                return false;
            }
        });

        hideAllArea();
        mBinding.imageArea.setVisibility(View.INVISIBLE);
        mTestData = getMainActivity().getTestData().getTestLooking();

        return mBinding.getRoot();
    }

    @Override
    protected void onCameraPrepared() {
        if (mVideoType == VideoType.None) {
            mVideoType = VideoType.Calibration;
            startRecordingVideo(StartSdk.getInstance().getCalibrationVideoPath().getPath());
        } else {
            onVideoStopRecoding();
        }
    }

    @Override
    protected void onCameraDisconnected() {
        // not need process
    }

    @Override
    protected void onCameraFail() {
        // not need process
    }

    @Override
    protected void onError() {
        // not need process
    }

    @Override
    public void onResume() {
        super.onResume();
        mNextVideoAbsolutePath = getVideoFilePath(getActivity());
        mSettings = SettingsFactory.getReleaseDetect2x1();
        mSettings.getGeneral().setViewFile(mNextVideoAbsolutePath);
        StartSdk.getInstance().start(getActivity(), mBinding.fdActivitySurfaceView, mSettings, mStartSdkListener);
    }

    @Override
    public void onPause() {
        if (!mIsStop && !mIsSkipped && !mIsBack && mIsStarted) {
            mIsSkipped = true;
            mTestData.setInterrupted(true);
            onTestCompleted(false);
        }

        StartSdk.getInstance().stop();
        stopTest();
        super.onPause();
    }

    @Override
    protected int getCameraIndex() {
        if (mSettings != null && mSettings.getGeneral().getCameraIndex() != -1) {
            return mSettings.getGeneral().getCameraIndex();
        } else {
            return 0;
        }
    }

    @Override
    protected Size getVideoSize() {
        if (mSettings != null) {
            return mSettings.getGeneral().getCameraSize();
        } else {
            return null;
        }
    }

    @Override
    protected void onVideoStartRecoding() {
        if (mVideoType != null) {
            TestLog.d(TAG, "onVideoStartRecoding, VideoType: " + mVideoType);

            if (mVideoType == VideoType.Calibration) {
                mIsCalibrating = true;
                showCalibrateArea(CalibrateType.Center);
                runOnUiThread(() -> startChangeCalibrationImages());
                mHandler.postDelayed(() -> stopRecordingVideo(), com.reading.start.sdk.Constants.CALIBRATE_DELAY);
            } else if (mVideoType == VideoType.PostCalibration) {
                runOnUiThread(() -> showCalibrationCheckArea());
                mHandler.postDelayed(() -> stopRecordingVideo(), com.reading.start.sdk.Constants.POST_CALIBRATE_DELAY);
            } else if (mVideoType == VideoType.Test) {
                showStateArea();
            }
        }
    }

    @Override
    protected void onVideoStopRecoding() {
        if (mVideoType != null) {
            TestLog.d(TAG, "onVideoStopRecoding, VideoType: " + mVideoType);
        }

        if (mVideoType == VideoType.Calibration) {
            mIsCalibrating = false;
            mVideoType = VideoType.PostCalibration;
            runOnUiThread(() -> startRecordingVideo(StartSdk.getInstance().getPostCalibrationVideoPath().getPath()));
        } else if (mVideoType == VideoType.PostCalibration) {
            mVideoType = VideoType.Test;
            runOnUiThread(() -> startRecordingVideo(StartSdk.getInstance().getTestVideoPath().getPath()));
        } else {
            mVideoType = VideoType.None;
        }
    }

    private void showSettingsArea() {
        runOnUiThread(() -> {
            hideAllArea();
            mBinding.settingsScreen.setVisibility(View.VISIBLE);
            mBinding.testScreen.setVisibility(View.INVISIBLE);
        });
    }

    private void showCalibrateArea(final CalibrateType type) {
        runOnUiThread(() -> {
            mBinding.imageArea.setVisibility(View.INVISIBLE);

            if (type != null) {
                switch (type) {
                    case Center: {
                        mBinding.calibrationCenter.setVisibility(View.VISIBLE);
                        mBinding.calibrationTopLeft.setVisibility(View.INVISIBLE);
                        mBinding.calibrationTopRight.setVisibility(View.INVISIBLE);
                        mBinding.calibrationBottomLeft.setVisibility(View.INVISIBLE);
                        mBinding.calibrationBottomRight.setVisibility(View.INVISIBLE);
                        mCurrentCalibrationView = mBinding.calibrationCenter;
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
                        mCurrentCalibrationView = null;
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
            mBinding.calibrationCheck.setVisibility(View.GONE);
        });
    }

    private void hideAllArea() {
        runOnUiThread(() -> {
            mBinding.cursorState.setVisibility(View.GONE);
            mBinding.cursorCalibration.setVisibility(View.GONE);
        });
    }

    private void showCalibrationCheckArea() {
        if (!mIsStop) {
            mIsCalibrationCheck = true;
            mCalibrationCheckSteps = 0;
            mCalibrationCheckItems.clear();

            if (Constants.SHOW_STATE_AREA) {
                mBinding.cursorState.setVisibility(View.VISIBLE);
            } else {
                mBinding.cursorState.setVisibility(View.GONE);
            }

            mBinding.cursorCalibration.setVisibility(View.GONE);
            mBinding.imageArea.setVisibility(View.GONE);
            mBinding.calibrationCheck.setVisibility(View.VISIBLE);
            mBinding.calibrationCheckLeft.setVisibility(View.VISIBLE);
            mBinding.calibrationCheckRight.setVisibility(View.GONE);

            mCurrentTestDataStep = new TestDataStep("Calibration check");
            mTestData.getItemsStimulusQuality().add(mCurrentTestDataStep);
            mTestData.setStartTime(Calendar.getInstance().getTimeInMillis());
            showCalibrationCheckStep();
        }
    }

    private void showCalibrationCheckStep() {
        if (!mIsStop) {
            int resourceId = 0;
            TestDataStimulus stimulusItem;

            switch (mCalibrationCheckSteps) {
                case 1: {
                    resourceId = R.drawable.calibration_3;
                    stimulusItem = new TestDataStimulus(Calendar.getInstance().getTimeInMillis() - mTestData.getStartTime(),
                            Constants.STIMULUS_GIRAFFE, "");
                    break;
                }
                case 2: {
                    resourceId = R.drawable.calibration_4;
                    stimulusItem = new TestDataStimulus(Calendar.getInstance().getTimeInMillis() - mTestData.getStartTime(),
                            Constants.STIMULUS_LION, "");
                    break;
                }
                case 3: {
                    resourceId = R.drawable.calibration_5;
                    stimulusItem = new TestDataStimulus(Calendar.getInstance().getTimeInMillis() - mTestData.getStartTime(),
                            Constants.STIMULUS_ELEPHANT, "");
                    break;
                }
                case 0:
                default: {
                    resourceId = R.drawable.calibration_2;
                    stimulusItem = new TestDataStimulus(Calendar.getInstance().getTimeInMillis() - mTestData.getStartTime(),
                            Constants.STIMULUS_MONKEY, "");
                    break;
                }
            }

            mCalibrationCheckSteps++;

            if (mBinding.calibrationCheckLeft.getVisibility() == View.VISIBLE) {
                mBinding.calibrationCheckLeft.setVisibility(View.GONE);
                mBinding.calibrationCheckRight.setVisibility(View.VISIBLE);
                mBinding.calibrationCheckRight.setImageResource(resourceId);
                stimulusItem.setStimulusSide(Constants.STIMULUS_SIDE_RIGHT);
            } else {
                mBinding.calibrationCheckLeft.setVisibility(View.VISIBLE);
                mBinding.calibrationCheckRight.setVisibility(View.GONE);
                mBinding.calibrationCheckLeft.setImageResource(resourceId);
                stimulusItem.setStimulusSide(Constants.STIMULUS_SIDE_LEFT);
            }

            if (stimulusItem != null) {
                mCurrentTestDataStep.getItems().add(stimulusItem);
            }

            mHandler.postDelayed(() -> {
                if (!mIsStop) {
                    if (mCalibrationCheckSteps > Constants.CALIBRATION_CHECK_STEPS) {
                        setCalibrationCheckResult();
                        mIsCalibrationCheck = false;
                        mBinding.calibrationCheck.setVisibility(View.GONE);
                        //showStateArea();
                    } else {
                        showCalibrationCheckStep();
                    }
                }
            }, POST_CALIBRATION_STEP_DELAY);
        }
    }

    private void setCalibrationCheckResult() {
        if (mCalibrationCheckItems.size() > 0) {
            int countValid = 0;

            for (CalibrationCheckItem item : mCalibrationCheckItems) {
                if (item.isValid()) {
                    countValid++;
                }
            }

            int quality = (100 * countValid) / mCalibrationCheckItems.size();

            if (mTestData != null) {
                mTestData.setCalibrationQuality(quality);
            }
        }
    }

    private void showStateArea() {
        if (!mIsStop) {
            runOnUiThread(() -> {
                if (Constants.SHOW_STATE_AREA) {
                    mBinding.cursorState.setVisibility(View.VISIBLE);
                } else {
                    mBinding.cursorState.setVisibility(View.GONE);
                }

                mBinding.cursorCalibration.setVisibility(View.GONE);

                mAttempt = -1;
                mBinding.imageArea.setVisibility(View.VISIBLE);
                mTestData.setStartTime(Calendar.getInstance().getTimeInMillis());
                startAttempt();
            });
        }
    }

    private synchronized void onTestCompleted(boolean goToNextStep) {
        if (!mIsStop) {
            mIsStop = true;
            stopTest();
            showEndTestDialog(goToNextStep);
        }
    }

    private void updateVideoSizeArea() {
        int width = mBinding.getRoot().getMeasuredWidth();
        int height = mBinding.getRoot().getMeasuredHeight();

        if (mBinding.videoView.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            mBinding.videoView.setPreferredWidth(width);
            mBinding.videoView.setPreferredHeight(height);
            mBinding.videoView.requestLayout();
        }
    }

    private void startAttempt() {
        mAttempt++;
        mCurrentTestDataStep = new TestDataStep("Preferential looking test " + String.valueOf(mAttempt + 1));
        mTestData.getItemsStimulus().add(mCurrentTestDataStep);
        mCurrentStimulusItem = new TestDataStimulus(Calendar.getInstance().getTimeInMillis() - mTestData.getStartTime(), "", "");
        mCurrentTestDataStep.getItems().add(mCurrentStimulusItem);

        if (isVideoAttempt(mAttempt + 1)) {
            mBinding.videoView.setVisibility(View.VISIBLE);
            mBinding.imageViewLeft.setVisibility(View.GONE);
            mBinding.imageViewRight.setVisibility(View.GONE);

            updateVideoSizeArea();

            if (mBinding.videoView.isPlaying()) {
                mBinding.videoView.stopPlayback();
            }

            if (!mBinding.videoView.isPlaying()) {
                Uri leftVideo = getVideoUri();

                if (leftVideo != null) {
                    mBinding.videoView.setVideoURI(leftVideo);
                    mBinding.videoView.setOnCompletionListener(mp -> mBinding.videoView.start());
                    mBinding.videoView.start();
                }
            }
        } else {
            if (mBinding.videoView.isPlaying()) {
                mBinding.videoView.stopPlayback();
            }

            mBinding.videoView.setVisibility(View.GONE);
            mBinding.imageViewLeft.setVisibility(View.VISIBLE);
            mBinding.imageViewRight.setVisibility(View.VISIBLE);
            mBinding.imageViewLeft.setImageBitmap(getBackgroundPart1(mAttempt + 1));
            mBinding.imageViewRight.setImageBitmap(getBackgroundPart2(mAttempt + 1));
        }

        startTest();
    }

    private Uri getVideoUri() {
        Uri result = null;
        File fileFile = getVideoFile(mAttempt + 1);

        if (fileFile != null) {
            if (fileFile.getName() != null) {
                mCurrentTestDataStep.setVideoName(fileFile.getName());
                mCurrentStimulusItem.setStimulusAppear(fileFile.getName());
            }

            result = Uri.fromFile(fileFile);
        }

        return result;
    }

    private void startTest() {
        if (!mInProgress) {
            mInProgress = true;

            if (mTestTimer != null) {
                mTestTimer.needProcess = false;
            }

            mTestTimer = new TestTimer(() -> {
                if (!mIsStop && mAttempt < Constants.TEST_LOOK_ATTEMPT - 1) {
                    mInProgress = false;
                    startAttempt();
                } else {
                    onTestCompleted(true);
                }
            });

            mHandler.postDelayed(mTestTimer, Constants.TEST_LOOK_DURATION);
        }
    }

    private void stopTest() {
        if (mInProgress) {
            mInProgress = false;
            mIsStop = true;

            if (mTestTimer != null) {
                mTestTimer.needProcess = false;
                mTestTimer = null;
            }
        }
    }

    private void showEndTestDialog(boolean goToNextStep) {
        MainActivity activity = getMainActivity();

        if (activity != null) {
            mTestData.setGazeDetection(mTestData.calculateGazeDetection());
            mTestData.setGazeOnTheScreen(mTestData.calculateGazeOnScreen());
            mTestData.setEndTime(Calendar.getInstance().getTimeInMillis());

            if (mIsSkipped) {
                mTestData.setFilePath("");
                mTestData.setInterrupted(true);
            } else {
                mTestData.setFilePath(mNextVideoAbsolutePath);
            }

            if (goToNextStep) {
                activity.openAttentionTestFragment();
            } else {
                int attempt = saveTestResult();
                boolean showNewAttempt = attempt < com.reading.start.tests.Constants.ATTEMPT_COUNT;
                boolean showNextStep = !TestsProvider.getInstance(getActivity()).isLastTest(EyeTrackingTest.getInstance());

                String title = mTestData.isInterrupted() ? getResources().getString(R.string.test_eye_tracking_dialog_interrupted_title)
                        : getResources().getString(R.string.test_eye_tracking_dialog_success_title);
                String message = mTestData.isInterrupted() ? getResources().getString(R.string.test_eye_tracking_dialog_interrupted_message)
                        : getResources().getString(R.string.test_eye_tracking_dialog_success_message);

                DialogCompleteTest dialog = DialogCompleteTest.getInstance(title, message, showNewAttempt, showNextStep,
                        new DialogCompleteTest.DialogListener() {
                            @Override
                            public void onBack() {
                                activity.finishAsOpenChildTest();
                            }

                            @Override
                            public void onNext() {
                                activity.finishAsStartNext();
                            }

                            @Override
                            public void onAddNew() {
                                activity.finishAsTryAgain();
                            }
                        });

                dialog.setCancelable(false);
                dialog.show(getFragmentManager(), TAG);
            }
        }
    }

    private static class TestTimer implements Runnable {
        public boolean needProcess = true;

        private Runnable mLocalRun = null;

        public TestTimer(Runnable runnable) {
            mLocalRun = runnable;
        }

        @Override
        public void run() {
            if (needProcess && mLocalRun != null) {
                mLocalRun.run();
            }
        }
    }

    private String getVideoFilePath(Context context) {
        String result = null;

        try {
            final MainActivity activity = getMainActivity();

            if (activity != null && context != null) {
                String fileName = String.valueOf(activity.getSurveyId());
                fileName += "_" + String.valueOf(Calendar.getInstance().getTimeInMillis());
                fileName += Constants.VIDEO_FILE_NAME_LOOKING_TEST + "." + Constants.VIDEO_FILE_EXT;
                result = new File(context.getFilesDir(), fileName).toString();
            }
        } catch (Exception e) {
            TestLog.e(TAG, e);
        }

        return result;
    }

    private File getVideoFile(int attempt) {
        File result = null;
        Realm realm = null;

        try {
            realm = DataProvider.getInstance(getActivity()).getRealm();
            EyeTrackingTestContentPairs results = realm.where(EyeTrackingTestContentPairs.class).findFirst();

            if (results != null) {
                boolean find = false;
                int iterationCheck = 0;

                while (!find && iterationCheck < 20) {
                    iterationCheck++;
                    int index = mRandom.nextInt(8);

                    if (!mPlayedVideo.contains(index)) {
                        switch (index) {
                            case 1: {
                                result = new File(results.getPair2part1());
                                break;
                            }
                            case 2: {
                                result = new File(results.getPair3part1());
                                break;
                            }
                            case 3: {
                                result = new File(results.getPair4part1());
                                break;
                            }
                            case 4: {
                                result = new File(results.getPair5part1());
                                break;
                            }
                            case 5: {
                                result = new File(results.getPair6part1());
                                break;
                            }
                            case 6: {
                                result = new File(results.getPair7part1());
                                break;
                            }
                            case 7: {
                                result = new File(results.getPair8part1());
                                break;
                            }
                            case 0:
                            default: {
                                result = new File(results.getPair1part1());
                                break;
                            }
                        }

                        if (result != null) {
                            find = true;
                            mPlayedVideo.add(index);
                        }
                    } else if (mPlayedVideo.size() >= 8) {
                        mPlayedVideo.clear();
                    }
                }
            }
        } catch (Exception e) {
            TestLog.e(TAG, e);
        } finally {
            if (realm != null) {
                realm.close();
            }
        }

        return result;
    }

    private boolean isVideoAttempt(int attempt) {
        boolean result = false;

        Realm realm = null;

        try {
            realm = DataProvider.getInstance(getActivity()).getRealm();
            EyeTrackingTestContentPairs results = realm.where(EyeTrackingTestContentPairs.class).findFirst();

            if (results != null) {
                switch (attempt) {
                    case 2: {
                        result = results.getPair2part1().contains(Constants.FILE_TYPE_VIDEO);
                        break;
                    }
                    case 3: {
                        result = results.getPair3part1().contains(Constants.FILE_TYPE_VIDEO);
                        break;
                    }
                    case 4: {
                        result = results.getPair4part1().contains(Constants.FILE_TYPE_VIDEO);
                        break;
                    }
                    case 5: {
                        result = results.getPair5part1().contains(Constants.FILE_TYPE_VIDEO);
                        break;
                    }
                    case 6: {
                        result = results.getPair6part1().contains(Constants.FILE_TYPE_VIDEO);
                        break;
                    }
                    case 7: {
                        result = results.getPair7part1().contains(Constants.FILE_TYPE_VIDEO);
                        break;
                    }
                    case 8: {
                        result = results.getPair8part1().contains(Constants.FILE_TYPE_VIDEO);
                        break;
                    }
                    case 1:
                    default: {
                        result = results.getPair1part1().contains(Constants.FILE_TYPE_VIDEO);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            TestLog.e(TAG, e);
        } finally {
            if (realm != null) {
                realm.close();
            }
        }

        return result;
    }

    private Bitmap getBackgroundPart1(int attempt) {
        Bitmap bitmap = null;
        Realm realm = null;

        try {
            realm = DataProvider.getInstance(getActivity()).getRealm();
            EyeTrackingTestContentPairs results = realm.where(EyeTrackingTestContentPairs.class).findFirst();

            if (results != null) {
                switch (attempt) {
                    case 2: {
                        bitmap = BitmapFactory.decodeFile(results.getPair2part1());
                        break;
                    }
                    case 3: {
                        bitmap = BitmapFactory.decodeFile(results.getPair3part1());
                        break;
                    }
                    case 4: {
                        bitmap = BitmapFactory.decodeFile(results.getPair4part1());
                        break;
                    }
                    case 5: {
                        bitmap = BitmapFactory.decodeFile(results.getPair5part1());
                        break;
                    }
                    case 1:
                    default: {
                        bitmap = BitmapFactory.decodeFile(results.getPair1part1());
                        break;
                    }
                }
            }
        } catch (Exception e) {
            TestLog.e(TAG, e);
        } finally {
            if (realm != null) {
                realm.close();
            }
        }

        return bitmap;
    }

    private Bitmap getBackgroundPart2(int attempt) {
        Bitmap bitmap = null;
        Realm realm = null;

        try {
            realm = DataProvider.getInstance(getActivity()).getRealm();
            EyeTrackingTestContentPairs results = realm.where(EyeTrackingTestContentPairs.class).findFirst();

            if (results != null) {
                switch (attempt) {
                    case 2: {
                        bitmap = BitmapFactory.decodeFile(results.getPair2part2());
                        break;
                    }
                    case 3: {
                        bitmap = BitmapFactory.decodeFile(results.getPair3part2());
                        break;
                    }
                    case 4: {
                        bitmap = BitmapFactory.decodeFile(results.getPair4part2());
                        break;
                    }
                    case 5: {
                        bitmap = BitmapFactory.decodeFile(results.getPair5part2());
                        break;
                    }
                    case 1:
                    default: {
                        bitmap = BitmapFactory.decodeFile(results.getPair1part2());
                        break;
                    }
                }
            }
        } catch (Exception e) {
            TestLog.e(TAG, e);
        } finally {
            if (realm != null) {
                realm.close();
            }
        }

        return bitmap;
    }

    private void startChangeCalibrationImages() {
        if (!mIsStop && mIsCalibrating) {
            mHandler.postDelayed(() -> {
                if (mCurrentCalibrationView != null) {
                    mCalibrationImageIndex++;

                    if (mCalibrationImageIndex > 3) {
                        mCalibrationImageIndex = 0;
                    }

                    switch (mCalibrationImageIndex) {
                        case 1: {
                            mCurrentCalibrationView.setImageResource(R.drawable.calibration_3);
                            break;
                        }
                        case 2: {
                            mCurrentCalibrationView.setImageResource(R.drawable.calibration_4);
                            break;
                        }
                        case 3: {
                            mCurrentCalibrationView.setImageResource(R.drawable.calibration_5);
                            break;
                        }
                        case 0:
                        default: {
                            mCurrentCalibrationView.setImageResource(R.drawable.calibration_2);
                            break;
                        }
                    }

                    if (!mIsStop) {
                        startChangeCalibrationImages();
                    }
                }
            }, Constants.CALIBRATION_IMAGE_DELAY);
        }
    }

    private int saveTestResult() {
        int attempt = 0;
        Realm realm = null;

        try {
            if (!mSaving) {
                mSaving = true;
                final MainActivity activity = getMainActivity();
                realm = DataProvider.getInstance(getActivity()).getRealm();

                if (activity != null) {
                    if (realm != null && !realm.isClosed()) {
                        TestDataTest attentionData = getMainActivity().getTestData().getTestAttention();

                        attentionData.setEndTime(Calendar.getInstance().getTimeInMillis());
                        attentionData.setGazeDetection(mTestData.calculateGazeDetection());
                        attentionData.setGazeOnTheScreen(mTestData.calculateGazeOnScreen());
                        attentionData.setFilePath("");
                        attentionData.setInterrupted(true);

                        EyeTrackingTestSurveyResult result = new EyeTrackingTestSurveyResult();
                        result.setSurveyId(activity.getSurveyId());
                        Gson gson = new Gson();
                        String testValue = gson.toJson(getMainActivity().getTestData());
                        result.setResultFiles(testValue);
                        result.setTestRefId(EyeTrackingTest.TYPE);
                        result.setStartTime(mTestData.getStartTime());
                        result.setEndTime(mTestData.getEndTime());

                        try {
                            realm.beginTransaction();
                            Number currentId = realm.where(EyeTrackingTestSurveyResult.class).max(EyeTrackingTestSurveyResult.FILED_ID);
                            int nextId;

                            if (currentId == null) {
                                nextId = 0;
                            } else {
                                nextId = currentId.intValue() + 1;
                            }

                            result.setId(nextId);
                            realm.copyToRealmOrUpdate(result);
                            realm.commitTransaction();
                            attempt = (int) realm.where(EyeTrackingTestSurveyResult.class)
                                    .equalTo(EyeTrackingTestSurveyResult.FILED_SURVEY_ID, activity.getSurveyId()).count();
                        } catch (Exception e) {
                            TestLog.e(TAG, e);
                            realm.cancelTransaction();
                        }
                    }
                }
            }
        } catch (Exception e) {
            TestLog.e(TAG, e);
        } finally {
            if (realm != null) {
                realm.close();
            }
        }

        return attempt;
    }
}
