package com.reading.start.data.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Represent data for reset password
 */
public class DataPasswordRecoveryRequest {
    @SerializedName("email")
    private String mUserName = null;

    @SerializedName("birth_date")
    private String mBirthDate = null;

    public DataPasswordRecoveryRequest(String userName, String birthDate) {
        mUserName = userName;
        mBirthDate = birthDate;
    }

    public String getUserName() {
        return mUserName;
    }

    public String getBirthDate() {
        return mBirthDate;
    }
}
