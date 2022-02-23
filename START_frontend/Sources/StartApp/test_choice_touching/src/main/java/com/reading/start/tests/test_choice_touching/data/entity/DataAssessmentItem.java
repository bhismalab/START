package com.reading.start.tests.test_choice_touching.data.entity;

import com.google.gson.annotations.SerializedName;

public class DataAssessmentItem {
    public static final String FILED_ID = "id";
    public static final String FILED_NAME = "name";
    public static final String FILED_ADD_DATETIME = "add_datetime";
    public static final String FILED_ADD_BY = "add_by";
    public static final String FILED_MOD_DATETIME = "mod_datetime";
    public static final String FILED_MOD_BY = "mod_by";
    public static final String FILED_VIDEO_1_1 = "video_1_social";
    public static final String FILED_VIDEO_1_2 = "video_1_nonsocial";
    public static final String FILED_VIDEO_2_1 = "video_2_social";
    public static final String FILED_VIDEO_2_2 = "video_2_nonsocial";
    public static final String FILED_VIDEO_3_1 = "video_3_social";
    public static final String FILED_VIDEO_3_2 = "video_3_nonsocial";
    public static final String FILED_VIDEO_4_1 = "video_4_social";
    public static final String FILED_VIDEO_4_2 = "video_4_nonsocial";
    public static final String FILED_VIDEO_DEMO_1 = "demo_social";
    public static final String FILED_VIDEO_DEMO_2 = "demo_nonsocial";

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

    @SerializedName(FILED_VIDEO_1_1)
    private String mVideo_1_1;

    @SerializedName(FILED_VIDEO_1_2)
    private String mVideo_1_2;

    @SerializedName(FILED_VIDEO_2_1)
    private String mVideo_2_1;

    @SerializedName(FILED_VIDEO_2_2)
    private String mVideo_2_2;

    @SerializedName(FILED_VIDEO_3_1)
    private String mVideo_3_1;

    @SerializedName(FILED_VIDEO_3_2)
    private String mVideo_3_2;

    @SerializedName(FILED_VIDEO_4_1)
    private String mVideo_4_1;

    @SerializedName(FILED_VIDEO_4_2)
    private String mVideo_4_2;

    @SerializedName(FILED_VIDEO_DEMO_1)
    private String mVideoDemo1;

    @SerializedName(FILED_VIDEO_DEMO_2)
    private String mVideoDemo2;

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

    public String getVideo_1_social() {
        return mVideo_1_1;
    }

    public String getVideo_1_nonsocial() {
        return mVideo_1_2;
    }

    public String getVideo_2_social() {
        return mVideo_2_1;
    }

    public String getVideo_2_nonsocial() {
        return mVideo_2_2;
    }

    public String getVideo_3_social() {
        return mVideo_3_1;
    }

    public String getVideo_3_nonsocial() {
        return mVideo_3_2;
    }

    public String getVideo_4_social() {
        return mVideo_4_1;
    }

    public String getVideo_4_nonsocial() {
        return mVideo_4_2;
    }

    public String getVideoDemo_social() {
        return mVideoDemo1;
    }

    public String getVideoDemo_nonsocial() {
        return mVideoDemo2;
    }
}
