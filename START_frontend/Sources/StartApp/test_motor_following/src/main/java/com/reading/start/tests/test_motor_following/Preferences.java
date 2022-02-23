package com.reading.start.tests.test_motor_following;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;

import com.reading.start.tests.TestLog;

/**
 * Uses for save settings of the application.
 */
public class Preferences {
    private static final String TAG = Preferences.class.getSimpleName();

    private static final String s_preference_db_key = "db_key";

    private static final String s_preference_sync_time = "sync_time_motor_following";
    private static final long s_preference_sync_time_default = 0;

    private final SharedPreferences m_prefs;

    public Preferences(Context context) {
        m_prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Set last success synchronize time.
     */
    public void setSyncTime(long value) {
        setLongPreference(s_preference_sync_time, value);
    }

    /**
     * Get last success synchronize time.
     */
    public long getSyncTime() {
        long value = s_preference_sync_time_default;

        try {
            value = m_prefs.getLong(s_preference_sync_time, s_preference_sync_time_default);
        } catch (Exception e) {
            TestLog.e(TAG, "Unable get preferences.", e);
        }

        return value;
    }

    public void setDbKey(byte[] value) {
        setBytesPreference(s_preference_db_key, value);
    }

    public byte[] getDbKey() {
        return getBytesPreference(s_preference_db_key);
    }

    private void setBytesPreference(String key, byte[] value) {
        try {
            SharedPreferences.Editor editor = m_prefs.edit();
            String base64Value = Base64.encodeToString(value, Base64.DEFAULT);
            editor.putString(key, base64Value);
            editor.commit();
        } catch (Exception e) {
            TestLog.e(TAG, "Unable set byte[] preferences: " + key, e);
        }
    }

    private byte[] getBytesPreference(String key) {
        byte[] value = null;

        try {
            String base64Value = m_prefs.getString(key, null);

            if (base64Value != null) {
                value = Base64.decode(base64Value, Base64.DEFAULT);
            }
        } catch (Exception e) {
            TestLog.e(TAG, "Unable get preferences.", e);
        }

        return value;
    }

    private void setLongPreference(String key, long value) {
        try {
            SharedPreferences.Editor editor = m_prefs.edit();
            editor.putLong(key, value);
            editor.commit();
        } catch (Exception e) {
            TestLog.e(TAG, "Unable set Integer preferences: " + key, e);
        }
    }
}
