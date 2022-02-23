package com.reading.start.tests.test_eye_tracking.ui.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Size;
import android.view.WindowManager;

import com.crashlytics.android.Crashlytics;
import com.reading.start.tests.Constants;
import com.reading.start.tests.test_eye_tracking.EyeTrackingTest;
import com.reading.start.tests.test_eye_tracking.R;
import com.reading.start.tests.test_eye_tracking.domain.entity.TestData;
import com.reading.start.tests.test_eye_tracking.ui.fragments.EyeTrackingAttentionTestFragment;
import com.reading.start.tests.test_eye_tracking.ui.fragments.EyeTrackingLookingTestFragment;
import com.reading.start.tests.test_eye_tracking.ui.fragments.EyeTrackingTestResultFragment;
import com.reading.start.tests.test_eye_tracking.utils.Utility;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends BaseLanguageActivity {

    private int mSurveyId = -1;

    private TestData mTestData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.test_eye_tracking_activity_main);
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
            openTestFragment();
            //openAttentionTestFragment();
        }

        Size screenSize = Utility.getDisplaySize(this);
        float dpi[] = Utility.getXYDpi(this);
        mTestData = new TestData(screenSize.getWidth(), screenSize.getHeight(), dpi[0], dpi[1]);
        mTestData.setDeviceId(Utility.getDeviceId(this));
    }

    public TestData getTestData() {
        return mTestData;
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

        if (frag != null && frag instanceof EyeTrackingTestResultFragment) {
            ft.attach(frag);
        } else {
            frag = new EyeTrackingTestResultFragment();
            ft.replace(R.id.content_frame, frag);
        }

        ((EyeTrackingTestResultFragment) frag).setAttempt(attempt);
        ft.commit();
    }

    public void openTestFragment() {
        Fragment frag = getFragmentManager().findFragmentById(R.id.content_frame);
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        if (frag != null && frag instanceof EyeTrackingLookingTestFragment) {
            ft.attach(frag);
        } else {
            frag = new EyeTrackingLookingTestFragment();
        }

        ft.replace(R.id.content_frame, frag);
        //ft.addToBackStack(EyeTrackingLookingTestFragment.class.getSimpleName());
        ft.commit();
    }

    public void openAttentionTestFragment() {
        Fragment frag = getFragmentManager().findFragmentById(R.id.content_frame);
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        if (frag != null && frag instanceof EyeTrackingAttentionTestFragment) {
            ft.attach(frag);
        } else {
            frag = new EyeTrackingAttentionTestFragment();
        }

        ft.replace(R.id.content_frame, frag);
        //ft.addToBackStack(EyeTrackingAttentionTestFragment.class.getSimpleName());
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

        if (frag != null && (frag instanceof EyeTrackingLookingTestFragment
                || frag instanceof EyeTrackingAttentionTestFragment)) {
            // skip back press for EyeTrackingLookingTestFragment
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
            intent.putExtra(Constants.SURVEY_TYPE, EyeTrackingTest.TYPE);
        }
    }
}
