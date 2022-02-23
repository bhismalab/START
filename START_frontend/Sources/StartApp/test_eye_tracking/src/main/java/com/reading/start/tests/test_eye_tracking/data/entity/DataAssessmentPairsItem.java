package com.reading.start.tests.test_eye_tracking.data.entity;

import com.google.gson.annotations.SerializedName;

public class DataAssessmentPairsItem {
    public static final String FILED_ID = "id";
    public static final String FILED_NAME = "name";
    public static final String FILED_ADD_DATETIME = "add_datetime";
    public static final String FILED_ADD_BY = "add_by";
    public static final String FILED_MOD_DATETIME = "mod_datetime";
    public static final String FILED_MOD_BY = "mod_by";
    public static final String FILED_PAIR_1_PART_1 = "pair_1_img_1";
    public static final String FILED_PAIR_1_PART_2 = "pair_1_img_2";
    public static final String FILED_PAIR_2_PART_1 = "pair_2_img_1";
    public static final String FILED_PAIR_2_PART_2 = "pair_2_img_2";
    public static final String FILED_PAIR_3_PART_1 = "pair_3_img_1";
    public static final String FILED_PAIR_3_PART_2 = "pair_3_img_2";
    public static final String FILED_PAIR_4_PART_1 = "pair_4_img_1";
    public static final String FILED_PAIR_4_PART_2 = "pair_4_img_2";
    public static final String FILED_PAIR_5_PART_1 = "pair_5_img_1";
    public static final String FILED_PAIR_5_PART_2 = "pair_5_img_2";
    public static final String FILED_PAIR_6_PART_1 = "pair_6_img_1";
    public static final String FILED_PAIR_6_PART_2 = "pair_6_img_2";
    public static final String FILED_PAIR_7_PART_1 = "pair_7_img_1";
    public static final String FILED_PAIR_7_PART_2 = "pair_7_img_2";
    public static final String FILED_PAIR_8_PART_1 = "pair_8_img_1";
    public static final String FILED_PAIR_8_PART_2 = "pair_8_img_2";

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

    @SerializedName(FILED_PAIR_1_PART_1)
    private String mPair1Part1;

    @SerializedName(FILED_PAIR_1_PART_2)
    private String mPair1Part2;

    @SerializedName(FILED_PAIR_2_PART_1)
    private String mPair2Part1;

    @SerializedName(FILED_PAIR_2_PART_2)
    private String mPair2Part2;

    @SerializedName(FILED_PAIR_3_PART_1)
    private String mPair3Part1;

    @SerializedName(FILED_PAIR_3_PART_2)
    private String mPair3Part2;

    @SerializedName(FILED_PAIR_4_PART_1)
    private String mPair4Part1;

    @SerializedName(FILED_PAIR_4_PART_2)
    private String mPair4Part2;

    @SerializedName(FILED_PAIR_5_PART_1)
    private String mPair5Part1;

    @SerializedName(FILED_PAIR_5_PART_2)
    private String mPair5Part2;

    @SerializedName(FILED_PAIR_6_PART_1)
    private String mPair6Part1;

    @SerializedName(FILED_PAIR_6_PART_2)
    private String mPair6Part2;

    @SerializedName(FILED_PAIR_7_PART_1)
    private String mPair7Part1;

    @SerializedName(FILED_PAIR_7_PART_2)
    private String mPair7Part2;

    @SerializedName(FILED_PAIR_8_PART_1)
    private String mPair8Part1;

    @SerializedName(FILED_PAIR_8_PART_2)
    private String mPair8Part2;

    public DataAssessmentPairsItem() {
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

    public String getPair1Part1() {
        return mPair1Part1;
    }

    public String getPair1Part2() {
        return mPair1Part2;
    }

    public String getPair2Part1() {
        return mPair2Part1;
    }

    public String getPair2Part2() {
        return mPair2Part2;
    }

    public String getPair3Part1() {
        return mPair3Part1;
    }

    public String getPair3Part2() {
        return mPair3Part2;
    }

    public String getPair4Part1() {
        return mPair4Part1;
    }

    public String getPair4Part2() {
        return mPair4Part2;
    }

    public String getPair5Part1() {
        return mPair5Part1;
    }

    public String getPair5Part2() {
        return mPair5Part2;
    }

    public String getPair6Part1() {
        return mPair6Part1;
    }

    public String getPair6Part2() {
        return mPair6Part2;
    }

    public String getPair7Part1() {
        return mPair7Part1;
    }

    public String getPair7Part2() {
        return mPair7Part2;
    }

    public String getPair8Part1() {
        return mPair8Part1;
    }

    public String getPair8Part2() {
        return mPair8Part2;
    }
}
