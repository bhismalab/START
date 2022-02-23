package com.reading.start.tests.test_jabble;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;

import com.google.gson.Gson;
import com.reading.start.tests.Constants;
import com.reading.start.tests.ITestModule;
import com.reading.start.tests.ITestModuleResult;
import com.reading.start.tests.TestLog;
import com.reading.start.tests.UploadResult;
import com.reading.start.tests.test_jabble.data.DataProvider;
import com.reading.start.tests.test_jabble.data.DataServerManager;
import com.reading.start.tests.test_jabble.data.entity.DataSurveyResultItem;
import com.reading.start.tests.test_jabble.data.response.ResponseDownloadSurveyResultData;
import com.reading.start.tests.test_jabble.domain.entity.JabbleTestSurveyResult;
import com.reading.start.tests.test_jabble.domain.entity.JabbleTestSurveyResultInfo;
import com.reading.start.tests.test_jabble.domain.entity.TestData;
import com.reading.start.tests.test_jabble.ui.activities.MainActivity;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Class represent bubble test.
 */
public class JabbleTest implements ITestModule {
    public static final String TYPE = "com.reading.start.tests.test_jabble";

    private static final String TAG = JabbleTest.class.getSimpleName();

    private Context mContext;

    private ArrayList<ITestModuleResult> mResults = null;

    private static JabbleTest sInstance = null;

    public static JabbleTest getInstance() {
        return sInstance;
    }

    public JabbleTest(Context context) {
        mContext = context;
        sInstance = this;
    }

    @Override
    public String getSurveyType() {
        return TYPE;
    }

    @Override
    public int getNameResource() {
        return R.string.test_jabble_name;
    }

    @Override
    public void startTest(Activity context, int surveyId) {
        startTest(context, surveyId, null);
    }

    @Override
    public void startTest(Activity context, int surveyId, Bundle data) {
        if (context != null) {
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra(Constants.SURVEY_ID, surveyId);

            if (data != null) {
                intent.putExtra(Constants.SURVEY_DATA, data);
            }

            context.startActivityForResult(intent, Constants.ACTIVITY_SURVEY);
        }
    }

    @Override
    public void showResult(Activity context, int surveyId, int attempt) {
        // not need show result
    }

    @Override
    public boolean hasExtendedResult(Context context, int surveyId, int attempt) {
        return false;
    }

    @Override
    public boolean downloadTestData(String token, String sinceDateTime) {
        return true;
    }

    @Override
    public boolean downloadResult(String token, int surveyId, int surveyIdServer) {
        boolean result = true;
        Realm realm = null;

        try {
            realm = DataProvider.getInstance(mContext).getRealm();

            if (realm != null && !realm.isClosed()) {
                ResponseDownloadSurveyResultData data = DataServerManager.getInstance(mContext).getSurveyResult(token, surveyIdServer);

                if (data != null) {
                    realm.beginTransaction();

                    Number currentIdNum = realm.where(JabbleTestSurveyResult.class).max(JabbleTestSurveyResult.FILED_ID);
                    int nextId;

                    if (currentIdNum == null) {
                        nextId = 0;
                    } else {
                        nextId = currentIdNum.intValue() + 1;
                    }

                    for (int i = 0; i < data.getData().size(); i++) {
                        JabbleTestSurveyResult surveyResult = new JabbleTestSurveyResult();
                        surveyResult.setId(nextId + i);
                        Gson gson = new Gson();
                        surveyResult.setResultFiles(gson.toJson(data.getData().get(i).getFileContent()));
                        surveyResult.setSurveyId(surveyId);
                        // this is fake id, should be greater than -1
                        surveyResult.setIdServer(0);

                        TestData testData = convertToTestData(surveyResult.getResultFiles());

                        if (testData != null) {
                            surveyResult.setStartTime(testData.getStartTime());
                            surveyResult.setEndTime(testData.getEndTime());
                        }

                        realm.copyToRealmOrUpdate(surveyResult);
                    }

                    realm.commitTransaction();
                }
            }
        } catch (Exception e) {
            TestLog.e(TAG, e);

            if (realm != null) {
                realm.cancelTransaction();
            }
        } finally {
            if (realm != null) {
                realm.close();
            }
        }

        return result;
    }

    @Override
    public UploadResult uploadResult(String token, int surveyId, int surveyIdServer) {
        UploadResult result = new UploadResult(true);
        Realm realm = null;

        try {
            realm = DataProvider.getInstance(mContext).getRealm();

            if (realm != null && !realm.isClosed()) {
                // upload results
                List<JabbleTestSurveyResult> dataResult = realm.where(JabbleTestSurveyResult.class).equalTo(JabbleTestSurveyResult.FILED_SURVEY_ID, surveyId).findAll();

                if (dataResult != null && dataResult.size() > 0) {
                    int index = 0;

                    for (int i = 0; i < dataResult.size(); i++) {
                        JabbleTestSurveyResult item = dataResult.get(i);
                        index++;

                        if (item.getIdServer() == -1) {
                            DataSurveyResultItem resultItem = new DataSurveyResultItem();
                            resultItem.setAssessmentType("bubbles_jubbing");
                            resultItem.setSurveyId(String.valueOf(surveyIdServer));
                            resultItem.setFileName("bubbles_jubbing_test_data_" + index + ".json");
                            resultItem.setFileContent("application/json;base64," + Base64.encodeToString(item.getResultFiles().getBytes(), Base64.DEFAULT));
                            int serverId = DataServerManager.getInstance(mContext).addSurveyResult(token, resultItem);
                            TestLog.d(TAG, "serverId: " + serverId);
                            realm.beginTransaction();
                            item.setIdServer(serverId);
                            realm.commitTransaction();
                            result.setSuccess(serverId != -1);
                        }

                        if (!result.isSuccess()) {
                            result.setMessage(mContext.getString(R.string.test_jabble_upload_error));
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            TestLog.e(TAG, e);
        } finally {
            if (realm != null) {
                realm.close();
            }
        }

        return result;
    }

    @Override
    public ArrayList<ITestModuleResult> getTestResults(int surveyId) {
        return mResults;
    }

    @Override
    public ArrayList<ITestModuleResult> fetchTestResults(int surveyId) {
        mResults = new ArrayList<>();

        if (mContext != null) {
            Realm realm = null;

            try {
                realm = DataProvider.getInstance(mContext).getRealm();

                if (realm != null && !realm.isClosed()) {
                    List<JabbleTestSurveyResult> rResult = realm.where(JabbleTestSurveyResult.class).equalTo(JabbleTestSurveyResult.FILED_SURVEY_ID, surveyId).findAll();

                    if (rResult != null) {
                        JabbleTestSurveyResultInfo info;
                        int index = 1;
                        long testTime;
                        String infoText = null;

                        for (JabbleTestSurveyResult item : rResult) {
                            String value = item.getResultFiles();
                            int poppedCount = 0;
                            int totalCount = 0;
                            boolean interrupted = false;

                            try {
                                Gson gson = new Gson();
                                TestData data = gson.fromJson(value, TestData.class);

                                if (data != null) {
                                    poppedCount = data.getBubblesPopped();
                                    totalCount = data.getBubblesTotal();
                                    interrupted = data.isInterrupted();
                                }
                            } catch (Exception e) {
                                TestLog.e(TAG, e);
                            }

                            infoText = String.format(mContext.getString(R.string.test_jabble_result),
                                    String.valueOf(poppedCount), String.valueOf(totalCount));

                            testTime = item.getEndTime() - item.getStartTime();
                            info = new JabbleTestSurveyResultInfo(index, testTime, infoText, interrupted, item);
                            mResults.add(info);
                            index++;
                        }
                    }
                }
            } catch (Exception e) {
                TestLog.e(TAG, e);
            } finally {
                if (realm != null) {
                    realm.close();
                }
            }
        }

        return mResults;
    }

    @Override
    public void deleteResults(int surveyId) {
        Realm realm = null;

        try {
            realm = DataProvider.getInstance(mContext).getRealm();

            if (realm != null && !realm.isClosed()) {
                RealmResults<JabbleTestSurveyResult> rResult = realm.where(JabbleTestSurveyResult.class).equalTo(JabbleTestSurveyResult.FILED_SURVEY_ID, surveyId).findAll();

                if (rResult != null) {
                    realm.beginTransaction();

                    if (mResults != null) {
                        mResults.clear();
                    }

                    rResult.deleteAllFromRealm();
                    realm.commitTransaction();
                }
            }
        } catch (Exception e) {
            TestLog.e(TAG, e);
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    @Override
    public boolean isCompleted(int surveyId) {
        boolean result = false;
        Realm realm = null;

        try {
            realm = DataProvider.getInstance(mContext).getRealm();

            if (realm != null && !realm.isClosed()) {
                long count = realm.where(JabbleTestSurveyResult.class)
                        .equalTo(JabbleTestSurveyResult.FILED_SURVEY_ID, surveyId).count();

                if (count > 0) {
                    result = true;
                }
            }
        } catch (Exception e) {
            TestLog.e(TAG, e);
        } finally {
            if (realm != null) {
                realm.close();
            }
        }

        return result;
    }

    @Override
    public boolean isUploaded(int surveyId) {
        boolean result = true;

        Realm realm = null;

        try {
            realm = DataProvider.getInstance(mContext).getRealm();

            if (realm != null && !realm.isClosed()) {
                long count = realm.where(JabbleTestSurveyResult.class)
                        .equalTo(JabbleTestSurveyResult.FILED_ID_SERVER, -1).count();

                if (count > 0) {
                    result = false;
                }
            }
        } catch (Exception e) {
            TestLog.e(TAG, e);
        } finally {
            if (realm != null) {
                realm.close();
            }
        }

        return result;
    }

    @Override
    public int getIndexDisplaying() {
        return 4;
    }

    @Override
    public int getIndexProcessing() {
        return 1;
    }

    @Override
    public boolean makeDump(Context context, int surveyId, int attempt, String path) {
        boolean result = false;

        try {
            ArrayList<TestData> results = new ArrayList<>();

            if (mContext != null) {
                Realm realm = DataProvider.getInstance(mContext).getRealm();

                if (realm != null && !realm.isClosed()) {
                    List<JabbleTestSurveyResult> rResult = realm.where(JabbleTestSurveyResult.class).equalTo(JabbleTestSurveyResult.FILED_SURVEY_ID, surveyId).findAll();

                    if (rResult != null) {
                        for (JabbleTestSurveyResult item : rResult) {
                            String value = item.getResultFiles();

                            try {
                                Gson gson = new Gson();
                                TestData data = gson.fromJson(value, TestData.class);
                                results.add(data);
                            } catch (Exception e) {
                                TestLog.e(TAG, e);
                            }
                        }
                    }
                }
            }

            if (results.size() > 0 && attempt < results.size()) {
                Gson gson = new Gson();
                String value = gson.toJson(results.get(attempt));

                try {
                    String fileName = JabbleTest.class.getSimpleName() + "_" + surveyId + "_" + attempt + ".txt";
                    File file = new File(path, fileName);
                    FileWriter out = new FileWriter(file);
                    out.write(value);
                    out.close();

                    result = true;
                } catch (Exception e) {
                    TestLog.e(TAG, "makeDump", e);
                }
            }
        } catch (Exception e) {
            TestLog.e(TAG, e);
        }

        return result;
    }

    private TestData convertToTestData(String value) {
        TestData result = null;

        try {
            if (value != null) {
                Gson gson = new Gson();
                result = gson.fromJson(value, TestData.class);
            }
        } catch (Exception e) {
            TestLog.e(TAG, e);
        }

        return result;
    }
}
