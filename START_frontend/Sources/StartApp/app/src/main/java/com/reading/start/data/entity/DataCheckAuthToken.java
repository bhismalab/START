package com.reading.start.data.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Represent response data with token
 */
public class DataCheckAuthToken {
    @SerializedName("payload")
    private Object mPayload;

    public DataCheckAuthToken() {
    }

    public Object getPayload() {
        return mPayload;
    }
}
