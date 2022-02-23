package com.reading.start.tests.test_coloring.data.entity;

import com.google.gson.annotations.SerializedName;

public class DataAssessmentItem {
    public static final String FILED_ID = "id";
    public static final String FILED_NAME = "name";
    public static final String FILED_ADD_DATETIME = "add_datetime";
    public static final String FILED_ADD_BY = "add_by";
    public static final String FILED_MOD_DATETIME = "mod_datetime";
    public static final String FILED_MOD_BY = "mod_by";
    public static final String FILED_IMAGE_1 = "img_1";
    public static final String FILED_IMAGE_2 = "img_2";
    public static final String FILED_IMAGE_3 = "img_3";
    public static final String FILED_IMAGE_4 = "img_4";
    public static final String FILED_IMAGE_5 = "img_5";
    public static final String FILED_IMAGE_6 = "img_6";

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

    @SerializedName(FILED_IMAGE_1)
    private String mImage1;

    @SerializedName(FILED_IMAGE_2)
    private String mImage2;

    @SerializedName(FILED_IMAGE_3)
    private String mImage3;

    @SerializedName(FILED_IMAGE_4)
    private String mImage4;

    @SerializedName(FILED_IMAGE_5)
    private String mImage5;

    @SerializedName(FILED_IMAGE_6)
    private String mImage6;

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

    public String getImage1() {
        return mImage1;
    }

    public String getImage2() {
        return mImage2;
    }

    public String getImage3() {
        return mImage3;
    }

    public String getImage4() {
        return mImage4;
    }

    public String getImage5() {
        return mImage5;
    }

    public String getImage6() {
        return mImage6;
    }
}
