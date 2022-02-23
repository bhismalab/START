package com.reading.start.domain.entity;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Presents table for save surveys status
 */
@RealmClass
public class SurveyStatus extends RealmObject {
    public static final String FILED_ID = "id";
    public static final String FILED_ID_SURVEY = "idSurvey";
    public static final String FILED_IS_UPLOADED = "isUploaded";
    public static final String FILED_IS_NEED_DOWNLOAD = "isNeedDownload";
    public static final String FILED_IS_NEED_UPLOAD = "isNeedUpload";
    public static final String FILED_IS_REMOTE = "isRemote";
    public static final String FILED_UPLOAD_PROGRESS = "progressUpload";
    public static final String FILED_IS_DELETED = "isDeleted";
    public static final String FILED_IS_IN_PROGRESS = "isInProgress";
    public static final String FILED_TRIES = "tries";
    public static final String FILED_COMPLETE_PROGRESS = "progressComplete";

    public SurveyStatus() {
        tries = 0;
    }

    @PrimaryKey
    @SerializedName(FILED_ID)
    private int id;

    @SerializedName(FILED_ID_SURVEY)
    private int idSurvey = -1;

    @SerializedName(FILED_IS_UPLOADED)
    private boolean isUploaded = false;

    @SerializedName(FILED_UPLOAD_PROGRESS)
    private int uploadProgress;

    @SerializedName(FILED_IS_NEED_DOWNLOAD)
    private boolean isNeedDownload = false;

    @SerializedName(FILED_IS_NEED_UPLOAD)
    private boolean isNeedUpload = false;

    @SerializedName(FILED_IS_REMOTE)
    private boolean isRemote = false;

    @SerializedName(FILED_IS_DELETED)
    private boolean isDeleted = false;

    @SerializedName(FILED_IS_IN_PROGRESS)
    private boolean isInProgress = false;

    @SerializedName(FILED_TRIES)
    private int tries;

    @SerializedName(FILED_COMPLETE_PROGRESS)
    private int completeProgress;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdSurvey() {
        return idSurvey;
    }

    public void setIdSurvey(int idSurvey) {
        this.idSurvey = idSurvey;
    }

    public boolean isUploaded() {
        return isUploaded;
    }

    public void setUploaded(boolean uploaded) {
        isUploaded = uploaded;
    }

    public int getUploadProgress() {
        return uploadProgress;
    }

    public void setUploadProgress(int uploadProgress) {
        this.uploadProgress = uploadProgress;
    }

    public boolean isNeedDownload() {
        return isNeedDownload;
    }

    public void setNeedDownload(boolean needDownload) {
        isNeedDownload = needDownload;
    }

    public boolean isNeedUpload() {
        return isNeedUpload;
    }

    public void setNeedUpload(boolean needUpload) {
        isNeedUpload = needUpload;
    }

    public boolean isRemote() {
        return isRemote;
    }

    public void setRemote(boolean remote) {
        isRemote = remote;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public boolean isInProgress() {
        return isInProgress;
    }

    public void setInProgress(boolean inProgress) {
        isInProgress = inProgress;
    }

    public int getTries() {
        return tries;
    }

    public void setTries(int tries) {
        this.tries = tries;
    }

    public int getCompleteProgress() {
        return completeProgress;
    }

    public void setCompleteProgress(int completeProgress) {
        this.completeProgress = completeProgress;
    }
}
