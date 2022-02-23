package com.reading.start.tests.test_eye_tracking.data.entity;

import com.google.gson.annotations.SerializedName;

public class DataAssessmentSlide {
    @SerializedName("assestment")
    private DataAssessmentSlideItem mAssessment;

    public DataAssessmentSlideItem getAssessment() {
        return mAssessment;
    }
}
