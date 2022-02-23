package com.reading.start.tests.test_choice_touching.ui.fragments;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.reading.start.tests.Constants;
import com.reading.start.tests.TestLog;
import com.reading.start.tests.test_choice_touching.R;
import com.reading.start.tests.test_choice_touching.data.DataProvider;
import com.reading.start.tests.test_choice_touching.databinding.TestChoiceTouchingFragmentResultTestBinding;
import com.reading.start.tests.test_choice_touching.domain.entity.ChoiceTouchingTestSurveyResult;
import com.reading.start.tests.test_choice_touching.domain.entity.TestData;
import com.reading.start.tests.test_choice_touching.ui.activities.MainActivity;

import java.util.List;

import io.realm.Realm;

public class ChoiceTouchingTestResultFragment extends BaseFragment {
    private static final String TAG = ChoiceTouchingTestResultFragment.class.getSimpleName();

    private TestChoiceTouchingFragmentResultTestBinding mBinding;

    private int mAttempt = 0;

    public ChoiceTouchingTestResultFragment() {
        // Required empty public constructor
    }

    public void setAttempt(int value) {
        mAttempt = value;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.test_choice_touching_fragment_result_test, container, false);

        TextView title = mBinding.actionBar.findViewById(R.id.text_actionbar);
        title.setText(R.string.test_choice_touching_action_bar_test_result);

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
                    List<ChoiceTouchingTestSurveyResult> results = realm.where(ChoiceTouchingTestSurveyResult.class)
                            .equalTo(ChoiceTouchingTestSurveyResult.FILED_SURVEY_ID, activity.getSurveyId()).findAll();

                    if (results != null && results.size() > 0 && attempt < results.size()) {
                        String value = results.get(attempt).getResultFiles();

                        try {
                            Gson gson = new Gson();
                            TestData data = gson.fromJson(value, TestData.class);

                            if (data != null) {
                                mBinding.leftCount.setText(String.valueOf(data.getGreenClickCount()));
                                mBinding.rightCount.setText(String.valueOf(data.getRedClickCount()));
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
}
