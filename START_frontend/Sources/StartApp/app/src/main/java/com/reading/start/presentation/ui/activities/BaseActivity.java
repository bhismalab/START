package com.reading.start.presentation.ui.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.reading.start.AppCore;
import com.reading.start.Preferences;
import com.reading.start.general.TLog;

/**
 * Contains all base logic for all activity in the project.
 */
public class BaseActivity extends Activity {
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
     * Get application preferences.
     */
    public Preferences getPreferences() {
        if (AppCore.getInstance() != null) {
            return AppCore.getInstance().getPreferences();
        } else {
            return null;
        }
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
            TLog.e(TAG, "setFullScreen", e);
        }
    }
}
