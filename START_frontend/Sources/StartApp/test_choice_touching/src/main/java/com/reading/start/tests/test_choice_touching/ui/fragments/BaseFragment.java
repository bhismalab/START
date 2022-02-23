package com.reading.start.tests.test_choice_touching.ui.fragments;

import android.app.Fragment;

import com.reading.start.tests.test_choice_touching.ui.activities.MainActivity;

/**
 * Base fragment that contain some base logic for all fragments.
 */
public abstract class BaseFragment extends Fragment {
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
