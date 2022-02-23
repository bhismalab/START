package com.reading.start.tests.test_parent.data.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DataAssessment {
    @SerializedName("questions")
    private ArrayList<DataAssessmentItem> mAssessment;

    public ArrayList<DataAssessmentItem> getAssessment() {
        return mAssessment;
    }
}
