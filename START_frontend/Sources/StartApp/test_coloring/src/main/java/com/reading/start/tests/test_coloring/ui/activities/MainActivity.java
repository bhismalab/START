package com.reading.start.tests.test_coloring.ui.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import com.reading.start.tests.Constants;
import com.reading.start.tests.TestLog;
import com.reading.start.tests.test_coloring.ColoringTest;
import com.reading.start.tests.test_coloring.R;
import com.reading.start.tests.test_coloring.data.DataProvider;
import com.reading.start.tests.test_coloring.domain.entity.ColoringTestSurveyResult;
import com.reading.start.tests.test_coloring.ui.fragments.ColoringTestFragment;
import com.reading.start.tests.test_coloring.ui.fragments.ColoringTestResultFragment;
import com.reading.start.tests.test_coloring.ui.fragments.ColoringTestTrainingFragment;

import io.realm.Realm;

public class MainActivity extends BaseLanguageActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private int mSurveyId = -1;

    private int mSurveyAttempt = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_coloring_activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setResult(Constants.ACTIVITY_RESULT_BACK);

        boolean showResult = false;
        int attempt = 0;

        if (getIntent() != null && getIntent().getExtras() != null) {
            if (getIntent().getExtras().containsKey(Constants.SURVEY_ID)) {
                mSurveyId = getIntent().getExtras().getInt(Constants.SURVEY_ID);
            }

            if (getIntent().getExtras().containsKey(Constants.SURVEY_SHOW_RESULT)) {
                attempt = getIntent().getExtras().getInt(Constants.SURVEY_RESULT_ATTEMPT, 0);
                showResult = true;
            }
        }

        mSurveyAttempt = getSurveyAttempt(mSurveyId);

        if (showResult) {
            openTestResultFragment(attempt);
        } else {
            openTrainingTestFragment();
        }
    }

    public int getSurveyId() {
        return mSurveyId;
    }

    public int getSurveyAttempt() {
        return mSurveyAttempt;
    }

    /**
     * Open test result screen.
     */
    public void openTestResultFragment(int attempt) {
        android.support.v4.app.Fragment frag = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        if (frag != null && frag instanceof ColoringTestResultFragment) {
            ft.attach(frag);
        } else {
            frag = new ColoringTestResultFragment();
            ft.replace(R.id.content_frame, frag);
        }

        ((ColoringTestResultFragment) frag).setAttempt(attempt);
        ft.commit();
    }

    /**
     * Open coloring test screen.
     */
    public void openTestFragment() {
        Fragment frag = getFragmentManager().findFragmentById(R.id.content_frame);
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        if (frag != null && frag instanceof ColoringTestFragment) {
            ft.attach(frag);
        } else {
            frag = new ColoringTestFragment();
        }

        ft.replace(R.id.content_frame, frag).addToBackStack(ColoringTestFragment.class.getSimpleName());
        ft.commit();
    }

    /**
     * Open coloring test screen.
     */
    public void openTrainingTestFragment() {
        Fragment frag = getFragmentManager().findFragmentById(R.id.content_frame);
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        if (frag != null && frag instanceof ColoringTestTrainingFragment) {
            ft.attach(frag);
        } else {
            frag = new ColoringTestTrainingFragment();
        }

        ft.replace(R.id.content_frame, frag);
        //ft.addToBackStack(ColoringTestTrainingFragment.class.getSimpleName());
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

        if (frag != null && frag instanceof ColoringTestFragment) {
            // skip back press for ColoringTestFragment
        } else {
            super.onBackPressed();
        }
    }

    private void putSurveyResult(Intent intent) {
        if (intent != null) {
            intent.putExtra(Constants.SURVEY_ID, mSurveyId);
            intent.putExtra(Constants.SURVEY_TYPE, ColoringTest.TYPE);
        }
    }

    private int getSurveyAttempt(int surveyId) {
        int result = 0;

        Realm realm = null;

        try {
            realm = DataProvider.getInstance(this).getRealm();
            result = (int) realm.where(ColoringTestSurveyResult.class)
                    .equalTo(ColoringTestSurveyResult.FILED_SURVEY_ID, surveyId).count();
        } catch (Exception e) {
            TestLog.e(TAG, e);
        } finally {
            if (realm != null) {
                realm.close();
            }
        }

        return result;
    }
}
