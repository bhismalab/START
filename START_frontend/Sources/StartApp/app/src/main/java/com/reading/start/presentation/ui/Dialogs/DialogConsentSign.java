package com.reading.start.presentation.ui.dialogs;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.jakewharton.rxbinding2.view.RxView;
import com.reading.start.R;
import com.reading.start.general.TLog;
import com.reading.start.utils.BitmapUtils;

/**
 * Dialog that displaying fields for signature of consent form.
 */
public class DialogConsentSign extends DialogFragment {
    public interface DialogConsentSignListener {
        void onSave(String signature_1_Base64, String signature_2_Base64);

        void onBack();
    }

    public static final String TAG = DialogConsentSign.class.getSimpleName();

    private static final String TITLE_TAG = "Title";
    private static final String SINGLE_PARENT_TAG = "SingleParent";
    private static final String PARENT_NAME_1_TAG = "ParentName1";
    private static final String PARENT_NAME_2_TAG = "ParentName2";

    private DialogConsentSignListener mListener = null;

    private RadioButton mParent1Button = null;
    private RadioButton mParent2Button = null;

    private SignaturePad mSignaturePad1 = null;
    private SignaturePad mSignaturePad2 = null;

    private boolean mSingleParent = true;

    private String mParentName1 = "";
    private String mParentName2 = "";

    public static DialogConsentSign getInstance(String title, boolean singleParent,
                                                String parentName1, String parentName2,
                                                DialogConsentSign.DialogConsentSignListener listener) {
        DialogConsentSign frag = new DialogConsentSign();
        frag.mListener = listener;
        Bundle bundle = new Bundle();
        bundle.putString(TITLE_TAG, title);
        bundle.putBoolean(SINGLE_PARENT_TAG, singleParent);
        bundle.putString(PARENT_NAME_1_TAG, parentName1);
        bundle.putString(PARENT_NAME_2_TAG, parentName2);
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        View root = inflater.inflate(R.layout.dialog_consent_sign, container);

        Bundle bundle = getArguments();
        String titleValue = bundle.getString(TITLE_TAG);
        mSingleParent = bundle.getBoolean(SINGLE_PARENT_TAG);

        if (bundle.containsKey(PARENT_NAME_1_TAG)) {
            mParentName1 = bundle.getString(PARENT_NAME_1_TAG);
        }

        if (bundle.containsKey(PARENT_NAME_2_TAG)) {
            mParentName2 = bundle.getString(PARENT_NAME_2_TAG);
        }

        mParent1Button = root.findViewById(R.id.parent_1_radio_button);
        mParent1Button.setText(mParentName1);
        mParent2Button = root.findViewById(R.id.parent_2_radio_button);
        mParent2Button.setText(mParentName2);

        if (mSingleParent) {
            mParent1Button.setVisibility(View.GONE);
            mParent2Button.setVisibility(View.GONE);
        } else {
            mParent1Button.setVisibility(View.VISIBLE);
            mParent2Button.setVisibility(View.VISIBLE);
        }

        if (mParent1Button.isChecked()) {
            mParent1Button.setPaintFlags(mParent1Button.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        } else {
            mParent2Button.setPaintFlags(mParent2Button.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }

        RxView.clicks(mParent1Button).subscribe(aVoid -> {
            mParent1Button.setPaintFlags(mParent1Button.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            mParent2Button.setPaintFlags(0);

            mSignaturePad1.setVisibility(View.VISIBLE);
            mSignaturePad2.setVisibility(View.GONE);
        });

        RxView.clicks(mParent2Button).subscribe(aVoid -> {
            mParent2Button.setPaintFlags(mParent2Button.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            mParent1Button.setPaintFlags(0);

            mSignaturePad1.setVisibility(View.GONE);
            mSignaturePad2.setVisibility(View.VISIBLE);
        });

        mSignaturePad1 = root.findViewById(R.id.signature_pad_1);
        mSignaturePad1.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
                //implement if will be needed
            }

            @Override
            public void onSigned() {
                //implement if will be needed
            }

            @Override
            public void onClear() {
                //implement if will be needed
            }
        });

        mSignaturePad2 = root.findViewById(R.id.signature_pad_2);
        mSignaturePad2.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
                //implement if will be needed
            }

            @Override
            public void onSigned() {
                //implement if will be needed
            }

            @Override
            public void onClear() {
                //implement if will be needed
            }
        });

        mSignaturePad1.setVisibility(View.VISIBLE);
        mSignaturePad2.setVisibility(View.GONE);

        View nextButton = root.findViewById(R.id.button_next);
        nextButton.setEnabled(false);
        CheckBox agree = root.findViewById(R.id.checkbox_agree);

        agree.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                nextButton.setEnabled(true);
            } else {
                nextButton.setEnabled(false);
            }
        });

        nextButton.setOnClickListener(v1 -> {
            dismiss();

            if (mListener != null) {
                String signature1 = null;

                try {
                    Bitmap sign = mSignaturePad1.getSignatureBitmap();

                    if (sign != null) {
                        signature1 = BitmapUtils.bitmapToBase64(sign);
                    }
                } catch (Exception e) {
                    TLog.e(TAG, e);
                }

                String signature2 = null;

                if (!mSingleParent) {
                    try {
                        Bitmap sign = mSignaturePad2.getSignatureBitmap();

                        if (sign != null) {
                            signature2 = BitmapUtils.bitmapToBase64(sign);
                        }
                    } catch (Exception e) {
                        TLog.e(TAG, e);
                    }
                }

                mListener.onSave(signature1, signature2);
            }
        });

        root.findViewById(R.id.button_back).setOnClickListener(v1 -> {
            dismiss();

            if (mListener != null) {
                mListener.onBack();
            }
        });

        TextView title = root.findViewById(R.id.dialog_title);
        title.setText(titleValue);

        return root;
    }

    public DialogConsentSignListener getListener() {
        return mListener;
    }

    public void setListener(DialogConsentSignListener listener) {
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
