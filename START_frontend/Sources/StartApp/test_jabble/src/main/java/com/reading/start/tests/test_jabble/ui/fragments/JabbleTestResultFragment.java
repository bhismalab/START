package com.reading.start.tests.test_jabble.ui.fragments;

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
import com.reading.start.tests.test_jabble.R;
import com.reading.start.tests.test_jabble.data.DataProvider;
import com.reading.start.tests.test_jabble.databinding.TestJabbleFragmentResultTestBinding;
import com.reading.start.tests.test_jabble.domain.entity.JabbleTestSurveyResult;
import com.reading.start.tests.test_jabble.domain.entity.TestData;
import com.reading.start.tests.test_jabble.ui.activities.MainActivity;

import java.util.List;

import io.realm.Realm;

public class JabbleTestResultFragment extends BaseFragment {
    private static final String TAG = JabbleTestResultFragment.class.getSimpleName();

    private TestJabbleFragmentResultTestBinding mBinding;

    private int mAttempt = 0;


    public JabbleTestResultFragment() {
        // Required empty public constructor
    }

    public void setAttempt(int value) {
        mAttempt = value;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.test_jabble_fragment_result_test, container, false);

        TextView title = mBinding.actionBar.findViewById(R.id.text_actionbar);
        title.setText(R.string.test_jabble_action_bar_test_result);

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
                    List<JabbleTestSurveyResult> results = realm.where(JabbleTestSurveyResult.class)
                            .equalTo(JabbleTestSurveyResult.FILED_SURVEY_ID, activity.getSurveyId()).findAll();

                    if (results != null && results.size() > 0 && attempt < results.size()) {
                        String value = results.get(attempt).getResultFiles();

                        try {
                            Gson gson = new Gson();
                            TestData data = gson.fromJson(value, TestData.class);

                            if (data != null) {
                                String valuePropped = String.format(getString(R.string.test_jabble_result_popped_count), String.valueOf(data.getBubblesPopped()));
                                String valueTotal = String.format(getString(R.string.test_jabble_result_total_count), String.valueOf(data.getBubblesTotal()));
                                mBinding.resultPopped.setText(valuePropped);
                                mBinding.resultTotal.setText(valueTotal);
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
