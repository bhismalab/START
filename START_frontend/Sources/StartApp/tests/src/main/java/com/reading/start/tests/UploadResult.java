package com.reading.start.tests;

/**
 * Represent info about upload status
 */
public class UploadResult {
    private boolean mSuccess = false;

    private String mMessage = null;

    public UploadResult(boolean success, String message) {
        mSuccess = success;
        mMessage = message;
    }

    public UploadResult(boolean success) {
        mSuccess = success;
    }

    public boolean isSuccess() {
        return mSuccess;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setSuccess(boolean success) {
        mSuccess = success;
    }

    public void setMessage(String message) {
        mMessage = message;
    }
}
