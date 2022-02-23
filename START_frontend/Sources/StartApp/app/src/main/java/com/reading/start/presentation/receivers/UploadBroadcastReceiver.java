package com.reading.start.presentation.receivers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.reading.start.presentation.services.UploadService;

/**
 * Intended for receive events related with upload of surveys.
 */
public abstract class UploadBroadcastReceiver extends BaseReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction() != null && intent.getAction().equals(UploadService.NOTIFICATION)) {
            Bundle bundle = intent.getExtras();

            if (bundle != null) {
                if (bundle.containsKey(UploadService.ACTION_UPLOAD_START)) {
                    int surveyId = bundle.getInt(UploadService.SURVEY_ID_KEY, -1);
                    onUploadStart(surveyId);
                } else if (bundle.containsKey(UploadService.ACTION_UPLOAD_PROGRESS)) {
                    int surveyId = bundle.getInt(UploadService.SURVEY_ID_KEY, -1);
                    int progress = bundle.getInt(UploadService.SURVEY_PROGRESS_KEY, 0);
                    onUploadProgress(surveyId, progress);
                } else if (bundle.containsKey(UploadService.ACTION_UPLOAD_COMPLETED)) {
                    int surveyId = bundle.getInt(UploadService.SURVEY_ID_KEY, -1);
                    onUploadCompleted(surveyId);
                } else if (bundle.containsKey(UploadService.ACTION_UPLOAD_ERROR)) {
                    String message = bundle.getString(UploadService.SURVEY_MESSAGE);
                    int surveyId = bundle.getInt(UploadService.SURVEY_ID_KEY, -1);
                    onUploadError(surveyId, message);
                }
            }
        }
    }

    /**
     * Raise when survey start uploading.
     */
    public abstract void onUploadStart(int surveyId);

    /**
     * Raise when updated progress of survey uploading.
     */
    public abstract void onUploadProgress(int surveyId, int progress);

    /**
     * Raise when survey completed uploading.
     */
    public abstract void onUploadCompleted(int surveyId);

    /**
     * Raise when occurred error when survey downloading.
     */
    public abstract void onUploadError(int surveyId, String message);

    @Override
    protected String getAction() {
        return UploadService.NOTIFICATION;
    }
}
