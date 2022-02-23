package com.reading.start.sdk.ui.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.reading.start.sdk.R;
import com.reading.start.sdk.core.Settings;
import com.reading.start.sdk.core.SettingsFactory;
import com.reading.start.sdk.databinding.SdkFragmentSelectModeBinding;
import com.reading.start.sdk.ui.activities.MainActivity;

public class SelectModeFragment extends BaseFragment {

    private SdkFragmentSelectModeBinding mBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.sdk_fragment_select_mode, container, false);
        setHasOptionsMenu(false);

        mBinding.modeDebug.setOnClickListener(v -> {
            final MainActivity activity = getMainActivity();

            if (activity != null) {
                activity.openCameraFragment();
            }
        });

        mBinding.modeDebugPostprocessing.setOnClickListener(v -> {
            final MainActivity activity = getMainActivity();

            if (activity != null) {
                activity.openCameraPostprocessingFragment();
            }
        });

        mBinding.mode2x1.setOnClickListener(v -> {
            final MainActivity activity = getMainActivity();

            if (activity != null) {
                Settings settings = SettingsFactory.getReleaseDetect2x1();
                activity.openTestFragment(settings);
            }
        });

        mBinding.mode3x1.setOnClickListener(v -> {
            final MainActivity activity = getMainActivity();

            if (activity != null) {
                Settings settings = SettingsFactory.getReleaseDetect3x1();
                activity.openTestFragment(settings);
            }
        });

        mBinding.mode2x2.setOnClickListener(v -> {
            final MainActivity activity = getMainActivity();

            if (activity != null) {
                Settings settings = SettingsFactory.getReleaseDetect2x2();
                activity.openTestFragment(settings);
            }
        });

        return mBinding.getRoot();
    }
}
