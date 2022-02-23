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
import android.widget.TextView;

import com.reading.start.R;
import com.reading.start.general.TLog;

/**
 * Dialog that displaying when register of child success.
 */
public class DialogConsentSignSuccess extends DialogFragment {
    public interface DialogConsentSignSuccessListener {
        void onMySurveys();

        void onStartSurvey();
    }

    public static final String TAG = DialogConsentSignSuccess.class.getSimpleName();

    private static final String TITLE_TAG = "Title";
    private static final String MESSAGE_TAG = "Message";

    private DialogConsentSignSuccessListener mListener = null;

    public static DialogConsentSignSuccess getInstance(String title, String message, DialogConsentSignSuccess.DialogConsentSignSuccessListener listener) {
        DialogConsentSignSuccess frag = new DialogConsentSignSuccess();
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

        View rootView = inflater.inflate(R.layout.dialog_consent_sign_success, container);

        rootView.findViewById(R.id.button_my_survey_page).setOnClickListener(v12 -> {
            if (mListener != null) {
                mListener.onMySurveys();
            }
        });

        rootView.findViewById(R.id.button_start_survey).setOnClickListener(v1 -> {
            if (mListener != null) {
                mListener.onStartSurvey();
            }
        });

        Bundle bundle = getArguments();
        String mTitle = bundle.getString(TITLE_TAG);
        String mMessage = bundle.getString(MESSAGE_TAG);

        TextView title = rootView.findViewById(R.id.dialog_title);
        title.setText(mTitle);
        TextView message = rootView.findViewById(R.id.content_text);
        message.setText(mMessage);

        return rootView;
    }

    public DialogConsentSignSuccessListener getListener() {
        return mListener;
    }

    public void setListener(DialogConsentSignSuccessListener listener) {
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

