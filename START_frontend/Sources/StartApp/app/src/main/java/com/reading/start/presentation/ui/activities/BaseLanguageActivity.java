package com.reading.start.presentation.ui.activities;

import android.content.res.Configuration;
import android.os.Bundle;

import com.reading.start.AppCore;
import com.reading.start.Preferences;
import com.reading.start.general.TLog;
import com.reading.start.utils.LanguageUtils;

import java.util.Locale;

/**
 * Base activity that contains logic for set app languages.
 */
public abstract class BaseLanguageActivity extends BaseActivity {
    private static final String TAG = BaseLanguageActivity.class.getSimpleName();

    public BaseLanguageActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateLanguage();
    }

    /**
     * Update language for activity
     */
    protected void updateLanguage() {
        try {
            String lang = LanguageUtils.getLanguageString(getPreferences().getLanguage());

            if (lang != null && lang.length() > 0) {
                Locale locale = new Locale(lang);
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getBaseContext().getResources().updateConfiguration(config,
                        getBaseContext().getResources().getDisplayMetrics());
            }
        } catch (Exception e) {
            TLog.e(TAG, "updateLanguage", e);
        }
    }

    public Preferences getPreferences() {
        if (AppCore.getInstance() != null) {
            return AppCore.getInstance().getPreferences();
        } else {
            return null;
        }
    }
}