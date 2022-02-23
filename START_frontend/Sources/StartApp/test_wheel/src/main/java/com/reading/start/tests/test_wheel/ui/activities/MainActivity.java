package com.reading.start.tests.test_wheel.ui.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import com.reading.start.tests.Constants;
import com.reading.start.tests.test_wheel.R;
import com.reading.start.tests.test_wheel.WheelTest;
import com.reading.start.tests.test_wheel.ui.fragments.WheelTestCalibrationFragment;
import com.reading.start.tests.test_wheel.ui.fragments.WheelTestFragment;
import com.reading.start.tests.test_wheel.ui.fragments.WheelTestResultFragment;
import com.reading.start.tests.test_wheel.ui.fragments.WheelTestTrainingFragment;

public class MainActivity extends BaseLanguageActivity {

    private int mSurveyId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_wheel_activity_main);
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

        if (showResult) {
            openTestResultFragment(attempt);
        } else {
            openCalibrationTestFragment();
        }
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

    /**
     * Open test result screen.
     */
    public void openTestResultFragment(int attempt) {
        Fragment frag = getFragmentManager().findFragmentById(R.id.content_frame);
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        if (frag != null && frag instanceof WheelTestResultFragment) {
            ft.attach(frag);
        } else {
            frag = new WheelTestResultFragment();
            ft.replace(R.id.content_frame, frag);
        }

        ((WheelTestResultFragment) frag).setAttempt(attempt);
        ft.commit();
    }

    /**
     * Open coloring test screen.
     */
    public void openTestFragment() {
        Fragment frag = getFragmentManager().findFragmentById(R.id.content_frame);
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        if (frag != null && frag instanceof WheelTestFragment) {
            ft.attach(frag);
        } else {
            frag = new WheelTestFragment();
        }

        ft.replace(R.id.content_frame, frag).addToBackStack(WheelTestFragment.class.getSimpleName());
        ft.commit();
    }

    /**
     * Open coloring test screen.
     */
    public void openTrainingTestFragment() {
        Fragment frag = getFragmentManager().findFragmentById(R.id.content_frame);
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        if (frag != null && frag instanceof WheelTestTrainingFragment) {
            ft.attach(frag);
        } else {
            frag = new WheelTestTrainingFragment();
        }

        ft.replace(R.id.content_frame, frag);
        ft.addToBackStack(WheelTestTrainingFragment.class.getSimpleName());
        ft.commit();
    }

    public void openCalibrationTestFragment() {
        Fragment frag = getFragmentManager().findFragmentById(R.id.content_frame);
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        if (frag != null && frag instanceof WheelTestCalibrationFragment) {
            ft.attach(frag);
        } else {
            frag = new WheelTestCalibrationFragment();
        }

        ft.replace(R.id.content_frame, frag);
        //ft.addToBackStack(WheelTestCalibrationFragment.class.getSimpleName());
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

        if (frag != null && frag instanceof WheelTestFragment) {
            // skip back press for WheelTestFragment
        } else {
            super.onBackPressed();
        }
    }

    private void putSurveyResult(Intent intent) {
        if (intent != null) {
            intent.putExtra(Constants.SURVEY_ID, mSurveyId);
            intent.putExtra(Constants.SURVEY_TYPE, WheelTest.TYPE);
        }
    }
}
