package com.reading.start.presentation.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Base class for all broadcast receivers in the application.
 */
public abstract class BaseReceiver extends BroadcastReceiver {
    private boolean mIsRegistered;

    protected abstract String getAction();

    public final void register(Context context) {
        if (!mIsRegistered) {
            mIsRegistered = true;
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(getAction());
            LocalBroadcastManager.getInstance(context).registerReceiver(this, intentFilter);
        }
    }

    public final void unregister(Context context) {
        if (mIsRegistered) {
            mIsRegistered = false;
            LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
        }
    }
}
