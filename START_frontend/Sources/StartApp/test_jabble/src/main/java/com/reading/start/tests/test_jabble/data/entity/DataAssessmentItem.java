package com.reading.start.tests.test_jabble.data.entity;

import com.google.gson.annotations.SerializedName;

public class DataAssessmentItem {
    public static final String FILED_ID = "id";
    public static final String FILED_NAME = "name";
    public static final String FILED_ADD_DATETIME = "add_datetime";
    public static final String FILED_ADD_BY = "add_by";
    public static final String FILED_MOD_DATETIME = "mod_datetime";
    public static final String FILED_MOD_BY = "mod_by";

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

    public DataAssessmentItem() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddDateTime() {
        return addDateTime;
    }

    public void setAddDateTime(String addDateTime) {
        this.addDateTime = addDateTime;
    }

    public String getAddBy() {
        return addBy;
    }

    public void setAddBy(String addBy) {
        this.addBy = addBy;
    }

    public String getModDateTime() {
        return modDateTime;
    }

    public void setModDateTime(String modDateTime) {
        this.modDateTime = modDateTime;
    }

    public String getModBy() {
        return modBy;
    }

    public void setModBy(String modBy) {
        this.modBy = modBy;
    }
}
