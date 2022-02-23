package com.reading.start.tests.test_motor_following.data.entity;

import com.google.gson.annotations.SerializedName;

public class DataDownloadSurveyResultItem {
    public static final String FILED_ASSESSMENT_TYPE = "assestment_type";
    public static final String FILED_SURVEY_ID = "survey_id";
    public static final String FILED_FILE_NAME = "result_file";
    public static final String FILED_FILE_CONTENT = "survey_result";

    @SerializedName(FILED_ASSESSMENT_TYPE)
    private String assessmentType = "";

    @SerializedName(FILED_SURVEY_ID)
    private String surveyId = null;

    @SerializedName(FILED_FILE_NAME)
    private String fileName = "";

    @SerializedName(FILED_FILE_CONTENT)
    private Object fileContent = "";

    public DataDownloadSurveyResultItem() {
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

    public Object getFileContent() {
        return fileContent;
    }

    public void setFileContent(Object fileContent) {
        this.fileContent = fileContent;
    }
}
