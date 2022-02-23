package com.reading.start.tests.test_eye_tracking.ui.dialogs;

import android.app.Dialog;
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

import com.reading.start.tests.TestLog;
import com.reading.start.tests.test_eye_tracking.R;

public class DialogCompleteTest extends DialogFragment {
    public interface DialogListener {
        void onBack();

        void onNext();

        void onAddNew();
    }

    public static final String TAG = DialogCompleteTest.class.getSimpleName();

    private static final String TITLE_TAG = "Title";
    private static final String MESSAGE_TAG = "Message";
    private static final String SHOT_NEW_ATTEMPT_TAG = "ShowNewAttempt";
    private static final String SHOW_NEXT_TEST_TAG = "ShowNextTest";

    private DialogListener mListener = null;

    public static DialogCompleteTest getInstance(String title, String message, boolean showNewAttempt,
                                                 boolean showNextTest, DialogListener listener) {
        DialogCompleteTest frag = new DialogCompleteTest();
        frag.mListener = listener;
        Bundle bundle = new Bundle();
        bundle.putString(TITLE_TAG, title);
        bundle.putString(MESSAGE_TAG, message);
        bundle.putBoolean(SHOT_NEW_ATTEMPT_TAG, showNewAttempt);
        bundle.putBoolean(SHOW_NEXT_TEST_TAG, showNextTest);
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        View mRootView = inflater.inflate(R.layout.test_eye_tracking_dialog_completed_test, container);
        mRootView.findViewById(R.id.button_back).setOnClickListener(v -> {
            dismiss();

            if (mListener != null) {
                mListener.onBack();
            }
        });

        mRootView.findViewById(R.id.button_next).setOnClickListener(v -> {
            dismiss();

            if (mListener != null) {
                mListener.onNext();
            }
        });

        mRootView.findViewById(R.id.button_new).setOnClickListener(v -> {
            dismiss();

            if (mListener != null) {
                mListener.onAddNew();
            }
        });

        Bundle bundle = getArguments();
        String mTitle = bundle.getString(TITLE_TAG);
        String mMessage = bundle.getString(MESSAGE_TAG);
        boolean mShowNewAttempt = bundle.getBoolean(SHOT_NEW_ATTEMPT_TAG);
        boolean mShowNextTest = bundle.getBoolean(SHOW_NEXT_TEST_TAG);

        if (mShowNewAttempt) {
            mRootView.findViewById(R.id.button_new).setVisibility(View.VISIBLE);
        } else {
            mRootView.findViewById(R.id.button_new).setVisibility(View.GONE);
        }

        if (mShowNextTest) {
            mRootView.findViewById(R.id.button_next).setVisibility(View.VISIBLE);
        } else {
            mRootView.findViewById(R.id.button_next).setVisibility(View.GONE);
        }

        TextView title = mRootView.findViewById(R.id.dialog_title);
        title.setText(mTitle);
        TextView message = mRootView.findViewById(R.id.content_text);
        message.setText(mMessage);

        return mRootView;
    }

    @Override
    public Dialog getDialog() {
        return super.getDialog();
    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        TestLog.d(TAG, "Dialog: onDismiss");
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        TestLog.d(TAG, "Dialog: onCancel");
    }
}
