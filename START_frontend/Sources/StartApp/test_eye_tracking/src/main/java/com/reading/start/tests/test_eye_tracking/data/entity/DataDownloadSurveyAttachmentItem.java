package com.reading.start.tests.test_eye_tracking.data.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DataDownloadSurveyAttachmentItem {
    public static final String FILED_ASSESSMENT_TYPE = "assestment_type";
    public static final String FILED_SURVEY_ID = "survey_id";
    public static final String FILED_ATTEMPT = "attempt";
    public static final String FILED_FILES = "files";

    @SerializedName(FILED_ASSESSMENT_TYPE)
    private String assessmentType = "";

    @SerializedName(FILED_SURVEY_ID)
    private String surveyId = null;

    @SerializedName(FILED_ATTEMPT)
    private String attempt = null;

    @SerializedName(FILED_FILES)
    private ArrayList<String> files;

    public DataDownloadSurveyAttachmentItem() {
    }

    public String getAssessmentType() {
        return assessmentType;
    }

    public void setAssessmentType(String assessmentType) {
        this.assessmentType = assessmentType;
    }

    public String getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(String surveyId) {
        this.surveyId = surveyId;
    }

    public String getAttempt() {
        return attempt;
    }

    public void setAttempt(String attempt) {
        this.attempt = attempt;
    }

    public ArrayList<String> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<String> files) {
        this.files = files;
    }
}
