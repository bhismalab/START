package com.reading.start.tests.test_eye_tracking.ui.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
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
import com.reading.start.tests.test_eye_tracking.databinding.TestEyeTrackingFragmentTestAttentionBinding;
import com.reading.start.tests.test_eye_tracking.domain.entity.CalibrationCheckItem;
import com.reading.start.tests.test_eye_tracking.domain.entity.EyeTrackingTestSurveyResult;
import com.reading.start.tests.test_eye_tracking.domain.entity.TestDataEyeTracking;
import com.reading.start.tests.test_eye_tracking.domain.entity.TestDataStep;
import com.reading.start.tests.test_eye_tracking.domain.entity.TestDataStimulus;
import com.reading.start.tests.test_eye_tracking.domain.entity.TestDataTest;
import com.reading.start.tests.test_eye_tracking.ui.activities.MainActivity;
import com.reading.start.tests.test_eye_tracking.ui.dialogs.DialogCompleteTest;
import com.reading.start.tests.test_eye_tracking.ui.views.CompleteTriggerView;
import com.reading.start.tests.test_eye_tracking.utils.SoundManager;

import org.opencv.core.Point;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import io.realm.Realm;

public class EyeTrackingAttentionTestFragment extends BaseCameraFragment {
    private static final String TAG = EyeTrackingAttentionTestFragment.class.getSimpleName();

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
                int imagePosition = mBinding.calibrationCheckLeft.getVisibility() == View.VISIBLE ? 0 : 2;
                mCalibrationCheckItems.add(new CalibrationCheckItem(col, imagePosition));
            } else if (mCurrentTestDataStep != null) {
                float x = col;
                float y = row;
                long time = Calendar.getInstance().getTimeInMillis();
                TestDataEyeTracking eyeTrackingItem = new TestDataEyeTracking(x, y, time, isEyeDetected ? 1 : 0, gazeOutside ? 1 : 0);
                TestDataStimulus stimulusItem = new TestDataStimulus(time, mStimulusAppear, mStimulusSide);
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

    private TestEyeTrackingFragmentTestAttentionBinding mBinding;

    private boolean mInProgress = false;

    private boolean mIsStop = false;

    private Handler mHandler = null;

    private EyeTrackingAttentionTestFragment.TestTimer mTestTimer = null;

    private boolean mSaving = false;

    private TestDataTest mTestData = null;

    private String mNextVideoAbsolutePath;

    private Settings mSettings = null;

    private int mAttempt = 0;

    private int mTestAttempt = 0;

    private TestDataStep mCurrentTestDataStep;

    private Random mRandom = new Random();

    private boolean mIsLeftStimulus = false;

    private ImageView mCurrentCalibrationView = null;

    private int mCalibrationImageIndex = 0;

    private boolean mIsCalibrating = false;

    private String mStimulusAppear = "";

    private String mStimulusSide = "";

    private boolean mIsCalibrationCheck = false;

    private int mCalibrationCheckSteps = 0;

    private ArrayList<CalibrationCheckItem> mCalibrationCheckItems = new ArrayList<>();

    private VideoType mVideoType = VideoType.None;

    private boolean mIsSkipped = false;

    private boolean mIsBack = false;

    private boolean mIsStarted = false;

    private ArrayList<Integer> mShowedStimulus = new ArrayList<>();

    private int mCountLeft = 0;

    private int mCountRight = 0;

    public void setSettings(Settings settings) {
        mSettings = settings;
    }

    public EyeTrackingAttentionTestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.test_eye_tracking_fragment_test_attention, container, false);
        mHandler = new Handler();
        TextView title = mBinding.actionBar.findViewById(R.id.text_actionbar);
        title.setText(R.string.test_eye_tracking_assessment_actionbar_2);

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
            showEndTestDialog();
        });

        mBinding.startTest.setOnClickListener(v -> {
            mIsStarted = true;
            mBinding.settingsScreen.setVisibility(View.INVISIBLE);
            mBinding.testScreen.setVisibility(View.VISIBLE);
            showCalibrateArea(CalibrateType.Center);
            StartSdk.getInstance().stop();
            mBinding.texture.setVisibility(View.VISIBLE);
            startCamera();
        });

        mBinding.completeTriggerView.setListener(new CompleteTriggerView.CompleteTriggerViewListener() {
            @Override
            public void onCompleteTrigger() {
                mIsSkipped = true;
                mTestData.setInterrupted(true);
                onTestCompleted();
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
        mBinding.imageTestArea.setVisibility(View.INVISIBLE);
        mTestData = getMainActivity().getTestData().getTestAttention();

        //call only for init sounds
        SoundManager.getInstance(getActivity());

        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        mNextVideoAbsolutePath = getVideoFilePath(getActivity());
        mSettings = SettingsFactory.getReleaseDetect3x1();
        mSettings.getGeneral().setViewFile(mNextVideoAbsolutePath);
        StartSdk.getInstance().start(getActivity(), mBinding.fdActivitySurfaceView, mSettings, mStartSdkListener);
    }

    @Override
    public void onPause() {
        if (!mIsStop && !mIsSkipped && !mIsBack && mIsStarted) {
            mIsSkipped = true;
            mTestData.setInterrupted(true);
            onTestCompleted();
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
                // not need call this
                //showStateArea();
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

    private void showSettingsArea() {
        runOnUiThread(() -> {
            hideAllArea();
            mBinding.settingsScreen.setVisibility(View.VISIBLE);
            mBinding.testScreen.setVisibility(View.INVISIBLE);
        });
    }

    private void showCalibrateArea(final CalibrateType type) {
        if (!mIsStop) {
            runOnUiThread(() -> {
                mBinding.imageTestArea.setVisibility(View.INVISIBLE);

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
                    showStateArea();
                } else {
                    showCalibrationCheckStep();
                }
            }
        }, POST_CALIBRATION_STEP_DELAY);
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
        runOnUiThread(() -> {
            mBinding.imageArea.setVisibility(View.VISIBLE);

            if (Constants.SHOW_STATE_AREA) {
                mBinding.cursorState.setVisibility(View.VISIBLE);
            } else {
                mBinding.cursorState.setVisibility(View.GONE);
            }

            mBinding.cursorCalibration.setVisibility(View.GONE);

            mAttempt = -1;
            mTestAttempt = 0;
            mTestData.setStartTime(Calendar.getInstance().getTimeInMillis());
            mBinding.imageTestArea.setVisibility(View.VISIBLE);
            mCountLeft = 0;
            mCountRight = 0;

            startTestAttempt();
        });
    }

    private synchronized void onTestCompleted() {
        if (!mIsStop) {
            mIsStop = true;
            hideAllStimulus();
            stopTest();
            showEndTestDialog();
        }
    }

    private void startTestAttempt() {
        mInProgress = true;
        mTestAttempt++;
        hideAllStimulus();

        mBinding.imageTestArea.setVisibility(View.VISIBLE);
        mBinding.imageFixationArea.setVisibility(View.GONE);
        mBinding.imageCenter.setVisibility(View.VISIBLE);
        mBinding.imageCenter.setImageResource(R.drawable.stimul_star);
        Animation anim1 = getAnimationSize();

        if (anim1 != null) {
            mBinding.imageCenter.startAnimation(anim1);
        }

        // show star on the center
        mStimulusAppear = Constants.STIMULUS_STAR;
        mStimulusSide = Constants.STIMULUS_SIDE_CENTER;

        TestTimer timer = new TestTimer(() -> {
            if (mIsStop) {
                return;
            }

            hideAllStimulus();

            mBinding.imageTestArea.setVisibility(View.VISIBLE);
            mBinding.imageFixationArea.setVisibility(View.GONE);

            ImageView view = getHorizontalStimulusView(mIsLeftStimulus);
            view.setVisibility(View.VISIBLE);
            view.setImageResource(R.drawable.stimul_circle_black);

            // show black circle
            mStimulusAppear = Constants.STIMULUS_CIRCLE_BLACK;

            if (mIsLeftStimulus) {
                mStimulusSide = Constants.STIMULUS_SIDE_LEFT;
            } else {
                mStimulusSide = Constants.STIMULUS_SIDE_RIGHT;
            }

            mHandler.postDelayed(() -> {
                if (mIsStop) {
                    return;
                }

                hideAllStimulus();
                mStimulusAppear = "";
                mStimulusSide = "";

                mHandler.postDelayed(() -> {
                    if (mIsStop) {
                        return;
                    }

                    hideAllStimulus();
                    ImageView viewRed = null;

                    if (mIsLeftStimulus) {
                        viewRed = mBinding.imageCenterRight;
                    } else {
                        viewRed = mBinding.imageCenterLeft;
                    }

                    viewRed.setVisibility(View.VISIBLE);
                    viewRed.setImageResource(R.drawable.stimul_circle_red);

                    // show red circle
                    mStimulusAppear = Constants.STIMULUS_CIRCLE_RED;

                    if (mIsLeftStimulus) {
                        mStimulusSide = Constants.STIMULUS_SIDE_LEFT;
                    } else {
                        mStimulusSide = Constants.STIMULUS_SIDE_RIGHT;
                    }

                    mHandler.postDelayed(() -> {
                        if (mIsStop) {
                            return;
                        }

                        hideAllStimulus();
                        ImageView viewRed1 = null;

                        if (mIsLeftStimulus) {
                            viewRed1 = mBinding.imageCenterRight;
                        } else {
                            viewRed1 = mBinding.imageCenterLeft;
                        }

                        viewRed1.setVisibility(View.VISIBLE);
                        viewRed1.setImageResource(getRandomStimulusImage());
                        Animation anim2 = getAnimationSize();

                        if (anim2 != null) {
                            viewRed1.startAnimation(anim2);
                        }

                        mHandler.postDelayed(() -> {
                            if (mIsStop) {
                                return;
                            }

                            hideAllStimulus();

                            if (!mIsStop && mTestAttempt < Constants.TEST_ASSESSMENT_TEST_ATTEMPT) {
                                mIsLeftStimulus = !mIsLeftStimulus;
                                startTestAttempt();
                            } else {
                                mIsLeftStimulus = getRandomIsLeftStimulus();
                                mAttempt = 0;
                                mTestData.setStartTime(Calendar.getInstance().getTimeInMillis());
                                startAttempt();
                            }
                        }, 2000);
                    }, 1000);
                }, 600);
            }, 200);
        });

        mHandler.postDelayed(timer, 2000);
    }

    private void startAttempt() {
        if (mIsStop) {
            return;
        }

        mAttempt++;
        hideAllStimulus();

        mBinding.imageTestArea.setVisibility(View.VISIBLE);
        mBinding.imageFixationArea.setVisibility(View.GONE);
        mBinding.imageCenter.setVisibility(View.VISIBLE);
        mBinding.imageCenter.setImageResource(R.drawable.stimul_star);
        Animation anim1 = getAnimationSize();

        if (anim1 != null) {
            mBinding.imageCenter.startAnimation(anim1);
        }

        // Fixation stimulus
        mCurrentTestDataStep = new TestDataStep(Constants.FIXATION_STIMULUS + " - " + String.valueOf(mAttempt));
        mTestData.getItemsStimulus().add(mCurrentTestDataStep);
        // show star on the center
        mStimulusAppear = Constants.STIMULUS_STAR;
        mStimulusSide = Constants.STIMULUS_SIDE_CENTER;
        mCurrentTestDataStep.getItems().add(new TestDataStimulus(Calendar.getInstance().getTimeInMillis() - mTestData.getStartTime(), mStimulusAppear, mStimulusSide));

        TestTimer timer = new TestTimer(() -> {
            if (mIsStop) {
                return;
            }

            hideAllStimulus();

            mBinding.imageTestArea.setVisibility(View.VISIBLE);
            mBinding.imageFixationArea.setVisibility(View.GONE);

            ImageView view = getHorizontalStimulusView(mIsLeftStimulus);
            view.setVisibility(View.VISIBLE);
            view.setImageResource(R.drawable.stimul_circle_black);

            // cue stimulus
            mCurrentTestDataStep = new TestDataStep(Constants.CUE_STIMULUS + " - " + String.valueOf(mAttempt));
            mTestData.getItemsStimulus().add(mCurrentTestDataStep);

            // show black circle
            mStimulusAppear = Constants.STIMULUS_CIRCLE_BLACK;

            if (mIsLeftStimulus) {
                mStimulusSide = Constants.STIMULUS_SIDE_LEFT;
            } else {
                mStimulusSide = Constants.STIMULUS_SIDE_RIGHT;
            }

            mCurrentTestDataStep.getItems().add(new TestDataStimulus(Calendar.getInstance().getTimeInMillis() - mTestData.getStartTime(), mStimulusAppear, mStimulusSide));

            mHandler.postDelayed(() -> {
                if (mIsStop) {
                    return;
                }

                hideAllStimulus();

                // delay
                mCurrentTestDataStep = new TestDataStep(Constants.DELAY + " - " + String.valueOf(mAttempt));
                mTestData.getItemsStimulus().add(mCurrentTestDataStep);

                mStimulusAppear = "";
                mStimulusSide = "";
                mCurrentTestDataStep.getItems().add(new TestDataStimulus(Calendar.getInstance().getTimeInMillis() - mTestData.getStartTime(), mStimulusAppear, mStimulusSide));

                mHandler.postDelayed(() -> {
                    if (mIsStop) {
                        return;
                    }

                    hideAllStimulus();

                    ImageView viewRed = null;

                    if (mIsLeftStimulus) {
                        viewRed = mBinding.imageCenterRight;
                    } else {
                        viewRed = mBinding.imageCenterLeft;
                    }

                    viewRed.setVisibility(View.VISIBLE);
                    viewRed.setImageResource(R.drawable.stimul_circle_red);

                    // target stimulus
                    mCurrentTestDataStep = new TestDataStep(Constants.TARGET_STIMULUS + " - " + String.valueOf(mAttempt));
                    mTestData.getItemsStimulus().add(mCurrentTestDataStep);

                    // show red circle
                    mStimulusAppear = Constants.STIMULUS_CIRCLE_RED;

                    if (mIsLeftStimulus) {
                        mStimulusSide = Constants.STIMULUS_SIDE_LEFT;
                    } else {
                        mStimulusSide = Constants.STIMULUS_SIDE_RIGHT;
                    }

                    mCurrentTestDataStep.getItems().add(new TestDataStimulus(Calendar.getInstance().getTimeInMillis() - mTestData.getStartTime(), mStimulusAppear, mStimulusSide));

                    mHandler.postDelayed(() -> {
                        if (mIsStop) {
                            return;
                        }

                        hideAllStimulus();
                        ImageView viewRed1 = null;

                        if (mIsLeftStimulus) {
                            viewRed1 = mBinding.imageCenterRight;
                        } else {
                            viewRed1 = mBinding.imageCenterLeft;
                        }

                        viewRed1.setVisibility(View.VISIBLE);
                        viewRed1.setImageResource(getRandomStimulusImage());
                        Animation anim2 = getAnimationSize();

                        if (anim2 != null) {
                            viewRed1.startAnimation(anim2);
                        }

                        // reward stimulus
                        mCurrentTestDataStep = new TestDataStep(Constants.REWARDS_STIMULUS + " - " + String.valueOf(mAttempt));
                        mTestData.getItemsStimulus().add(mCurrentTestDataStep);

                        // show animal
                        mCurrentTestDataStep.getItems().add(new TestDataStimulus(Calendar.getInstance().getTimeInMillis() - mTestData.getStartTime(), mStimulusAppear, mStimulusSide));

                        mHandler.postDelayed(() -> {
                            if (mIsStop) {
                                return;
                            }

                            hideAllStimulus();

                            if (!mIsStop && mAttempt <= Constants.TEST_ASSESSMENT_ATTEMPT + 1) {
                                mIsLeftStimulus = getRandomIsLeftStimulus();
                                startAttempt();
                            } else {
                                onTestCompleted();
                            }
                        }, 2000);
                    }, 1000);
                }, 600);
            }, 200);
        });

        mHandler.postDelayed(timer, 2000);
    }

    private boolean getRandomIsLeftStimulus() {
        boolean result = false;
        boolean find = false;
        int iterationCheck = 0;

        while (!find && iterationCheck < 20) {
            iterationCheck++;
            boolean value = mRandom.nextBoolean();

            // left side
            if (value) {
                if (mCountLeft <= Constants.TEST_ASSESSMENT_ATTEMPT / 2) {
                    mCountLeft++;
                    result = value;
                    find = true;
                } else if (mCountRight <= Constants.TEST_ASSESSMENT_ATTEMPT / 2) {
                    mCountRight++;
                    result = !value;
                    find = true;
                } else {
                    mCountLeft = 0;
                    mCountRight = 0;
                }
            }
            // right side
            else {
                if (mCountRight <= Constants.TEST_ASSESSMENT_ATTEMPT / 2) {
                    mCountRight++;
                    result = value;
                    find = true;
                } else if (mCountLeft <= Constants.TEST_ASSESSMENT_ATTEMPT / 2) {
                    mCountLeft++;
                    result = !value;
                    find = true;
                } else {
                    mCountLeft = 0;
                    mCountRight = 0;
                }
            }
        }

        return result;
    }

    private int getRandomStimulusImage() {
        int result = -1;
        boolean find = false;
        int iterationCheck = 0;

        while (!find && iterationCheck < 20) {
            iterationCheck++;
            int index = mRandom.nextInt(4);

            if (!mShowedStimulus.contains(index)) {
                switch (index) {
                    case 0: {
                        result = R.drawable.calibration_2;
                        runOnUiThread(() -> SoundManager.getInstance(getActivity()).playSoundMonkey());
                        mStimulusAppear = Constants.STIMULUS_MONKEY;
                        break;
                    }
                    case 1: {
                        result = R.drawable.calibration_3;
                        runOnUiThread(() -> SoundManager.getInstance(getActivity()).playSoundGiraffe());
                        mStimulusAppear = Constants.STIMULUS_GIRAFFE;
                        break;
                    }
                    case 2: {
                        result = R.drawable.calibration_4;
                        runOnUiThread(() -> SoundManager.getInstance(getActivity()).playSoundLion());
                        mStimulusAppear = Constants.STIMULUS_LION;
                        break;
                    }
                    case 3: {
                        result = R.drawable.calibration_5;
                        runOnUiThread(() -> SoundManager.getInstance(getActivity()).playSoundElephant());
                        mStimulusAppear = Constants.STIMULUS_ELEPHANT;
                        break;
                    }
                    default: {
                        result = R.drawable.calibration_5;
                        runOnUiThread(() -> SoundManager.getInstance(getActivity()).playSoundElephant());
                        mStimulusAppear = Constants.STIMULUS_ELEPHANT;
                        break;
                    }
                }

                if (result != -1) {
                    find = true;
                    mShowedStimulus.add(index);
                }
            } else if (mShowedStimulus.size() >= 4) {
                mShowedStimulus.clear();
            }
        }

        return result;
    }

    private ImageView getHorizontalStimulusView(boolean isLeftStimulus) {
        return isLeftStimulus ? mBinding.imageCenterLeft : mBinding.imageCenterRight;
    }

    private void hideAllStimulus() {
        mBinding.imageCenter.setAnimation(null);
        mBinding.imageRightTop.setAnimation(null);
        mBinding.imageLeftTop.setAnimation(null);
        mBinding.imageRightBottom.setAnimation(null);
        mBinding.imageLeftBottom.setAnimation(null);
        mBinding.imageCenterLeft.setAnimation(null);
        mBinding.imageCenterRight.setAnimation(null);

        mBinding.imageCenter.setVisibility(View.GONE);
        mBinding.imageRightTop.setVisibility(View.GONE);
        mBinding.imageLeftTop.setVisibility(View.GONE);
        mBinding.imageRightBottom.setVisibility(View.GONE);
        mBinding.imageLeftBottom.setVisibility(View.GONE);
        mBinding.imageCenterLeft.setVisibility(View.GONE);
        mBinding.imageCenterRight.setVisibility(View.GONE);
    }

    private Animation getAnimationSize() {
        Animation anim = null;

        try {
            if (getActivity() != null && !isDetached() && !isRemoving())
                anim = AnimationUtils.loadAnimation(getActivity(), R.anim.pulse);
        } catch (Exception e) {
            TestLog.e(TAG, e);
        }

        return anim;
    }

    private void stopTest() {
        runOnUiThread(() -> {
            if (mBinding.imageCenter.getAnimation() != null) {
                mBinding.imageCenter.setAnimation(null);
            }

            if (mBinding.imageRightTop.getAnimation() != null) {
                mBinding.imageRightTop.setAnimation(null);
            }
        });

        if (mInProgress) {
            mInProgress = false;
            mIsStop = true;

            if (mTestTimer != null) {
                mTestTimer.needProcess = false;
                mTestTimer = null;
            }
        }
    }

    private void showEndTestDialog() {
        MainActivity activity = getMainActivity();

        if (activity != null) {
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
                        mTestData.setEndTime(Calendar.getInstance().getTimeInMillis());
                        mTestData.setGazeDetection(mTestData.calculateGazeDetection());
                        mTestData.setGazeOnTheScreen(mTestData.calculateGazeOnScreen());

                        if (mIsSkipped) {
                            mTestData.setFilePath("");
                            mTestData.setInterrupted(true);
                        } else {
                            mTestData.setFilePath(mNextVideoAbsolutePath);
                        }

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
        final MainActivity activity = getMainActivity();

        if (activity != null && context != null) {
            String fileName = String.valueOf(activity.getSurveyId());
            fileName += "_" + String.valueOf(Calendar.getInstance().getTimeInMillis());
            fileName += Constants.VIDEO_FILE_NAME_ATTENTION_TEST + "." + Constants.VIDEO_FILE_EXT;
            result = new File(context.getFilesDir(), fileName).toString();
        }

        return result;
    }

    private void startChangeCalibrationImages() {
        if (mIsCalibrating) {
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

                    startChangeCalibrationImages();
                }
            }, Constants.CALIBRATION_IMAGE_DELAY);
        }
    }
}
