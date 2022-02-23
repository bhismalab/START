package com.reading.start.tests.test_parent.ui.fragments;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.reading.start.tests.Constants;
import com.reading.start.tests.TestLog;
import com.reading.start.tests.TestsProvider;
import com.reading.start.tests.test_parent.ParentTest;
import com.reading.start.tests.test_parent.R;
import com.reading.start.tests.test_parent.data.DataProvider;
import com.reading.start.tests.test_parent.data.ParentTestTrainingModel;
import com.reading.start.tests.test_parent.databinding.TestParentFragmentTestBinding;
import com.reading.start.tests.test_parent.domain.entity.IParentTestItem;
import com.reading.start.tests.test_parent.domain.entity.ParentTestSurveyResult;
import com.reading.start.tests.test_parent.domain.entity.TestData;
import com.reading.start.tests.test_parent.ui.activities.MainActivity;
import com.reading.start.tests.test_parent.ui.adapters.ParentTestAdapter;
import com.reading.start.tests.test_parent.ui.dialogs.DialogCompleteTest;
import com.reading.start.tests.test_parent.utils.Utility;

import java.util.ArrayList;
import java.util.Calendar;

import io.realm.Realm;

public class ParentTestFragment extends BaseFragment {
    private static final String TAG = ParentTestFragment.class.getSimpleName();

    private TestParentFragmentTestBinding mBinding;

    private boolean mInProgress = false;

    private boolean mSaving = false;

    private TestData mTestData = null;

    private ParentTestAdapter mAdapter;

    private RecyclerView mRecyclerView;

    public ParentTestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.test_parent_fragment_test, container, false);

        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.bg_small);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bmp);
        bitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        mBinding.root.setBackground(bitmapDrawable);

        mRecyclerView = mBinding.recyclerView;
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));

        ParentTestTrainingModel model = new ParentTestTrainingModel();

        if (getMainActivity() != null) {
            mTestData = getMainActivity().getTestData();
        } else {
            Size screenSize = Utility.getDisplaySize(getActivity());
            float dpi[] = Utility.getXYDpi(getActivity());
            mTestData = new TestData(screenSize.getWidth(), screenSize.getHeight(), dpi[0], dpi[1]);
        }

        if (mTestData.getItems().size() == 0 && mTestData.getVideoItems().size() == 0) {
            model.fillTestDate(getActivity(), mTestData);
        }

        ArrayList<IParentTestItem> items = new ArrayList<>();
        items.addAll(mTestData.getItems());
        items.addAll(mTestData.getVideoItems());

        mAdapter = new ParentTestAdapter(items, getMainActivity().getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);

        boolean singleParent = true;
        String parent1 = null;
        String parent2 = null;

        if (getArguments().containsKey(Constants.SURVEY_PARENT_1)) {
            parent1 = getArguments().getString(Constants.SURVEY_PARENT_1);
        }

        if (getArguments().containsKey(Constants.SURVEY_PARENT_2)) {
            parent2 = getArguments().getString(Constants.SURVEY_PARENT_2);
            singleParent = false;
        }

        if (singleParent) {
            mBinding.parentSelector.setVisibility(View.GONE);
            mBinding.parent1.setText(parent1);
        } else {
            mBinding.parentSelector.setVisibility(View.VISIBLE);
            mBinding.parent1.setText(parent1);
            mBinding.parent2.setText(parent2);
        }

        mBinding.actionBar.findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        mBinding.actionBar.findViewById(R.id.home_button).setVisibility(View.GONE);
        mBinding.actionBar.findViewById(R.id.save_button).setVisibility(View.VISIBLE);
        mBinding.actionBar.findViewById(R.id.record_button).setVisibility(View.VISIBLE);

        mBinding.actionBar.findViewById(R.id.back_button).setOnClickListener(v -> {
            final MainActivity activity = getMainActivity();

            if (activity != null) {
                activity.finish();
            }
        });

        mBinding.actionBar.findViewById(R.id.record_button).setOnClickListener(v -> {
            if (getMainActivity() != null) {
                getMainActivity().openTestRecordVideoFragment();
            }
        });

        mBinding.actionBar.findViewById(R.id.save_button).setOnClickListener(v -> {
            showEndTestDialog();
        });

        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        startTest();
    }

    @Override
    public void onPause() {
        stopTest();
        super.onPause();
    }

    private void startTest() {
        if (!mInProgress) {
            mInProgress = true;
        }
    }

    private void stopTest() {
        if (mInProgress) {
            mInProgress = false;
        }
    }

    private void showEndTestDialog() {
        MainActivity activity = getMainActivity();

        if (activity != null) {
            int attempt = saveTestResult();
            boolean showNewAttempt = attempt < com.reading.start.tests.Constants.ATTEMPT_COUNT;
            boolean showNextStep = !TestsProvider.getInstance(getActivity()).isLastTest(ParentTest.getInstance());

            String title = mTestData.isInterrupted() ? getResources().getString(R.string.test_paren_dialog_interrupted_title)
                    : getResources().getString(R.string.test_paren_dialog_success_title);
            String message = mTestData.isInterrupted() ? getResources().getString(R.string.test_paren_dialog_interrupted_message)
                    : getResources().getString(R.string.test_paren_dialog_success_message);

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

                if (activity != null && realm != null && !realm.isClosed()) {
                    mTestData.setEndTime(Calendar.getInstance().getTimeInMillis());
                    mTestData.setDeviceId(Utility.getDeviceId(activity));
                    String parent = mBinding.parent2.isChecked() ? mBinding.parent2.getText().toString()
                            : mBinding.parent1.getText().toString();
                    mTestData.setParent(parent);

                    ParentTestSurveyResult result = new ParentTestSurveyResult();
                    result.setSurveyId(activity.getSurveyId());
                    Gson gson = new Gson();
                    String testValue = gson.toJson(mTestData);
                    result.setResultFiles(testValue);
                    result.setTestRefId(ParentTest.TYPE);
                    result.setStartTime(mTestData.getStartTime());
                    result.setEndTime(mTestData.getEndTime());

                    try {
                        realm.beginTransaction();
                        Number currentId = realm.where(ParentTestSurveyResult.class).max(ParentTestSurveyResult.FILED_ID);
                        int nextId;

                        if (currentId == null) {
                            nextId = 0;
                        } else {
                            nextId = currentId.intValue() + 1;
                        }

                        result.setId(nextId);
                        realm.copyToRealmOrUpdate(result);
                        realm.commitTransaction();
                        attempt = (int) realm.where(ParentTestSurveyResult.class)
                                .equalTo(ParentTestSurveyResult.FILED_SURVEY_ID, activity.getSurveyId()).count();
                    } catch (Exception e) {
                        TestLog.e(TAG, e);
                        realm.cancelTransaction();
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
}
