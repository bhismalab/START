package com.reading.start;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Environment;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.reading.start.general.TLog;
import com.reading.start.presentation.receivers.NetworkStateReceiver;
import com.reading.start.presentation.services.UploadService;
import com.reading.start.tests.*;
import com.reading.start.tests.BuildConfig;
import com.reading.start.tests.Constants;
import com.reading.start.utils.LanguageUtils;
import com.reading.start.utils.NetworkHelper;

import java.io.File;

import io.fabric.sdk.android.Fabric;

/**
 * Override default application class. Contains references to preferences.
 */
public class AppCore extends MultiDexApplication {
    private static final String TAG = AppCore.class.getSimpleName();

    private static Context sContext = null;

    /**
     * Instance of shared preferences.
     */
    private Preferences mPreferences = null;

    private NetworkStateReceiver mNetworkStateReceiver = null;

    private NetworkStateReceiver.NetworkStateReceiverListener mNetworkStateReceiverListener = new NetworkStateReceiver.NetworkStateReceiverListener() {
        @Override
        public void networkUnavailable() {
            // no need any actions
        }

        @Override
        public void networkAvailable() {
            // check upload queue
            UploadService.checkUpload(AppCore.getInstance());
        }
    };

    @Override
    protected void attachBaseContext(Context newBase) {
        MultiDex.install(newBase);
        super.attachBaseContext(newBase);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        AppCore.sContext = getApplicationContext();

        // load all tests modules
        TestsProvider.getInstance(this).getAllDisplayingTestModules();
        TestsProvider.getInstance(this).getAllProcessingTestModules();
        TestsProvider.setLanguage(() -> LanguageUtils.getLanguageString(getPreferences().getLanguage()));

        // init helpers
        NetworkHelper.initialize(this);

        // register network state receiver
        registerNetworkStateReceiver();

        ServerLog.log("AppCore", "INIT");

        if(BuildConfig.LOG_TO_FILE)
        {
            redirectLogToFile();
        }
    }

    /**
     * Return instance of AppCore.
     */
    public static AppCore getInstance() {
        return (AppCore) AppCore.sContext;
    }

    /**
     * Return application preferences.
     */
    public Preferences getPreferences() {
        if (mPreferences == null) {
            mPreferences = new Preferences(this);
        }

        return mPreferences;
    }

    @Override
    protected void finalize() throws Throwable {
        if (mNetworkStateReceiver != null) {
            mNetworkStateReceiver.removeListener(mNetworkStateReceiverListener);
        }

        super.finalize();
    }

    /**
     * Register network receiver.
     */
    private void registerNetworkStateReceiver() {
        try {
            mNetworkStateReceiver = new NetworkStateReceiver();
            mNetworkStateReceiver.addListener(mNetworkStateReceiverListener);
            registerReceiver(mNetworkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
        } catch (Exception e) {
            TLog.e(TAG, "registerNetworkStateReceiver", e);
        }
    }

    private void redirectLogToFile()
    {
        try {
            String root = Environment.getExternalStorageDirectory().toString();
            File folder = new File(root + "/" + com.reading.start.tests.Constants.LOG_FOLDER);

            if (!folder.exists()) {
                folder.mkdir();
            }

            File logFile = new File(folder, Constants.LOG_FILE);

            if (!logFile.exists()) {
                if (logFile.createNewFile()) {
                    // no need any action
                }
            }

            String cmd = "logcat -f " + logFile.getPath() + "\n";
            Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {
            TestLog.d(TAG, e);
        }
    }
}
