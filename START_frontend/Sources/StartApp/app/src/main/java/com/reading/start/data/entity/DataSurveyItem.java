package com.reading.start.data.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Represent response data with survey info
 */
public class DataSurveyItem {
    public static final String FILED_ID = "id";
    public static final String FILED_CHILD_ID = "child_id";
    public static final String FILED_CREATED_DATETIME = "created_datetime";
    public static final String FILED_COMPLETED_DATETIME = "completed_datetime";

    @SerializedName(FILED_ID)
    private int id;

    @SerializedName(FILED_CHILD_ID)
    private String childId;

    @SerializedName(FILED_CREATED_DATETIME)
    private String createdDateTime;

    @SerializedName(FILED_COMPLETED_DATETIME)
    private String completedDateTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    public String getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(String createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public String getCompletedDateTime() {
        return completedDateTime;
    }

    public void setCompletedDateTime(String completedDateTime) {
        this.completedDateTime = completedDateTime;
    }
}
