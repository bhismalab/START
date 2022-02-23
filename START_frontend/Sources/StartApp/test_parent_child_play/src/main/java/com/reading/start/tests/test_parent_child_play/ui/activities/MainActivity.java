package com.reading.start.tests.test_parent_child_play.ui.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import com.reading.start.tests.Constants;
import com.reading.start.tests.test_parent_child_play.ParentChildPlayTest;
import com.reading.start.tests.test_parent_child_play.R;
import com.reading.start.tests.test_parent_child_play.ui.fragments.ParentChildPlayTestFragment;
import com.reading.start.tests.test_parent_child_play.ui.fragments.ParentChildPlayTestInstructionFragment;
import com.reading.start.tests.test_parent_child_play.ui.fragments.ParentChildPlayTestResultFragment;

public class MainActivity extends BaseLanguageActivity {

    private int mSurveyId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_parent_child_play_activity_main);
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
            openTestInstructionFragment();
            //openTestFragment();
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

        if (frag != null && frag instanceof ParentChildPlayTestResultFragment) {
            ft.attach(frag);
        } else {
            frag = new ParentChildPlayTestResultFragment();
            ft.replace(R.id.content_frame, frag);
        }

        ((ParentChildPlayTestResultFragment) frag).setAttempt(attempt);
        ft.commit();
    }

    /**
     * Open coloring test screen.
     */
    public void openTestFragment() {
        Fragment frag = getFragmentManager().findFragmentById(R.id.content_frame);
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        if (frag != null && frag instanceof ParentChildPlayTestFragment) {
            ft.attach(frag);
        } else {
            frag = new ParentChildPlayTestFragment();
        }

        ft.replace(R.id.content_frame, frag);
        //ft.addToBackStack(ParentChildPlayTestFragment.class.getSimpleName());
        ft.commit();
    }

    public void openTestInstructionFragment() {
        Fragment frag = getFragmentManager().findFragmentById(R.id.content_frame);
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        if (frag != null && frag instanceof ParentChildPlayTestInstructionFragment) {
            ft.attach(frag);
        } else {
            frag = new ParentChildPlayTestInstructionFragment();
            ft.replace(R.id.content_frame, frag);
        }

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

        if (frag != null && frag instanceof ParentChildPlayTestFragment) {
            // skip back press for ParentChildPlayTestFragment
        } else {
            super.onBackPressed();
        }
    }

    public void onBackPressedSupper() {
        super.onBackPressed();
    }

    private void putSurveyResult(Intent intent) {
        if (intent != null) {
            intent.putExtra(Constants.SURVEY_ID, mSurveyId);
            intent.putExtra(Constants.SURVEY_TYPE, ParentChildPlayTest.TYPE);
        }
    }
}
