package com.reading.start.tests.test_eye_tracking.domain.entity;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class EyeTrackingTestSurveyResult extends RealmObject {
    public static final String FILED_ID = "id";
    public static final String FILED_ID_SERVER_1 = "idServer_1";
    public static final String FILED_ID_SERVER_2 = "idServer_2";
    public static final String FILED_ID_ATTACHMENT_SERVER_1 = "idAttachmentServer_1";
    public static final String FILED_ID_ATTACHMENT_SERVER_2 = "idAttachmentServer_2";
    public static final String FILED_SURVEY_ID = "surveyId";
    public static final String FILED_TEST_REF_TABLE = "testRefTable";
    public static final String FILED_TEST_REF_ID = "testRefId";
    public static final String FILED_RESULT_FILES = "resultFiles";
    public static final String FILED_START_TIME = "startTime";
    public static final String FILED_END_TIME = "endTime";

    @PrimaryKey
    @SerializedName(FILED_ID)
    private int id;

    @SerializedName(FILED_ID_SERVER_1)
    private int idServer_1 = -1;

    @SerializedName(FILED_ID_SERVER_2)
    private int idServer_2 = -1;

    @SerializedName(FILED_ID_ATTACHMENT_SERVER_1)
    private int idAttachmentServer_1 = -1;

    @SerializedName(FILED_ID_ATTACHMENT_SERVER_2)
    private int idAttachmentServer_2 = -1;

    @SerializedName(FILED_SURVEY_ID)
    private int surveyId;

    @SerializedName(FILED_TEST_REF_TABLE)
    private String testRefTable;

    @SerializedName(FILED_TEST_REF_ID)
    private String testRefId;

    @SerializedName(FILED_RESULT_FILES)
    private String resultFiles;

    @SerializedName(FILED_START_TIME)
    private long startTime = 0;

    @SerializedName(FILED_END_TIME)
    private long endTime = 0;

    public EyeTrackingTestSurveyResult() {
    }

    public EyeTrackingTestSurveyResult(int id, int surveyId, String testRefTable, String testRefId, String resultFiles,
                                       long startTime, long endTime) {
        this.id = id;
        this.surveyId = surveyId;
        this.testRefTable = testRefTable;
        this.testRefId = testRefId;
        this.resultFiles = resultFiles;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdServer_1() {
        return idServer_1;
    }

    public void setIdServer_1(int idServer_1) {
        this.idServer_1 = idServer_1;
    }

    public int getIdServer_2() {
        return idServer_2;
    }

    public void setIdServer_2(int idServer_2) {
        this.idServer_2 = idServer_2;
    }

    public int getIdAttachmentServer_1() {
        return idAttachmentServer_1;
    }

    public void setIdAttachmentServer_1(int idAttachmentServer_1) {
        this.idAttachmentServer_1 = idAttachmentServer_1;
    }

    public int getIdAttachmentServer_2() {
        return idAttachmentServer_2;
    }

    public void setIdAttachmentServer_2(int idAttachmentServer_2) {
        this.idAttachmentServer_2 = idAttachmentServer_2;
    }

    public int getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(int surveyId) {
        this.surveyId = surveyId;
    }

    public String getTestRefTable() {
        return testRefTable;
    }

    public void setTestRefTable(String testRefTable) {
        this.testRefTable = testRefTable;
    }

    public String getTestRefId() {
        return testRefId;
    }

    public void setTestRefId(String testRefId) {
        this.testRefId = testRefId;
    }

    public String getResultFiles() {
        return resultFiles;
    }

    public void setResultFiles(String resultFiles) {
        this.resultFiles = resultFiles;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return this.endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}
