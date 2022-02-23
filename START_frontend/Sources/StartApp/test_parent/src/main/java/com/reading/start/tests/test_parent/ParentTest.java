package com.reading.start.tests.test_parent;

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
import com.reading.start.tests.test_parent.data.DataProvider;
import com.reading.start.tests.test_parent.data.DataServerManager;
import com.reading.start.tests.test_parent.data.entity.DataAssessmentItem;
import com.reading.start.tests.test_parent.data.entity.DataUploadSurveyAttachmentItem;
import com.reading.start.tests.test_parent.data.entity.DataUploadSurveyResultItem;
import com.reading.start.tests.test_parent.data.response.ResponseAssessmentData;
import com.reading.start.tests.test_parent.data.response.ResponseDownloadSurveyAttachmentData;
import com.reading.start.tests.test_parent.data.response.ResponseDownloadSurveyResultData;
import com.reading.start.tests.test_parent.domain.entity.ParentTestContent;
import com.reading.start.tests.test_parent.domain.entity.ParentTestContentChoices;
import com.reading.start.tests.test_parent.domain.entity.ParentTestSurveyResult;
import com.reading.start.tests.test_parent.domain.entity.ParentTestSurveyResultInfo;
import com.reading.start.tests.test_parent.domain.entity.TestData;
import com.reading.start.tests.test_parent.ui.activities.MainActivity;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Class represent parent test.
 */
public class ParentTest implements ITestModule {
    public static final String TYPE = "com.reading.start.tests.test_parent";

    private static final String TAG = ParentTest.class.getSimpleName();

    private Context mContext;

    private ArrayList<ITestModuleResult> mResults = null;

    private static ParentTest sInstance = null;

    public static ParentTest getInstance() {
        return sInstance;
    }

    public ParentTest(Context context) {
        mContext = context;
        sInstance = this;
    }

    @Override
    public String getSurveyType() {
        return TYPE;
    }

    @Override
    public int getNameResource() {
        return R.string.test_parent_name;
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
        boolean result = false;
        Realm realm = null;

        try {
            realm = DataProvider.getInstance(mContext).getRealm();

            if (realm != null && !realm.isClosed()) {
                List<ParentTestSurveyResult> results = realm.where(ParentTestSurveyResult.class)
                        .equalTo(ParentTestSurveyResult.FILED_SURVEY_ID, surveyId).findAll();

                if (results != null && results.size() > 0 && attempt < results.size()) {
                    String value = results.get(attempt).getResultFiles();

                    try {
                        Gson gson = new Gson();
                        TestData data = gson.fromJson(value, TestData.class);

                        if (data != null) {
                            File videoFile = new File(data.getFilePath());

                            if (videoFile != null && videoFile.exists()) {
                                result = true;
                            }
                        }
                    } catch (Exception e) {
                        TestLog.e(TAG, e);
                    }
                }
            }

            realm.close();
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
    public boolean downloadTestData(String token, String sinceDateTime) {
        boolean result = false;
        Realm realm = null;
        Preferences pref = new Preferences(mContext);
        long syncTime = Calendar.getInstance().getTimeInMillis();

        try {
            realm = DataProvider.getInstance(mContext).getRealm();
            long countRecords = realm.where(ParentTestContent.class).count();

            String date = null;

            if (pref.getSyncTime() > 0) {
                date = ISO8601.fromTime(pref.getSyncTime());
            }

            ResponseAssessmentData response = DataServerManager.getInstance(mContext).getContent(token, date);

            if (response != null) {
                result = true;

                if (response.getData() != null && response.getData().getAssessment() != null) {
                    RealmResults<ParentTestContent> results = realm.where(ParentTestContent.class).findAll();
                    RealmResults<ParentTestContentChoices> resultsChoices = realm.where(ParentTestContentChoices.class).findAll();
                    realm.beginTransaction();
                    results.deleteAllFromRealm();
                    resultsChoices.deleteAllFromRealm();
                    ArrayList<DataAssessmentItem> items = response.getData().getAssessment();

                    if (items != null && items.size() > 0) {
                        for (DataAssessmentItem data : items) {
                            ParentTestContent content = new ParentTestContent();
                            content.setId(data.getId());
                            content.setType(data.getType());
                            content.setQuestion(data.getQuestion());
                            content.setQuestion_hindi(data.getQuestionHindi());

                            if (data.getChoices() != null) {
                                Number currentIdNum = realm.where(ParentTestContentChoices.class).max(ParentTestContentChoices.FILED_ID);
                                int nextId;

                                if (currentIdNum == null) {
                                    nextId = 0;
                                } else {
                                    nextId = currentIdNum.intValue() + 1;
                                }

                                for (int i = 0; i < data.getChoices().getChoicesEnglish().size(); i++) {
                                    String choiceNameEnglish = data.getChoices().getChoicesEnglish().get(i);
                                    ParentTestContentChoices choiceItem = new ParentTestContentChoices();
                                    choiceItem.setId(nextId + i);
                                    choiceItem.setIdContent(content.getId());
                                    choiceItem.setChoiceEnglish(choiceNameEnglish);

                                    if (data.getChoices().getChoicesHindi() != null && i < data.getChoices().getChoicesHindi().size()) {
                                        choiceItem.setChoiceHindi(data.getChoices().getChoicesHindi().get(i));
                                    }

                                    realm.copyToRealmOrUpdate(choiceItem);
                                }
                            }

                            if (data.getVideo1() != null) {
                                File file = new File(mContext.getFilesDir(), "video_1_" + items.indexOf(data) + ".mp4");

                                if (file.exists()) {
                                    file.delete();
                                }

                                if (DownloadHelper.downloadFile(data.getVideo1(), file)) {
                                    content.setVideo1(file.getPath());
                                } else {
                                    result = false;
                                    break;
                                }
                            }

                            if (data.getVideo2() != null) {
                                File file = new File(mContext.getFilesDir(), "video_2_" + items.indexOf(data) + ".mp4");

                                if (file.exists()) {
                                    file.delete();
                                }

                                if (DownloadHelper.downloadFile(data.getVideo2(), file)) {
                                    content.setVideo2(file.getPath());
                                } else {
                                    result = false;
                                    break;
                                }
                            }

                            realm.copyToRealmOrUpdate(content);
                        }
                    }

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

                if (data != null) {
                    realm.beginTransaction();
                    boolean isSuccess = false;
                    Number currentIdNum = realm.where(ParentTestSurveyResult.class).max(ParentTestSurveyResult.FILED_ID);
                    int nextId;

                    if (currentIdNum == null) {
                        nextId = 0;
                    } else {
                        nextId = currentIdNum.intValue() + 1;
                    }

                    for (int i = 0; i < data.getData().size(); i++) {
                        ParentTestSurveyResult surveyResult = new ParentTestSurveyResult();
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

                        if (dataAttachment != null && i < dataAttachment.getData().size()) {
                            for (int j = 0; j < dataAttachment.getData().get(i).getFiles().size(); j++) {
                                testData = convertToTestData(surveyResult.getResultFiles());

                                if (testData != null && testData.getFilePath() != null) {
                                    File videoFile = new File(testData.getFilePath());
                                    isSuccess = DownloadHelper.downloadFile(dataAttachment.getData().get(i).getFiles().get(0), videoFile);
                                    break;
                                }
                            }
                        } else {
                            isSuccess = true;
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
                List<ParentTestSurveyResult> dataResult = realm.where(ParentTestSurveyResult.class).equalTo(ParentTestSurveyResult.FILED_SURVEY_ID, surveyId).findAll();

                if (dataResult != null && dataResult.size() > 0) {
                    int index = 0;

                    for (int i = 0; i < dataResult.size(); i++) {
                        ParentTestSurveyResult item = dataResult.get(i);
                        index++;

                        if (item.getIdServer() == -1) {
                            DataUploadSurveyResultItem resultItem = new DataUploadSurveyResultItem();
                            resultItem.setAssessmentType("parent_assestment");
                            resultItem.setSurveyId(String.valueOf(surveyIdServer));
                            resultItem.setFileName("parent_assessment_test_data_" + index + ".json");
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
                                    resultVideo.setAssessmentType("parent_assestment");
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
                            result.setMessage(mContext.getString(R.string.test_paren_following_upload_error));
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
                    List<ParentTestSurveyResult> rResult = realm.where(ParentTestSurveyResult.class).equalTo(ParentTestSurveyResult.FILED_SURVEY_ID, surveyId).findAll();

                    if (rResult != null) {
                        ParentTestSurveyResultInfo info;
                        int index = 1;
                        long testTime;
                        String infoText = null;

                        for (ParentTestSurveyResult item : rResult) {
                            String value = item.getResultFiles();
                            int answeredCount = 0;
                            int totalCount = 0;

                            try {
                                Gson gson = new Gson();
                                TestData data = gson.fromJson(value, TestData.class);

                                if (data != null) {
                                    answeredCount = data.getAnsweredCountItems();
                                    totalCount = data.getTotalCountItems();
                                }
                            } catch (Exception e) {
                                TestLog.e(TAG, e);
                            }

                            infoText = String.format(mContext.getString(R.string.test_parent_result),
                                    String.valueOf(answeredCount), String.valueOf(totalCount));

                            testTime = item.getEndTime() - item.getStartTime();
                            info = new ParentTestSurveyResultInfo(index, testTime, infoText, item);
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
                RealmResults<ParentTestSurveyResult> rResult = realm.where(ParentTestSurveyResult.class).equalTo(ParentTestSurveyResult.FILED_SURVEY_ID, surveyId).findAll();

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

        try {
            Realm realm = DataProvider.getInstance(mContext).getRealm();

            if (realm != null && !realm.isClosed()) {
                long count = realm.where(ParentTestSurveyResult.class)
                        .equalTo(ParentTestSurveyResult.FILED_SURVEY_ID, surveyId).count();

                if (count > 0) {
                    result = true;
                }
            }
        } catch (Exception e) {
            TestLog.e(TAG, e);
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
                List<ParentTestSurveyResult> dataResult = realm.where(ParentTestSurveyResult.class).equalTo(ParentTestSurveyResult.FILED_SURVEY_ID, surveyId).findAll();
                int count = 0;

                if (dataResult != null && dataResult.size() > 0) {
                    for (int i = 0; i < dataResult.size(); i++) {
                        ParentTestSurveyResult item = dataResult.get(i);

                        if (item.getIdServer() == -1) {
                            count++;
                        }

                        TestData data = null;

                        try {
                            Gson gson = new Gson();
                            data = gson.fromJson(item.getResultFiles(), TestData.class);
                        } catch (Exception e) {
                            TestLog.e(TAG, e);
                        }

                        if (data != null) {
                            if (data.getFilePath() != null && !data.getFilePath().isEmpty() && item.getIdAttachmentServer() == -1) {
                                count++;
                            }
                        }
                    }
                }

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
        return 6;
    }

    @Override
    public int getIndexProcessing() {
        return 5;
    }

    @Override
    public boolean makeDump(Context context, int surveyId, int attempt, String path) {
        boolean result = false;

        try {
            ArrayList<TestData> results = new ArrayList<>();

            if (mContext != null) {
                Realm realm = DataProvider.getInstance(mContext).getRealm();

                if (realm != null && !realm.isClosed()) {
                    List<ParentTestSurveyResult> rResult = realm.where(ParentTestSurveyResult.class).equalTo(ParentTestSurveyResult.FILED_SURVEY_ID, surveyId).findAll();

                    if (rResult != null) {
                        for (ParentTestSurveyResult item : rResult) {
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
                    String fileName = ParentTest.class.getSimpleName() + "_" + surveyId + "_" + attempt + ".txt";
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
