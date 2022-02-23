package com.reading.start.data.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Represent data for login request
 */
public class DataLoginRequest {
    @SerializedName("email")
    private String mUserName = null;

    @SerializedName("password")
    private String mPassword = null;

    public DataLoginRequest(String userName, String password) {
        mUserName = userName;
        mPassword = password;
    }

    public String getUserName() {
        return mUserName;
    }

    public String getPassword() {
        return mPassword;
    }
}
