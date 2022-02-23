package com.reading.start.tests.test_parent.data.entity;

import com.google.gson.annotations.SerializedName;

public class DataAssessmentItem {
    public static final String FILED_ID = "id";
    public static final String FILED_TYPE = "type";
    public static final String FILED_QUESTION = "question_text";
    public static final String FILED_QUESTION_HINDI = "question_text_hindi";
    public static final String FILED_VIDEO_1 = "video_left";
    public static final String FILED_VIDEO_2 = "video_right";
    public static final String FILED_ADD_DATETIME = "add_datetime";
    public static final String FILED_ADD_BY = "add_by";
    public static final String FILED_MOD_DATETIME = "mod_datetime";
    public static final String FILED_MOD_BY = "mod_by";
    public static final String FILED_CHOICES = "choices";

    @SerializedName(FILED_ID)
    private int id = 0;

    @SerializedName(FILED_TYPE)
    private String type;

    @SerializedName(FILED_QUESTION_HINDI)
    private String questionHindi;

    @SerializedName(FILED_QUESTION)
    private String question;

    @SerializedName(FILED_VIDEO_1)
    private String video1;

    @SerializedName(FILED_VIDEO_2)
    private String video2;

    @SerializedName(FILED_ADD_DATETIME)
    private String addDateTime = null;

    @SerializedName(FILED_ADD_BY)
    private String addBy = "";

    @SerializedName(FILED_MOD_DATETIME)
    private String modDateTime = "";

    @SerializedName(FILED_MOD_BY)
    private String modBy = "";

    @SerializedName(FILED_CHOICES)
    private DataAssessmentItemChoices choices = null;

    public DataAssessmentItem() {
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getQuestion() {
        return question;
    }

    public String getQuestionHindi() {
        return questionHindi;
    }

    public String getVideo1() {
        return video1;
    }

    public String getVideo2() {
        return video2;
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

    public DataAssessmentItemChoices getChoices() {
        return choices;
    }
}
