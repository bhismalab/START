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
 * Dialog displaying when login failed.
 */
public class DialogLoginFailed extends DialogFragment implements View.OnClickListener {

    public static final String TAG = DialogLoginFailed.class.getSimpleName();
    private static final String TITLE_TAG = "Title";
    private static final String MESSAGE_TAG = "Message";

    public static DialogLoginFailed getInstance(String title, String message) {
        DialogLoginFailed frag = new DialogLoginFailed();
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

        View rootView = inflater.inflate(R.layout.dialog_login_failed, container);
        rootView.findViewById(R.id.button_next).setOnClickListener(this);

        Bundle bundle = getArguments();
        String mTitle = bundle.getString(TITLE_TAG);
        String mMessage = bundle.getString(MESSAGE_TAG);

        TextView title = rootView.findViewById(R.id.dialog_title);
        title.setText(mTitle);
        TextView message = rootView.findViewById(R.id.content_text);
        message.setText(mMessage);

        return rootView;
    }

    @Override
    public Dialog getDialog() {
        return super.getDialog();
    }

    @Override
    public void onClick(View v) {
        dismiss();
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
