package com.reading.start.presentation.mvp.presenters;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import com.jakewharton.rxbinding2.view.RxView;
import com.reading.start.AppCore;
import com.reading.start.BuildConfig;
import com.reading.start.Constants;
import com.reading.start.presentation.mvp.holders.LoginViewHolder;
import com.reading.start.presentation.mvp.models.LoginModel;
import com.reading.start.presentation.mvp.views.LoginView;
import com.reading.start.presentation.services.UploadService;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class LoginPresenter extends BasePresenter<LoginView, LoginModel, LoginViewHolder> {
    private static final String TAG = LoginPresenter.class.getSimpleName();

    private boolean mInProgress = false;

    public LoginPresenter() {
        super(true);
        setModel(new LoginModel(getRealm()));
    }

    @Override
    public void onCreate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getViewHolder() != null) {
            RxView.clicks(getViewHolder().getLogin()).subscribe(aVoid -> processLogin());
            RxView.clicks(getViewHolder().getForgot()).subscribe(aVoid -> processForgotPassword());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        processReset();

        getViewHolder().getName().setText(BuildConfig.PRE_DEFINDED_USER_NAME);
        getViewHolder().getName().setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        getViewHolder().getPassword().setText(BuildConfig.PRE_DEFINDED_USER_PASSWORD);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void processReset() {
        if (getViewHolder() != null) {
            getViewHolder().getName().setFocusable(false);
            getViewHolder().getName().setFocusableInTouchMode(false);
            getViewHolder().getName().setOnTouchListener((v, event) -> {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                getViewHolder().getPassword().setFocusable(true);
                getViewHolder().getPassword().setFocusableInTouchMode(true);
                return false;
            });

            getViewHolder().getName().setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_ACTION_NEXT
                        || actionId == EditorInfo.IME_ACTION_GO
                        || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    getViewHolder().getPassword().requestFocus();
                    return true;
                } else {
                    return false;
                }
            });

            getViewHolder().getPassword().setFocusable(false);
            getViewHolder().getPassword().setFocusableInTouchMode(false);
            getViewHolder().getPassword().setOnTouchListener((v, event) -> {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            });
        }
    }

    private void processLogin() {
        if (!mInProgress) {
            mInProgress = true;
            String name = getViewHolder().getName().getText().toString();
            String password = getViewHolder().getPassword().getText().toString();

            if (name != null && password != null && name.equals(BuildConfig.ADMIN_NAME)
                    && password.equals(BuildConfig.ADMIN_PASSWORD)) {
                // admin login
                getView().onAdminLogin();
                mInProgress = false;
            } else {
                // general login
                if (Calendar.getInstance().getTimeInMillis() > AppCore.getInstance().getPreferences().getLoginFailTime() + Constants.DEFAULT_LOGIN_ATTEMPT_FAIL_WAITING_TIME) {
                    AppCore.getInstance().getPreferences().setLoginFailTime(0);
                    AppCore.getInstance().getPreferences().setLoginAttempt(0);
                }

                if (AppCore.getInstance().getPreferences().getLoginAttempt() >= Constants.DEFAULT_LOGIN_ATTEMPT) {
                    getView().onAttemptFailLocal(null);
                    mInProgress = false;
                } else {
                    String savedToken = AppCore.getInstance().getPreferences().getLoginToken();
                    String serverToken = AppCore.getInstance().getPreferences().getServerToken();
                    final Observable<Integer> observable = getModel().getSignInObservable(name, password, savedToken, serverToken);

                    getSubscriptions().add(observable
                            .subscribeOn(Schedulers.io())
                            .timeout(Constants.DEFAULT_EXECUTE_TIME, TimeUnit.SECONDS)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new DisposableObserver<Integer>() {
                                @Override
                                public void onNext(Integer data) {
                                    getView().onLoginSuccess();
                                    UploadService.checkUpload(AppCore.getInstance());
                                }

                                @Override
                                public void onError(Throwable throwable) {
                                    int failCount = AppCore.getInstance().getPreferences().getLoginAttempt();
                                    AppCore.getInstance().getPreferences().setLoginAttempt(failCount + 1);
                                    AppCore.getInstance().getPreferences().setLoginFailTime(Calendar.getInstance().getTimeInMillis());

                                    if (throwable instanceof LoginModel.ResetTokenException) {
                                        AppCore.getInstance().getPreferences().setLoginToken("");
                                        AppCore.getInstance().getPreferences().setLoginWorker("");
                                        AppCore.getInstance().getPreferences().setLoginWorkerId(-1);
                                        AppCore.getInstance().getPreferences().setServerToken("");

                                        getView().onError(null);
                                    } else if (throwable instanceof LoginModel.SignInBlockedException) {
                                        getView().onAttemptFailServer(null);
                                    } else {
                                        getView().onError(throwable.getMessage());
                                    }

                                    mInProgress = false;
                                    processReset();
                                }

                                @Override
                                public void onComplete() {
                                    mInProgress = false;
                                }
                            }));
                }
            }
        }
    }

    private void processForgotPassword() {
        getView().onForgotPassword();
    }
}
