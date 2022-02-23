package com.reading.start.tests.test_motor_following.ui.fragments;


import android.support.v4.app.Fragment;

import com.reading.start.tests.test_motor_following.ui.activities.MainActivity;

/**
 * Base fragment that contain some base logic for all fragments.
 */
public abstract class BaseFragmentV4 extends Fragment {
    /**
     * Get reference to MainActivity.
     */
    protected MainActivity getMainActivity() {
        if (getActivity() instanceof MainActivity) {
            return (MainActivity) getActivity();
        } else {
            return null;
        }
    }

    /**
     * Run in UI thread.
     */
    protected void runOnUiThread(Runnable action) {
        if (action != null && getActivity() != null) {
            getActivity().runOnUiThread(action);
        }
    }
}
