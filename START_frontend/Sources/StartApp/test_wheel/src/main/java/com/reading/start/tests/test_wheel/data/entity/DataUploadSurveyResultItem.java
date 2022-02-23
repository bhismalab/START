package com.reading.start.tests.test_wheel.data.entity;

import com.google.gson.annotations.SerializedName;

public class DataUploadSurveyResultItem {
    public static final String FILED_ASSESSMENT_TYPE = "assestment_type";
    public static final String FILED_SURVEY_ID = "survey_id";
    public static final String FILED_FILE_NAME = "file_name";
    public static final String FILED_FILE_CONTENT = "file_content";

    @SerializedName(FILED_ASSESSMENT_TYPE)
    private String assessmentType = "";

    @SerializedName(FILED_SURVEY_ID)
    private String surveyId = null;

    @SerializedName(FILED_FILE_NAME)
    private String fileName = "";

    @SerializedName(FILED_FILE_CONTENT)
    private String fileContent = "";

    public DataUploadSurveyResultItem() {
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileContent() {
        return fileContent;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }
}
