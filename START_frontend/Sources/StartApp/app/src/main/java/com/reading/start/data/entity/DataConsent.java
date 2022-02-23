package com.reading.start.data.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Represent response data with consent form
 */
public class DataConsent {
    @SerializedName("consent_form")
    private DataConsentForm mConsentForm;

    @SerializedName("isHtml")
    private boolean mIsHtml;

    public DataConsentForm getConsentForm() {
        return mConsentForm;
    }

    public boolean isHtml() {
        return mIsHtml;
    }
}
