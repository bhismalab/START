package com.reading.start.data.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Represent data with survey id
 */
public class DataSurveyId {
    @SerializedName("survey_id")
    private String mSurveyId = null;

    public String getSurveyId() {
        return mSurveyId;
    }
}
