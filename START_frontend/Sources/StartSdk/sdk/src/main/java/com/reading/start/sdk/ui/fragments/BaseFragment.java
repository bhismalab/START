package com.reading.start.sdk.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.reading.start.sdk.ui.activities.MainActivity;

public abstract class BaseFragment extends Fragment {
    protected boolean mIsNeedBatteryCharge = false;

    private boolean mSkipBatteryCharge = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(false);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
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

    protected void showShortToast(final String message) {
        if (message != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
