package com.reading.start.tests.test_parent.ui.fragments;

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
import com.reading.start.tests.test_parent.R;
import com.reading.start.tests.test_parent.data.DataProvider;
import com.reading.start.tests.test_parent.databinding.TestParentFragmentResultTestBinding;
import com.reading.start.tests.test_parent.domain.entity.ParentTestSurveyResult;
import com.reading.start.tests.test_parent.domain.entity.TestData;
import com.reading.start.tests.test_parent.ui.activities.MainActivity;

import java.io.File;
import java.util.List;

import io.realm.Realm;

public class ParentTestResultFragment extends BaseFragment {
    private static final String TAG = ParentTestResultFragment.class.getSimpleName();

    private TestParentFragmentResultTestBinding mBinding;

    private int mAttempt = 0;

    public ParentTestResultFragment() {
        // Required empty public constructor
    }

    public void setAttempt(int value) {
        mAttempt = value;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.test_parent_fragment_result_test, container, false);

        TextView title = mBinding.actionBar.findViewById(R.id.text_actionbar);
        title.setText(R.string.test_parent_action_bar_test_result);

        mBinding.actionBar.findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        mBinding.actionBar.findViewById(R.id.home_button).setVisibility(View.GONE);
        mBinding.actionBar.findViewById(R.id.save_button).setVisibility(View.GONE);
        mBinding.actionBar.findViewById(R.id.save_button).setVisibility(View.GONE);
        mBinding.actionBar.findViewById(R.id.record_button).setVisibility(View.GONE);

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

            if (activity != null && realm != null && !realm.isClosed()) {
                List<ParentTestSurveyResult> results = realm.where(ParentTestSurveyResult.class)
                        .equalTo(ParentTestSurveyResult.FILED_SURVEY_ID, activity.getSurveyId()).findAll();

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
            videoView.setVisibility(View.VISIBLE);
            videoView.setVideoURI(video);
            videoView.setOnCompletionListener(mp -> {
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
