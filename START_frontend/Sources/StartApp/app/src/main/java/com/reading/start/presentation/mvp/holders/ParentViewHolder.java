package com.reading.start.presentation.mvp.holders;

import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

public class ParentViewHolder {

    private View mActionBar;

    private EditText mName;

    private EditText mSurname;

    private RadioButton mMale;

    private RadioButton mFemale;

    private RadioButton mParent;

    private RadioButton mGuardian;

    private TextView mDateOfBirth;

    private TextView mLanguage;

    private TextView mState;

    private EditText mAddress;

    private EditText mPhone;

    private EditText mEmail;

    private CheckBox mSkip;

    private TextView mPreferableContact;

    public ParentViewHolder(View actionBar, EditText name, EditText surname, RadioButton male, RadioButton female,
                            RadioButton parent, RadioButton guardian, TextView dateOfBirth, TextView language,
                            TextView state, EditText address, EditText phone, EditText email,
                            CheckBox skip, TextView preferableContact) {
        mActionBar = actionBar;
        mName = name;
        mSurname = surname;
        mMale = male;
        mFemale = female;
        mParent = parent;
        mGuardian = guardian;
        mDateOfBirth = dateOfBirth;
        mLanguage = language;
        mState = state;
        mAddress = address;
        mPhone = phone;
        mEmail = email;
        mSkip = skip;
        mPreferableContact = preferableContact;
    }

    public View getActionBar() {
        return mActionBar;
    }

    public EditText getName() {
        return mName;
    }

    public EditText getSurname() {
        return mSurname;
    }

    public RadioButton getMale() {
        return mMale;
    }

    public RadioButton getFemale() {
        return mFemale;
    }

    public RadioButton getParent() {
        return mParent;
    }

    public RadioButton getGuardian() {
        return mGuardian;
    }

    public TextView getDateOfBirth() {
        return mDateOfBirth;
    }

    public TextView getLanguage() {
        return mLanguage;
    }

    public TextView getState() {
        return mState;
    }

    public EditText getAddress() {
        return mAddress;
    }

    public EditText getPhone() {
        return mPhone;
    }

    public EditText getEmail() {
        return mEmail;
    }

    public CheckBox getSkip() {
        return mSkip;
    }

    public TextView getPreferableContact() {
        return mPreferableContact;
    }
}
