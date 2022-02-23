package com.reading.start.sdk;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

public class AppCore extends MultiDexApplication {
    private static final String TAG = AppCore.class.getSimpleName();

    private static Context sContext = null;

    @Override
    protected void attachBaseContext(Context newBase) {
        MultiDex.install(newBase);
        super.attachBaseContext(newBase);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        AppCore.sContext = getApplicationContext();
    }

    /**
     * Return instance of AppCore.
     */
    public static AppCore getInstance() {
        return (AppCore) AppCore.sContext;
    }
}
