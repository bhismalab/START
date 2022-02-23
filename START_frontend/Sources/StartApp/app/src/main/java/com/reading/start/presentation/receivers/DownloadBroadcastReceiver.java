package com.reading.start.presentation.receivers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.reading.start.presentation.services.DownloadService;

/**
 * Intended for receive events related with download of surveys.
 */
public abstract class DownloadBroadcastReceiver extends BaseReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction() != null && intent.getAction().equals(DownloadService.NOTIFICATION)) {
            Bundle bundle = intent.getExtras();

            if (bundle != null) {
                if (bundle.containsKey(DownloadService.ACTION_DOWNLOAD_START)) {
                    int surveyId = bundle.getInt(DownloadService.SURVEY_ID_KEY, -1);
                    onDownloadStart(surveyId);
                } else if (bundle.containsKey(DownloadService.ACTION_DOWNLOAD_PROGRESS)) {
                    int surveyId = bundle.getInt(DownloadService.SURVEY_ID_KEY, -1);
                    int progress = bundle.getInt(DownloadService.SURVEY_PROGRESS_KEY, 0);
                    onDownloadProgress(surveyId, progress);
                } else if (bundle.containsKey(DownloadService.ACTION_DOWNLOAD_COMPLETED)) {
                    int surveyId = bundle.getInt(DownloadService.SURVEY_ID_KEY, -1);
                    onDownloadCompleted(surveyId);
                }
            }
        }
    }

    /**
     * Raise when survey start downloading.
     */
    public abstract void onDownloadStart(int surveyId);

    /**
     * Raise when updated progress of survey downloading.
     */
    public abstract void onDownloadProgress(int surveyId, int progress);

    /**
     * Raise when survey completed downloading.
     */
    public abstract void onDownloadCompleted(int surveyId);

    @Override
    protected String getAction() {
        return DownloadService.NOTIFICATION;
    }
}
