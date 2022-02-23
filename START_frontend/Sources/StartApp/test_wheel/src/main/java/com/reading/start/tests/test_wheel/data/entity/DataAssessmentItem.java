package com.reading.start.tests.test_wheel.data.entity;

import com.google.gson.annotations.SerializedName;

public class DataAssessmentItem {
    public static final String FILED_ID = "id";
    public static final String FILED_NAME = "name";
    public static final String FILED_ADD_DATETIME = "add_datetime";
    public static final String FILED_ADD_BY = "add_by";
    public static final String FILED_MOD_DATETIME = "mod_datetime";
    public static final String FILED_MOD_BY = "mod_by";
    public static final String FILED_VIDEO = "video";
    public static final String FILED_DELETED = "is_deleted";

    @SerializedName(FILED_ID)
    private int id = 0;

    @SerializedName(FILED_NAME)
    private String name = "";

    @SerializedName(FILED_ADD_DATETIME)
    private String addDateTime = null;

    @SerializedName(FILED_ADD_BY)
    private String addBy = "";

    @SerializedName(FILED_MOD_DATETIME)
    private String modDateTime = "";

    @SerializedName(FILED_MOD_BY)
    private String modBy = "";

    @SerializedName(FILED_VIDEO)
    private String mVideo;

    @SerializedName(FILED_DELETED)
    private String mIsDeleted;

    public DataAssessmentItem() {
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddDateTime() {
        return addDateTime;
    }

    public String getAddBy() {
        return addBy;
    }

    public String getModDateTime() {
        return modDateTime;
    }

    public String getModBy() {
        return modBy;
    }

    public String getVideo() {
        return mVideo;
    }

    public String getIsDeleted() {
        return mIsDeleted;
    }
}
