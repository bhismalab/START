package com.reading.start.tests.test_eye_tracking.ui.fragments;

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
import com.reading.start.tests.test_eye_tracking.R;
import com.reading.start.tests.test_eye_tracking.data.DataProvider;
import com.reading.start.tests.test_eye_tracking.databinding.TestEyeTrackingFragmentResultTestBinding;
import com.reading.start.tests.test_eye_tracking.domain.entity.EyeTrackingTestSurveyResult;
import com.reading.start.tests.test_eye_tracking.domain.entity.TestData;
import com.reading.start.tests.test_eye_tracking.ui.activities.MainActivity;

import java.io.File;
import java.util.List;

import io.realm.Realm;

public class EyeTrackingTestResultFragment extends BaseFragment {
    private static final String TAG = EyeTrackingTestResultFragment.class.getSimpleName();

    private TestEyeTrackingFragmentResultTestBinding mBinding;

    private int mAttempt = 0;

    private boolean mInPlaying = false;

    public EyeTrackingTestResultFragment() {
        // Required empty public constructor
    }

    public void setAttempt(int value) {
        mAttempt = value;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.test_eye_tracking_fragment_result_test, container, false);

        TextView title = mBinding.actionBar.findViewById(R.id.text_actionbar);

        if (mAttempt == 0) {
            title.setText(R.string.test_eye_tracking_action_bar_test_result_1);
        } else if (mAttempt == 1) {
            title.setText(R.string.test_eye_tracking_action_bar_test_result_2);
        } else if (mAttempt == 2) {
            title.setText(R.string.test_eye_tracking_action_bar_test_result_3);
        }

        mBinding.actionBar.findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        mBinding.actionBar.findViewById(R.id.next_button).setVisibility(View.GONE);
        mBinding.actionBar.findViewById(R.id.home_button).setVisibility(View.GONE);
        mBinding.actionBar.findViewById(R.id.save_button).setVisibility(View.GONE);
        mBinding.actionBar.findViewById(R.id.skip_button).setVisibility(View.GONE);

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
                    List<EyeTrackingTestSurveyResult> results = realm.where(EyeTrackingTestSurveyResult.class)
                            .equalTo(EyeTrackingTestSurveyResult.FILED_SURVEY_ID, activity.getSurveyId()).findAll();

                    if (results != null && results.size() > 0 && attempt < results.size()) {
                        String value = results.get(attempt).getResultFiles();

                        try {
                            Gson gson = new Gson();
                            TestData data = gson.fromJson(value, TestData.class);

                            if (data != null) {
                                File mainFile1 = new File(data.getTestLooking().getFilePath());
                                File mainFile2 = new File(data.getTestAttention().getFilePath());

                                File videoFile1 = mainFile1.exists() ? mainFile1 : new File(mainFile1.getParent(), com.reading.start.sdk.Constants.VIDEO_FILE_PREFIX_TEMP_TEST + mainFile1.getName());
                                File videoFile2 = mainFile2.exists() ? mainFile2 : new File(mainFile2.getParent(), com.reading.start.sdk.Constants.VIDEO_FILE_PREFIX_TEMP_TEST + mainFile2.getName());
                                Uri videoUri1 = null;
                                Uri videoUri2 = null;

                                if (videoFile1 != null && videoFile1.exists()) {
                                    videoUri1 = Uri.fromFile(videoFile1);
                                }

                                if (videoFile2 != null && videoFile2.exists()) {
                                    videoUri2 = Uri.fromFile(videoFile2);
                                }

                                onPlayVideo(videoUri1, videoUri2);
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

    private void onPlayVideo(Uri video1, Uri video2) {
        final VideoView videoView = mBinding.videoView;

        if (!videoView.isPlaying()) {
            mInPlaying = true;

            if (video1 != null) {
                videoView.setVisibility(View.VISIBLE);
                videoView.setVideoURI(video1);
                videoView.setOnCompletionListener(mp -> {
                    videoView.setOnCompletionListener(null);
                    videoView.setVideoURI(video2);

                    videoView.setOnCompletionListener(mp1 -> {
                        mInPlaying = false;
                        videoView.setVisibility(View.GONE);

                        final Activity activity = getActivity();

                        if (activity != null) {
                            activity.setResult(Constants.ACTIVITY_RESULT_BACK);
                            activity.finish();
                        }
                    });

                    videoView.start();
                });
                videoView.start();
            } else if (video2 != null) {
                videoView.setVisibility(View.VISIBLE);
                videoView.setVideoURI(video2);

                videoView.setOnCompletionListener(mp1 -> {
                    mInPlaying = false;
                    videoView.setVisibility(View.GONE);

                    final Activity activity = getActivity();

                    if (activity != null) {
                        activity.setResult(Constants.ACTIVITY_RESULT_BACK);
                        activity.finish();
                    }
                });

                videoView.start();
            } else {
                mInPlaying = false;
            }
        }
    }
}
