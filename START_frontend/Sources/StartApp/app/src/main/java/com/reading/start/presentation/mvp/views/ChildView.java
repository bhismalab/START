package com.reading.start.presentation.mvp.views;

public interface ChildView {
    /**
     * Raises when diagnostic button pressed
     */
    void onChildDiagnosisPressed(String doctor, String diagnosis, long diagnosisDate);

    /**
     * Raises when next button pressed
     */
    void onNextPressed();

    /**
     * Raises when back button pressed
     */
    void onBackPressed();

    /**
     * Raises when save button pressed
     */
    void onSave();

    /**
     * Raises when save success
     */
    void onSaveSuccess(boolean isEdit);

    /**
     * Raises when need take a photo
     */
    void onTakePhoto();

    /**
     * Raises when need to show toast
     */
    void onShowToast(String message);

    /**
     * Raises when edit successf
     */
    void onEdited(boolean value);
}
