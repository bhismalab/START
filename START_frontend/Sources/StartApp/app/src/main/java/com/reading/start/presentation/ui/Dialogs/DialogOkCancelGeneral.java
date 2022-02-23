package com.reading.start.presentation.ui.dialogs;

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

import com.reading.start.R;
import com.reading.start.general.TLog;

/**
 * General dialogs with two buttons.
 */
public class DialogOkCancelGeneral extends DialogFragment {
    public interface DialogListener {
        void onOK();

        void onCancel();
    }

    public static final String TAG = DialogLoginFailed.class.getSimpleName();
    private static final String TITLE_TAG = "Title";
    private static final String MESSAGE_TAG = "Message";

    private DialogListener mListener = null;

    public static DialogOkCancelGeneral getInstance(String title, String message, DialogListener listener) {
        DialogOkCancelGeneral frag = new DialogOkCancelGeneral();
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

        View mRootView = inflater.inflate(R.layout.dialog_ok_cancel_general, container);
        mRootView.findViewById(R.id.button_next).setOnClickListener(v -> {
            dismiss();

            if (mListener != null) {
                mListener.onOK();
            }
        });

        mRootView.findViewById(R.id.button_cancel).setOnClickListener(v -> {
            dismiss();

            if (mListener != null) {
                mListener.onCancel();
            }
        });

        Bundle bundle = getArguments();
        String mTitle = bundle.getString(TITLE_TAG);
        String mMessage = bundle.getString(MESSAGE_TAG);

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
        TLog.d(TAG, "Dialog: onDismiss");
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        TLog.d(TAG, "Dialog: onCancel");
    }
}
