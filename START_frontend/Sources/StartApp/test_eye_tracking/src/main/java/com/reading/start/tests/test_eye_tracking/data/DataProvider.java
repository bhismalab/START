package com.reading.start.tests.test_eye_tracking.data;

import android.content.Context;

import com.reading.start.tests.TestLog;
import com.reading.start.tests.test_eye_tracking.Preferences;
import com.reading.start.tests.test_eye_tracking.domain.entity.EyeTrackingTestSurveyResult;

import java.security.SecureRandom;

import io.realm.FieldAttribute;
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
                    .name("test_eye_tracking.realm")
                    .schemaVersion(com.reading.start.tests.Constants.DATA_BASE_VERSION)
                    .encryptionKey(getDbKey())
                    .modules(new EyeTrackingTestModule())
                    .migration((realm, oldVersion, newVersion) -> {
                        try {
                            if (newVersion > 6) {
                                if (!realm.getSchema().get(EyeTrackingTestSurveyResult.class.getSimpleName()).getFieldNames().contains(EyeTrackingTestSurveyResult.FILED_ID_ATTACHMENT_SERVER_1)) {
                                    realm.getSchema().get(EyeTrackingTestSurveyResult.class.getSimpleName()).addField(EyeTrackingTestSurveyResult.FILED_ID_ATTACHMENT_SERVER_1, int.class, FieldAttribute.REQUIRED)
                                            .transform(obj -> obj.set(EyeTrackingTestSurveyResult.FILED_ID_ATTACHMENT_SERVER_1, -1));
                                }

                                if (!realm.getSchema().get(EyeTrackingTestSurveyResult.class.getSimpleName()).getFieldNames().contains(EyeTrackingTestSurveyResult.FILED_ID_ATTACHMENT_SERVER_2)) {
                                    realm.getSchema().get(EyeTrackingTestSurveyResult.class.getSimpleName()).addField(EyeTrackingTestSurveyResult.FILED_ID_ATTACHMENT_SERVER_2, int.class, FieldAttribute.REQUIRED)
                                            .transform(obj -> obj.set(EyeTrackingTestSurveyResult.FILED_ID_ATTACHMENT_SERVER_2, -1));
                                }
                            }
                        } catch (Exception e) {
                            TestLog.e(TAG, e);
                        }
                    })
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
