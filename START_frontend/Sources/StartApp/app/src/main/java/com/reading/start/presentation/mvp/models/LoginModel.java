package com.reading.start.presentation.mvp.models;

import android.util.Base64;

import com.reading.start.AppCore;
import com.reading.start.R;
import com.reading.start.data.DataBaseProvider;
import com.reading.start.data.DataServerManager;
import com.reading.start.data.response.ResponseCheckAuthTokenData;
import com.reading.start.data.response.ResponseLoginData;
import com.reading.start.domain.entity.SocialWorker;
import com.reading.start.general.TLog;
import com.reading.start.utils.NetworkHelper;

import io.reactivex.Observable;
import io.realm.Realm;

public class LoginModel extends BaseModel {
    public static class SignInException extends Exception {
        public SignInException(String message) {
            super(message);
        }

        @Override
        public String toString() {
            return SignInException.class.getSimpleName();
        }
    }

    public static class SignInBlockedException extends Exception {
        public SignInBlockedException(String message) {
            super(message);
        }

        @Override
        public String toString() {
            return SignInException.class.getSimpleName();
        }
    }


    public static class ResetTokenException extends Exception {
        public ResetTokenException(String message) {
            super(message);
        }

        @Override
        public String toString() {
            return ResetTokenException.class.getSimpleName();
        }
    }

    private static final String TAG = LoginModel.class.getSimpleName();

    public LoginModel(Realm realm) {
        super(realm);
    }

    /**
     * Returns observable for sing in.
     */
    public Observable<Integer> getSignInObservable(final String userName, final String password,
                                                   final String savedToken, final String serverToken) {
        Observable<Integer> result = Observable.create(subscriber -> {
            Realm realm = null;

            try {
                realm = DataBaseProvider.getInstance(AppCore.getInstance()).getRealm();

                if (NetworkHelper.hasActiveInternetConnection()) {
                    boolean loginWithToken = false;

                    if (savedToken != null && !savedToken.isEmpty()) {
                        byte[] base65Token = Base64.decode(savedToken, Base64.DEFAULT);
                        String token = new String(base65Token);
                        String[] tokenValues = token.split("\\|");
                        String savedUser = tokenValues[0];
                        String savedPassword = tokenValues[1];

                        if (serverToken != null && savedUser.equals(userName) && savedPassword.equals(password)) {
                            loginWithToken = true;
                        }
                    }

                    if (loginWithToken) {
                        ResponseCheckAuthTokenData checkTokenResult = loginRequestToServer(serverToken, realm);

                        if (checkTokenResult != null) {
                            if (checkTokenResult.isSuccess()) {
                                SocialWorker user = null;

                                if (realm != null && !realm.isClosed()) {
                                    user = realm.where(SocialWorker.class)
                                            .equalTo(SocialWorker.FILED_TOKEN, serverToken)
                                            .findFirst();

                                    if (user != null) {
                                        updateLoginUser(userName, password, user);
                                        subscriber.onNext(user.getId());
                                        subscriber.onComplete();
                                    } else {
                                        if (!subscriber.isDisposed()) {
                                            subscriber.onError(new SignInException(AppCore.getInstance().getString(R.string.error_message_login_failed)));
                                        }
                                    }
                                } else {
                                    if (!subscriber.isDisposed()) {
                                        subscriber.onError(new SignInException(AppCore.getInstance().getString(R.string.error_message_login_failed)));
                                    }
                                }
                            } else {
                                if (!subscriber.isDisposed()) {
                                    subscriber.onError(new SignInException(AppCore.getInstance().getString(R.string.error_message_login_failed)));
                                }
                            }
                        } else {
                            if (!subscriber.isDisposed()) {
                                subscriber.onError(new ResetTokenException(AppCore.getInstance().getString(R.string.error_message_login_failed)));
                            }
                        }
                    } else {
                        ResponseLoginData workerResponse = loginRequestToServer(userName, password);

                        if (workerResponse != null) {
                            if (workerResponse.isSuccess()) {
                                if (realm != null && !realm.isClosed()) {
                                    SocialWorker user = null;

                                    try {
                                        user = realm.where(SocialWorker.class)
                                                .equalTo(SocialWorker.FILED_ID_SERVER, workerResponse.getData().getSocialWorker().getId())
                                                .findFirst();

                                        realm.beginTransaction();

                                        if (user == null) {
                                            Number currentId = realm.where(SocialWorker.class).max(SocialWorker.FILED_ID);
                                            int nextId;

                                            if (currentId == null) {
                                                nextId = 0;
                                            } else {
                                                nextId = currentId.intValue() + 1;
                                            }

                                            SocialWorker worker = new SocialWorker();
                                            worker.setId(nextId);
                                            worker.setIdServer(workerResponse.getData().getSocialWorker().getId());
                                            worker.setEmail(workerResponse.getData().getSocialWorker().getEmail());
                                            worker.setPassword(password);
                                            worker.setFullName(workerResponse.getData().getSocialWorker().getFullName());
                                            worker.setColor(workerResponse.getData().getSocialWorker().getColor());
                                            worker.setGender(workerResponse.getData().getSocialWorker().getGender());
                                            worker.setBirthDate(workerResponse.getData().getSocialWorker().getBirthDate());
                                            worker.setRegDate(workerResponse.getData().getSocialWorker().getRegDate());
                                            worker.setRegBy(workerResponse.getData().getSocialWorker().getRegBy());
                                            worker.setIsBlocked(workerResponse.getData().getSocialWorker().getIsBlocked());
                                            worker.setIsDeleted(workerResponse.getData().getSocialWorker().getIsDeleted());
                                            worker.setToken(workerResponse.getData().getToken());
                                            worker.setSyncTime(0);

                                            user = realm.copyToRealmOrUpdate(worker);
                                        } else {
                                            user.setToken(workerResponse.getData().getToken());
                                        }

                                        realm.commitTransaction();
                                    } catch (Exception e) {
                                        realm.cancelTransaction();
                                        TLog.e(TAG, e);
                                    }

                                    if (user != null) {
                                        updateLoginUser(userName, password, user);
                                        subscriber.onNext(user.getId());
                                        subscriber.onComplete();
                                    } else {
                                        if (!subscriber.isDisposed()) {
                                            subscriber.onError(new SignInException(AppCore.getInstance().getString(R.string.error_message_login_failed)));
                                        }
                                    }
                                } else {
                                    if (!subscriber.isDisposed()) {
                                        subscriber.onError(new SignInException(AppCore.getInstance().getString(R.string.error_message_login_failed)));
                                    }
                                }
                            } else {
                                if (workerResponse.getCode() == 403) {
                                    if (!subscriber.isDisposed()) {
                                        subscriber.onError(new SignInBlockedException(AppCore.getInstance().getString(R.string.error_message_login_failed)));
                                    }
                                } else {
                                    if (!subscriber.isDisposed()) {
                                        subscriber.onError(new SignInException(AppCore.getInstance().getString(R.string.error_message_login_failed)));
                                    }
                                }
                            }
                        } else {
                            if (!subscriber.isDisposed()) {
                                subscriber.onError(new SignInException(AppCore.getInstance().getString(R.string.error_message_login_failed)));
                            }
                        }
                    }
                } else {
                    if (savedToken != null && !savedToken.isEmpty()) {
                        try {
                            byte[] base65Token = Base64.decode(savedToken, Base64.DEFAULT);
                            String token = new String(base65Token);
                            String[] tokenValues = token.split("\\|");
                            String savedUser = tokenValues[0];
                            String savedPassword = tokenValues[1];

                            if (serverToken != null && savedUser.equals(userName) && savedPassword.equals(password)) {
                                if (realm != null && !realm.isClosed()) {
                                    SocialWorker user = realm.where(SocialWorker.class)
                                            .endsWith(SocialWorker.FILED_TOKEN, serverToken)
                                            .findFirst();

                                    if (user != null) {
                                        updateLoginUser(userName, password, user);
                                        subscriber.onNext(user.getId());
                                        subscriber.onComplete();
                                    } else {
                                        if (!subscriber.isDisposed()) {
                                            subscriber.onError(new SignInException(AppCore.getInstance().getString(R.string.error_message_login_failed)));
                                        }
                                    }
                                } else {
                                    if (!subscriber.isDisposed()) {
                                        subscriber.onError(new SignInException(AppCore.getInstance().getString(R.string.error_message_login_failed)));
                                    }
                                }
                            } else {
                                if (!subscriber.isDisposed()) {
                                    subscriber.onError(new SignInException(AppCore.getInstance().getString(R.string.error_message_login_failed)));
                                }
                            }
                        } catch (Exception e) {
                            if (!subscriber.isDisposed()) {
                                subscriber.onError(new SignInException(AppCore.getInstance().getString(R.string.error_message_login_failed)));
                            }
                            TLog.e(TAG, e);
                        }
                    } else {
                        if (!subscriber.isDisposed()) {
                            subscriber.onError(new SignInException(AppCore.getInstance().getString(R.string.error_message_login_internet_connection_failed)));
                        }
                    }
                }
            } catch (Exception e) {
                TLog.e(TAG, e);
                subscriber.onError(e);
            } finally {
                if (realm != null) {
                    realm.close();
                }
            }
        });

        return result;
    }

    private void updateLoginUser(String userName, String password, SocialWorker worker) {
        if (worker != null) {
            AppCore.getInstance().getPreferences().setLoginAttempt(0);
            AppCore.getInstance().getPreferences().setLoginFailTime(0);

            try {
                String token = userName + "|" + password;
                String base64Token = Base64.encodeToString(token.getBytes(), Base64.DEFAULT);
                AppCore.getInstance().getPreferences().setLoginToken(base64Token);
                AppCore.getInstance().getPreferences().setLoginWorker(worker.getFullName());
                AppCore.getInstance().getPreferences().setLoginWorkerId(worker.getId());
                AppCore.getInstance().getPreferences().setServerToken(worker.getToken());
                AppCore.getInstance().getPreferences().setSyncTime(worker.getSyncTime());
            } catch (Exception e) {
                TLog.e(TAG, e);
            }
        }
    }


    private ResponseLoginData loginRequestToServer(String userName, String password) {
        DataServerManager manager = DataServerManager.getInstance();
        ResponseLoginData result = manager.login(userName, password);
        return result;
    }

    private ResponseCheckAuthTokenData loginRequestToServer(String serverToken, Realm realm) {
        ResponseCheckAuthTokenData result = null;

        try {
            if (serverToken != null) {
                DataServerManager manager = DataServerManager.getInstance();
                result = manager.checkAuthToke(serverToken);
            }
        } catch (Exception e) {
            TLog.e(TAG, e);
        }

        return result;
    }
}
