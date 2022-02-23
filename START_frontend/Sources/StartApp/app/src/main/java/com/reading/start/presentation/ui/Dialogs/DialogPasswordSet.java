package com.reading.start.presentation.ui.dialogs;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.reading.start.AppCore;
import com.reading.start.Constants;
import com.reading.start.R;
import com.reading.start.data.DataBaseProvider;
import com.reading.start.general.TLog;
import com.reading.start.presentation.mvp.models.ForgotPasswordModel;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;

/**
 * Dialog for set new password.
 */
public class DialogPasswordSet extends DialogFragment {
    public interface DialogPasswordSetListener {
        void onSet();
    }

    public static final String TAG = DialogPasswordSet.class.getSimpleName();

    private static final String TITLE_TAG = "Title";
    private static final String MESSAGE_TAG = "Message";
    private static final String USER_ID_TAG = "UserId";
    private static final String HASH_TAG = "Hash";

    private DialogPasswordSetListener mListener = null;

    private EditText mNewPassword;

    private EditText mConfirmPassword;

    private ForgotPasswordModel mModel;

    private CompositeDisposable mSubscriptions;

    private String mUserId = null;

    private String mHash = null;

    private Realm mRealm = null;

    public static DialogPasswordSet getInstance(String title, String message, String userId, String hash, DialogPasswordSetListener listener) {
        DialogPasswordSet frag = new DialogPasswordSet();
        frag.mListener = listener;
        Bundle bundle = new Bundle();
        bundle.putString(TITLE_TAG, title);
        bundle.putString(MESSAGE_TAG, message);
        bundle.putString(USER_ID_TAG, userId);
        bundle.putString(HASH_TAG, hash);
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        View rootView = inflater.inflate(R.layout.dialog_password_set, container);

        rootView.findViewById(R.id.button_cancel).setOnClickListener(v -> {
            dismiss();
        });

        rootView.findViewById(R.id.button_next).setOnClickListener(v -> {
            processSetPassword();
        });

        Bundle bundle = getArguments();
        String mTitle = bundle.getString(TITLE_TAG);
        String mMessage = bundle.getString(MESSAGE_TAG);
        mUserId = bundle.getString(USER_ID_TAG);
        mHash = bundle.getString(HASH_TAG);

        TextView title = rootView.findViewById(R.id.dialog_title);
        title.setText(mTitle);
        TextView message = rootView.findViewById(R.id.content_text);
        message.setText(mMessage);

        mNewPassword = rootView.findViewById(R.id.new_password);
        mConfirmPassword = rootView.findViewById(R.id.confirm_password);

        mRealm = DataBaseProvider.getInstance(AppCore.getInstance()).getRealm();
        mModel = new ForgotPasswordModel(mRealm);
        mSubscriptions = new CompositeDisposable();

        return rootView;
    }

    public DialogPasswordSetListener getListener() {
        return mListener;
    }

    public void setListener(DialogPasswordSetListener listener) {
        mListener = listener;
    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        TLog.d(TAG, "Dialog: onDismiss");
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        TLog.d(TAG, "Dialog: onCancel");
    }

    private void processSetPassword() {
        if (validate()) {
            String password = mNewPassword.getText().toString();
            final Observable<Boolean> observable = mModel.getResetPasswordObservable(mUserId, password, mHash);

            mSubscriptions.add(observable
                    .subscribeOn(Schedulers.io())
                    .timeout(Constants.DEFAULT_EXECUTE_TIME, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<Boolean>() {
                        @Override
                        public void onNext(Boolean aBoolean) {
                            dismiss();

                            if (mListener != null) {
                                mListener.onSet();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {
                        }
                    }));
        }
    }

    @Override
    public void onPause() {
        if (mSubscriptions != null && !mSubscriptions.isDisposed()) {
            mSubscriptions.dispose();
            mSubscriptions = null;
        }

        if (mRealm != null && !mRealm.isClosed()) {
            mRealm.close();
            mRealm = null;
        }

        super.onPause();
    }

    private boolean validate() {
        boolean validPassword;

        String newPassword = mNewPassword.getText().toString();
        String confirmPassword = mConfirmPassword.getText().toString();

        if (newPassword.isEmpty()) {
            validPassword = false;
        } else {

            validPassword = newPassword.equals(confirmPassword);
        }

        onPasswordValidation(validPassword);
        return validPassword;
    }

    private void onPasswordValidation(boolean valid) {
        if (!valid) {
            Toast.makeText(getActivity(), getString(R.string.error_message_forgot_password_validation), Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(getActivity(), getString(R.string.password_valid), Toast.LENGTH_SHORT).show();
        }
    }
}

