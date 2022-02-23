package com.reading.start.tests.test_parent_child_play.data.entity;

import com.google.gson.annotations.SerializedName;

public class DataUploadSurveyAttachmentItem {
    public static final String FILED_ASSESSMENT_TYPE = "assestment_type";

    @SerializedName(FILED_ASSESSMENT_TYPE)
    private String assessmentType = "";

    public DataUploadSurveyAttachmentItem() {
    }

    public String getAssessmentType() {
        return assessmentType;
    }

    public void setAssessmentType(String assessmentType) {
        this.assessmentType = assessmentType;
    }
}
