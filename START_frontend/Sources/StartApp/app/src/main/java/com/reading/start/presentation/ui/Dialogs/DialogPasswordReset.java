package com.reading.start.presentation.ui.dialogs;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
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
import com.reading.start.data.entity.DataPasswordRecovery;
import com.reading.start.general.TLog;
import com.reading.start.presentation.mvp.models.ForgotPasswordModel;
import com.reading.start.tests.ISO8601;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;

/**
 * Dialog for reset password.
 */
public class DialogPasswordReset extends DialogFragment {
    public interface DialogPasswordResetListener {
        void onCancel();

        void onReset(String userId, String hash);
    }

    public static final String TAG = DialogPasswordReset.class.getSimpleName();

    private static final String TITLE_TAG = "Title";
    private static final String MESSAGE_TAG = "Message";

    private DialogPasswordResetListener mListener = null;

    private DateFormat mDateFormatter = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault());

    private DatePickerDialog mDatePickerDialog;

    private EditText mUserName;

    private TextView mDateOfBirth;

    private ForgotPasswordModel mModel;

    private CompositeDisposable mSubscriptions;

    private Realm mRealm = null;

    public static DialogPasswordReset getInstance(String title, String message, DialogPasswordResetListener listener) {
        DialogPasswordReset frag = new DialogPasswordReset();
        frag.mListener = listener;
        Bundle bundle = new Bundle();
        bundle.putString(TITLE_TAG, title);
        bundle.putString(MESSAGE_TAG, message);
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        View rootView = inflater.inflate(R.layout.dialog_password_reset, container);

        rootView.findViewById(R.id.button_cancel).setOnClickListener(v -> {
            dismiss();

            if (mListener != null) {
                mListener.onCancel();
            }
        });

        rootView.findViewById(R.id.button_next).setOnClickListener(v -> {
            processResetPassword();
        });

        Bundle bundle = getArguments();
        String mTitle = bundle.getString(TITLE_TAG);
        String mMessage = bundle.getString(MESSAGE_TAG);

        TextView title = rootView.findViewById(R.id.dialog_title);
        title.setText(mTitle);
        TextView message = rootView.findViewById(R.id.content_text);
        message.setText(mMessage);

        mUserName = rootView.findViewById(R.id.user_name);
        mDateOfBirth = rootView.findViewById(R.id.date_of_birth);

        // set date time picker
        mDateOfBirth.setOnClickListener(view -> {
            Calendar newCalendar = Calendar.getInstance();
            newCalendar.setTimeInMillis(Constants.DEFAULT_DATE_PASSWORD);

            mDatePickerDialog.getDatePicker().updateDate(newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
            mDatePickerDialog.show();
        });

        Calendar newCalendar = Calendar.getInstance();
        newCalendar.setTimeInMillis(Constants.DEFAULT_DATE_PASSWORD);

        int theme;
        if (Build.VERSION.SDK_INT < 23) {
            theme = AlertDialog.THEME_HOLO_LIGHT;
        } else {
            theme = android.R.style.Theme_Holo_Dialog;
        }

        mDatePickerDialog = new DatePickerDialog(getActivity(), theme, (view, year, monthOfYear, dayOfMonth) -> {
            Calendar newDate = Calendar.getInstance();
            newDate.set(year, monthOfYear, dayOfMonth);
            mDateOfBirth.setText(mDateFormatter.format(newDate.getTime()));
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        mDatePickerDialog.getDatePicker().setCalendarViewShown(false);
        mDatePickerDialog.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());

        mRealm = DataBaseProvider.getInstance(AppCore.getInstance()).getRealm();
        mModel = new ForgotPasswordModel(mRealm);
        mSubscriptions = new CompositeDisposable();

        return rootView;
    }

    public DialogPasswordResetListener getListener() {
        return mListener;
    }

    public void setListener(DialogPasswordResetListener listener) {
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

    private void processResetPassword() {
        String name = mUserName.getText().toString();
        String date = mDateOfBirth.getText().toString();

        try {
            long defaultTime = mDateFormatter.parse(date).getTime();
            date = ISO8601.fromDate(defaultTime);
        } catch (ParseException e) {
            TLog.e(TAG, e);
        }

        final Observable<DataPasswordRecovery> observable = mModel.getForgotDateObservable(name, date);

        mSubscriptions.add(observable
                .subscribeOn(Schedulers.io())
                .timeout(Constants.DEFAULT_EXECUTE_TIME, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<DataPasswordRecovery>() {
                    @Override
                    public void onNext(DataPasswordRecovery data) {
                        if (data != null && !data.getId().equals("-1")) {
                            dismiss();

                            if (mListener != null) {
                                mListener.onReset(data.getId(), data.getSecurityHash());
                            }
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

