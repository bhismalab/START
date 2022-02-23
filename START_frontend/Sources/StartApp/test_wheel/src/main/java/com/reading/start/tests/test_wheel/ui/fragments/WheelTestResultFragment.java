package com.reading.start.tests.test_wheel.ui.fragments;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.gson.Gson;
import com.reading.start.tests.Constants;
import com.reading.start.tests.TestLog;
import com.reading.start.tests.test_wheel.R;
import com.reading.start.tests.test_wheel.data.DataProvider;
import com.reading.start.tests.test_wheel.databinding.TestWheelFragmentResultTestBinding;
import com.reading.start.tests.test_wheel.domain.entity.TestData;
import com.reading.start.tests.test_wheel.domain.entity.WheelTestSurveyResult;
import com.reading.start.tests.test_wheel.ui.activities.MainActivity;

import java.io.File;
import java.util.List;

import io.realm.Realm;

public class WheelTestResultFragment extends BaseFragment {
    private static final String TAG = WheelTestResultFragment.class.getSimpleName();

    private TestWheelFragmentResultTestBinding mBinding;

    private int mAttempt = 0;

    private boolean mInPlaying = false;

    public WheelTestResultFragment() {
        // Required empty public constructor
    }

    public void setAttempt(int value) {
        mAttempt = value;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.test_wheel_fragment_result_test, container, false);

        TextView title = mBinding.actionBar.findViewById(R.id.text_actionbar);

        if (mAttempt == 0) {
            title.setText(R.string.test_wheel_action_bar_test_result_1);
        } else if (mAttempt == 1) {
            title.setText(R.string.test_wheel_action_bar_test_result_2);
        } else if (mAttempt == 2) {
            title.setText(R.string.test_wheel_action_bar_test_result_3);
        }

        mBinding.actionBar.findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        mBinding.actionBar.findViewById(R.id.next_button).setVisibility(View.GONE);
        mBinding.actionBar.findViewById(R.id.home_button).setVisibility(View.GONE);
        mBinding.actionBar.findViewById(R.id.save_button).setVisibility(View.GONE);

        mBinding.actionBar.findViewById(R.id.back_button).setOnClickListener(v -> {
            final Activity activity = getActivity();

            if (activity != null) {
                activity.setResult(Constants.ACTIVITY_RESULT_BACK);
                activity.finish();
            }
        });

        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTestResult(mAttempt);
    }

    private void loadTestResult(int attempt) {
        Realm realm = null;

        try {
            final MainActivity activity = getMainActivity();
            realm = DataProvider.getInstance(getActivity()).getRealm();

            if (activity != null) {
                if (realm != null && !realm.isClosed()) {
                    List<WheelTestSurveyResult> results = realm.where(WheelTestSurveyResult.class)
                            .equalTo(WheelTestSurveyResult.FILED_SURVEY_ID, activity.getSurveyId()).findAll();

                    if (results != null && results.size() > 0 && attempt < results.size()) {
                        String value = results.get(attempt).getResultFiles();

                        try {
                            Gson gson = new Gson();
                            TestData data = gson.fromJson(value, TestData.class);

                            if (data != null) {
                                File videoFile = new File(data.getFilePath());

                                if (videoFile != null && videoFile.exists()) {
                                    Uri videoUri = Uri.fromFile(videoFile);
                                    onPlayVideo(videoUri);
                                }
                            }
                        } catch (Exception e) {
                            TestLog.e(TAG, e);
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
    }

    private void onPlayVideo(Uri video) {
        final VideoView videoView = mBinding.videoView;

        if (!videoView.isPlaying()) {
            mInPlaying = true;
            videoView.setVisibility(View.VISIBLE);
            videoView.setVideoURI(video);
            videoView.setOnCompletionListener(mp -> {
                mInPlaying = false;
                videoView.setVisibility(View.GONE);

                final Activity activity = getActivity();

                if (activity != null) {
                    activity.setResult(Constants.ACTIVITY_RESULT_BACK);
                    activity.finish();
                }
            });
            videoView.start();
        }
    }
}
