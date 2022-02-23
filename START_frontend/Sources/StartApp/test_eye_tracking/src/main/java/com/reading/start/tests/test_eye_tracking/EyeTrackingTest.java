package com.reading.start.tests.test_eye_tracking;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;

import com.google.gson.Gson;
import com.reading.start.sdk.core.CalibrateType;
import com.reading.start.sdk.core.DetectMode;
import com.reading.start.sdk.core.Settings;
import com.reading.start.sdk.core.SettingsFactory;
import com.reading.start.sdk.core.StartSdk;
import com.reading.start.sdk.core.StartSdkListener;
import com.reading.start.sdk.core.StartSdkState;
import com.reading.start.sdk.general.SdkLog;
import com.reading.start.tests.Constants;
import com.reading.start.tests.DownloadHelper;
import com.reading.start.tests.ISO8601;
import com.reading.start.tests.ITestModule;
import com.reading.start.tests.ITestModuleResult;
import com.reading.start.tests.ServerLog;
import com.reading.start.tests.TestLog;
import com.reading.start.tests.UploadResult;
import com.reading.start.tests.test_eye_tracking.data.DataProvider;
import com.reading.start.tests.test_eye_tracking.data.DataServerManager;
import com.reading.start.tests.test_eye_tracking.data.entity.DataAssessmentPairsItem;
import com.reading.start.tests.test_eye_tracking.data.entity.DataUploadSurveyAttachmentItem;
import com.reading.start.tests.test_eye_tracking.data.entity.DataUploadSurveyResultItem;
import com.reading.start.tests.test_eye_tracking.data.response.ResponseAssessmentPairsData;
import com.reading.start.tests.test_eye_tracking.data.response.ResponseDownloadSurveyAttachmentData;
import com.reading.start.tests.test_eye_tracking.data.response.ResponseDownloadSurveyResultData;
import com.reading.start.tests.test_eye_tracking.domain.entity.EyeTrackingTestContentPairs;
import com.reading.start.tests.test_eye_tracking.domain.entity.EyeTrackingTestSurveyResult;
import com.reading.start.tests.test_eye_tracking.domain.entity.EyeTrackingTestSurveyResultInfo;
import com.reading.start.tests.test_eye_tracking.domain.entity.TestData;
import com.reading.start.tests.test_eye_tracking.domain.entity.TestDataEyeTracking;
import com.reading.start.tests.test_eye_tracking.domain.entity.TestDataMerged;
import com.reading.start.tests.test_eye_tracking.domain.entity.TestDataStep;
import com.reading.start.tests.test_eye_tracking.domain.entity.TestDataTest;
import com.reading.start.tests.test_eye_tracking.ui.activities.MainActivity;

import org.opencv.core.Point;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Class represent eye-tracking test.
 */
public class EyeTrackingTest implements ITestModule {
    public static final String TYPE = "com.reading.start.tests.test_eye_tracking";

    private static final String TAG = EyeTrackingTest.class.getSimpleName();

    private static SimpleDateFormat mTimeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

    private Context mContext;

    private ArrayList<ITestModuleResult> mResults = new ArrayList<>();

    private static EyeTrackingTest sInstance = null;

    public static EyeTrackingTest getInstance() {
        return sInstance;
    }

    public EyeTrackingTest(Context context) {
        mContext = context;
        sInstance = this;
    }

    @Override
    public String getSurveyType() {
        return TYPE;
    }

    @Override
    public int getNameResource() {
        return R.string.test_eye_tracking_name;
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
                List<EyeTrackingTestSurveyResult> results = realm.where(EyeTrackingTestSurveyResult.class)
                        .equalTo(EyeTrackingTestSurveyResult.FILED_SURVEY_ID, surveyId).findAll();

                if (results != null && results.size() > 0 && attempt < results.size()) {
                    String value = results.get(attempt).getResultFiles();

                    try {
                        Gson gson = new Gson();
                        TestData data = gson.fromJson(value, TestData.class);

                        if (data != null) {
                            File mainFile = new File(data.getTestLooking().getFilePath());
                            File videoFile = mainFile.exists() ? mainFile : new File(mainFile.getParent(), com.reading.start.sdk.Constants.VIDEO_FILE_PREFIX_TEMP_TEST + mainFile.getName());

                            if (videoFile != null && videoFile.exists()) {
                                result = true;
                            }

                            if (!result) {
                                mainFile = new File(data.getTestAttention().getFilePath());
                                videoFile = mainFile.exists() ? mainFile : new File(mainFile.getParent(), com.reading.start.sdk.Constants.VIDEO_FILE_PREFIX_TEMP_TEST + mainFile.getName());

                                if (videoFile != null && videoFile.exists()) {
                                    result = true;
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
            }

            realm.close();
        } catch (Exception e) {
            TestLog.e(TAG, e);
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
            long countRecords = realm.where(EyeTrackingTestContentPairs.class).count();

            String date = null;

            if (pref.getSyncTime() > 0) {
                date = ISO8601.fromTime(pref.getSyncTime());
            }

            ResponseAssessmentPairsData responsePairs = (countRecords == 0) ?
                    DataServerManager.getInstance(mContext).getContentPairs(token, "") :
                    DataServerManager.getInstance(mContext).getContentPairs(token, date);

            if (responsePairs != null) {
                result = true;

                if (responsePairs.getData() != null && responsePairs.getData().getAssessment() != null) {
                    RealmResults<EyeTrackingTestContentPairs> results = realm.where(EyeTrackingTestContentPairs.class).findAll();
                    realm.beginTransaction();
                    results.deleteAllFromRealm();
                    DataAssessmentPairsItem data = responsePairs.getData().getAssessment();
                    EyeTrackingTestContentPairs content = new EyeTrackingTestContentPairs();
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
                    fileName = getFileNameFromUrl(data.getPair1Part1()) != null ? getFileNameFromUrl(data.getPair1Part1()) : com.reading.start.tests.test_eye_tracking.Constants.FILE_NAME_PAIR_1_PART_1;
                    file = new File(mContext.getFilesDir(), fileName);

                    if (file.exists() && needDelete) {
                        file.delete();
                    }

                    if (!file.exists()) {
                        filePath = downloadFile(data.getPair1Part1(), fileName);
                        content.setPair1part1(filePath);

                        if (filePath == null) {
                            result = false;
                        }
                    }

                    if (result) {
                        fileName = getFileNameFromUrl(data.getPair2Part1()) != null ? getFileNameFromUrl(data.getPair2Part1()) : com.reading.start.tests.test_eye_tracking.Constants.FILE_NAME_PAIR_2_PART_1;
                        file = new File(mContext.getFilesDir(), fileName);

                        if (file.exists() && needDelete) {
                            file.delete();
                        }

                        if (!file.exists()) {
                            filePath = downloadFile(data.getPair2Part1(), fileName);
                            content.setPair2part1(filePath);

                            if (filePath == null) {
                                result = false;
                            }
                        }
                    }

                    if (result) {
                        fileName = getFileNameFromUrl(data.getPair3Part1()) != null ? getFileNameFromUrl(data.getPair3Part1()) : com.reading.start.tests.test_eye_tracking.Constants.FILE_NAME_PAIR_3_PART_1;
                        file = new File(mContext.getFilesDir(), fileName);

                        if (file.exists() && needDelete) {
                            file.delete();
                        }

                        if (!file.exists()) {
                            filePath = downloadFile(data.getPair3Part1(), fileName);
                            content.setPair3part1(filePath);

                            if (filePath == null) {
                                result = false;
                            }
                        }
                    }

                    if (result) {
                        fileName = getFileNameFromUrl(data.getPair4Part1()) != null ? getFileNameFromUrl(data.getPair4Part1()) : com.reading.start.tests.test_eye_tracking.Constants.FILE_NAME_PAIR_4_PART_1;
                        file = new File(mContext.getFilesDir(), fileName);

                        if (file.exists() && needDelete) {
                            file.delete();
                        }

                        if (!file.exists()) {
                            filePath = downloadFile(data.getPair4Part1(), fileName);
                            content.setPair4part1(filePath);

                            if (filePath == null) {
                                result = false;
                            }
                        }
                    }

                    if (result) {
                        fileName = getFileNameFromUrl(data.getPair5Part1()) != null ? getFileNameFromUrl(data.getPair5Part1()) : com.reading.start.tests.test_eye_tracking.Constants.FILE_NAME_PAIR_5_PART_1;
                        file = new File(mContext.getFilesDir(), fileName);

                        if (file.exists() && needDelete) {
                            file.delete();
                        }

                        if (!file.exists()) {
                            filePath = downloadFile(data.getPair5Part1(), fileName);
                            content.setPair5part1(filePath);

                            if (filePath == null) {
                                result = false;
                            }
                        }
                    }

                    if (result) {
                        fileName = getFileNameFromUrl(data.getPair6Part1()) != null ? getFileNameFromUrl(data.getPair6Part1()) : com.reading.start.tests.test_eye_tracking.Constants.FILE_NAME_PAIR_6_PART_1;
                        file = new File(mContext.getFilesDir(), fileName);

                        if (file.exists() && needDelete) {
                            file.delete();
                        }

                        if (!file.exists()) {
                            filePath = downloadFile(data.getPair6Part1(), fileName);
                            content.setPair6part1(filePath);

                            if (filePath == null) {
                                result = false;
                            }
                        }
                    }

                    if (result) {
                        fileName = getFileNameFromUrl(data.getPair7Part1()) != null ? getFileNameFromUrl(data.getPair7Part1()) : com.reading.start.tests.test_eye_tracking.Constants.FILE_NAME_PAIR_7_PART_1;
                        file = new File(mContext.getFilesDir(), fileName);

                        if (file.exists() && needDelete) {
                            file.delete();
                        }

                        if (!file.exists()) {
                            filePath = downloadFile(data.getPair7Part1(), fileName);
                            content.setPair7part1(filePath);

                            if (filePath == null) {
                                result = false;
                            }
                        }
                    }

                    if (result) {
                        fileName = getFileNameFromUrl(data.getPair8Part1()) != null ? getFileNameFromUrl(data.getPair8Part1()) : com.reading.start.tests.test_eye_tracking.Constants.FILE_NAME_PAIR_8_PART_1;
                        file = new File(mContext.getFilesDir(), fileName);

                        if (file.exists() && needDelete) {
                            file.delete();
                        }

                        if (!file.exists()) {
                            filePath = downloadFile(data.getPair8Part1(), fileName);
                            content.setPair8part1(filePath);

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
                ResponseDownloadSurveyResultData data = DataServerManager.getInstance(mContext).getSurveyResultPair(token, surveyIdServer);
                ResponseDownloadSurveyAttachmentData dataAttachment1 = DataServerManager.getInstance(mContext).getSurveyAttachmentPair(token, surveyIdServer);
                ResponseDownloadSurveyAttachmentData dataAttachment2 = DataServerManager.getInstance(mContext).getSurveyAttachmentSlide(token, surveyIdServer);

                if (data != null && dataAttachment1 != null && dataAttachment2 != null) {
                    boolean isSuccess = false;
                    realm.beginTransaction();
                    Number currentIdNum = realm.where(EyeTrackingTestSurveyResult.class).max(EyeTrackingTestSurveyResult.FILED_ID);
                    int nextId;

                    if (currentIdNum == null) {
                        nextId = 0;
                    } else {
                        nextId = currentIdNum.intValue() + 1;
                    }

                    for (int i = 0; i < data.getData().size(); i++) {
                        EyeTrackingTestSurveyResult surveyResult = new EyeTrackingTestSurveyResult();
                        surveyResult.setId(nextId + i);
                        Gson gson = new Gson();
                        surveyResult.setResultFiles(gson.toJson(data.getData().get(i).getFileContent()));
                        surveyResult.setSurveyId(surveyId);
                        // this is fake id, should be greater than -1
                        surveyResult.setIdServer_1(0);
                        surveyResult.setIdServer_2(0);

                        TestData testData = convertToTestData(surveyResult.getResultFiles());

                        if (testData != null) {
                            surveyResult.setStartTime(testData.getTestLooking().getStartTime());

                            if (testData.getTestLooking().getEndTime() < testData.getTestAttention().getStartTime()) {
                                surveyResult.setEndTime(testData.getTestAttention().getEndTime());
                            } else {
                                surveyResult.setEndTime(testData.getTestLooking().getEndTime());
                            }
                        }

                        realm.copyToRealmOrUpdate(surveyResult);

                        if (i < dataAttachment1.getData().size()) {
                            for (int j = 0; j < dataAttachment1.getData().get(i).getFiles().size(); j++) {
                                if (testData != null && testData.getTestLooking().getFilePath() != null) {
                                    File videoFile = new File(testData.getTestLooking().getFilePath());
                                    isSuccess = DownloadHelper.downloadFile(dataAttachment1.getData().get(i).getFiles().get(0), videoFile);
                                    break;
                                }
                            }
                        }

                        if (isSuccess && i < dataAttachment2.getData().size()) {
                            for (int j = 0; j < dataAttachment2.getData().get(i).getFiles().size(); j++) {
                                if (testData != null && testData.getTestAttention().getFilePath() != null) {
                                    File videoFile = new File(testData.getTestAttention().getFilePath());
                                    isSuccess = DownloadHelper.downloadFile(dataAttachment2.getData().get(i).getFiles().get(0), videoFile);
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
        ServerLog.log("EYE-TRACKING-UPLOAD", "------------------ START UPLOAD ------------------");
        ArrayList<UploadResult> result1 = new ArrayList<>();
        ArrayList<UploadResult> result2 = new ArrayList<>();
        Realm realm = null;

        try {
            realm = DataProvider.getInstance(mContext).getRealm();

            if (realm != null && !realm.isClosed()) {
                // upload results
                List<EyeTrackingTestSurveyResult> dataResult = realm.where(EyeTrackingTestSurveyResult.class).equalTo(EyeTrackingTestSurveyResult.FILED_SURVEY_ID, surveyId).findAll();

                if (dataResult != null && dataResult.size() > 0) {
                    int index = 0;

                    for (int i = 0; i < dataResult.size(); i++) {
                        result1.add(new UploadResult(true));
                        result2.add(new UploadResult(true));
                        EyeTrackingTestSurveyResult item = dataResult.get(i);
                        index++;

                        TestData data = null;

                        try {
                            Gson gson = new Gson();
                            data = gson.fromJson(item.getResultFiles(), TestData.class);
                        } catch (Exception e) {
                            TestLog.e(TAG, e);
                            ServerLog.log("EYE-TRACKING-UPLOAD", "ERROR: " + e.getMessage());
                        }

                        if (!data.getTestLooking().isInterrupted()) {
                            if (item.getIdServer_1() == -1) {
                                if (processEyeTrackingLooking(realm, item)) {
                                    ServerLog.log("EYE-TRACKING-UPLOAD", "LOOKING PROCESSING SUCCESS");
                                    SdkLog.d(TAG, "processEyeTrackingLooking: success");
                                    ServerLog.log("EYE-TRACKING-UPLOAD", "LOOKING START UPLOAD");
                                    DataUploadSurveyResultItem resultItem = new DataUploadSurveyResultItem();
                                    resultItem.setAssessmentType("eye_tracking_pairs");
                                    resultItem.setSurveyId(String.valueOf(surveyIdServer));
                                    resultItem.setFileName("eye_tracking_pairs_test_data_" + index + ".json");
                                    resultItem.setFileContent("application/json;base64," + Base64.encodeToString(item.getResultFiles().getBytes(), Base64.DEFAULT));
                                    int serverId1 = DataServerManager.getInstance(mContext).addSurveyResult(token, resultItem);
                                    realm.beginTransaction();
                                    item.setIdServer_1(serverId1);
                                    item.setIdAttachmentServer_1(-1);
                                    realm.commitTransaction();
                                    result1.get(i).setSuccess(serverId1 != -1);
                                    SdkLog.d(TAG, "serverId1: " + serverId1);
                                } else {
                                    result1.get(i).setSuccess(false);
                                    ServerLog.log("EYE-TRACKING-UPLOAD", "LOOKING PROCESSING FAIL");
                                    result1.get(i).setMessage(mContext.getString(R.string.test_eye_tracking_upload_error_looking_test_processed));
                                }
                            } else {
                                result1.get(i).setSuccess(item.getIdServer_1() != -1);
                            }

                            if (result1.get(i).isSuccess()) {
                                if (item.getIdAttachmentServer_1() == -1) {
                                    File fileLook = null;

                                    if (data != null) {
                                        fileLook = new File(data.getTestLooking().getFilePath());
                                    }

                                    ServerLog.log("EYE-TRACKING-UPLOAD", "LOOKING UPLOAD SUCCESS");
                                    SdkLog.d(TAG, "upload looking: success");

                                    if (fileLook != null && fileLook.exists()) {
                                        DataUploadSurveyAttachmentItem resultVideo = new DataUploadSurveyAttachmentItem();
                                        resultVideo.setAssessmentType("eye_tracking_pairs");
                                        int serverIdAttachment = DataServerManager.getInstance(mContext).addSurveyAttachment(token, String.valueOf(surveyIdServer), resultVideo, fileLook);
                                        realm.beginTransaction();
                                        item.setIdAttachmentServer_1(serverIdAttachment);
                                        realm.commitTransaction();
                                        result1.get(i).setSuccess(serverIdAttachment != -1);
                                    }
                                } else {
                                    result1.get(i).setSuccess(true);
                                }
                            }

                            if (!result1.get(i).isSuccess()) {
                                SdkLog.d(TAG, "upload looking: fail");
                                ServerLog.log("EYE-TRACKING-UPLOAD", "LOOKING UPLOAD FAIL");
                                result1.get(i).setMessage(mContext.getString(R.string.test_eye_tracking_upload_error_looking_test_upload));
                            }
                        }

                        try {
                            Gson gson = new Gson();
                            data = gson.fromJson(item.getResultFiles(), TestData.class);
                        } catch (Exception e) {
                            TestLog.e(TAG, e);
                            ServerLog.log("EYE-TRACKING-UPLOAD", "ERROR: " + e.getMessage());
                        }

                        if (!data.getTestAttention().isInterrupted()) {
                            if (item.getIdServer_2() == -1) {
                                if (processEyeTrackingAttention(realm, item)) {
                                    ServerLog.log("EYE-TRACKING-UPLOAD", "ATTENTION PROCESSING SUCCESS");
                                    SdkLog.d(TAG, "processEyeTrackingAttention: success");
                                    ServerLog.log("EYE-TRACKING-UPLOAD", "ATTENTION START UPLOAD");
                                    DataUploadSurveyResultItem resultItem = new DataUploadSurveyResultItem();
                                    resultItem.setAssessmentType("eye_tracking_slide");
                                    resultItem.setSurveyId(String.valueOf(surveyIdServer));
                                    resultItem.setFileName("eye_tracking_slide_test_data_" + index + ".json");
                                    resultItem.setFileContent("application/json;base64," + Base64.encodeToString(item.getResultFiles().getBytes(), Base64.DEFAULT));
                                    int serverId2 = DataServerManager.getInstance(mContext).addSurveyResult(token, resultItem);
                                    realm.beginTransaction();
                                    item.setIdServer_2(serverId2);
                                    item.setIdAttachmentServer_2(-1);
                                    realm.commitTransaction();
                                    result2.get(i).setSuccess(serverId2 != -1);
                                    SdkLog.d(TAG, "serverId2: " + serverId2);
                                } else {
                                    result2.get(i).setSuccess(false);
                                    ServerLog.log("EYE-TRACKING-UPLOAD", "ATTENTION PROCESSING FAIL");
                                    result2.get(i).setMessage(mContext.getString(R.string.test_eye_tracking_upload_error_attention_test_processed));
                                }
                            } else {
                                result2.get(i).setSuccess(item.getIdServer_2() != -1);
                            }

                            if (result2.get(i).isSuccess()) {
                                if (item.getIdAttachmentServer_2() == -1) {
                                    File fileAttention = null;

                                    if (data != null) {
                                        fileAttention = new File(data.getTestAttention().getFilePath());
                                    }

                                    ServerLog.log("EYE-TRACKING-UPLOAD", "ATTENTION UPLOAD SUCCESS");
                                    SdkLog.d(TAG, "upload attention: success");

                                    if (fileAttention != null && fileAttention.exists()) {
                                        DataUploadSurveyAttachmentItem resultVideo = new DataUploadSurveyAttachmentItem();
                                        resultVideo.setAssessmentType("eye_tracking_slide");
                                        int serverIdAttachment = DataServerManager.getInstance(mContext).addSurveyAttachment(token, String.valueOf(surveyIdServer), resultVideo, fileAttention);
                                        realm.beginTransaction();
                                        item.setIdAttachmentServer_2(serverIdAttachment);
                                        realm.commitTransaction();
                                        result2.get(i).setSuccess(serverIdAttachment != -1);
                                    }
                                } else {
                                    result2.get(i).setSuccess(true);
                                }
                            }

                            if (!result2.get(i).isSuccess()) {
                                SdkLog.d(TAG, "upload attention: false");
                                ServerLog.log("EYE-TRACKING-UPLOAD", "ATTENTION UPLOAD FAIL");
                                result2.get(i).setMessage(mContext.getString(R.string.test_eye_tracking_upload_error_attention_test_upload));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            TestLog.e(TAG, e);
            ServerLog.log("EYE-TRACKING-UPLOAD", "ERROR: " + e.getMessage());
        } finally {
            if (realm != null) {
                realm.close();
            }
        }

        if (BuildConfig.MAKE_TEST_DATA_DUMP) {
            String root = Environment.getExternalStorageDirectory().toString();
            File folder = new File(root + "/" + com.reading.start.tests.test_eye_tracking.Constants.TEST_DUMP_FOLDER);

            if (!folder.exists()) {
                try {
                    folder.mkdir();
                } catch (Exception e) {
                    TestLog.e(TAG, e);
                }
            }

            // this code make dump of test data
            if (folder.exists()) {
                makeDump(mContext, surveyId, 0, folder.getPath());
            }
        }

        UploadResult result = new UploadResult(true);

        for (UploadResult rItem : result1) {
            if (!rItem.isSuccess()) {
                result.setSuccess(false);

                if (result.getMessage() == null || result.getMessage().isEmpty()) {
                    result.setMessage(mContext.getString(R.string.test_eye_tracking_upload_error));
                }

                break;
            }
        }

        if (result.isSuccess()) {
            for (UploadResult rItem : result2) {
                if (!rItem.isSuccess()) {
                    result.setSuccess(false);

                    if (result.getMessage() == null || result.getMessage().isEmpty()) {
                        result.setMessage(mContext.getString(R.string.test_eye_tracking_upload_error));
                    }

                    break;
                }
            }
        }

        ServerLog.log("EYE-TRACKING-UPLOAD", "REQUEST SUCCESS: " + (!result.isSuccess() ? "fail" : "success"));
        ServerLog.log("EYE-TRACKING-UPLOAD", " ------------------ END UPLOAD ------------------");
        return result;
    }

    private boolean processEyeTrackingLooking(Realm realm, EyeTrackingTestSurveyResult item) {
        ServerLog.log("EYE-TRACKING-UPLOAD", "LOOKING PROCESS START");
        boolean result = false;
        long startTime = Calendar.getInstance().getTimeInMillis();

        try {
            if (item != null) {
                Gson gson = new Gson();
                TestData data = gson.fromJson(item.getResultFiles(), TestData.class);

                if (data != null) {
                    ServerLog.log("EYE-TRACKING-UPLOAD", "LOOKING DATA EXIST");
                    result = processTest(data.getTestLooking(), false);

                    if (result) {
                        ServerLog.log("EYE-TRACKING-UPLOAD", "LOOKING VIDEO PROCESSED: success");
                        // set data for looking test
                        data.getTestLooking().setGazeDetection(data.getTestLooking().calculateGazeDetection());
                        data.getTestLooking().setGazeOnTheScreen(data.getTestLooking().calculateGazeOnScreen());
                        data.getTestLooking().setCalibrationQuality(data.getTestLooking().calculateGazeQuality());

                        SdkLog.d(TAG, "processEyeTracking, Looking: GazeDetection = " + data.getTestLooking().getGazeDetection()
                                + ", GazeOnTheScreen = " + data.getTestLooking().getGazeOnTheScreen()
                                + ", CalibrationQuality = " + data.getTestLooking().getCalibrationQuality());

                        // create merged data
                        processMergeTestResults(data.getTestLooking());
                    } else {
                        ServerLog.log("EYE-TRACKING-UPLOAD", "LOOKING VIDEO PROCESSED: false");
                    }
                } else {
                    ServerLog.log("EYE-TRACKING-UPLOAD", "LOOKING DATA EMPTY");
                }

                if (result) {
                    realm.beginTransaction();
                    String jsonValue = gson.toJson(data);
                    SdkLog.d(TAG, "processEyeTracking, json:" + jsonValue);
                    item.setResultFiles(jsonValue);
                    realm.commitTransaction();
                }
            }
        } catch (Exception e) {
            TestLog.e(TAG, e);
            result = false;
            ServerLog.log("EYE-TRACKING-UPLOAD", "ERROR: " + e.getMessage());
        } finally {
            try {
                long processingTime = Calendar.getInstance().getTimeInMillis() - startTime;
                String time = formatDuration(processingTime);
                ServerLog.log("EYE-TRACKING-UPLOAD", "LOOKING PROCESSING TIME: " + time);
            } catch (Exception e) {
                TestLog.e(TAG, e);
                ServerLog.log("EYE-TRACKING-UPLOAD", "ERROR: " + e.getMessage());
            }
        }

        ServerLog.log("EYE-TRACKING-UPLOAD", "LOOKING PROCESS: " + (result ? "true" : "false"));
        ServerLog.log("EYE-TRACKING-UPLOAD", "LOOKING PROCESS END");
        return result;
    }

    private boolean processEyeTrackingAttention(Realm realm, EyeTrackingTestSurveyResult item) {
        ServerLog.log("EYE-TRACKING-UPLOAD", "ATTENTION PROCESS START");
        boolean result = false;
        long startTime = Calendar.getInstance().getTimeInMillis();

        try {
            if (item != null) {
                Gson gson = new Gson();
                TestData data = gson.fromJson(item.getResultFiles(), TestData.class);

                if (data != null) {
                    ServerLog.log("EYE-TRACKING-UPLOAD", "ATTENTION DATA EXIST");
                    result = processTest(data.getTestAttention(), true);

                    if (result) {
                        ServerLog.log("EYE-TRACKING-UPLOAD", "ATTENTION VIDEO PROCESSED: success");
                        // set data for attention test
                        data.getTestAttention().setGazeDetection(data.getTestAttention().calculateGazeDetection());
                        data.getTestAttention().setGazeOnTheScreen(data.getTestAttention().calculateGazeOnScreen());
                        data.getTestAttention().setCalibrationQuality(data.getTestAttention().calculateGazeQuality());

                        SdkLog.d(TAG, "processEyeTracking, Attention: GazeDetection = " + data.getTestAttention().getGazeDetection()
                                + ", GazeOnTheScreen = " + data.getTestAttention().getGazeOnTheScreen()
                                + ", CalibrationQuality = " + data.getTestAttention().getCalibrationQuality());

                        // create merged data
                        processMergeTestResults(data.getTestAttention());
                    } else {
                        ServerLog.log("EYE-TRACKING-UPLOAD", "ATTENTION VIDEO PROCESSED: fail");
                    }
                } else {
                    ServerLog.log("EYE-TRACKING-UPLOAD", "ATTENTION DATA EMPTY");
                }

                if (result) {
                    realm.beginTransaction();
                    String jsonValue = gson.toJson(data);
                    SdkLog.d(TAG, "processEyeTracking, json:" + jsonValue);
                    item.setResultFiles(jsonValue);
                    realm.commitTransaction();
                }
            }
        } catch (Exception e) {
            TestLog.e(TAG, e);
            result = false;
            ServerLog.log("EYE-TRACKING-UPLOAD", "ERROR: " + e.getMessage());
        } finally {
            try {
                long processingTime = Calendar.getInstance().getTimeInMillis() - startTime;
                String time = formatDuration(processingTime);
                ServerLog.log("EYE-TRACKING-UPLOAD", "ATTENTION PROCESSING TIME: " + time);
            } catch (Exception e) {
                TestLog.e(TAG, e);
                ServerLog.log("EYE-TRACKING-UPLOAD", "ERROR: " + e.getMessage());
            }
        }

        ServerLog.log("EYE-TRACKING-UPLOAD", "ATTENTION PROCESS END");
        return result;
    }

    private void processMergeTestResults(TestDataTest test) {
        if (test != null && test.getItemsStimulus() != null && test.getItemsEyeTracking() != null) {
            TestDataMerged mItem = null;
            test.getItemsMerged().clear();
            test.getItemsMergedQuality().clear();

            for (TestDataEyeTracking item : test.getItemsEyeTracking()) {
                mItem = new TestDataMerged(item.getTime(), item.getX(), item.getY(), item.isValid(), item.getGazeOut(), "", "", "", "");
                test.getItemsMerged().add(mItem);
            }

            for (TestDataMerged item : test.getItemsMerged()) {
                updateTestDataMergedItem(item, test.getItemsStimulus());
            }

            for (TestDataEyeTracking item : test.getItemsEyeTrackingQuality()) {
                mItem = new TestDataMerged(item.getTime(), item.getX(), item.getY(), item.isValid(), item.getGazeOut(), "", "", "", "");
                test.getItemsMergedQuality().add(mItem);
            }

            for (TestDataMerged item : test.getItemsMergedQuality()) {
                updateTestDataMergedItem(item, test.getItemsStimulusQuality());
            }
        }
    }

    private void updateTestDataMergedItem(TestDataMerged item, ArrayList<TestDataStep> stimulus) {
        if (item != null && stimulus != null && stimulus.size() > 0) {
            boolean updated = false;

            for (int i = stimulus.size(); i > 0; i--) {
                if (stimulus.get(i - 1).getItems() != null && stimulus.get(i - 1).getItems().size() > 0) {
                    for (int j = stimulus.get(i - 1).getItems().size(); j > 0; j--) {
                        if (stimulus.get(i - 1).getItems().get(j - 1).getTime() <= item.getTime()) {
                            item.setStimulusName(stimulus.get(i - 1).getName());
                            item.setStimulusVideoName(stimulus.get(i - 1).getVideoName());
                            item.setStimulusAppear(stimulus.get(i - 1).getItems().get(j - 1).getStimulusAppear());
                            item.setStimulusSide(stimulus.get(i - 1).getItems().get(j - 1).getStimulusSide());
                            updated = true;
                            break;
                        }
                    }
                }

                if (updated) {
                    break;
                }
            }
        }
    }

    private boolean processTest(TestDataTest test, boolean isX3Mode) {
        boolean result = false;

        try {
            File processedFile = new File(test.getFilePath());

            if (test.isVideoProcessed() || (processedFile.exists() && processedFile.length() > 0)) {
                test.setVideoProcessed(true);
                result = true;
            } else if (!test.isInterrupted()) {
                String baseAbsolutePath = test.getFilePath();
                File mainFile = new File(baseAbsolutePath);

                Settings settings = isX3Mode ? SettingsFactory.getReleaseDetect3x1Postprocessing()
                        : SettingsFactory.getReleaseDetect2x1Postprocessing();
                settings.getGeneral().setViewFile(baseAbsolutePath);
                StartSdk.getInstance(mContext).setSettings(settings);
                boolean allFilesExist = true;

                if (StartSdk.getInstance().getCalibrationVideoPath() != null && StartSdk.getInstance().getCalibrationVideoPath().exists()) {
                    ServerLog.log("EYE-TRACKING-PROCESSING", "CALIBRATION VIDEO EXIST: true");
                } else {
                    allFilesExist = false;
                    ServerLog.log("EYE-TRACKING-PROCESSING", "CALIBRATION VIDEO EXIST: false");
                }

                if (StartSdk.getInstance().getPostCalibrationVideoPath() != null && StartSdk.getInstance().getPostCalibrationVideoPath().exists()) {
                    ServerLog.log("EYE-TRACKING-PROCESSING", "POST CALIBRATION VIDEO EXIST: true");
                } else {
                    allFilesExist = false;
                    ServerLog.log("EYE-TRACKING-PROCESSING", "POST CALIBRATION VIDEO EXIST: false");
                }

                if (StartSdk.getInstance().getTestVideoPath() != null && StartSdk.getInstance().getTestVideoPath().exists()) {
                    ServerLog.log("EYE-TRACKING-PROCESSING", "TEST VIDEO EXIST: true");
                } else {
                    allFilesExist = false;
                    ServerLog.log("EYE-TRACKING-PROCESSING", "TEST VIDEO EXIST: false");
                }

                if (allFilesExist && mainFile.exists()) {
                    mainFile.delete();
                }

                if (allFilesExist) {
                    AtomicBoolean stopProcessing = new AtomicBoolean();
                    AtomicBoolean postCalibrationProcessing = new AtomicBoolean(false);
                    AtomicBoolean testProcessing = new AtomicBoolean(false);
                    test.getItemsEyeTrackingQuality().clear();
                    test.getItemsEyeTracking().clear();

                    result = StartSdk.getInstance().processVideo(settings, stopProcessing, new StartSdkListenerImplement() {
                        @Override
                        public void onFrameProcessed(DetectMode mode, int col, int row, boolean isEyeDetected, boolean gazeOutside, long timeStamp) {
                            SdkLog.d(TAG, "onFrameProcessed: col=" + col + ", row=" + row);

                            float x = col;
                            float y = row;
                            TestDataEyeTracking eyeTrackingItem = new TestDataEyeTracking(x, y, timeStamp, isEyeDetected ? 1 : 0, gazeOutside ? 1 : 0);

                            if (postCalibrationProcessing.get()) {
                                test.getItemsEyeTrackingQuality().add(eyeTrackingItem);
                            }

                            if (testProcessing.get()) {
                                test.getItemsEyeTracking().add(eyeTrackingItem);
                            }
                        }

                        @Override
                        public void onPostProcessingPostCalibrationStart() {
                            SdkLog.d(TAG, "onPostProcessingPostCalibrationStart");
                            postCalibrationProcessing.set(true);
                        }

                        @Override
                        public void onPostProcessingPostCalibrationEnd() {
                            SdkLog.d(TAG, "onPostProcessingPostCalibrationEnd");
                            postCalibrationProcessing.set(false);
                        }

                        @Override
                        public void onPostProcessingTestStart() {
                            SdkLog.d(TAG, "onPostProcessingTestStart");
                            testProcessing.set(true);
                        }

                        @Override
                        public void onPostProcessingTestEnd() {
                            SdkLog.d(TAG, "onPostProcessingTestEnd");
                            testProcessing.set(false);
                        }
                    });
                } else {
                    result = false;
                }

                if (result) {
                    test.setVideoProcessed(true);
                    File pathCalibration = StartSdk.getInstance().getCalibrationVideoPath();

                    if (pathCalibration != null && pathCalibration.exists()) {
                        pathCalibration.delete();
                    }

                    File pathPostCalibration = StartSdk.getInstance().getPostCalibrationVideoPath();

                    if (pathPostCalibration != null && pathPostCalibration.exists()) {
                        pathPostCalibration.delete();
                    }

                    File pathTest = StartSdk.getInstance().getTestVideoPath();

                    if (pathTest != null && pathTest.exists()) {
                        pathTest.delete();
                    }
                } else {
                    test.setVideoProcessed(false);
                }
            }
        } catch (Exception e) {
            TestLog.e(TAG, e);
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
                    List<EyeTrackingTestSurveyResult> rResult = realm.where(EyeTrackingTestSurveyResult.class).equalTo(EyeTrackingTestSurveyResult.FILED_SURVEY_ID, surveyId).findAll();

                    if (rResult != null) {
                        EyeTrackingTestSurveyResultInfo info;
                        int index = 1;
                        long testTime;
                        String infoText = null;

                        for (EyeTrackingTestSurveyResult item : rResult) {
                            String value = item.getResultFiles();
                            int gazeDetection = 0;
                            int gazeOnScreen = 0;

                            try {
                                Gson gson = new Gson();
                                TestData data = gson.fromJson(value, TestData.class);

                                if (data != null) {
                                    gazeDetection = (data.getTestLooking().getGazeDetection() + data.getTestAttention().getGazeDetection()) / 2;
                                    gazeOnScreen = (data.getTestLooking().getGazeOnTheScreen() + data.getTestAttention().getGazeOnTheScreen()) / 2;
                                }
                            } catch (Exception e) {
                                TestLog.e(TAG, e);
                            }

                            if (gazeDetection == 0 && gazeDetection == 0) {
                                infoText = null;
                            } else {
                                infoText = String.format(mContext.getString(R.string.test_eye_tracking_result),
                                        String.valueOf(gazeDetection) + "%", String.valueOf(gazeOnScreen) + "%");
                            }

                            testTime = item.getEndTime() - item.getStartTime();
                            info = new EyeTrackingTestSurveyResultInfo(index, testTime, infoText, item);
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
                RealmResults<EyeTrackingTestSurveyResult> rResult = realm.where(EyeTrackingTestSurveyResult.class).equalTo(EyeTrackingTestSurveyResult.FILED_SURVEY_ID, surveyId).findAll();

                if (rResult != null) {
                    realm.beginTransaction();

                    if (mResults != null) {
                        mResults.clear();
                    }

                    if (rResult.size() > 0) {
                        Gson gson = new Gson();

                        for (EyeTrackingTestSurveyResult item : rResult) {
                            try {
                                TestData data = gson.fromJson(item.getResultFiles(), TestData.class);

                                if (data != null) {
                                    File videoFile = new File(data.getTestLooking().getFilePath());

                                    if (videoFile != null && videoFile.exists()) {
                                        videoFile.delete();
                                    }

                                    videoFile = new File(data.getTestAttention().getFilePath());

                                    if (videoFile != null && videoFile.exists()) {
                                        videoFile.delete();
                                    }
                                }
                            } catch (Exception e) {
                                TestLog.e(TAG, e);
                            }
                        }
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
                long count = realm.where(EyeTrackingTestSurveyResult.class)
                        .equalTo(EyeTrackingTestSurveyResult.FILED_SURVEY_ID, surveyId).count();

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
            List<EyeTrackingTestSurveyResult> dataResult = realm.where(EyeTrackingTestSurveyResult.class).equalTo(EyeTrackingTestSurveyResult.FILED_SURVEY_ID, surveyId).findAll();
            int countNotUploaded = 0;

            for (int i = 0; i < dataResult.size(); i++) {
                EyeTrackingTestSurveyResult item = dataResult.get(i);
                TestData data = null;

                try {
                    Gson gson = new Gson();
                    data = gson.fromJson(item.getResultFiles(), TestData.class);
                } catch (Exception e) {
                    TestLog.e(TAG, e);
                }

                if (data != null) {
                    if (!data.getTestLooking().isInterrupted() && (item.getIdServer_1() == -1 || item.getIdAttachmentServer_1() == -1)) {
                        countNotUploaded++;
                    }

                    if (!data.getTestAttention().isInterrupted() && (item.getIdServer_2() == -1 || item.getIdAttachmentServer_2() == -1)) {
                        countNotUploaded++;
                    }
                }
            }

            if (countNotUploaded > 0) {
                result = false;
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
        return 0;
    }

    @Override
    public int getIndexProcessing() {
        return 7;
    }

    @Override
    public boolean makeDump(Context context, int surveyId, int attempt, String path) {
        boolean result = false;

        try {
            ArrayList<TestData> results = new ArrayList<>();

            if (mContext != null) {
                Realm realm = DataProvider.getInstance(mContext).getRealm();

                if (realm != null && !realm.isClosed()) {
                    List<EyeTrackingTestSurveyResult> rResult = realm.where(EyeTrackingTestSurveyResult.class).equalTo(EyeTrackingTestSurveyResult.FILED_SURVEY_ID, surveyId).findAll();

                    if (rResult != null) {
                        for (EyeTrackingTestSurveyResult item : rResult) {
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
                    String fileName = EyeTrackingTest.class.getSimpleName() + "_" + surveyId + "_" + attempt + ".txt";
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

    private String excludeEncodedString(String value) {
        String result = "";
        int index = value.indexOf(',');

        if (index > 0) {
            result = value.substring(index);
        } else {
            result = value;
        }

        return result;
    }

    private String downloadFile(String url, String name) {
        String result = null;

        try {
            if (url != null) {
                File file = url.contains(com.reading.start.tests.test_eye_tracking.Constants.FILE_TYPE_VIDEO) ?
                        new File(mContext.getFilesDir(), name)
                        : new File(mContext.getFilesDir(), name);

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

    private class StartSdkListenerImplement implements StartSdkListener {
        @Override
        public void onCalibrateStart(long timeStamp) {

        }

        @Override
        public void onCalibrateChanged(CalibrateType type, long timeStamp) {

        }

        @Override
        public void onCalibrateCompleted(boolean success, long timeStamp) {

        }

        @Override
        public void onCalibrateStopped(long timeStamp) {

        }

        @Override
        public void onPositionUpdated(Point right, Point left, long timeStamp) {

        }

        @Override
        public void onDetectedSelectedArea(DetectMode mode, int col, int row, long timeStamp) {

        }

        @Override
        public void onFaceDetected(long timeStamp) {

        }

        @Override
        public void onFaceLost(long timeStamp) {

        }

        @Override
        public void onRightEyeDetected(long timeStamp) {

        }

        @Override
        public void onRightEyeLost(long timeStamp) {

        }

        @Override
        public void onLeftEyeDetected(long timeStamp) {

        }

        @Override
        public void onLeftEyeLost(long timeStamp) {

        }

        @Override
        public void onGazeOutside(long timeStamp) {

        }

        @Override
        public void onGazeDetected(long timeStamp) {

        }

        @Override
        public void onStartSdkStateChanged(StartSdkState state, long timeStamp) {

        }

        @Override
        public void onFrameProcessed(DetectMode mode, int col, int row, boolean isEyeDetected, boolean gazeOutside, long timeStamp) {

        }

        @Override
        public void onFramePostProcessed(Bitmap bitmap, long timeStamp) {

        }

        @Override
        public void onVideoRecordingStart(StartSdk.VideoType type) {

        }

        @Override
        public void onVideoRecordingStop(StartSdk.VideoType type) {

        }

        @Override
        public void onPostProcessingPostCalibrationStart() {

        }

        @Override
        public void onPostProcessingPostCalibrationEnd() {

        }

        @Override
        public void onPostProcessingTestStart() {

        }

        @Override
        public void onPostProcessingTestEnd() {

        }
    }

    private String formatDuration(long time) {
        long seconds = time / 1000;
        long absSeconds = Math.abs(seconds);
        String positive = String.format(
                "%d:%02d:%02d",
                absSeconds / 3600,
                (absSeconds % 3600) / 60,
                absSeconds % 60);
        return seconds < 0 ? "-" + positive : positive;
    }
}
