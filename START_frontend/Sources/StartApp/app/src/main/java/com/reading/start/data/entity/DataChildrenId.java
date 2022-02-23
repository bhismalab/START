package com.reading.start.data.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Represent response data with child id
 */
public class DataChildrenId {
    @SerializedName("child_id")
    private String mChildId = null;

    public String getChildId() {
        return mChildId;
    }
}
