package com.reading.start.tests.test_eye_tracking.data.entity;

import com.google.gson.annotations.SerializedName;

public class DataAssessmentPairs {
    @SerializedName("assestment")
    private DataAssessmentPairsItem mAssessment;

    public DataAssessmentPairsItem getAssessment() {
        return mAssessment;
    }
}
