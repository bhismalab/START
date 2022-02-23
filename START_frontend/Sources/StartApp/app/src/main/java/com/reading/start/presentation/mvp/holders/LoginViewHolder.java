package com.reading.start.presentation.mvp.holders;

import android.widget.Button;
import android.widget.TextView;

public class LoginViewHolder {
    private final TextView mName;
    private final TextView mPassword;
    private final Button mLogin;
    private final TextView mForgot;

    public LoginViewHolder(TextView name, TextView password, Button login, TextView forgot) {
        mName = name;
        mPassword = password;
        mLogin = login;
        mForgot = forgot;
    }

    public TextView getName() {
        return mName;
    }

    public TextView getPassword() {
        return mPassword;
    }

    public Button getLogin() {
        return mLogin;
    }

    public TextView getForgot() {
        return mForgot;
    }
}
