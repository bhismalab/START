package com.reading.start.tests.test_motor_following.ui.fragments;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.reading.start.tests.Constants;
import com.reading.start.tests.TestLog;
import com.reading.start.tests.test_motor_following.R;
import com.reading.start.tests.test_motor_following.data.DataProvider;
import com.reading.start.tests.test_motor_following.databinding.TestMotorFollowingFragmentResultTestBinding;
import com.reading.start.tests.test_motor_following.domain.entity.MotorFollowingTestSurveyAttachments;
import com.reading.start.tests.test_motor_following.domain.entity.MotorFollowingTestSurveyResult;
import com.reading.start.tests.test_motor_following.ui.activities.MainActivity;
import com.reading.start.tests.test_motor_following.ui.adapters.ScreenSlidePagerAdapter;
import com.reading.start.tests.test_motor_following.ui.views.CirclePageIndicator;

import java.util.List;

import io.realm.Realm;

public class MotorFollowingTestResultFragment extends BaseFragmentV4 {
    private static final String TAG = MotorFollowingTestResultFragment.class.getSimpleName();

    private TestMotorFollowingFragmentResultTestBinding mBinding;

    private int mAttempt = 0;

    private ViewPager mPager;

    public MotorFollowingTestResultFragment() {
        // Required empty public constructor
    }

    public void setAttempt(int value) {
        mAttempt = value;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.test_motor_following_fragment_result_test, container, false);

        TextView title = mBinding.actionBar.findViewById(R.id.text_actionbar);

        if (mAttempt == 0) {
            title.setText(R.string.test_motor_following_action_bar_test_result_1);
        } else if (mAttempt == 1) {
            title.setText(R.string.test_motor_following_action_bar_test_result_2);
        } else if (mAttempt == 2) {
            title.setText(R.string.test_motor_following_action_bar_test_result_3);
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
                    List<MotorFollowingTestSurveyResult> results = realm.where(MotorFollowingTestSurveyResult.class)
                            .equalTo(MotorFollowingTestSurveyResult.FILED_SURVEY_ID, activity.getSurveyId()).findAll();

                    if (results != null && results.size() > 0 && attempt < results.size()) {
                        MotorFollowingTestSurveyResult result = results.get(attempt);

                        List<MotorFollowingTestSurveyAttachments> attachments = realm.where(MotorFollowingTestSurveyAttachments.class)
                                .equalTo(MotorFollowingTestSurveyAttachments.FILED_SURVEY_RESULT_ID, result.getId()).findAll();

                        if (attachments != null && attachments.size() > 0) {
                            mPager = getView().findViewById(R.id.pager);
                            ScreenSlidePagerAdapter pagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());

                            for (MotorFollowingTestSurveyAttachments item : attachments) {
                                pagerAdapter.add(item.getAttachmentFile());
                            }

                            mPager.setAdapter(pagerAdapter);
                            CirclePageIndicator indicator = getView().findViewById(R.id.indicator);
                            indicator.setViewPager(mPager);
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
}
