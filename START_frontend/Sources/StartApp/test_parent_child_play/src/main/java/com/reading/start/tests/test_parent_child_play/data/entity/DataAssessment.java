package com.reading.start.tests.test_parent_child_play.data.entity;

import com.google.gson.annotations.SerializedName;

public class DataAssessment {
    @SerializedName("assestment")
    private DataAssessmentItem mAssessment;

    public DataAssessmentItem getAssessment() {
        return mAssessment;
    }
}
