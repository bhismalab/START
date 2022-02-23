package com.reading.start.presentation.mvp.models;

import com.reading.start.AppCore;
import com.reading.start.R;
import com.reading.start.data.DataServerManager;
import com.reading.start.data.entity.DataPasswordRecovery;
import com.reading.start.data.response.ResponsePasswordRecovery;
import com.reading.start.data.response.ResponsePasswordReset;

import io.reactivex.Observable;
import io.realm.Realm;

public class ForgotPasswordModel extends BaseModel {

    public static class ForgotPasswordException extends Exception {
        public ForgotPasswordException(String message) {
            super(message);
        }

        @Override
        public String toString() {
            return ForgotPasswordException.class.getSimpleName();
        }
    }

    public static final String TAG = ForgotPasswordModel.class.getSimpleName();

    public ForgotPasswordModel(Realm realm) {
        super(realm);
    }

    /**
     * Returns observable for forgot date of birth.
     */
    public Observable<DataPasswordRecovery> getForgotDateObservable(final String userName, final String dateOfBirth) {
        Observable<DataPasswordRecovery> result = Observable.create(subscriber -> {
            DataServerManager manager = DataServerManager.getInstance();
            ResponsePasswordRecovery resultData = manager.passwordRecovery(userName, dateOfBirth);

            if (resultData != null && resultData.getData() != null) {
                if (resultData.isSuccess()) {
                    subscriber.onNext(resultData.getData());
                    subscriber.onComplete();
                } else {
                    if (subscriber != null && !subscriber.isDisposed()) {
                        subscriber.onError(new ForgotPasswordException(AppCore.getInstance().getString(R.string.error_message_reset_password)));
                    }
                }

            } else {
                if (subscriber != null && !subscriber.isDisposed()) {
                    subscriber.onError(new ForgotPasswordException(AppCore.getInstance().getString(R.string.error_message_reset_password)));
                }
            }
        });

        return result;
    }

    /**
     * Returns observable for reset password.
     */
    public Observable<Boolean> getResetPasswordObservable(final String userId, final String password, final String hash) {
        Observable<Boolean> result = Observable.create(subscriber -> {

            DataServerManager manager = DataServerManager.getInstance();
            ResponsePasswordReset resultData = manager.passwordReset(userId, password, hash);

            if (resultData != null) {
                if (resultData.isSuccess()) {
                    subscriber.onNext(true);
                    subscriber.onComplete();
                } else {
                    if (!subscriber.isDisposed()) {
                        subscriber.onError(new ForgotPasswordException(resultData.getMessage()));
                    }
                }

            } else {
                if (!subscriber.isDisposed()) {
                    subscriber.onError(new ForgotPasswordException(AppCore.getInstance().getString(R.string.error_message_reset_password)));
                }
            }


            subscriber.onNext(Boolean.TRUE);
            subscriber.onComplete();
        });

        return result;
    }
}
