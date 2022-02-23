package com.reading.start.tests.test_motor_following;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;

import com.google.gson.Gson;
import com.reading.start.tests.Constants;
import com.reading.start.tests.DownloadHelper;
import com.reading.start.tests.ITestModule;
import com.reading.start.tests.ITestModuleResult;
import com.reading.start.tests.TestLog;
import com.reading.start.tests.UploadResult;
import com.reading.start.tests.test_motor_following.data.DataProvider;
import com.reading.start.tests.test_motor_following.data.DataServerManager;
import com.reading.start.tests.test_motor_following.data.entity.DataUploadSurveyAttachmentItem;
import com.reading.start.tests.test_motor_following.data.entity.DataUploadSurveyResultItem;
import com.reading.start.tests.test_motor_following.data.response.ResponseDownloadSurveyAttachmentData;
import com.reading.start.tests.test_motor_following.data.response.ResponseDownloadSurveyResultData;
import com.reading.start.tests.test_motor_following.domain.entity.MotorFollowingTestSurveyAttachments;
import com.reading.start.tests.test_motor_following.domain.entity.MotorFollowingTestSurveyResult;
import com.reading.start.tests.test_motor_following.domain.entity.MotorFollowingTestSurveyResultInfo;
import com.reading.start.tests.test_motor_following.domain.entity.TestData;
import com.reading.start.tests.test_motor_following.ui.activities.MainActivity;
import com.reading.start.tests.test_motor_following.utils.BitmapUtils;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Class represent motor following test.
 */
public class MotorFollowingTest implements ITestModule {
    public static final String TYPE = "com.reading.start.tests.test_motor_following";

    private static final String TAG = MotorFollowingTest.class.getSimpleName();

    private Context mContext;

    private ArrayList<ITestModuleResult> mResults = null;

    private static MotorFollowingTest sInstance = null;

    public static MotorFollowingTest getInstance() {
        return sInstance;
    }

    public MotorFollowingTest(Context context) {
        mContext = context;
        sInstance = this;
    }

    @Override
    public String getSurveyType() {
        return TYPE;
    }

    @Override
    public int getNameResource() {
        return R.string.test_motor_following_name;
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
                ResponseDownloadSurveyAttachmentData dataAttachment = DataServerManager.getInstance(mContext).getSurveyAttachment(token, surveyIdServer);

                if (data != null && dataAttachment != null) {
                    realm.beginTransaction();

                    Number currentIdNum = realm.where(MotorFollowingTestSurveyResult.class).max(MotorFollowingTestSurveyResult.FILED_ID);
                    int nextId;

                    if (currentIdNum == null) {
                        nextId = 0;
                    } else {
                        nextId = currentIdNum.intValue() + 1;
                    }

                    Number attachmentIdNum = realm.where(MotorFollowingTestSurveyAttachments.class).max(MotorFollowingTestSurveyAttachments.FILED_ID);
                    int attachmentId;

                    if (attachmentIdNum == null) {
                        attachmentId = 0;
                    } else {
                        attachmentId = attachmentIdNum.intValue() + 1;
                    }

                    for (int i = 0; i < data.getData().size(); i++) {
                        MotorFollowingTestSurveyResult surveyResult = new MotorFollowingTestSurveyResult();
                        surveyResult.setId(nextId);
                        nextId++;
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
                                MotorFollowingTestSurveyAttachments surveyAttachment = new MotorFollowingTestSurveyAttachments();
                                surveyAttachment.setId(attachmentId);
                                attachmentId++;
                                surveyAttachment.setSurveyId(surveyId);
                                surveyAttachment.setSurveyResultId(surveyResult.getId());

                                // this is fake id, should be greater than -1
                                surveyAttachment.setIdServer(0);
                                Bitmap bitmap = DownloadHelper.downloadBitmap(dataAttachment.getData().get(i).getFiles().get(j));

                                if (bitmap != null) {
                                    surveyAttachment.setAttachmentFile(BitmapUtils.bitmapToBase64(bitmap));
                                }

                                realm.copyToRealmOrUpdate(surveyAttachment);
                            }
                        }
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
                List<MotorFollowingTestSurveyResult> dataResult = realm.where(MotorFollowingTestSurveyResult.class).equalTo(MotorFollowingTestSurveyResult.FILED_SURVEY_ID, surveyId).findAll();

                if (dataResult != null && dataResult.size() > 0) {
                    int index = 0;

                    for (int i = 0; i < dataResult.size(); i++) {
                        MotorFollowingTestSurveyResult item = dataResult.get(i);
                        index++;

                        if (item.getIdServer() == -1) {
                            DataUploadSurveyResultItem resultItem = new DataUploadSurveyResultItem();
                            resultItem.setAssessmentType("motoric_following");
                            resultItem.setSurveyId(String.valueOf(surveyIdServer));
                            resultItem.setFileName("motoric_following_test_data_" + index + ".json");
                            resultItem.setFileContent("application/json;base64," + Base64.encodeToString(item.getResultFiles().getBytes(), Base64.DEFAULT));
                            int serverId = DataServerManager.getInstance(mContext).addSurveyResult(token, resultItem);
                            TestLog.d(TAG, "serverId: " + serverId);
                            realm.beginTransaction();
                            item.setIdServer(serverId);
                            realm.commitTransaction();
                            result.setSuccess(serverId != -1);
                        }

                        if (!result.isSuccess()) {
                            break;
                        }
                    }
                }

                if (result.isSuccess()) {
                    // upload attachments
                    List<MotorFollowingTestSurveyAttachments> attachmentsResult = realm.where(MotorFollowingTestSurveyAttachments.class).equalTo(MotorFollowingTestSurveyAttachments.FILED_SURVEY_ID, surveyId).findAll();

                    if (attachmentsResult != null) {
                        for (int i = 0; i < attachmentsResult.size(); i++) {
                            MotorFollowingTestSurveyAttachments item = attachmentsResult.get(i);

                            if (item.getIdServer() == -1) {
                                String fileName = "motor_following_test_image_" + item.getId() + ".png";
                                File file = null;

                                try {
                                    Bitmap bitmap = BitmapUtils.bitmapFromBase64(item.getAttachmentFile());

                                    if (bitmap != null) {
                                        file = BitmapUtils.bitmapToFile(bitmap, fileName, mContext.getFilesDir());
                                    }

                                    DataUploadSurveyAttachmentItem resultItem = new DataUploadSurveyAttachmentItem();
                                    resultItem.setAssessmentType("motoric_following");
                                    int serverId = DataServerManager.getInstance(mContext).addSurveyAttachment(token, String.valueOf(surveyIdServer), resultItem, file);

                                    if (serverId != -1) {
                                        realm.beginTransaction();
                                        item.setIdServer(serverId);
                                        realm.commitTransaction();
                                    }

                                    result.setSuccess(serverId != -1);
                                } catch (Exception e) {
                                    TestLog.e(TAG, e);
                                } finally {
                                    if (file != null && file.exists()) {
                                        file.delete();
                                    }
                                }

                                if (!result.isSuccess()) {
                                    result.setMessage(mContext.getString(R.string.test_motor_following_upload_error));
                                    break;
                                }
                            }
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
                    List<MotorFollowingTestSurveyResult> rResult = realm.where(MotorFollowingTestSurveyResult.class).equalTo(MotorFollowingTestSurveyResult.FILED_SURVEY_ID, surveyId).findAll();

                    if (rResult != null) {
                        MotorFollowingTestSurveyResultInfo info;
                        int index = 1;
                        long testTime;
                        String infoText = null;

                        for (MotorFollowingTestSurveyResult item : rResult) {
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
                            info = new MotorFollowingTestSurveyResultInfo(index, testTime, infoText, interrupted, item);
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
                RealmResults<MotorFollowingTestSurveyResult> rResult = realm.where(MotorFollowingTestSurveyResult.class).equalTo(MotorFollowingTestSurveyResult.FILED_SURVEY_ID, surveyId).findAll();
                RealmResults<MotorFollowingTestSurveyAttachments> rResultAttachments = realm.where(MotorFollowingTestSurveyAttachments.class).equalTo(MotorFollowingTestSurveyAttachments.FILED_SURVEY_ID, surveyId).findAll();

                if (rResult != null) {
                    realm.beginTransaction();

                    if (mResults != null) {
                        mResults.clear();
                    }

                    rResult.deleteAllFromRealm();
                    rResultAttachments.deleteAllFromRealm();
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
                long count = realm.where(MotorFollowingTestSurveyResult.class)
                        .equalTo(MotorFollowingTestSurveyResult.FILED_SURVEY_ID, surveyId).count();

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
                long count = realm.where(MotorFollowingTestSurveyResult.class)
                        .equalTo(MotorFollowingTestSurveyResult.FILED_ID_SERVER, -1).count();

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
        return 3;
    }

    @Override
    public int getIndexProcessing() {
        return 3;
    }

    @Override
    public boolean makeDump(Context context, int surveyId, int attempt, String path) {
        boolean result = false;

        try {
            ArrayList<TestData> results = new ArrayList<>();

            if (mContext != null) {
                Realm realm = DataProvider.getInstance(mContext).getRealm();

                if (realm != null && !realm.isClosed()) {
                    List<MotorFollowingTestSurveyResult> rResult = realm.where(MotorFollowingTestSurveyResult.class).equalTo(MotorFollowingTestSurveyResult.FILED_SURVEY_ID, surveyId).findAll();

                    if (rResult != null) {
                        for (MotorFollowingTestSurveyResult item : rResult) {
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
                    String fileName = MotorFollowingTest.class.getSimpleName() + "_" + surveyId + "_" + attempt + ".txt";
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
