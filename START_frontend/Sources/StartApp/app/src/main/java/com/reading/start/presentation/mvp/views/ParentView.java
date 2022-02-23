package com.reading.start.presentation.mvp.views;

import com.reading.start.domain.entity.Children;

public interface ParentView {

    /**
     * Raises when pressed back button
     */
    void onBackPressed();

    /**
     * Raises when pressed next button
     */
    void onNextPressed();

    /**
     * Raises when pressed save button
     */
    void onSaveParentPressed();

    /**
     * Raises when save success
     */
    void onSaveParentSuccess(boolean isEdit);

    /**
     * Raises when need to save child/parent
     */
    void onSave(boolean success, Children children);

    /**
     * Raises when need to show toast
     */
    void onShowToast(String message);
}
