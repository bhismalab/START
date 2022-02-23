package com.reading.start.tests.test_choice_touching.data;

import android.content.Context;

import com.reading.start.tests.TestLog;
import com.reading.start.tests.test_choice_touching.Preferences;

import java.security.SecureRandom;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Helper class for initialize Realm database.
 */
public class DataProvider {
    private static final String TAG = DataProvider.class.getSimpleName();

    private static DataProvider sDataProvider = null;

    public static DataProvider getInstance(Context context) {
        if (sDataProvider == null) {
            sDataProvider = new DataProvider(context);
        }

        return sDataProvider;
    }

    private Context mContext = null;

    private RealmConfiguration mConfig = null;

    private DataProvider(Context context) {
        mContext = context;
        initDb();
    }

    public Realm getRealm() {
        return Realm.getInstance(mConfig);
    }

    private void initDb() {
        try {
            Realm.init(mContext);
            mConfig = new RealmConfiguration.Builder()
                    .name("test_choice_touching.realm")
                    .schemaVersion(com.reading.start.tests.Constants.DATA_BASE_VERSION)
                    .encryptionKey(getDbKey())
                    .modules(new ChoiceTouchingTestModule())
                    .build();
            Realm realm = Realm.getInstance(mConfig);
            realm.close();
        } catch (Exception e) {
            TestLog.e(TAG, e);
        }
    }

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
