package com.reading.start.tests.test_coloring;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.reading.start.tests.test_coloring.data.DataProvider;
import com.reading.start.tests.test_coloring.data.DataServerManager;
import com.reading.start.tests.test_coloring.data.entity.DataAssessmentItem;
import com.reading.start.tests.test_coloring.data.entity.DataUploadSurveyAttachmentItem;
import com.reading.start.tests.test_coloring.data.entity.DataUploadSurveyResultItem;
import com.reading.start.tests.test_coloring.data.response.ResponseAssessmentData;
import com.reading.start.tests.test_coloring.data.response.ResponseDownloadSurveyAttachmentData;
import com.reading.start.tests.test_coloring.data.response.ResponseDownloadSurveyResultData;
import com.reading.start.tests.test_coloring.domain.entity.ColoringTestContent;
import com.reading.start.tests.test_coloring.domain.entity.ColoringTestSurveyAttachments;
import com.reading.start.tests.test_coloring.domain.entity.ColoringTestSurveyResult;
import com.reading.start.tests.test_coloring.domain.entity.ColoringTestSurveyResultInfo;
import com.reading.start.tests.test_coloring.domain.entity.TestData;
import com.reading.start.tests.test_coloring.ui.activities.MainActivity;
import com.reading.start.tests.test_coloring.utils.BitmapUtils;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Class represent coloring test.
 */
public class ColoringTest implements ITestModule {
    public static final String TYPE = "com.reading.start.tests.test_coloring";

    private static final String TAG = ColoringTest.class.getSimpleName();

    private Context mContext;

    private ArrayList<ITestModuleResult> mResults = null;

    private static ColoringTest sInstance = null;

    public static ColoringTest getInstance() {
        return sInstance;
    }

    public ColoringTest(Context context) {
        mContext = context;
        sInstance = this;
    }

    @Override
    public String getSurveyType() {
        return TYPE;
    }

    @Override
    public int getNameResource() {
        return R.string.test_coloring_name;
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
            long countRecords = realm.where(ColoringTestContent.class).count();

            String date = null;

            if (pref.getSyncTime() > 0) {
                date = ISO8601.fromTime(pref.getSyncTime());
            }

            ResponseAssessmentData response = DataServerManager.getInstance(mContext).getContent(token, date);

            if (response != null) {
                result = true;

                if (response.getData() != null && response.getData().getAssessment() != null) {
                    RealmResults<ColoringTestContent> results = realm.where(ColoringTestContent.class).findAll();
                    realm.beginTransaction();
                    results.deleteAllFromRealm();
                    DataAssessmentItem data = response.getData().getAssessment();
                    ColoringTestContent content = new ColoringTestContent();
                    content.setId(data.getId());
                    content.setName(data.getName());

                    String filePath = downloadFile(data.getImage1(), com.reading.start.tests.test_coloring.Constants.IMAGE_FILE_NAME_1);
                    content.setImg1(filePath);
                    content.setImgName1(getNameFromUrl(data.getImage1()));
                    content.setImg1Dump(BitmapUtils.makeBitmapDump(BitmapFactory.decodeFile(filePath)));

                    if (filePath == null) {
                        result = false;
                    }

                    if (result) {
                        filePath = downloadFile(data.getImage2(), com.reading.start.tests.test_coloring.Constants.IMAGE_FILE_NAME_2);
                        content.setImg2(filePath);
                        content.setImgName2(getNameFromUrl(data.getImage2()));
                        content.setImg2Dump(BitmapUtils.makeBitmapDump(BitmapFactory.decodeFile(filePath)));
                    }

                    if (filePath == null) {
                        result = false;
                    }

                    if (result) {
                        filePath = downloadFile(data.getImage3(), com.reading.start.tests.test_coloring.Constants.IMAGE_FILE_NAME_3);
                        content.setImg3(filePath);
                        content.setImgName3(getNameFromUrl(data.getImage3()));
                        content.setImg3Dump(BitmapUtils.makeBitmapDump(BitmapFactory.decodeFile(filePath)));
                    }

                    if (filePath == null) {
                        result = false;
                    }

                    if (result) {
                        filePath = downloadFile(data.getImage4(), com.reading.start.tests.test_coloring.Constants.IMAGE_FILE_NAME_4);
                        content.setImg4(filePath);
                        content.setImgName4(getNameFromUrl(data.getImage4()));
                        content.setImg4Dump(BitmapUtils.makeBitmapDump(BitmapFactory.decodeFile(filePath)));
                    }

                    if (filePath == null) {
                        result = false;
                    }

                    if (result) {
                        filePath = downloadFile(data.getImage5(), com.reading.start.tests.test_coloring.Constants.IMAGE_FILE_NAME_5);
                        content.setImg5(filePath);
                        content.setImgName5(getNameFromUrl(data.getImage5()));
                        content.setImg5Dump(BitmapUtils.makeBitmapDump(BitmapFactory.decodeFile(filePath)));
                    }

                    if (filePath == null) {
                        result = false;
                    }

                    if (result) {
                        filePath = downloadFile(data.getImage6(), com.reading.start.tests.test_coloring.Constants.IMAGE_FILE_NAME_6);
                        content.setImg6(filePath);
                        content.setImgName6(getNameFromUrl(data.getImage6()));
                        content.setImg6Dump(BitmapUtils.makeBitmapDump(BitmapFactory.decodeFile(filePath)));
                    }

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
                    realm.beginTransaction();
                    Number currentIdNum = realm.where(ColoringTestSurveyResult.class).max(ColoringTestSurveyResult.FILED_ID);
                    int nextId;

                    if (currentIdNum == null) {
                        nextId = 0;
                    } else {
                        nextId = currentIdNum.intValue() + 1;
                    }

                    Number attachmentIdNum = realm.where(ColoringTestSurveyAttachments.class).max(ColoringTestSurveyAttachments.FILED_ID);
                    int attachmentId;

                    if (attachmentIdNum == null) {
                        attachmentId = 0;
                    } else {
                        attachmentId = attachmentIdNum.intValue() + 1;
                    }

                    for (int i = 0; i < data.getData().size(); i++) {
                        ColoringTestSurveyResult surveyResult = new ColoringTestSurveyResult();
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
                                ColoringTestSurveyAttachments surveyAttachment = new ColoringTestSurveyAttachments();
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
                List<ColoringTestSurveyResult> dataResult = realm.where(ColoringTestSurveyResult.class).equalTo(ColoringTestSurveyResult.FILED_SURVEY_ID, surveyId).findAll();

                if (dataResult != null) {
                    int index = 0;

                    for (int i = 0; i < dataResult.size(); i++) {
                        ColoringTestSurveyResult item = dataResult.get(i);
                        index++;

                        if (item.getIdServer() == -1) {
                            DataUploadSurveyResultItem resultItem = new DataUploadSurveyResultItem();
                            resultItem.setAssessmentType("coloring");
                            resultItem.setSurveyId(String.valueOf(surveyIdServer));
                            resultItem.setFileName("coloring_test_data_" + index + ".json");
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
                    List<ColoringTestSurveyAttachments> attachmentsResult = realm.where(ColoringTestSurveyAttachments.class).equalTo(ColoringTestSurveyAttachments.FILED_SURVEY_ID, surveyId).findAll();

                    if (attachmentsResult != null) {
                        for (int i = 0; i < attachmentsResult.size(); i++) {
                            ColoringTestSurveyAttachments item = attachmentsResult.get(i);

                            if (item.getIdServer() == -1) {
                                String fileName = "coloring_test_image_" + item.getId() + ".jpg";
                                File file = null;

                                try {
                                    Bitmap bitmap = BitmapUtils.bitmapFromBase64(item.getAttachmentFile());

                                    if (bitmap != null) {
                                        file = BitmapUtils.bitmapToFile(bitmap, fileName, mContext.getFilesDir());
                                    }

                                    DataUploadSurveyAttachmentItem resultItem = new DataUploadSurveyAttachmentItem();
                                    resultItem.setAssessmentType("coloring");
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
                                    result.setMessage(mContext.getString(R.string.test_coloring_upload_error));
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
                    List<ColoringTestSurveyResult> rResult = realm.where(ColoringTestSurveyResult.class).equalTo(ColoringTestSurveyResult.FILED_SURVEY_ID, surveyId).findAll();

                    if (rResult != null) {
                        ColoringTestSurveyResultInfo info;
                        int index = 1;
                        long testTime;
                        String infoText = null;

                        for (ColoringTestSurveyResult item : rResult) {
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
                            info = new ColoringTestSurveyResultInfo(index, testTime, infoText, interrupted, item);
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
                RealmResults<ColoringTestSurveyResult> rResult = realm.where(ColoringTestSurveyResult.class).equalTo(ColoringTestSurveyResult.FILED_SURVEY_ID, surveyId).findAll();
                RealmResults<ColoringTestSurveyAttachments> rResultAttachments = realm.where(ColoringTestSurveyAttachments.class).equalTo(ColoringTestSurveyAttachments.FILED_SURVEY_ID, surveyId).findAll();

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
                long count = realm.where(ColoringTestSurveyResult.class)
                        .equalTo(ColoringTestSurveyResult.FILED_SURVEY_ID, surveyId).count();

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
                long count = realm.where(ColoringTestSurveyResult.class)
                        .equalTo(ColoringTestSurveyResult.FILED_ID_SERVER, -1).count();

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
        return 5;
    }

    @Override
    public int getIndexProcessing() {
        return 2;
    }

    @Override
    public boolean makeDump(Context context, int surveyId, int attempt, String path) {
        boolean result = false;

        try {
            ArrayList<TestData> results = new ArrayList<>();

            if (mContext != null) {
                Realm realm = DataProvider.getInstance(mContext).getRealm();

                if (realm != null && !realm.isClosed()) {
                    // need only for testing
                    //makeDumpOfContent(realm, path);

                    List<ColoringTestSurveyResult> rResult = realm.where(ColoringTestSurveyResult.class).equalTo(ColoringTestSurveyResult.FILED_SURVEY_ID, surveyId).findAll();

                    if (rResult != null) {
                        for (ColoringTestSurveyResult item : rResult) {
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
                    String fileName = ColoringTest.class.getSimpleName() + "_" + surveyId + "_" + attempt + ".txt";
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

    private void makeDumpOfContent(Realm realm, String path) {
        try {
            if (realm != null && path != null) {
                ColoringTestContent results = realm.where(ColoringTestContent.class).findFirst();

                try {
                    String fileName = ColoringTest.class.getSimpleName() + "_content_image_1.json";
                    File file = new File(path, fileName);

                    if (file.exists()) {
                        file.delete();
                    }

                    FileWriter out = new FileWriter(file);
                    out.write(results.getImg1Dump());
                    out.close();
                } catch (Exception e) {
                    TestLog.e(TAG, "makeDumpOfContent", e);
                }

                try {
                    String fileName = ColoringTest.class.getSimpleName() + "_content_image_2.json";
                    File file = new File(path, fileName);

                    if (file.exists()) {
                        file.delete();
                    }

                    FileWriter out = new FileWriter(file);
                    out.write(results.getImg2Dump());
                    out.close();
                } catch (Exception e) {
                    TestLog.e(TAG, "makeDumpOfContent", e);
                }

                try {
                    String fileName = ColoringTest.class.getSimpleName() + "_content_image_3.json";
                    File file = new File(path, fileName);

                    if (file.exists()) {
                        file.delete();
                    }

                    FileWriter out = new FileWriter(file);
                    out.write(results.getImg3Dump());
                    out.close();
                } catch (Exception e) {
                    TestLog.e(TAG, "makeDumpOfContent", e);
                }

                try {
                    String fileName = ColoringTest.class.getSimpleName() + "_content_image_4.json";
                    File file = new File(path, fileName);

                    if (file.exists()) {
                        file.delete();
                    }

                    FileWriter out = new FileWriter(file);
                    out.write(results.getImg4Dump());
                    out.close();
                } catch (Exception e) {
                    TestLog.e(TAG, "makeDumpOfContent", e);
                }

                try {
                    String fileName = ColoringTest.class.getSimpleName() + "_content_image_5.json";
                    File file = new File(path, fileName);

                    if (file.exists()) {
                        file.delete();
                    }

                    FileWriter out = new FileWriter(file);
                    out.write(results.getImg5Dump());
                    out.close();
                } catch (Exception e) {
                    TestLog.e(TAG, "makeDumpOfContent", e);
                }

                try {
                    String fileName = ColoringTest.class.getSimpleName() + "_content_image_6.json";
                    File file = new File(path, fileName);

                    if (file.exists()) {
                        file.delete();
                    }

                    FileWriter out = new FileWriter(file);
                    out.write(results.getImg6Dump());
                    out.close();
                } catch (Exception e) {
                    TestLog.e(TAG, "makeDumpOfContent", e);
                }
            }
        } catch (Exception e) {
            TestLog.e(TAG, e);
        }
    }

    private String getNameFromUrl(String url) {
        String result = "";

        try {
            if (url != null) {
                int index = url.lastIndexOf("/");

                if (index > 0) {
                    result = url.substring(index + 1);
                }
            }
        } catch (Exception e) {
            TestLog.e(TAG, e);
        }

        return result;
    }
}
