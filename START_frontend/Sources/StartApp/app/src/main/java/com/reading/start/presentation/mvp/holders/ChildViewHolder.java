package com.reading.start.presentation.mvp.holders;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

public class ChildViewHolder {

    private View mActionBar;

    private ImageView mChildPhoto;

    private ImageView mDiagnosis;

    private EditText mName;

    private EditText mSurname;

    private RadioButton mMale;

    private RadioButton mFemale;

    private TextView mDateOfBirth;

    private TextView mState;

    private EditText mAddress;

    private TextView mHand;

    public ChildViewHolder(View actionBar, ImageView childPhoto,
                           ImageView diagnosis, EditText name, EditText surname,
                           RadioButton male, RadioButton female, TextView dateOfBirth,
                           TextView state, EditText address, TextView hand) {
        mActionBar = actionBar;
        mChildPhoto = childPhoto;
        mDiagnosis = diagnosis;
        mName = name;
        mSurname = surname;
        mMale = male;
        mFemale = female;
        mDateOfBirth = dateOfBirth;
        mState = state;
        mAddress = address;
        mHand = hand;
    }

    public View getActionBar() {
        return mActionBar;
    }

    public ImageView getChildPhoto() {
        return mChildPhoto;
    }

    public ImageView getDiagnosis() {
        return mDiagnosis;
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

    public TextView getDateOfBirth() {
        return mDateOfBirth;
    }

    public TextView getState() {
        return mState;
    }

    public EditText getAddress() {
        return mAddress;
    }

    public TextView getHand() {
        return mHand;
    }
}
