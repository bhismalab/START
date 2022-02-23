package com.reading.start.presentation.mvp.views;

public interface SynchronizeView {
    /**
     * Raises when loading started
     */
    void onLoading();

    /**
     * Raises when loading success
     */
    void onLoadSuccess();

    /**
     * Raises when cause error
     */
    void onError(String message);
}
