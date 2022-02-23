package com.reading.start.data.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Represent data with state info
 */
public class DataStateItem {
    @SerializedName("id")
    private String mId;

    @SerializedName("name")
    private String mNameEnglish;

    @SerializedName("name_hindi")
    private String mNameHindi;

    public String getId() {
        return mId;
    }

    public String getNameEnglish() {
        return mNameEnglish;
    }

    public String getNameHindi() {
        return mNameHindi;
    }
}
