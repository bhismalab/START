package com.reading.start.data.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Represent data for reset password
 */
public class DataPasswordResetRequest {
    @SerializedName("password")
    private String mPassword = null;

    @SerializedName("security_hash")
    private String mSecurityHash = null;

    public DataPasswordResetRequest(String password, String securityHash) {
        mPassword = password;
        mSecurityHash = securityHash;
    }

    public String getPassword() {
        return mPassword;
    }

    public String getSecurityHash() {
        return mSecurityHash;
    }
}
