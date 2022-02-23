package com.reading.start.data.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Represent response data with list of surveys
 */
public class DataChildrenSurveys {
    @SerializedName("surveys")
    private ArrayList<DataChildrenSurveyItem> mSurveys;

    @SerializedName("items_count")
    private int mCount;

    public ArrayList<DataChildrenSurveyItem> getSurveys() {
        return mSurveys;
    }

    public int getCount() {
        return mCount;
    }
}
