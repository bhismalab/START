package com.reading.start;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;

import com.reading.start.domain.entity.CurrentScreen;
import com.reading.start.general.TLog;

/**
 * Uses for save settings of the application.
 */
public class Preferences {
    private static final String TAG = Preferences.class.getSimpleName();

    private final static String s_preference_first_run = "first_run";
    private final static boolean s_preference_first_run_default = false;

    private static final String s_preference_login_attempt = "login_attempt";
    private static final int s_preference_login_attempt_default = 0;

    private static final String s_preference_login_fail_time = "login_fail_time";
    private static final long s_preference_login_fail_time_default = 0;

    private static final String s_preference_login_worker = "login_worker";
    private static final String s_preference_login_worker_default = "";

    private static final String s_preference_login_worker_id = "login_worker_id";
    private static final int s_preference_login_worker_id_default = -1;

    private static final String s_preference_login_token = "login_token";
    private static final String s_preference_login_token_default = "";

    private static final String s_preference_server_token = "server_token";
    private static final String s_preference_server_token_default = "";

    private static final String s_preference_sync_time = "sync_time";
    private static final long s_preference_sync_time_default = 0;

    private static final String s_preference_db_key = "db_key";

    private final static String s_preference_language = "language_v2";
    private final static String s_preference_language_default = String.valueOf(Constants.LANGUAGE_VALUE_EN);

    private static final String s_preference_current_screen = "current_screen";
    private static final int s_preference_current_screen_default = CurrentScreen.ChildList.ordinal();

    private static final String s_preference_current_child = "current_child";
    private static final int s_preference_current_child_default = -1;

    private static final String s_preference_current_survey = "current_survey";
    private static final int s_preference_current_survey_default = -1;

    private final SharedPreferences m_prefs;

    public Preferences(Context context) {
        m_prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public CurrentScreen getCurrentScreen() {
        CurrentScreen value = CurrentScreen.ChildList;

        try {
            value = CurrentScreen.values()[Integer.parseInt(m_prefs.getString(s_preference_current_screen, String.valueOf(s_preference_current_screen_default)))];
        } catch (ClassCastException e) {
            TLog.e(TAG, "Unable get preferences.", e);
        } catch (Exception e) {
            TLog.e(TAG, "Unable get preferences.", e);
        }

        return value;
    }

    public void setCurrentScreen(CurrentScreen value) {
        setStringPreference(s_preference_current_screen, String.valueOf(value.ordinal()));
    }

    public int getCurrentChild() {
        int value = s_preference_current_child_default;

        try {
            value = Integer.parseInt(m_prefs.getString(s_preference_current_child, String.valueOf(s_preference_current_child_default)));
        } catch (ClassCastException e) {
            TLog.e(TAG, "Unable get preferences.", e);
        } catch (Exception e) {
            TLog.e(TAG, "Unable get preferences.", e);
        }

        return value;
    }

    public void setCurrentChild(int value) {
        setStringPreference(s_preference_current_child, String.valueOf(value));
    }

    public int getCurrentSurvey() {
        int value = s_preference_current_survey_default;

        try {
            value = Integer.parseInt(m_prefs.getString(s_preference_current_survey, String.valueOf(s_preference_current_survey_default)));
        } catch (ClassCastException e) {
            TLog.e(TAG, "Unable get preferences.", e);
        } catch (Exception e) {
            TLog.e(TAG, "Unable get preferences.", e);
        }

        return value;
    }

    public void setCurrentSurvey(int value) {
        setStringPreference(s_preference_current_survey, String.valueOf(value));
    }

    /**
     * Get current language of the application.
     */
    public int getLanguage() {
        int value = Constants.LANGUAGE_VALUE_EN;

        try {
            value = Integer.parseInt(m_prefs.getString(s_preference_language, String.valueOf(s_preference_language_default)));
        } catch (ClassCastException e) {
            TLog.e(TAG, "Unable get preferences.", e);
        } catch (Exception e) {
            TLog.e(TAG, "Unable get preferences.", e);
        }

        return value;
    }

    /**
     * Set current language of the application.
     */
    public void setLanguage(int value) {
        setStringPreference(s_preference_language, String.valueOf(value));
    }

    /**
     * Get name of logged social worker.
     */
    public String getLoginWorker() {
        String value = s_preference_login_worker_default;

        try {
            value = m_prefs.getString(s_preference_login_worker, s_preference_login_worker_default);
        } catch (Exception e) {
            TLog.e(TAG, "Unable get preferences.", e);
        }

        return value;
    }

    /**
     * Set name of logged social worker.
     */
    public void setLoginWorker(String value) {
        setStringPreference(s_preference_login_worker, value);
    }

    /**
     * Get in app token of logged social worker.
     */
    public void setLoginToken(String value) {
        setStringPreference(s_preference_login_token, value);
    }

    /**
     * Set in app token of logged social worker.
     */
    public void setLoginWorkerId(int value) {
        setIntPreference(s_preference_login_worker_id, value);
    }

    /**
     * Get id of logged social worker.
     */
    public int getLoginWorkerId() {
        int value = s_preference_login_worker_id_default;

        try {
            value = m_prefs.getInt(s_preference_login_worker_id, s_preference_login_worker_id_default);
        } catch (Exception e) {
            TLog.e(TAG, "Unable get preferences.", e);
        }

        return value;
    }

    /**
     * Set id of logged social worker.
     */
    public String getLoginToken() {
        String value = s_preference_login_token_default;

        try {
            value = m_prefs.getString(s_preference_login_token, s_preference_login_token_default);
        } catch (Exception e) {
            TLog.e(TAG, "Unable get preferences.", e);
        }

        return value;
    }

    /**
     * Set server token of logged social worker.
     */
    public void setServerToken(String value) {
        setStringPreference(s_preference_server_token, value);
    }

    /**
     * Get server token of logged social worker.
     */
    public String getServerToken() {
        String value = s_preference_login_token_default;

        try {
            value = m_prefs.getString(s_preference_server_token, s_preference_server_token_default);
        } catch (Exception e) {
            TLog.e(TAG, "Unable get preferences.", e);
        }

        return value;
    }

    /**
     * Set password for database.
     */
    public void setDbKey(byte[] value) {
        setBytesPreference(s_preference_db_key, value);
    }

    /**
     * Get password for database.
     */
    public byte[] getDbKey() {
        return getBytesPreference(s_preference_db_key);
    }

    /**
     * Indicates whether if app first run.
     */
    public boolean isFirstRun() {
        boolean value = s_preference_first_run_default;

        try {
            value = m_prefs.getBoolean(s_preference_first_run, s_preference_first_run_default);
        } catch (Exception e) {
            TLog.e(TAG, "Unable get preferences.", e);
        }

        return value;
    }

    /**
     * Set value that indicates whether if app first run.
     */
    public void setIsFirstRun(boolean value) {
        setBooleanPreference(s_preference_first_run, value);
    }

    /**
     * Set count attempt of un-successful login.
     */
    public void setLoginAttempt(int value) {
        setIntPreference(s_preference_login_attempt, value);
    }

    /**
     * Get count attempt of un-successful login.
     */
    public int getLoginAttempt() {
        int value = s_preference_login_attempt_default;

        try {
            value = m_prefs.getInt(s_preference_login_attempt, s_preference_login_attempt_default);
        } catch (Exception e) {
            TLog.e(TAG, "Unable get preferences.", e);
        }

        return value;
    }

    /**
     * Set time of un-success login.
     */
    public void setLoginFailTime(long value) {
        setLongPreference(s_preference_login_fail_time, value);
    }

    /**
     * Get time of un-success login.
     */
    public long getLoginFailTime() {
        long value = s_preference_login_fail_time_default;

        try {
            value = m_prefs.getLong(s_preference_login_fail_time, s_preference_login_fail_time_default);
        } catch (Exception e) {
            TLog.e(TAG, "Unable get preferences.", e);
        }

        return value;
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
            TLog.e(TAG, "Unable get preferences.", e);
        }

        return value;
    }

    /**
     * Set boolean preference
     */
    private void setBooleanPreference(String key, Boolean value) {
        try {
            SharedPreferences.Editor editor = m_prefs.edit();
            editor.putBoolean(key, value);
            editor.commit();
        } catch (Exception e) {
            TLog.e(TAG, "Unable set Boolean preferences: " + key, e);
        }
    }

    /**
     * Set string preference
     */
    private void setStringPreference(String key, String value) {
        try {
            SharedPreferences.Editor editor = m_prefs.edit();
            editor.putString(key, value);
            editor.commit();
        } catch (Exception e) {
            TLog.e(TAG, "Unable set Boolean preferences: " + key, e);
        }
    }

    /**
     * Set integer preference
     */
    private void setIntPreference(String key, int value) {
        try {
            SharedPreferences.Editor editor = m_prefs.edit();
            editor.putInt(key, value);
            editor.commit();
        } catch (Exception e) {
            TLog.e(TAG, "Unable set Integer preferences: " + key, e);
        }
    }

    private void setLongPreference(String key, long value) {
        try {
            SharedPreferences.Editor editor = m_prefs.edit();
            editor.putLong(key, value);
            editor.commit();
        } catch (Exception e) {
            TLog.e(TAG, "Unable set Integer preferences: " + key, e);
        }
    }

    /**
     * Save byte array to preference
     */
    private void setBytesPreference(String key, byte[] value) {
        try {
            SharedPreferences.Editor editor = m_prefs.edit();
            String base64Value = Base64.encodeToString(value, Base64.DEFAULT);
            editor.putString(key, base64Value);
            editor.commit();
        } catch (Exception e) {
            TLog.e(TAG, "Unable set byte[] preferences: " + key, e);
        }
    }

    /**
     * Get byte array from preference
     */
    private byte[] getBytesPreference(String key) {
        byte[] value = null;

        try {
            String base64Value = m_prefs.getString(key, null);

            if (base64Value != null) {
                value = Base64.decode(base64Value, Base64.DEFAULT);
            }
        } catch (Exception e) {
            TLog.e(TAG, "Unable get preferences.", e);
        }

        return value;
    }
}
