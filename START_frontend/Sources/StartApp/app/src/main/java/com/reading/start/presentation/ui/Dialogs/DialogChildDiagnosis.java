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

import com.reading.start.Constants;
import com.reading.start.R;
import com.reading.start.general.TLog;
import com.reading.start.presentation.ui.UiConstants;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Dialog for displaying diagnostic info for child.
 */
public class DialogChildDiagnosis extends DialogFragment {
    public interface DialogChildDiagnosisListener {
        void onSave(String doctorName, String diagnosis, long date);
    }

    public static final String TAG = DialogChildDiagnosis.class.getSimpleName();

    private static final String TITLE_TAG = "Title";

    private DialogChildDiagnosisListener mListener = null;

    private EditText mDoctorText;

    private EditText mDiagnosisText;

    private TextView mDateOfBirth;

    private DateFormat mDateFormatter = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault());

    private DatePickerDialog mDatePickerDialog;

    private Calendar mSelectedCalendar = null;

    public static DialogChildDiagnosis getInstance(String title, String doctorValue, String diagnosisValue,
                                                   long diagnosisDate, DialogChildDiagnosis.DialogChildDiagnosisListener listener) {
        DialogChildDiagnosis frag = new DialogChildDiagnosis();
        frag.mListener = listener;
        Bundle bundle = new Bundle();
        bundle.putString(TITLE_TAG, title);
        bundle.putString(UiConstants.DOCTOR_ID, doctorValue);
        bundle.putString(UiConstants.DIAGNOSIS_ID, diagnosisValue);
        bundle.putLong(UiConstants.ASSESSMENT_TIME_ID, diagnosisDate);
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        View root = inflater.inflate(R.layout.dialog_child_diagnosis, container);

        Bundle bundle = getArguments();
        String mTitle = bundle.getString(TITLE_TAG);

        TextView title = root.findViewById(R.id.dialog_title);
        title.setText(mTitle);

        mDateOfBirth = root.findViewById(R.id.date_of_birth);

        String doctorValue = "";
        String diagnosisValue = "";
        mSelectedCalendar = Calendar.getInstance();

        if (getArguments() != null) {
            if (getArguments().containsKey(UiConstants.DOCTOR_ID)) {
                doctorValue = getArguments().getString(UiConstants.DOCTOR_ID);
            }

            if (getArguments().containsKey(UiConstants.DIAGNOSIS_ID)) {
                diagnosisValue = getArguments().getString(UiConstants.DIAGNOSIS_ID);
            }

            if (getArguments().containsKey(UiConstants.ASSESSMENT_TIME_ID)) {
                long time = getArguments().getLong(UiConstants.ASSESSMENT_TIME_ID);

                if (time != 0) {
                    mSelectedCalendar.setTimeInMillis(time);
                    mDateOfBirth.setText(mDateFormatter.format(mSelectedCalendar.getTime()));
                }
            }
        }

        mDoctorText = root.findViewById(R.id.name_clinic_doctor);
        mDoctorText.setText(doctorValue);
        mDiagnosisText = root.findViewById(R.id.text_diagnosis);
        mDiagnosisText.setText(diagnosisValue);

        // set date time picker
        mDateOfBirth.setOnClickListener(view -> {
            Calendar newCalendar = mSelectedCalendar;

            try {
                String value = mDateOfBirth.getText().toString();
                long defaultTime = 0;

                if (value == null || value.isEmpty()) {
                    defaultTime = Constants.DEFAULT_DATE_ASSESSMENT;
                } else {
                    defaultTime = mDateFormatter.parse(value).getTime();
                }

                newCalendar.setTimeInMillis(defaultTime);
            } catch (ParseException e) {
                TLog.e(TAG, e);
            }

            mDatePickerDialog.getDatePicker().updateDate(newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
            mDatePickerDialog.show();
        });

        long defaultTime = -1;
        String value = mDateOfBirth.getText().toString();

        if (value == null || value.isEmpty()) {
            defaultTime = Constants.DEFAULT_DATE_ASSESSMENT;
            mSelectedCalendar.setTimeInMillis(defaultTime);
        }

        int theme;

        if (Build.VERSION.SDK_INT < 23) {
            theme = AlertDialog.THEME_HOLO_LIGHT;
        } else {
            theme = android.R.style.Theme_Holo_Dialog;
        }

        mDatePickerDialog = new DatePickerDialog(getActivity(), theme, (view, year, monthOfYear, dayOfMonth) -> {
            mSelectedCalendar.set(year, monthOfYear, dayOfMonth);
            mDateOfBirth.setText(mDateFormatter.format(mSelectedCalendar.getTime()));
        }, mSelectedCalendar.get(Calendar.YEAR), mSelectedCalendar.get(Calendar.MONTH), mSelectedCalendar.get(Calendar.DAY_OF_MONTH));
        mDatePickerDialog.getDatePicker().setCalendarViewShown(false);
        mDatePickerDialog.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());

        root.findViewById(R.id.button_save).setOnClickListener(v1 -> {
            dismiss();

            if (mListener != null) {
                String doctorName = mDoctorText.getText().toString();
                String diagnosis = mDiagnosisText.getText().toString();

                long date = 0;

                if (mSelectedCalendar != null) {
                    date = mSelectedCalendar.getTimeInMillis();
                }

                mListener.onSave(doctorName, diagnosis, date);
            }
        });

        return root;
    }

    public DialogChildDiagnosisListener getListener() {
        return mListener;
    }

    public void setListener(DialogChildDiagnosisListener listener) {
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
}
