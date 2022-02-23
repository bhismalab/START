package com.reading.start.presentation.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.reading.start.AppCore;
import com.reading.start.R;
import com.reading.start.data.DataBaseProvider;
import com.reading.start.domain.entity.Survey;
import com.reading.start.domain.entity.SurveyStatus;
import com.reading.start.general.TLog;
import com.reading.start.presentation.mvp.models.DownloadSurveyModel;

import java.util.concurrent.CountDownLatch;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;

/**
 * Service intended for downloading of surveys in background.
 */
public class DownloadService extends IntentService {
    private static final String TAG = DownloadService.class.getSimpleName();

    public static final String NOTIFICATION = "com.reading.start.presentation.services.DownloadService";

    public static final String ACTION_DOWNLOAD_START = "ACTION_DOWNLOAD_START";
    public static final String ACTION_DOWNLOAD_PROGRESS = "ACTION_DOWNLOAD_PROGRESS";
    public static final String ACTION_DOWNLOAD_COMPLETED = "ACTION_DOWNLOAD_COMPLETED";

    public static final String SURVEY_ID_KEY = "SURVEY_ID_KEY";
    public static final String SURVEY_PROGRESS_KEY = "SURVEY_PROGRESS_KEY";

    private static final int NOTIFICATION_ID = 2000;

    private static boolean mInProgress = false;

    private static boolean mNeedCheck = false;

    public DownloadService() {
        super(DownloadService.class.getSimpleName());
    }

    public DownloadService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (!mInProgress) {
            Realm realm = null;

            try {
                mInProgress = true;

                if (intent != null && intent.getExtras() != null) {
                    final int surveyId = intent.getExtras().getInt(SURVEY_ID_KEY, -1);
                    realm = DataBaseProvider.getInstance(AppCore.getInstance()).getRealm();

                    if (realm != null && !realm.isClosed()) {
                        Survey survey = null;

                        if (surveyId == -1) {
                            SurveyStatus status = realm.where(SurveyStatus.class).equalTo(SurveyStatus.FILED_IS_NEED_UPLOAD, true).findFirst();

                            if (status != null) {
                                survey = realm.where(Survey.class).equalTo(Survey.FILED_ID, status.getIdSurvey()).findFirst();
                            }
                        } else {
                            survey = realm.where(Survey.class).equalTo(Survey.FILED_ID, surveyId).findFirst();
                        }

                        if (survey != null) {
                            CountDownLatch countDownLatch = new CountDownLatch(1);
                            DownloadSurveyModel model = new DownloadSurveyModel(realm);
                            final Observable<Integer> observable = model.getDownloadObservable(survey.getChildId(), survey.getId());

                            observable
                                    .subscribeOn(Schedulers.io())
                                    .retry(1)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeWith(new DisposableObserver<Integer>() {
                                        @Override
                                        public void onNext(Integer integer) {
                                            if (integer == 0) {
                                                sendCallbackOnDownloadStart(surveyId);
                                                showNotification();
                                            } else if (integer > 0 && integer < 100) {
                                                sendCallbackOnDownloadProgress(surveyId, integer);
                                                updateNotification(integer);
                                            }
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            hideNotification();
                                            countDownLatch.countDown();
                                        }

                                        @Override
                                        public void onComplete() {
                                            // update survey progress
                                            final Observable<Boolean> observableUpdate = model.getCheckIsSurveyCompletedObservable(surveyId);
                                            observableUpdate
                                                    .subscribeOn(Schedulers.io())
                                                    .retry(1)
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribeWith(new DisposableObserver<Boolean>() {
                                                        @Override
                                                        public void onNext(Boolean aBoolean) {
                                                        }

                                                        @Override
                                                        public void onError(Throwable e) {
                                                            countDownLatch.countDown();
                                                        }

                                                        @Override
                                                        public void onComplete() {
                                                            sendCallbackOnDownloadCompleted(surveyId);
                                                            hideNotification();
                                                            mNeedCheck = true;
                                                            countDownLatch.countDown();
                                                        }
                                                    });
                                        }
                                    });

                            countDownLatch.await();
                        }
                    }
                }
            } catch (Exception e) {
                TLog.e(TAG, e);
            } finally {
                mInProgress = false;

                if (realm != null) {
                    realm.close();
                }

                if (mNeedCheck) {
                    DownloadService.checkDownload(AppCore.getInstance(), false);
                }
            }
        }
    }

    /**
     * Start download survey
     */
    public static void startDownload(Context context, int surveyId) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra(SURVEY_ID_KEY, surveyId);
        context.startService(intent);
    }

    /**
     * Check if exist survey for download and start download if exist
     */
    public static void checkDownload(Context context) {
        mNeedCheck = true;
        Intent intent = new Intent(context, DownloadService.class);
        context.startService(intent);
    }

    /**
     * Check if exist survey for download and start download if exist
     */
    private static void checkDownload(Context context, boolean check) {
        mNeedCheck = check;
        Intent intent = new Intent(context, DownloadService.class);
        context.startService(intent);
    }

    /**
     * Send broadcast when download started
     */
    private void sendCallbackOnDownloadStart(int surveyId) {
        Intent intent = new Intent(NOTIFICATION);
        Bundle bundle = new Bundle();
        bundle.putBoolean(ACTION_DOWNLOAD_START, true);
        bundle.putInt(SURVEY_ID_KEY, surveyId);
        intent.putExtras(bundle);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    /**
     * Send broadcast with progress of download
     */
    private void sendCallbackOnDownloadProgress(int surveyId, int progress) {
        Intent intent = new Intent(NOTIFICATION);
        Bundle bundle = new Bundle();
        bundle.putBoolean(ACTION_DOWNLOAD_PROGRESS, true);
        bundle.putInt(SURVEY_ID_KEY, surveyId);
        bundle.putInt(SURVEY_PROGRESS_KEY, progress);
        intent.putExtras(bundle);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    /**
     * Send broadcast when download completed
     */
    private void sendCallbackOnDownloadCompleted(int surveyId) {
        Intent intent = new Intent(NOTIFICATION);
        Bundle bundle = new Bundle();
        bundle.putBoolean(ACTION_DOWNLOAD_COMPLETED, true);
        bundle.putInt(SURVEY_ID_KEY, surveyId);
        intent.putExtras(bundle);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    /**
     * Show system notification for download
     */
    private void showNotification() {
        Notification.Builder builder = new Notification.Builder(getBaseContext())
                .setSmallIcon(R.drawable.icon_download)
                .setContentTitle(getString(R.string.survey_download_notification))
                .setProgress(100, 0, true);
        startForeground(NOTIFICATION_ID, builder.build());
    }

    /**
     * Update system notification for download
     */
    private void updateNotification(int progress) {
        NotificationManager notifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (notifyManager != null) {
            Notification.Builder builder = new Notification.Builder(getBaseContext())
                    .setSmallIcon(R.drawable.icon_download)
                    .setContentTitle(getString(R.string.survey_download_notification))
                    .setProgress(100, progress, true);
            notifyManager.notify(NOTIFICATION_ID, builder.build());
        }
    }

    /**
     * Hide system notification for download
     */
    private void hideNotification() {
        stopForeground(true);
        NotificationManager notifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (notifyManager != null) {
            notifyManager.cancel(NOTIFICATION_ID);
        }
    }
}
