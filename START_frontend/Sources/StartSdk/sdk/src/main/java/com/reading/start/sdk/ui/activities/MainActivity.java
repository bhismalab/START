package com.reading.start.sdk.ui.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.reading.start.sdk.R;
import com.reading.start.sdk.core.Settings;
import com.reading.start.sdk.core.SettingsFactory;
import com.reading.start.sdk.databinding.SdkActivityMainBinding;
import com.reading.start.sdk.ui.fragments.CameraFragment;
import com.reading.start.sdk.ui.fragments.CameraPostprocessingFragment;
import com.reading.start.sdk.ui.fragments.SelectModeFragment;
import com.reading.start.sdk.ui.fragments.SettingsFragment;
import com.reading.start.sdk.ui.fragments.SettingsPostprocessingFragment;
import com.reading.start.sdk.ui.fragments.TestFragmentFragment;
import com.reading.start.sdk.utils.CameraUtils;
import com.reading.start.sdk.utils.SettingsHelper;

public class MainActivity extends BasePermissionActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private SdkActivityMainBinding mBinding;

    private Settings mCameraSettings = null;

    private Settings mCameraPostProcessingSettings = null;

    public MainActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mBinding = DataBindingUtil.setContentView(this, R.layout.sdk_activity_main);

        mCameraSettings = SettingsHelper.loadSetting();
        mCameraPostProcessingSettings = SettingsHelper.loadPostprocessingSetting();

        if (mCameraSettings == null) {
            mCameraSettings = SettingsFactory.getDebugDetect();
            SettingsHelper.saveSetting(mCameraSettings);
        }

        if (mCameraPostProcessingSettings == null) {
            mCameraPostProcessingSettings = SettingsFactory.getDebugDetectPostprocessing();
            SettingsHelper.savePostprocessingSetting(mCameraPostProcessingSettings);
        }

        // check camera id settings
        if (mCameraSettings.getGeneral().getCameraIndex() == -1) {
            mCameraSettings.getGeneral().setCameraIndex(CameraUtils.getCameraIndex());
            SettingsHelper.saveSetting(mCameraSettings);
        }

        if (mCameraPostProcessingSettings.getGeneral().getCameraIndex() == -1) {
            mCameraPostProcessingSettings.getGeneral().setCameraIndex(CameraUtils.getCameraIndex());
            SettingsHelper.savePostprocessingSetting(mCameraPostProcessingSettings);
        }

        openSelectModeFragment();
    }

    @Override
    protected void onAllowPermissionOk() {
    }

    @Override
    protected boolean onPermissionDenied() {
        return false;
    }

    public Settings getCameraSettings() {
        return mCameraSettings;
    }

    public void setCameraSettings(Settings cameraSettings) {
        mCameraSettings = cameraSettings;
    }

    public Settings getCameraPostProcessingSettings() {
        return mCameraPostProcessingSettings;
    }

    public void setCameraPostProcessingSettings(Settings cameraPostProcessingSettings) {
        mCameraPostProcessingSettings = cameraPostProcessingSettings;
    }

    public void openSelectModeFragment() {
        Fragment frag = getFragmentManager().findFragmentById(R.id.content_frame);
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        if (frag != null && frag instanceof SelectModeFragment) {
            ft.attach(frag);
        } else {
            frag = new SelectModeFragment();
            ft.replace(R.id.content_frame, frag);
        }

        ft.commit();
    }

    public void openCameraFragment() {
        Fragment frag = getFragmentManager().findFragmentById(R.id.content_frame);
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        if (frag != null && frag instanceof CameraFragment) {
            ft.attach(frag);
        } else {
            frag = new CameraFragment();
            ft.replace(R.id.content_frame, frag);
        }

        ft.addToBackStack(CameraFragment.class.getSimpleName());
        ft.commit();
    }

    public void openCameraPostprocessingFragment() {
        Fragment frag = getFragmentManager().findFragmentById(R.id.content_frame);
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        if (frag != null && frag instanceof CameraPostprocessingFragment) {
            ft.attach(frag);
        } else {
            frag = new CameraPostprocessingFragment();
            ft.replace(R.id.content_frame, frag);
        }

        ft.addToBackStack(CameraPostprocessingFragment.class.getSimpleName());
        ft.commit();
    }

    public void openTestFragment(Settings settings) {
        Fragment frag = getFragmentManager().findFragmentById(R.id.content_frame);
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        if (frag != null && frag instanceof TestFragmentFragment) {
            ft.attach(frag);
        } else {
            TestFragmentFragment fragTest = new TestFragmentFragment();
            fragTest.setSettings(settings);
            ft.replace(R.id.content_frame, fragTest);
        }

        ft.addToBackStack(TestFragmentFragment.class.getSimpleName());
        ft.commit();
    }

    public void openSettingsFragment() {
        Fragment frag = getFragmentManager().findFragmentById(R.id.content_frame);
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        if (frag != null && frag instanceof SettingsFragment) {
            ft.attach(frag);
        } else {
            frag = new SettingsFragment();
            ft.replace(R.id.content_frame, frag);
        }

        ft.addToBackStack(SettingsFragment.class.getSimpleName());
        ft.commit();
    }

    public void openPostprocessingSettingsFragment() {
        Fragment frag = getFragmentManager().findFragmentById(R.id.content_frame);
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        if (frag != null && frag instanceof SettingsPostprocessingFragment) {
            ft.attach(frag);
        } else {
            frag = new SettingsPostprocessingFragment();
            ft.replace(R.id.content_frame, frag);
        }

        ft.addToBackStack(SettingsPostprocessingFragment.class.getSimpleName());
        ft.commit();
    }
}