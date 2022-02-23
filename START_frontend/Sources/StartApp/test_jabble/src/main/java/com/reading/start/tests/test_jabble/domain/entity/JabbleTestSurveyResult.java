package com.reading.start.tests.test_jabble.domain.entity;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class JabbleTestSurveyResult extends RealmObject {
    public static final String FILED_ID = "id";
    public static final String FILED_ID_SERVER = "idServer";
    public static final String FILED_SURVEY_ID = "surveyId";
    public static final String FILED_TEST_REF_TABLE = "testRefTable";
    public static final String FILED_TEST_REF_ID = "testRefId";
    public static final String FILED_RESULT_FILES = "resultFiles";
    public static final String FILED_START_TIME = "startTime";
    public static final String FILED_END_TIME = "endTime";

    @PrimaryKey
    @SerializedName(FILED_ID)
    private int id;

    @SerializedName(FILED_ID_SERVER)
    private int idServer = -1;

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

    public JabbleTestSurveyResult() {
    }

    public JabbleTestSurveyResult(int id, int surveyId, String testRefTable, String testRefId, String resultFiles,
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
