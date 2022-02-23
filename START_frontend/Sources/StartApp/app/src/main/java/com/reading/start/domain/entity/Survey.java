package com.reading.start.domain.entity;

import com.google.gson.annotations.SerializedName;

import java.util.Calendar;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Presents table for save surveys
 */
@RealmClass
public class Survey extends RealmObject implements ISurvey {
    public static final String FILED_ID = "id";
    public static final String FILED_ID_SERVER = "idServer";
    public static final String FILED_CHILD_ID = "childId";
    public static final String FILED_CREATED_DATETIME = "createdDateTime";
    public static final String FILED_CREATED_BY_SOCIAL_WORKER = "createdBySocialWorker";
    public static final String FILED_IS_COMPLETED = "isCompleted";
    public static final String FILED_COMPLETED_DATETIME = "completedDateTime";
    public static final String FILED_IS_CLOSE = "isClosed";
    public static final String FILED_IS_INSPECTED = "isInspected";

    @PrimaryKey
    @SerializedName(FILED_ID)
    private int id;

    @SerializedName(FILED_ID_SERVER)
    private int idServer = -1;

    @SerializedName(FILED_CHILD_ID)
    private int childId;

    @SerializedName(FILED_CREATED_DATETIME)
    private long createdDateTime;

    @SerializedName(FILED_CREATED_BY_SOCIAL_WORKER)
    private String createdBySocialWorker;

    @SerializedName(FILED_IS_COMPLETED)
    private boolean isCompleted;

    @SerializedName(FILED_COMPLETED_DATETIME)
    private long completedDateTime;

    @SerializedName(FILED_IS_CLOSE)
    private boolean isClosed;

    @SerializedName(FILED_IS_INSPECTED)
    private boolean isInspected;

    public Survey() {
    }

    public Survey(int id, int childId, String worker) {
        this.id = id;
        this.childId = childId;
        this.createdDateTime = Calendar.getInstance().getTimeInMillis();
        this.createdBySocialWorker = worker;
        this.isCompleted = false;
        this.completedDateTime = 0;
        this.isClosed = false;
        this.isInspected = false;
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

    public int getChildId() {
        return childId;
    }

    public void setChildId(int childId) {
        this.childId = childId;
    }

    public long getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(long createdDateTime) {
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

    public long getCompletedDateTime() {
        return completedDateTime;
    }

    public void setCompletedDateTime(long completedDateTime) {
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
}
