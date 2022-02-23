package com.reading.start.tests.test_coloring.data.entity;

import com.google.gson.annotations.SerializedName;

public class DataAssessment {
    @SerializedName("assestment")
    private DataAssessmentItem mAssessment;

    public DataAssessmentItem getAssessment() {
        return mAssessment;
    }
}
