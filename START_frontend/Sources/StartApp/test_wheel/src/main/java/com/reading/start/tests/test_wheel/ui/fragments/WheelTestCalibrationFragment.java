package com.reading.start.tests.test_wheel.ui.fragments;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.reading.start.sdk.core.CalibrateType;
import com.reading.start.sdk.core.DetectMode;
import com.reading.start.sdk.core.Settings;
import com.reading.start.sdk.core.SettingsFactory;
import com.reading.start.sdk.core.StartSdk;
import com.reading.start.sdk.core.StartSdkListener;
import com.reading.start.sdk.core.StartSdkState;
import com.reading.start.tests.test_wheel.R;
import com.reading.start.tests.test_wheel.databinding.TestWheelFragmentTestCalibrationBinding;
import com.reading.start.tests.test_wheel.ui.activities.MainActivity;

import org.opencv.core.Point;

public class WheelTestCalibrationFragment extends BaseCameraFragment {
    private static final String TAG = WheelTestCalibrationFragment.class.getSimpleName();

    private StartSdkListener mStartSdkListener = new StartSdkListener() {
        @Override
        public void onCalibrateStart(long timeStamp) {
        }

        @Override
        public void onCalibrateChanged(CalibrateType type, long timeStamp) {
        }

        @Override
        public void onCalibrateCompleted(boolean success, long timeStamp) {
        }

        @Override
        public void onCalibrateStopped(long timeStamp) {
        }

        @Override
        public void onPositionUpdated(Point right, Point left, long timeStamp) {
        }

        @Override
        public void onDetectedSelectedArea(final DetectMode mode, final int col, final int row, long timeStamp) {
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
                    if (state.isHeadDetectedOk() && state.isHeadCloseOk() && state.isHeadFarOk()) {
                        mBinding.startTest.setEnabled(true);
                    } else {
                        mBinding.startTest.setEnabled(false);
                    }
                }
            });
        }

        @Override
        public void onFrameProcessed(DetectMode mode, int col, int row, boolean isEyeDetected,
                                     boolean gazeOutside, long timeStamp) {
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

    private TestWheelFragmentTestCalibrationBinding mBinding;

    private Settings mSettings = null;

    public void setSettings(Settings settings) {
        mSettings = settings;
    }

    public WheelTestCalibrationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.test_wheel_fragment_test_calibration, container, false);

        TextView title = mBinding.actionBar.findViewById(R.id.text_actionbar);

        mBinding.actionBar.findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        mBinding.actionBar.findViewById(R.id.next_button).setVisibility(View.GONE);
        mBinding.actionBar.findViewById(R.id.home_button).setVisibility(View.GONE);
        mBinding.actionBar.findViewById(R.id.save_button).setVisibility(View.GONE);

        mBinding.actionBar.findViewById(R.id.back_button).setOnClickListener(v -> {
            final Activity activity = getActivity();

            if (activity != null) {
                activity.onBackPressed();
            }
        });

        mBinding.startTest.setOnClickListener(v -> {
            final MainActivity activity = (MainActivity) getActivity();

            if (activity != null) {
                activity.openTrainingTestFragment();
            }
        });

        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        mSettings = SettingsFactory.getReleaseDetect2x1();
        StartSdk.getInstance().start(getActivity(), mBinding.fdActivitySurfaceView, mSettings, mStartSdkListener);
    }

    @Override
    public void onPause() {
        StartSdk.getInstance().stop();
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
    }

    @Override
    protected void onVideoStopRecoding() {
    }
}
