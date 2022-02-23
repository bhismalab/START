package com.reading.start.data.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Represent response data with survey info
 */
public class DataChildrenSurveyItem {
    public static final String FILED_ID = "id";
    public static final String FILED_CHILD_ID = "child_id";
    public static final String FILED_CREATED_DATETIME = "created_datetime";
    public static final String FILED_CREATED_BY_SOCIAL_WORKER = "created_by_social_worker";
    public static final String FILED_IS_COMPLETED = "is_completed";
    public static final String FILED_COMPLETED_DATETIME = "completed_datetime";
    public static final String FILED_IS_CLOSE = "is_closed";
    public static final String FILED_IS_INSPECTED = "is_inspected";
    public static final String FILED_IS_DELETED = "is_deleted";

    @SerializedName(FILED_ID)
    private int id;

    @SerializedName(FILED_CHILD_ID)
    private int childId;

    @SerializedName(FILED_CREATED_DATETIME)
    private String createdDateTime;

    @SerializedName(FILED_CREATED_BY_SOCIAL_WORKER)
    private String createdBySocialWorker;

    @SerializedName(FILED_IS_COMPLETED)
    private boolean isCompleted;

    @SerializedName(FILED_COMPLETED_DATETIME)
    private String completedDateTime;

    @SerializedName(FILED_IS_CLOSE)
    private boolean isClosed;

    @SerializedName(FILED_IS_INSPECTED)
    private boolean isInspected;

    @SerializedName(FILED_IS_DELETED)
    private String isDeleted = "0";

    public DataChildrenSurveyItem() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getChildId() {
        return childId;
    }

    public void setChildId(int childId) {
        this.childId = childId;
    }

    public String getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(String createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public String getCreatedBySocialWorker() {
        return createdBySocialWorker;
    }

    public void setCreatedBySocialWorker(String createdBySocialWorker) {
        this.createdBySocialWorker = createdBySocialWorker;
    }

    public boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public String getCompletedDateTime() {
        return completedDateTime;
    }

    public void setCompletedDateTime(String completedDateTime) {
        this.completedDateTime = completedDateTime;
    }

    public boolean getIsClosed() {
        return isClosed;
    }

    public void setIsClosed(boolean isClosed) {
        this.isClosed = isClosed;
    }

    public boolean getIsInspected() {
        return isInspected;
    }

    public void setIsInspected(boolean isInspected) {
        this.isInspected = isInspected;
    }

    public String isDeleted() {
        return isDeleted;
    }

    public void setDeleted(String deleted) {
        isDeleted = deleted;
    }
}
