package com.reading.start.data.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Represent response data with password recovery info
 */
public class DataPasswordRecovery {
    @SerializedName("social_worker_id")
    private String mId = "-1";

    @SerializedName("security_hash")
    private String mSecurityHash = "-1";

    public DataPasswordRecovery() {
    }

    public String getId() {
        return mId;
    }

    public String getSecurityHash() {
        return mSecurityHash;
    }
}
