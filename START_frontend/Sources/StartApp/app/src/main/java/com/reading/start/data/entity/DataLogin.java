package com.reading.start.data.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Represent response data with login info
 */
public class DataLogin {
    @SerializedName("jwt_token")
    private String mToken;

    @SerializedName("social_worker")
    private DataSocialWorker mSocialWorker;

    private int mCode = 0;

    public DataLogin() {
    }

    public String getToken() {
        return mToken;
    }

    public DataSocialWorker getSocialWorker() {
        return mSocialWorker;
    }
}
