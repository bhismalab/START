package com.reading.start.tests.test_wheel;

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
import com.reading.start.tests.test_wheel.data.DataProvider;
import com.reading.start.tests.test_wheel.data.DataServerManager;
import com.reading.start.tests.test_wheel.data.entity.DataAssessmentItem;
import com.reading.start.tests.test_wheel.data.entity.DataUploadSurveyAttachmentItem;
import com.reading.start.tests.test_wheel.data.entity.DataUploadSurveyResultItem;
import com.reading.start.tests.test_wheel.data.response.ResponseAssessmentData;
import com.reading.start.tests.test_wheel.data.response.ResponseDownloadSurveyAttachmentData;
import com.reading.start.tests.test_wheel.data.response.ResponseDownloadSurveyResultData;
import com.reading.start.tests.test_wheel.domain.entity.TestData;
import com.reading.start.tests.test_wheel.domain.entity.WheelTestContent;
import com.reading.start.tests.test_wheel.domain.entity.WheelTestSurveyResult;
import com.reading.start.tests.test_wheel.domain.entity.WheelTestSurveyResultInfo;
import com.reading.start.tests.test_wheel.ui.activities.MainActivity;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Class represent wheel test.
 */
public class WheelTest implements ITestModule {
    public static final String TYPE = "com.reading.start.tests.test_wheel";

    private static final String TAG = WheelTest.class.getSimpleName();

    private Context mContext;

    private ArrayList<ITestModuleResult> mResults = null;

    private static WheelTest sInstance = null;

    public static WheelTest getInstance() {
        return sInstance;
    }

    public WheelTest(Context context) {
        mContext = context;
        sInstance = this;
    }

    @Override
    public String getSurveyType() {
        return TYPE;
    }

    @Override
    public int getNameResource() {
        return R.string.test_wheel_name;
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
        if (context != null) {
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra(Constants.SURVEY_ID, surveyId);
            intent.putExtra(Constants.SURVEY_SHOW_RESULT, true);
            intent.putExtra(Constants.SURVEY_RESULT_ATTEMPT, attempt);
            context.startActivityForResult(intent, Constants.ACTIVITY_SURVEY);
        }
    }

    @Override
    public boolean hasExtendedResult(Context context, int surveyId, int attempt) {
        return true;
    }

    @Override
    public boolean downloadTestData(String token, String sinceDateTime) {
        boolean result = false;
        Realm realm = null;
        Preferences pref = new Preferences(mContext);
        long syncTime = Calendar.getInstance().getTimeInMillis();

        try {
            realm = DataProvider.getInstance(mContext).getRealm();
            long countRecords = realm.where(WheelTestContent.class).count();

            String date = null;

            if (pref.getSyncTime() > 0) {
                date = ISO8601.fromTime(pref.getSyncTime());
            }

            ResponseAssessmentData response = DataServerManager.getInstance(mContext).getContent(token, date);

            if (response != null) {
                result = true;

                if (response.getData() != null && response.getData().getAssessment() != null) {
                    RealmResults<WheelTestContent> results = realm.where(WheelTestContent.class).findAll();
                    realm.beginTransaction();
                    results.deleteAllFromRealm();
                    DataAssessmentItem data = response.getData().getAssessment();
                    WheelTestContent content = new WheelTestContent();
                    content.setId(data.getId());
                    content.setName(data.getName());

                    String filePath = downloadFile(data.getVideo(), getFileNameFromUrl(data.getVideo()) != null
                            ? getFileNameFromUrl(data.getVideo()) : com.reading.start.tests.test_wheel.Constants.VIDEO_FILE_NAME);
                    content.setVideo(filePath);

                    if (filePath == null) {
                        result = false;
                    }

                    realm.copyToRealmOrUpdate(content);
                    realm.commitTransaction();
                }
            } else if (countRecords > 0) {
                result = true;
            }
        } catch (Exception e) {
            result = false;
            realm.cancelTransaction();
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
                ResponseDownloadSurveyAttachmentData dataAttachment = DataServerManager.getInstance(mContext).getSurveyAttachment(token, surveyIdServer);

                if (data != null && dataAttachment != null) {
                    boolean isSuccess = false;
                    realm.beginTransaction();
                    Number currentIdNum = realm.where(WheelTestSurveyResult.class).max(WheelTestSurveyResult.FILED_ID);
                    int nextId;

                    if (currentIdNum == null) {
                        nextId = 0;
                    } else {
                        nextId = currentIdNum.intValue() + 1;
                    }

                    for (int i = 0; i < data.getData().size(); i++) {
                        WheelTestSurveyResult surveyResult = new WheelTestSurveyResult();
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

                        if (i < dataAttachment.getData().size()) {
                            for (int j = 0; j < dataAttachment.getData().get(i).getFiles().size(); j++) {
                                testData = convertToTestData(surveyResult.getResultFiles());

                                if (testData != null && testData.getFilePath() != null) {
                                    File videoFile = new File(testData.getFilePath());
                                    isSuccess = DownloadHelper.downloadFile(dataAttachment.getData().get(i).getFiles().get(0), videoFile);
                                    break;
                                }
                            }
                        }
                    }

                    if (isSuccess) {
                        realm.commitTransaction();
                    } else {
                        realm.cancelTransaction();
                    }
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
                List<WheelTestSurveyResult> dataResult = realm.where(WheelTestSurveyResult.class).equalTo(WheelTestSurveyResult.FILED_SURVEY_ID, surveyId).findAll();

                if (dataResult != null && dataResult.size() > 0) {
                    int index = 0;

                    for (int i = 0; i < dataResult.size(); i++) {
                        WheelTestSurveyResult item = dataResult.get(i);
                        index++;

                        if (item.getIdServer() == -1) {
                            DataUploadSurveyResultItem resultItem = new DataUploadSurveyResultItem();
                            resultItem.setAssessmentType("wheel");
                            resultItem.setSurveyId(String.valueOf(surveyIdServer));
                            resultItem.setFileName("wheel_test_data_" + index + ".json");
                            resultItem.setFileContent("application/json;base64," + Base64.encodeToString(item.getResultFiles().getBytes(), Base64.DEFAULT));
                            int serverId = DataServerManager.getInstance(mContext).addSurveyResult(token, resultItem);
                            realm.beginTransaction();
                            item.setIdServer(serverId);
                            item.setIdAttachmentServer(-1);
                            realm.commitTransaction();
                            TestLog.d(TAG, "serverId: " + serverId);
                            result.setSuccess(serverId != -1);
                        } else {
                            result.setSuccess(item.getIdServer() != -1);
                        }

                        if (result.isSuccess()) {
                            if (item.getIdAttachmentServer() == -1) {
                                String value = item.getResultFiles();
                                File file = null;

                                try {
                                    Gson gson = new Gson();
                                    TestData data = gson.fromJson(value, TestData.class);
                                    file = new File(data.getFilePath());
                                } catch (Exception e) {
                                    TestLog.e(TAG, e);
                                }

                                if (file != null && file.exists()) {
                                    DataUploadSurveyAttachmentItem resultVideo = new DataUploadSurveyAttachmentItem();
                                    resultVideo.setAssessmentType("wheel");
                                    int serverIdAttachment = DataServerManager.getInstance(mContext).addSurveyAttachment(token, String.valueOf(surveyIdServer), resultVideo, file);
                                    realm.beginTransaction();
                                    item.setIdAttachmentServer(serverIdAttachment);
                                    realm.commitTransaction();
                                    result.setSuccess(serverIdAttachment != -1);
                                }
                            } else {
                                result.setSuccess(true);
                            }
                        }

                        if (!result.isSuccess()) {
                            result.setMessage(mContext.getString(R.string.test_wheel_following_upload_error));
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
                    List<WheelTestSurveyResult> rResult = realm.where(WheelTestSurveyResult.class)
                            .equalTo(WheelTestSurveyResult.FILED_SURVEY_ID, surveyId).findAll();

                    if (rResult != null) {
                        WheelTestSurveyResultInfo info;
                        int index = 1;
                        long testTime;
                        String infoText = null;

                        for (WheelTestSurveyResult item : rResult) {
                            String value = item.getResultFiles();
                            boolean interrupted = false;

                            try {
                                Gson gson = new Gson();
                                TestData data = gson.fromJson(value, TestData.class);

                                if (data != null) {
                                    interrupted = data.isInterrupted();
                                }
                            } catch (Exception e) {
                                TestLog.e(TAG, e);
                            }

                            testTime = item.getEndTime() - item.getStartTime();
                            info = new WheelTestSurveyResultInfo(index, testTime, infoText, interrupted, item);
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
                RealmResults<WheelTestSurveyResult> rResult = realm.where(WheelTestSurveyResult.class)
                        .equalTo(WheelTestSurveyResult.FILED_SURVEY_ID, surveyId).findAll();

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
                long count = realm.where(WheelTestSurveyResult.class)
                        .equalTo(WheelTestSurveyResult.FILED_SURVEY_ID, surveyId).count();

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
                long count = realm.where(WheelTestSurveyResult.class)
                        .equalTo(WheelTestSurveyResult.FILED_ID_SERVER, -1)
                        .or()
                        .equalTo(WheelTestSurveyResult.FILED_ID_ATTACHMENT_SERVER, -1).count();

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
        return 1;
    }

    @Override
    public int getIndexProcessing() {
        return 4;
    }

    @Override
    public boolean makeDump(Context context, int surveyId, int attempt, String path) {
        boolean result = false;

        try {
            ArrayList<TestData> results = new ArrayList<>();

            if (mContext != null) {
                Realm realm = DataProvider.getInstance(mContext).getRealm();

                if (realm != null && !realm.isClosed()) {
                    List<WheelTestSurveyResult> rResult = realm.where(WheelTestSurveyResult.class).equalTo(WheelTestSurveyResult.FILED_SURVEY_ID, surveyId).findAll();

                    if (rResult != null) {
                        for (WheelTestSurveyResult item : rResult) {
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
                    String fileName = WheelTest.class.getSimpleName() + "_" + surveyId + "_" + attempt + ".txt";
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
