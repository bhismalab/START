package com.reading.start.tests.test_coloring.domain.entity;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class ColoringTestSurveyAttachments extends RealmObject {
    public static final String FILED_ID = "id";
    public static final String FILED_ID_SERVER = "idServer";
    public static final String FILED_SURVEY_ID = "surveyId";
    public static final String FILED_SURVEY_RESULT_ID = "surveyResultId";
    public static final String FILED_ATTACHMENT_FILE = "attachmentFile";

    @PrimaryKey
    @SerializedName(FILED_ID)
    private int id;

    @SerializedName(FILED_ID_SERVER)
    private int idServer = -1;

    @SerializedName(FILED_SURVEY_ID)
    private int surveyId;

    @SerializedName(FILED_SURVEY_RESULT_ID)
    private int surveyResultId;

    @SerializedName(FILED_ATTACHMENT_FILE)
    private String attachmentFile;

    public ColoringTestSurveyAttachments() {
    }

    public ColoringTestSurveyAttachments(int id, int surveyId, String attachmentFile) {
        this.id = id;
        this.surveyId = surveyId;
        this.attachmentFile = attachmentFile;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdServer() {
        return idServer;
    }

    public void setIdServer(int idServer) {
        this.idServer = idServer;
    }

    public int getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(int surveyId) {
        this.surveyId = surveyId;
    }

    public int getSurveyResultId() {
        return surveyResultId;
    }

    public void setSurveyResultId(int surveyResultId) {
        this.surveyResultId = surveyResultId;
    }

    public String getAttachmentFile() {
        return attachmentFile;
    }

    public void setAttachmentFile(String attachmentFile) {
        this.attachmentFile = attachmentFile;
    }
}
