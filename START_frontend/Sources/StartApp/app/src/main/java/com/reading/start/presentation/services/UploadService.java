package com.reading.start.presentation.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.reading.start.AppCore;
import com.reading.start.BuildConfig;
import com.reading.start.Constants;
import com.reading.start.R;
import com.reading.start.data.DataBaseProvider;
import com.reading.start.domain.entity.Survey;
import com.reading.start.domain.entity.SurveyStatus;
import com.reading.start.domain.rx.ProgressRxResult;
import com.reading.start.general.TLog;
import com.reading.start.presentation.mvp.models.UploadSurveyModel;
import com.reading.start.tests.ServerLog;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;

/**
 * Service intended for uploading of surveys in background.
 */
public class UploadService extends IntentService {
    private static final String TAG = UploadService.class.getSimpleName();

    public static final String NOTIFICATION = "com.reading.start.presentation.services.UploadService";

    public static final String ACTION_UPLOAD_START = "ACTION_UPLOAD_START";
    public static final String ACTION_UPLOAD_PROGRESS = "ACTION_UPLOAD_PROGRESS";
    public static final String ACTION_UPLOAD_COMPLETED = "ACTION_UPLOAD_COMPLETED";
    public static final String ACTION_UPLOAD_ERROR = "ACTION_ERROR";

    public static final String SURVEY_ID_KEY = "SURVEY_ID_KEY";
    public static final String SURVEY_PROGRESS_KEY = "SURVEY_PROGRESS_KEY";
    public static final String SURVEY_MESSAGE = "SURVEY_MESSAGE";

    private static final int NOTIFICATION_ID = 1000;

    private static boolean mInProgress = false;

    private static boolean mNeedCheck = false;

    private static int mUploadingSurveyId = -1;

    private PowerManager.WakeLock mWakeLock = null;

    private Object mLock = new Object();

    private boolean mIsNotificationShowing = false;

    public static int getUploadingSurveyId() {
        return mUploadingSurveyId;
    }

    public UploadService() {
        super(UploadService.class.getSimpleName());
    }

    public UploadService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        synchronized (mLock) {
            if (!mInProgress) {
                Realm realm = null;

                try {
                    if (intent != null) {
                        int surveyId = intent.getExtras() != null ? intent.getExtras().getInt(SURVEY_ID_KEY, -1) : -1;
                        realm = DataBaseProvider.getInstance(AppCore.getInstance()).getRealm();

                        if (realm != null && !realm.isClosed()) {
                            Survey survey = null;

                            if (surveyId == -1) {
                                SurveyStatus status = realm.where(SurveyStatus.class).equalTo(SurveyStatus.FILED_IS_NEED_UPLOAD, true).findFirst();

                                if (status != null) {
                                    survey = realm.where(Survey.class).equalTo(Survey.FILED_ID, status.getIdSurvey()).findFirst();
                                    surveyId = survey.getId();
                                }
                            } else {
                                survey = realm.where(Survey.class).equalTo(Survey.FILED_ID, surveyId).findFirst();
                            }

                            if (survey != null) {
                                startWakeLock();
                                SurveyStatus surveyStatus = realm.where(SurveyStatus.class).equalTo(SurveyStatus.FILED_ID_SURVEY, surveyId).findFirst();

                                if (surveyStatus != null) {
                                    realm.beginTransaction();
                                    surveyStatus.setUploadProgress(0);
                                    surveyStatus.setInProgress(true);
                                    realm.commitTransaction();
                                }

                                UploadSurveyModel model = new UploadSurveyModel(realm);
                                final Observable<ProgressRxResult> observable = model.getUploadObservable(survey.getChildId(), survey.getId());

                                int threadCount = Runtime.getRuntime().availableProcessors();
                                ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(threadCount);
                                Scheduler scheduler = Schedulers.from(threadPoolExecutor);
                                showNotification();
                                sendCallbackOnUploadStart(surveyId);
                                mInProgress = true;
                                mUploadingSurveyId = surveyId;

                                observable
                                        .subscribeOn(scheduler)
                                        //.retry(Constants.UPLOADING_COUNT_RETRY)
                                        //.timeout(Constants.UPLOADING_TIMEOUT, TimeUnit.MINUTES)
                                        .observeOn(scheduler)
                                        .subscribeWith(new DisposableObserver<ProgressRxResult>() {
                                            @Override
                                            public void onNext(ProgressRxResult result) {
                                                if (result != null) {
                                                    sendCallbackOnUploadProgress(mUploadingSurveyId, result.getResult());
                                                    updateNotification(result.getResult());
                                                }
                                            }

                                            @Override
                                            public void onError(Throwable throwable) {
                                                TLog.e(TAG, throwable);
                                                ServerLog.log("UPLOAD SERVICE", "INTERNAL ERROR: " + throwable != null ? throwable.getMessage() : "fail");

                                                resetUploadState(mUploadingSurveyId);

                                                if (throwable != null) {
                                                    sendCallbackOnUploadError(mUploadingSurveyId, (throwable.getMessage() != null && throwable.getMessage().length() > 0) ? throwable.getMessage().toString() : null);
                                                }

                                                mUploadingSurveyId = -1;
                                                mInProgress = false;

                                                stopWakeLock();
                                                hideNotification();

                                                if (mNeedCheck) {
                                                    UploadService.checkUpload(AppCore.getInstance(), false);
                                                }
                                            }

                                            @Override
                                            public void onComplete() {
                                                if (BuildConfig.FAKE_UPLOAD) {
                                                    resetUploadState(mUploadingSurveyId);
                                                    sendCallbackOnUploadError(mUploadingSurveyId, null);
                                                }

                                                sendCallbackOnUploadCompleted(mUploadingSurveyId);
                                                mNeedCheck = true;
                                                mUploadingSurveyId = -1;
                                                mInProgress = false;

                                                hideNotification();
                                                stopWakeLock();

                                                if (mNeedCheck) {
                                                    UploadService.checkUpload(AppCore.getInstance(), false);
                                                }
                                            }
                                        });
                            }
                        }
                    }
                } catch (Exception e) {
                    TLog.e(TAG, e);
                    ServerLog.log("UPLOAD SERVICE", "ERROR: " + e != null ? e.getMessage() : "fail");

                    resetUploadState(mUploadingSurveyId);
                    hideNotification();
                    stopWakeLock();
                    mUploadingSurveyId = -1;
                    mInProgress = false;
                } finally {
                    if (realm != null) {
                        realm.close();
                    }
                }
            }
        }
    }

    /**
     * Start upload survey
     */
    public static void startUpload(Context context, int surveyId) {
        Intent intent = new Intent(context, UploadService.class);
        intent.putExtra(SURVEY_ID_KEY, surveyId);
        context.startService(intent);
    }

    /**
     * Check if exist survey for upload and start upload if exist
     */
    public static void checkUpload(Context context) {
        mNeedCheck = true;
        Intent intent = new Intent(context, UploadService.class);
        context.startService(intent);
    }

    /**
     * Check if exist survey for upload and start upload if exist
     */
    private static void checkUpload(Context context, boolean check) {
        mNeedCheck = check;
        Intent intent = new Intent(context, UploadService.class);
        context.startService(intent);
    }

    /**
     * Send broadcast when upload started
     */
    private void sendCallbackOnUploadStart(int surveyId) {
        Intent intent = new Intent(NOTIFICATION);
        Bundle bundle = new Bundle();
        bundle.putBoolean(ACTION_UPLOAD_START, true);
        bundle.putInt(SURVEY_ID_KEY, surveyId);
        intent.putExtras(bundle);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    /**
     * Send broadcast with progress of upload
     */
    private void sendCallbackOnUploadProgress(int surveyId, int progress) {
        Intent intent = new Intent(NOTIFICATION);
        Bundle bundle = new Bundle();
        bundle.putBoolean(ACTION_UPLOAD_PROGRESS, true);
        bundle.putInt(SURVEY_ID_KEY, surveyId);
        bundle.putInt(SURVEY_PROGRESS_KEY, progress);
        intent.putExtras(bundle);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    /**
     * Send broadcast when upload completed
     */
    private void sendCallbackOnUploadCompleted(int surveyId) {
        Intent intent = new Intent(NOTIFICATION);
        Bundle bundle = new Bundle();
        bundle.putBoolean(ACTION_UPLOAD_COMPLETED, true);
        bundle.putInt(SURVEY_ID_KEY, surveyId);
        intent.putExtras(bundle);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    private void sendCallbackOnUploadError(int surveyId, String message) {
        Intent intent = new Intent(NOTIFICATION);
        Bundle bundle = new Bundle();
        bundle.putBoolean(ACTION_UPLOAD_ERROR, true);
        bundle.putString(SURVEY_MESSAGE, message);
        bundle.putInt(SURVEY_ID_KEY, surveyId);
        intent.putExtras(bundle);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    /**
     * Show system notification for upload
     */
    private void showNotification() {
        mIsNotificationShowing = true;
        Notification.Builder builder = new Notification.Builder(getBaseContext())
                .setSmallIcon(R.drawable.icon_upload)
                .setContentTitle(getString(R.string.survey_upload_notification))
                .setProgress(100, 0, true);
        startForeground(NOTIFICATION_ID, builder.build());
    }

    /**
     * Update system notification for upload
     */
    private void updateNotification(int progress) {
        if (mIsNotificationShowing) {
            NotificationManager notifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (notifyManager != null) {
                Notification.Builder builder = new Notification.Builder(getBaseContext())
                        .setSmallIcon(R.drawable.icon_upload)
                        .setContentTitle(getString(R.string.survey_upload_notification))
                        .setProgress(100, progress, true);
                notifyManager.notify(NOTIFICATION_ID, builder.build());
            }
        }
    }

    /**
     * Hide system notification for upload
     */
    private void hideNotification() {
        mIsNotificationShowing = false;
        stopForeground(true);

        NotificationManager notifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (notifyManager != null) {
            notifyManager.cancel(NOTIFICATION_ID);
        }
    }

    private void resetUploadState(int surveyId) {
        Realm realm = null;

        try {
            realm = DataBaseProvider.getInstance(AppCore.getInstance()).getRealm();

            if (realm != null) {
                SurveyStatus surveyStatus = realm.where(SurveyStatus.class).equalTo(SurveyStatus.FILED_ID_SURVEY, surveyId).findFirst();

                if (surveyStatus != null) {
                    realm.beginTransaction();
                    surveyStatus.setUploadProgress(0);
                    surveyStatus.setNeedDownload(false);
                    surveyStatus.setUploaded(false);
                    surveyStatus.setRemote(false);
                    surveyStatus.setTries(surveyStatus.getTries() + 1);

                    if (surveyStatus.getTries() > Constants.UPLOADING_COUNT_RETRY) {
                        surveyStatus.setTries(0);
                    }

                    surveyStatus.setNeedUpload(true);
                    realm.commitTransaction();
                }
            }
        } catch (Exception e) {
            TLog.e(TAG, e);
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    private void startWakeLock() {
        try {
            if (mWakeLock != null) {
                mWakeLock.release();
                mWakeLock = null;
            }
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);

            if (pm != null) {
                mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
                mWakeLock.acquire();
            }
        } catch (Exception e) {
            TLog.e(TAG, e);
        }
    }

    private void stopWakeLock() {
        try {
            if (mWakeLock != null) {
                mWakeLock.release();
                mWakeLock = null;
            }
        } catch (Exception e) {
            TLog.e(TAG, e);
        }
    }
}
