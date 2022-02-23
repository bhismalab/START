package com.reading.start.data.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Represent response data with consent form content
 */
public class DataConsentForm {
    @SerializedName("english")
    private String mEnglish;

    @SerializedName("hindi")
    private String mHindi;

    public String getEnglish() {
        return mEnglish;
    }

    public String getHindi() {
        return mHindi;
    }
}
