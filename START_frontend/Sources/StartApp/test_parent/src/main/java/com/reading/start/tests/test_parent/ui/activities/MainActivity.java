package com.reading.start.tests.test_parent.ui.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Size;
import android.view.WindowManager;

import com.reading.start.tests.Constants;
import com.reading.start.tests.test_parent.ParentTest;
import com.reading.start.tests.test_parent.R;
import com.reading.start.tests.test_parent.domain.entity.TestData;
import com.reading.start.tests.test_parent.ui.fragments.ParentTestFragment;
import com.reading.start.tests.test_parent.ui.fragments.ParentTestRecordVideoFragment;
import com.reading.start.tests.test_parent.ui.fragments.ParentTestResultFragment;
import com.reading.start.tests.test_parent.ui.fragments.ParentTestTrainingFragment;
import com.reading.start.tests.test_parent.utils.Utility;

public class MainActivity extends BaseLanguageActivity {

    private int mSurveyId = -1;

    private TestData mTestData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_parent_activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setResult(Constants.ACTIVITY_RESULT_BACK);

        boolean showResult = false;
        int attempt = 0;

        String parent1 = null;
        String parent2 = null;

        if (getIntent() != null && getIntent().getExtras() != null) {
            if (getIntent().getExtras().containsKey(Constants.SURVEY_ID)) {
                mSurveyId = getIntent().getExtras().getInt(Constants.SURVEY_ID);
            }

            if (getIntent().getExtras().containsKey(Constants.SURVEY_SHOW_RESULT)) {
                attempt = getIntent().getExtras().getInt(Constants.SURVEY_RESULT_ATTEMPT, 0);
                showResult = true;
            }

            if (getIntent().getExtras().containsKey(Constants.SURVEY_DATA)) {
                Bundle data = getIntent().getExtras().getBundle(Constants.SURVEY_DATA);

                if (data != null) {
                    if (data.containsKey(Constants.SURVEY_PARENT_1)) {
                        parent1 = data.getString(Constants.SURVEY_PARENT_1);
                        parent2 = data.getString(Constants.SURVEY_PARENT_2);
                    }
                }
            }
        }

        if (showResult) {
            openTestResultFragment(attempt);
        } else {
            openTestFragment(parent1, parent2);
        }

        Size screenSize = Utility.getDisplaySize(this);
        float dpi[] = Utility.getXYDpi(this);
        mTestData = new TestData(screenSize.getWidth(), screenSize.getHeight(), dpi[0], dpi[1]);
    }

    public TestData getTestData() {
        return mTestData;
    }

    public int getSurveyId() {
        return mSurveyId;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void openTestRecordVideoFragment() {
        Fragment frag = getFragmentManager().findFragmentById(R.id.content_frame);
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        if (frag != null && frag instanceof ParentTestRecordVideoFragment) {
            ft.attach(frag);
        } else {
            frag = new ParentTestRecordVideoFragment();
            ft.replace(R.id.content_frame, frag);
        }

        ft.addToBackStack(ParentTestRecordVideoFragment.class.getSimpleName());
        ft.commit();
    }

    /**
     * Open test result screen.
     */
    public void openTestResultFragment(int attempt) {
        Fragment frag = getFragmentManager().findFragmentById(R.id.content_frame);
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        if (frag != null && frag instanceof ParentTestResultFragment) {
            ft.attach(frag);
        } else {
            frag = new ParentTestResultFragment();
            ft.replace(R.id.content_frame, frag);
        }

        ((ParentTestResultFragment) frag).setAttempt(attempt);
        ft.commit();
    }

    /**
     * Open coloring test screen.
     */
    public void openTestFragment(String parent1, String parent2) {
        Fragment frag = getFragmentManager().findFragmentById(R.id.content_frame);
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        if (frag != null && frag instanceof ParentTestFragment) {
            ft.attach(frag);
        } else {
            frag = new ParentTestFragment();
        }

        if (frag.getArguments() == null) {
            frag.setArguments(new Bundle());
        }

        if (parent1 != null) {
            frag.getArguments().putString(Constants.SURVEY_PARENT_1, parent1);
        }

        if (parent2 != null) {
            frag.getArguments().putString(Constants.SURVEY_PARENT_2, parent2);
        }

        ft.replace(R.id.content_frame, frag).addToBackStack(ParentTestFragment.class.getSimpleName());
        ft.commit();
    }

    /**
     * Open coloring test screen.
     */
    public void openTrainingTestFragment() {
        Fragment frag = getFragmentManager().findFragmentById(R.id.content_frame);
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        if (frag != null && frag instanceof ParentTestTrainingFragment) {
            ft.attach(frag);
        } else {
            frag = new ParentTestTrainingFragment();
        }

        ft.replace(R.id.content_frame, frag);
        //ft.addToBackStack(ParentTestTrainingFragment.class.getSimpleName());
        ft.commit();
    }

    public void finishAsTryAgain() {
        Intent intent = getIntent();
        putSurveyResult(intent);
        setResult(Constants.ACTIVITY_RESULT_TRY_AGAIN, intent);
        finish();
    }

    public void finishAsStartNext() {
        Intent intent = getIntent();
        putSurveyResult(intent);
        setResult(Constants.ACTIVITY_RESULT_START_NEXT_SURVEY, intent);
        finish();
    }

    public void finishAsOpenChildTest() {
        Intent intent = getIntent();
        putSurveyResult(intent);
        setResult(Constants.ACTIVITY_RESULT_OPEN_CHILD_TESTS, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Fragment frag = getFragmentManager().findFragmentById(R.id.content_frame);

        if (frag != null && (frag instanceof ParentTestFragment || frag instanceof ParentTestRecordVideoFragment)) {
            // skip back press for ParentTestFragment
        } else {
            super.onBackPressed();
        }
    }

    public void onBackPressedSuper() {
        super.onBackPressed();
    }

    private void putSurveyResult(Intent intent) {
        if (intent != null) {
            intent.putExtra(Constants.SURVEY_ID, mSurveyId);
            intent.putExtra(Constants.SURVEY_TYPE, ParentTest.TYPE);
        }
    }
}
