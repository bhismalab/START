package com.reading.start.tests.test_motor_following.ui.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import com.reading.start.tests.Constants;
import com.reading.start.tests.test_motor_following.MotorFollowingTest;
import com.reading.start.tests.test_motor_following.R;
import com.reading.start.tests.test_motor_following.ui.fragments.MotorFollowingTestFragment;
import com.reading.start.tests.test_motor_following.ui.fragments.MotorFollowingTestResultFragment;
import com.reading.start.tests.test_motor_following.ui.fragments.MotorFollowingTestTrainingFragment;

public class MainActivity extends BaseLanguageActivity {

    private int mSurveyId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_motor_following_activity_main);
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
            //openTestInstructionFragment();
            openTrainingTestFragment();
        }
    }

    public int getSurveyId() {
        return mSurveyId;
    }

    /**
     * Open test result screen.
     */
    public void openTestResultFragment(int attempt) {
        android.support.v4.app.Fragment frag = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        if (frag != null && frag instanceof MotorFollowingTestResultFragment) {
            ft.attach(frag);
        } else {
            frag = new MotorFollowingTestResultFragment();
            ft.replace(R.id.content_frame, frag);
        }

        ((MotorFollowingTestResultFragment) frag).setAttempt(attempt);
        ft.commit();
    }

    /**
     * Open coloring test screen.
     */
    public void openTestFragment() {
        Fragment frag = getFragmentManager().findFragmentById(R.id.content_frame);
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        if (frag != null && frag instanceof MotorFollowingTestFragment) {
            ft.attach(frag);
        } else {
            frag = new MotorFollowingTestFragment();
        }

        ft.replace(R.id.content_frame, frag).addToBackStack(MotorFollowingTestFragment.class.getSimpleName());
        ft.commit();
    }

    /**
     * Open coloring test screen.
     */
    public void openTrainingTestFragment() {
        Fragment frag = getFragmentManager().findFragmentById(R.id.content_frame);
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        if (frag != null && frag instanceof MotorFollowingTestTrainingFragment) {
            ft.attach(frag);
        } else {
            frag = new MotorFollowingTestTrainingFragment();
        }

        ft.replace(R.id.content_frame, frag);
        //ft.addToBackStack(MotorFollowingTestTrainingFragment.class.getSimpleName());
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

        if (frag != null && frag instanceof MotorFollowingTestFragment) {
            // skip back press for MotorFollowingTestFragment
        } else {
            super.onBackPressed();
        }
    }

    private void putSurveyResult(Intent intent) {
        if (intent != null) {
            intent.putExtra(Constants.SURVEY_ID, mSurveyId);
            intent.putExtra(Constants.SURVEY_TYPE, MotorFollowingTest.TYPE);
        }
    }
}
