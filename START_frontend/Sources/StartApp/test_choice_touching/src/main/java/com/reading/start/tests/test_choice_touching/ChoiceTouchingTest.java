package com.reading.start.tests.test_choice_touching;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;

import com.google.gson.Gson;
import com.reading.start.tests.Constants;
import com.reading.start.tests.DownloadHelper;
import com.reading.start.tests.ISO8601;
import com.reading.start.tests.ITestModule;
import com.reading.start.tests.ITestModuleResult;
import com.reading.start.tests.TestLog;
import com.reading.start.tests.UploadResult;
import com.reading.start.tests.test_choice_touching.data.DataProvider;
import com.reading.start.tests.test_choice_touching.data.DataServerManager;
import com.reading.start.tests.test_choice_touching.data.entity.DataAssessmentItem;
import com.reading.start.tests.test_choice_touching.data.entity.DataSurveyResultItem;
import com.reading.start.tests.test_choice_touching.data.response.ResponseAssessmentData;
import com.reading.start.tests.test_choice_touching.data.response.ResponseDownloadSurveyResultData;
import com.reading.start.tests.test_choice_touching.domain.entity.ChoiceTouchingTestContent;
import com.reading.start.tests.test_choice_touching.domain.entity.ChoiceTouchingTestSurveyResult;
import com.reading.start.tests.test_choice_touching.domain.entity.ChoiceTouchingTestSurveyResultInfo;
import com.reading.start.tests.test_choice_touching.domain.entity.TestData;
import com.reading.start.tests.test_choice_touching.ui.activities.MainActivity;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Class represent choice touching test.
 */
public class ChoiceTouchingTest implements ITestModule {
    public static final String TYPE = "com.reading.start.tests.test_choice_touching";

    private static final String TAG = ChoiceTouchingTest.class.getSimpleName();

    private Context mContext;

    private ArrayList<ITestModuleResult> mResults = null;

    private static ChoiceTouchingTest sInstance = null;

    public static ChoiceTouchingTest getInstance() {
        return sInstance;
    }

    public ChoiceTouchingTest(Context context) {
        mContext = context;
        sInstance = this;
    }

    @Override
    public String getSurveyType() {
        return TYPE;
    }

    @Override
    public int getNameResource() {
        return R.string.test_choice_touching_name;
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
        // no need show any results
    }

    @Override
    public boolean hasExtendedResult(Context context, int surveyId, int attempt) {
        return false;
    }

    @Override
    public boolean downloadTestData(String token, String sinceDateTime) {
        boolean result = false;
        Realm realm = null;
        Preferences pref = new Preferences(mContext);
        long syncTime = Calendar.getInstance().getTimeInMillis();

        try {
            realm = DataProvider.getInstance(mContext).getRealm();
            long countRecords = realm.where(ChoiceTouchingTestContent.class).count();

            String date = null;

            if (pref.getSyncTime() > 0) {
                date = ISO8601.fromTime(pref.getSyncTime());
            }

            ResponseAssessmentData response = DataServerManager.getInstance(mContext).getContent(token, date);

            if (response != null) {
                result = true;

                if (response.getData() != null && response.getData().getAssessment() != null) {
                    RealmResults<ChoiceTouchingTestContent> results = realm.where(ChoiceTouchingTestContent.class).findAll();
                    realm.beginTransaction();
                    results.deleteAllFromRealm();
                    DataAssessmentItem data = response.getData().getAssessment();
                    ChoiceTouchingTestContent content = new ChoiceTouchingTestContent();
                    content.setId(data.getId());
                    content.setName(data.getName());

                    String filePath = null;
                    String fileName = null;
                    File file = null;
                    boolean needDelete = false;

                    if (pref.getSyncTime() != pref.getSyncTimeInProgress()) {
                        needDelete = true;
                        pref.setSyncTimeInProgress(pref.getSyncTime());
                    }

                    // social video 1
                    fileName = getFileNameFromUrl(data.getVideo_1_social()) != null ? getFileNameFromUrl(data.getVideo_1_social()) : com.reading.start.tests.test_choice_touching.Constants.VIDEO_FILE_NAME_1_SOCIAL;
                    file = new File(mContext.getFilesDir(), fileName);

                    if (file.exists() && needDelete) {
                        file.delete();
                    }

                    if (!file.exists()) {
                        filePath = downloadFile(data.getVideo_1_social(), fileName);
                        content.setVideo_1_social(filePath);

                        if (filePath == null) {
                            result = false;
                        }
                    }

                    // non social video 1
                    if (result) {
                        fileName = getFileNameFromUrl(data.getVideo_1_nonsocial()) != null ? getFileNameFromUrl(data.getVideo_1_nonsocial()) : com.reading.start.tests.test_choice_touching.Constants.VIDEO_FILE_NAME_1_NO_SOCIAL;
                        file = new File(mContext.getFilesDir(), fileName);

                        if (file.exists() && needDelete) {
                            file.delete();
                        }

                        if (!file.exists()) {
                            filePath = downloadFile(data.getVideo_1_nonsocial(), fileName);
                            content.setVideo_1_no_social(filePath);

                            if (filePath == null) {
                                result = false;
                            }
                        }
                    }

                    // social video 2
                    if (result) {
                        fileName = getFileNameFromUrl(data.getVideo_2_social()) != null ? getFileNameFromUrl(data.getVideo_2_social()) : com.reading.start.tests.test_choice_touching.Constants.VIDEO_FILE_NAME_2_SOCIAL;
                        file = new File(mContext.getFilesDir(), fileName);

                        if (file.exists() && needDelete) {
                            file.delete();
                        }

                        if (!file.exists()) {
                            filePath = downloadFile(data.getVideo_2_social(), fileName);
                            content.setVideo_2_social(filePath);

                            if (filePath == null) {
                                result = false;
                            }
                        }
                    }

                    // non social video 2
                    if (result) {
                        fileName = getFileNameFromUrl(data.getVideo_2_nonsocial()) != null ? getFileNameFromUrl(data.getVideo_2_nonsocial()) : com.reading.start.tests.test_choice_touching.Constants.VIDEO_FILE_NAME_2_NO_SOCIAL;
                        file = new File(mContext.getFilesDir(), fileName);

                        if (file.exists() && needDelete) {
                            file.delete();
                        }

                        if (!file.exists()) {
                            filePath = downloadFile(data.getVideo_2_nonsocial(), fileName);
                            content.setVideo_2_no_social(filePath);

                            if (filePath == null) {
                                result = false;
                            }
                        }
                    }

                    // social video 3
                    if (result) {
                        fileName = getFileNameFromUrl(data.getVideo_3_social()) != null ? getFileNameFromUrl(data.getVideo_3_social()) : com.reading.start.tests.test_choice_touching.Constants.VIDEO_FILE_NAME_3_SOCIAL;
                        file = new File(mContext.getFilesDir(), fileName);

                        if (file.exists() && needDelete) {
                            file.delete();
                        }

                        if (!file.exists()) {
                            filePath = downloadFile(data.getVideo_3_social(), fileName);
                            content.setVideo_3_social(filePath);

                            if (filePath == null) {
                                result = false;
                            }
                        }
                    }

                    // non social video 3
                    if (result) {
                        fileName = getFileNameFromUrl(data.getVideo_3_nonsocial()) != null ? getFileNameFromUrl(data.getVideo_3_nonsocial()) : com.reading.start.tests.test_choice_touching.Constants.VIDEO_FILE_NAME_3_NO_SOCIAL;
                        file = new File(mContext.getFilesDir(), fileName);

                        if (file.exists() && needDelete) {
                            file.delete();
                        }

                        if (!file.exists()) {
                            filePath = downloadFile(data.getVideo_3_nonsocial(), fileName);
                            content.setVideo_3_no_social(filePath);

                            if (filePath == null) {
                                result = false;
                            }
                        }
                    }

                    // social video 4
                    if (result) {
                        fileName = getFileNameFromUrl(data.getVideo_4_social()) != null ? getFileNameFromUrl(data.getVideo_4_social()) : com.reading.start.tests.test_choice_touching.Constants.VIDEO_FILE_NAME_4_SOCIAL;
                        file = new File(mContext.getFilesDir(), fileName);

                        if (file.exists() && needDelete) {
                            file.delete();
                        }

                        if (!file.exists()) {
                            filePath = downloadFile(data.getVideo_4_social(), fileName);
                            content.setVideo_4_social(filePath);

                            if (filePath == null) {
                                result = false;
                            }
                        }
                    }

                    // non social video 4
                    if (result) {
                        fileName = getFileNameFromUrl(data.getVideo_4_nonsocial()) != null ? getFileNameFromUrl(data.getVideo_4_nonsocial()) : com.reading.start.tests.test_choice_touching.Constants.VIDEO_FILE_NAME_4_NO_SOCIAL;
                        file = new File(mContext.getFilesDir(), fileName);

                        if (file.exists() && needDelete) {
                            file.delete();
                        }

                        if (!file.exists()) {
                            filePath = downloadFile(data.getVideo_4_nonsocial(), fileName);
                            content.setVideo_4_no_social(filePath);

                            if (filePath == null) {
                                result = false;
                            }
                        }
                    }

                    // social video demo
                    if (result) {
                        fileName = getFileNameFromUrl(data.getVideoDemo_social()) != null ? getFileNameFromUrl(data.getVideoDemo_social()) : com.reading.start.tests.test_choice_touching.Constants.VIDEO_DEMO_FILE_NAME_SOCIAL;
                        file = new File(mContext.getFilesDir(), fileName);

                        if (file.exists() && needDelete) {
                            file.delete();
                        }

                        if (!file.exists()) {
                            filePath = downloadFile(data.getVideoDemo_social(), fileName);
                            content.setVideoDemo_social(filePath);

                            if (filePath == null) {
                                result = false;
                            }
                        }
                    }

                    // non social video demo
                    if (result) {
                        fileName = getFileNameFromUrl(data.getVideoDemo_nonsocial()) != null
                                ? getFileNameFromUrl(data.getVideoDemo_nonsocial()) : com.reading.start.tests.test_choice_touching.Constants.VIDEO_DEMO_FILE_NAME_NO_SOCIAL;
                        file = new File(mContext.getFilesDir(), fileName);

                        if (file.exists() && needDelete) {
                            file.delete();
                        }

                        if (!file.exists()) {
                            filePath = downloadFile(data.getVideoDemo_nonsocial(), fileName);
                            content.setVideoDemo_no_social(filePath);

                            if (filePath == null) {
                                result = false;
                            }
                        }
                    }

                    realm.copyToRealmOrUpdate(content);
                    realm.commitTransaction();

                }
            } else if (countRecords > 0) {
                result = true;
            }
        } catch (Exception e) {
            result = false;

            if (realm != null) {
                realm.cancelTransaction();
            }

            TestLog.e(TAG, e);
        } finally {
            if (realm != null) {
                realm.close();
            }

            if (result) {
                pref.setSyncTime(syncTime);
            }
        }

        return result;
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
                    Number currentIdNum = realm.where(ChoiceTouchingTestSurveyResult.class).max(ChoiceTouchingTestSurveyResult.FILED_ID);
                    int nextId;

                    if (currentIdNum == null) {
                        nextId = 0;
                    } else {
                        nextId = currentIdNum.intValue() + 1;
                    }

                    for (int i = 0; i < data.getData().size(); i++) {
                        ChoiceTouchingTestSurveyResult surveyResult = new ChoiceTouchingTestSurveyResult();
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
                List<ChoiceTouchingTestSurveyResult> dataResult = realm.where(ChoiceTouchingTestSurveyResult.class).equalTo(ChoiceTouchingTestSurveyResult.FILED_SURVEY_ID, surveyId).findAll();

                if (dataResult != null) {
                    int index = 0;

                    for (int i = 0; i < dataResult.size(); i++) {
                        ChoiceTouchingTestSurveyResult item = dataResult.get(i);
                        index++;

                        if (item.getIdServer() == -1) {
                            DataSurveyResultItem resultItem = new DataSurveyResultItem();
                            resultItem.setAssessmentType("choose_touching");
                            resultItem.setSurveyId(String.valueOf(surveyIdServer));
                            resultItem.setFileName("choose_touching_test_data_" + index + ".json");
                            resultItem.setFileContent("application/json;base64," + Base64.encodeToString(item.getResultFiles().getBytes(), Base64.DEFAULT));
                            int serverId = DataServerManager.getInstance(mContext).addSurveyResult(token, resultItem);
                            TestLog.d(TAG, "serverId: " + serverId);
                            realm.beginTransaction();
                            item.setIdServer(serverId);
                            realm.commitTransaction();
                            result.setSuccess(serverId != -1);
                        }

                        if (!result.isSuccess()) {
                            result.setMessage(mContext.getString(R.string.test_choice_touching_upload_error));
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
                    List<ChoiceTouchingTestSurveyResult> rResult = realm.where(ChoiceTouchingTestSurveyResult.class)
                            .equalTo(ChoiceTouchingTestSurveyResult.FILED_SURVEY_ID, surveyId).findAll();

                    if (rResult != null) {
                        ChoiceTouchingTestSurveyResultInfo info;
                        int index = 1;
                        long testTime;
                        String infoText = null;

                        for (ChoiceTouchingTestSurveyResult item : rResult) {
                            String value = item.getResultFiles();
                            int redCount = 0;
                            int greenCount = 0;
                            boolean interrupted = false;

                            try {
                                Gson gson = new Gson();
                                TestData data = gson.fromJson(value, TestData.class);

                                if (data != null) {
                                    redCount = data.getRedClickCount();
                                    greenCount = data.getGreenClickCount();
                                    interrupted = data.isInterrupted();
                                }
                            } catch (Exception e) {
                                TestLog.e(TAG, e);
                            }

                            infoText = String.format(mContext.getString(R.string.test_choice_touching_result),
                                    String.valueOf(redCount), String.valueOf(greenCount));
                            testTime = item.getEndTime() - item.getStartTime();
                            info = new ChoiceTouchingTestSurveyResultInfo(index, testTime, infoText, interrupted, item);
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
                RealmResults<ChoiceTouchingTestSurveyResult> rResult = realm.where(ChoiceTouchingTestSurveyResult.class)
                        .equalTo(ChoiceTouchingTestSurveyResult.FILED_SURVEY_ID, surveyId).findAll();

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

            if (realm != null) {
                realm.cancelTransaction();
            }
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
                long count = realm.where(ChoiceTouchingTestSurveyResult.class)
                        .equalTo(ChoiceTouchingTestSurveyResult.FILED_SURVEY_ID, surveyId).count();

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
                long count = realm.where(ChoiceTouchingTestSurveyResult.class)
                        .equalTo(ChoiceTouchingTestSurveyResult.FILED_ID_SERVER, -1).count();

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
        return 2;
    }

    @Override
    public int getIndexProcessing() {
        return 0;
    }

    @Override
    public boolean makeDump(Context context, int surveyId, int attempt, String path) {
        boolean result = false;
        Realm realm = null;

        try {
            ArrayList<TestData> results = new ArrayList<>();

            if (mContext != null) {
                realm = DataProvider.getInstance(mContext).getRealm();

                if (realm != null && !realm.isClosed()) {
                    List<ChoiceTouchingTestSurveyResult> rResult = realm.where(ChoiceTouchingTestSurveyResult.class).equalTo(ChoiceTouchingTestSurveyResult.FILED_SURVEY_ID, surveyId).findAll();

                    if (rResult != null) {
                        for (ChoiceTouchingTestSurveyResult item : rResult) {
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
                    String fileName = ChoiceTouchingTest.class.getSimpleName() + "_" + surveyId + "_" + attempt + ".txt";
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
        } finally {
            if (realm != null) {
                realm.close();
            }
        }

        return result;
    }

    private String downloadFile(String url, String name) {
        String result = null;

        try {
            if (url != null) {
                File file = new File(mContext.getFilesDir(), name);

                if (file.exists()) {
                    file.delete();
                }

                if (DownloadHelper.downloadFile(url, file)) {
                    result = file.getPath();
                } else {
                    file.delete();
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

    private String getFileNameFromUrl(String url) {
        String result = null;

        try {
            if (url != null) {
                int index = url.lastIndexOf("/");
                result = url.substring(index + 1);
            }
        } catch (Exception e) {
            TestLog.e(TAG, e);
        }

        return result;
    }
}
