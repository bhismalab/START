package com.reading.start.presentation.mvp.views;

import com.reading.start.domain.entity.SocialWorker;

public interface LoginView {
    /**
     * Raises when admin loginned
     */
    void onAdminLogin();

    /**
     * Raises when login success
     */
    void onLoginSuccess();

    /**
     * Raises when pressed forgot password
     */
    void onForgotPassword();

    /**
     * Raises when caused error
     */
    void onError(String message);

    /**
     * Raises when social worker locked from server
     */
    void onAttemptFailServer(String message);

    /**
     * Raises when social worker locked on the application side
     */
    void onAttemptFailLocal(String message);
}
