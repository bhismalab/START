package com.reading.start.tests.test_parent.ui.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.reading.start.tests.ILanguage;
import com.reading.start.tests.TestLog;
import com.reading.start.tests.TestsProvider;

import java.util.Locale;

public abstract class BaseLanguageActivity extends BaseActivity {
    private static final String TAG = BaseLanguageActivity.class.getSimpleName();

    public BaseLanguageActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateLanguage();
    }

    protected void updateLanguage() {
        try {
            ILanguage lang = TestsProvider.getLanguage();

            if (lang != null && !lang.getLocale().isEmpty()) {
                Locale locale = new Locale(lang.getLocale());
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getBaseContext().getResources().updateConfiguration(config,
                        getBaseContext().getResources().getDisplayMetrics());
            }
        } catch (Exception e) {
            TestLog.e(TAG, "updateLanguage", e);
        }
    }
}