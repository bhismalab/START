package com.reading.start.presentation.ui.fragments.base;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.reading.start.AppCore;
import com.reading.start.Preferences;
import com.reading.start.presentation.ui.activities.MainActivity;
import com.reading.start.presentation.ui.dialogs.DialogProgress;

/**
 * Represent base fragment for all fragment int the application. Implemented some general helpers methods.
 */
public abstract class BaseFragment extends Fragment {
    private static final String TAG = BaseFragment.class.getSimpleName();

    private DialogProgress mProgressDialog = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(false);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        MainActivity activity = getMainActivity();

        if (activity != null) {
            activity.setFullScreen();
        }
    }

    /**
     * Get application preferences.
     */
    protected Preferences getPreferences() {
        return AppCore.getInstance().getPreferences();
    }

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
