package com.reading.start.data;

import android.content.Context;

import com.reading.start.Preferences;
import com.reading.start.general.TLog;

import java.security.SecureRandom;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Helper class for initialize Realm database.
 */
public class DataBaseProvider {
    private static final String TAG = DataBaseProvider.class.getSimpleName();

    private static DataBaseProvider sDataProvider = null;

    public static DataBaseProvider getInstance(Context context) {
        if (sDataProvider == null) {
            sDataProvider = new DataBaseProvider(context);
        }

        return sDataProvider;
    }

    private RealmConfiguration mConfig = null;

    private Context mContext = null;

    private DataBaseProvider(Context context) {
        mContext = context;
        initDb();
    }

    /**
     * Get Realm instance.
     */
    public Realm getRealm() {
        return Realm.getInstance(mConfig);
    }

    /**
     * Initialize database
     */
    private void initDb() {
        try {
            Realm.init(mContext);
            mConfig = new RealmConfiguration.Builder()
                    .name("start_app.realm")
                    .schemaVersion(com.reading.start.tests.Constants.DATA_BASE_VERSION)
                    .encryptionKey(getDbKey())
                    .modules(new StartAppModule())
                    .build();
            Realm realm = Realm.getInstance(mConfig);
            realm.close();
        } catch (Exception e) {
            TLog.e(TAG, e);
        }
    }

    /**
     * Gets key fro database
     */
    private byte[] getDbKey() {
        Preferences pref = new Preferences(mContext);
        byte[] key = pref.getDbKey();

        if (key == null || key.length != 64) {
            key = new byte[64];
            new SecureRandom().nextBytes(key);
            pref.setDbKey(key);
        }

        return key;
    }
}
