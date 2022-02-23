package com.reading.start.data.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Represent response data with list of surveys
 */
public class DataSurvey {
    @SerializedName("surveys")
    private ArrayList<DataSurveyItem> mSurveys;

    public ArrayList<DataSurveyItem> getSurveys() {
        return mSurveys;
    }
}
