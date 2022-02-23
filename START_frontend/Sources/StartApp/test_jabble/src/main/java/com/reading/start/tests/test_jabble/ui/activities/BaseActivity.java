package com.reading.start.tests.test_jabble.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.reading.start.tests.TestLog;

/**
 * Contains all base logic for all activity in the project.
 */
public class BaseActivity extends AppCompatActivity {
    private static final String TAG = BaseActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setFullScreen();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * Set fullscreen for activity.
     */
    public void setFullScreen() {
        try {
            final View view = getWindow().getDecorView();

            if (view != null) {
                //"hides" back, home and return button on screen.
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                view.setOnSystemUiVisibilityChangeListener
                        (visibility -> {
                            // Note that system bars will only be "visible" if none of the
                            // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
                            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                        | View.SYSTEM_UI_FLAG_IMMERSIVE
                                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                            }
                        });
            }
        } catch (Exception e) {
            TestLog.e(TAG, "setFullScreen", e);
        }
    }
}
