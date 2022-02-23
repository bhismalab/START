package com.reading.start.tests.test_choice_touching.ui.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import com.reading.start.tests.Constants;
import com.reading.start.tests.test_choice_touching.ChoiceTouchingTest;
import com.reading.start.tests.test_choice_touching.Preferences;
import com.reading.start.tests.test_choice_touching.R;
import com.reading.start.tests.test_choice_touching.ui.fragments.ChoiceTouchingTestFragment;
import com.reading.start.tests.test_choice_touching.ui.fragments.ChoiceTouchingTestResultFragment;
import com.reading.start.tests.test_choice_touching.ui.fragments.ChoiceTouchingTestTrainingFragment;

public class MainActivity extends BaseLanguageActivity {

    private int mSurveyId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_choice_touching_activity_main);
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

            if (getIntent().getExtras().containsKey(Constants.SURVEY_DATA)) {
                Bundle data = getIntent().getExtras().getBundle(Constants.SURVEY_DATA);

                if (data != null) {
                    if (data.containsKey(Constants.SURVEY_NUMBER)) {
                        int surveyNumber = data.getInt(Constants.SURVEY_NUMBER, 1);
                        Preferences pref = new Preferences(this);

                        if (pref != null) {
                            if (surveyNumber % 2 == 0) {
                                pref.setIsGreenVideo(false);
                            } else {
                                pref.setIsGreenVideo(true);
                            }
                        }
                    }
                }
            }
        }

        if (showResult) {
            openTestResultFragment(attempt);
        } else {
            //openInstructionTestFragment();
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
        Fragment frag = getFragmentManager().findFragmentById(R.id.content_frame);
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        if (frag != null && frag instanceof ChoiceTouchingTestResultFragment) {
            ft.attach(frag);
        } else {
            frag = new ChoiceTouchingTestResultFragment();
            ft.replace(R.id.content_frame, frag);
        }

        ((ChoiceTouchingTestResultFragment) frag).setAttempt(attempt);
        ft.commit();
    }

    /**
     * Open coloring test screen.
     */
    public void openTestFragment() {
        Fragment frag = getFragmentManager().findFragmentById(R.id.content_frame);
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        if (frag != null && frag instanceof ChoiceTouchingTestFragment) {
            ft.attach(frag);
        } else {
            frag = new ChoiceTouchingTestFragment();
        }

        ft.replace(R.id.content_frame, frag).addToBackStack(ChoiceTouchingTestFragment.class.getSimpleName());
        ft.commit();
    }

    /**
     * Open coloring test screen.
     */
    public void openTrainingTestFragment() {
        Fragment frag = getFragmentManager().findFragmentById(R.id.content_frame);
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        if (frag != null && frag instanceof ChoiceTouchingTestTrainingFragment) {
            ft.attach(frag);
        } else {
            frag = new ChoiceTouchingTestTrainingFragment();
        }

        ft.replace(R.id.content_frame, frag);
        //ft.addToBackStack(ChoiceTouchingTestTrainingFragment.class.getSimpleName());
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

        if (frag != null && frag instanceof ChoiceTouchingTestFragment) {
            // skip back press for ChoiceTouchingTestFragment
        } else {
            super.onBackPressed();
        }
    }

    private void putSurveyResult(Intent intent) {
        if (intent != null) {
            intent.putExtra(Constants.SURVEY_ID, mSurveyId);
            intent.putExtra(Constants.SURVEY_TYPE, ChoiceTouchingTest.TYPE);
        }
    }
}
