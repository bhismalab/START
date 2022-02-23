package com.reading.start.presentation.ui.dialogs;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.reading.start.R;
import com.reading.start.data.DataBaseProvider;
import com.reading.start.domain.entity.Consent;
import com.reading.start.general.TLog;
import com.reading.start.presentation.ui.views.URLImageParser;
import com.reading.start.tests.TestLog;

import java.util.Locale;

import io.realm.Realm;

/**
 * Dialog that displaying consent form.
 */
public class DialogConsent extends DialogFragment {
    public interface DialogConsentListener {
        void onAccept();

        void onBack();
    }

    public static final String TAG = DialogConsent.class.getSimpleName();

    private static final String TITLE_TAG = "Title";

    private DialogConsentListener mListener = null;

    public static DialogConsent getInstance(String title, DialogConsent.DialogConsentListener listener) {
        DialogConsent frag = new DialogConsent();
        frag.mListener = listener;
        Bundle bundle = new Bundle();
        bundle.putString(TITLE_TAG, title);
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        View rootView = inflater.inflate(R.layout.dialog_consent, container);
        rootView.findViewById(R.id.button_next).setOnClickListener(v1 -> {
            dismiss();

            if (mListener != null) {
                mListener.onAccept();
            }
        });

        rootView.findViewById(R.id.button_back).setOnClickListener(v1 -> {
            dismiss();

            if (mListener != null) {
                mListener.onBack();
            }
        });

        Bundle bundle = getArguments();
        String mTitle = bundle.getString(TITLE_TAG);

        TextView title = rootView.findViewById(R.id.dialog_title);
        title.setText(mTitle);

        TextView content = rootView.findViewById(R.id.content);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Spanned spanned = Html.fromHtml(getConsentText(), Html.FROM_HTML_MODE_LEGACY,
                    new URLImageParser(content, getActivity()), null);
            content.setText(spanned);
        } else {
            Spanned spanned = Html.fromHtml(getConsentText(),
                    new URLImageParser(content, getActivity()), null);
            content.setText(spanned);
        }

        return rootView;
    }

    public DialogConsentListener getListener() {
        return mListener;
    }

    public void setListener(DialogConsentListener listener) {
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

    private String getConsentText() {
        String result = "";
        Realm realm = null;

        try {
            realm = DataBaseProvider.getInstance(getActivity()).getRealm();

            if (realm != null && !realm.isClosed()) {
                Consent consent = realm.where(Consent.class).findFirst();

                if (consent != null) {
                    result = isHindiLanguage() ? consent.getHindi() : consent.getEnglish();
                }
            } else {
                TLog.d(TAG, "Realm closed");
            }
        } catch (Exception e) {
            TLog.e(TAG, e);
        } finally {
            if (realm != null) {
                realm.close();
            }
        }

        return result;
    }

    private boolean isHindiLanguage() {
        boolean result = false;

        try {
            if (Locale.getDefault().getLanguage().equals("hi")) {
                result = true;
            }
        } catch (Exception e) {
            TestLog.e(TAG, "updateLanguage", e);
        }

        return result;
    }
}
