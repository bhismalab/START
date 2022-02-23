package com.reading.start.tests.test_choice_touching.domain.entity;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class ChoiceTouchingTestContent extends RealmObject {
    public static final String FILED_ID = "id";
    public static final String FILED_NAME = "name";
    public static final String FILED_VIDEO_1_SOCIAL = "video_1_social";
    public static final String FILED_VIDEO_1_NO_SOCIAL = "video_1_no_social";
    public static final String FILED_VIDEO_2_SOCIAL = "video_2_social";
    public static final String FILED_VIDEO_2_NO_SOCIAL = "video_3_no_social";
    public static final String FILED_VIDEO_3_SOCIAL = "video_3_social";
    public static final String FILED_VIDEO_3_NO_SOCIAL = "video_3_no_social";
    public static final String FILED_VIDEO_4_SOCIAL = "video_4_social";
    public static final String FILED_VIDEO_4_NO_SOCIAL = "video_4_no_social";
    public static final String FILED_VIDEO_DEMO_SOCIAL = "demo_social";
    public static final String FILED_VIDEO_DEMO_NO_SOCIAL = "demo_no_social";
    public static final String FILED_ADD_DATETIME = "add_datetime";
    public static final String FILED_ADD_BY = "add_by";
    public static final String FILED_MOD_DATETIME = "mod_datetime";
    public static final String FILED_MOD_BY = "mod_by";
    public static final String FILED_IS_DELETED = "is_deleted";

    @PrimaryKey
    @SerializedName(FILED_ID)
    private int id;

    @SerializedName(FILED_NAME)
    private String name;

    @SerializedName(FILED_VIDEO_1_SOCIAL)
    private String video_1_social;

    @SerializedName(FILED_VIDEO_1_NO_SOCIAL)
    private String video_1_no_social;

    @SerializedName(FILED_VIDEO_2_SOCIAL)
    private String video_2_social;

    @SerializedName(FILED_VIDEO_2_NO_SOCIAL)
    private String video_2_no_social;

    @SerializedName(FILED_VIDEO_3_SOCIAL)
    private String video_3_social;

    @SerializedName(FILED_VIDEO_3_NO_SOCIAL)
    private String video_3_no_social;

    @SerializedName(FILED_VIDEO_4_SOCIAL)
    private String video_4_social;

    @SerializedName(FILED_VIDEO_4_NO_SOCIAL)
    private String video_4_no_social;

    @SerializedName(FILED_VIDEO_DEMO_SOCIAL)
    private String videoDemo_social;

    @SerializedName(FILED_VIDEO_DEMO_NO_SOCIAL)
    private String videoDemo_no_social;

    @SerializedName(FILED_ADD_DATETIME)
    private String addDateTime;

    @SerializedName(FILED_ADD_BY)
    private String addBy;

    @SerializedName(FILED_MOD_DATETIME)
    private String modDateTime;

    @SerializedName(FILED_MOD_BY)
    private String modBy;

    @SerializedName(FILED_IS_DELETED)
    private String isDeleted;

    public ChoiceTouchingTestContent() {
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

    public String getVideo_1_social() {
        return video_1_social;
    }

    public void setVideo_1_social(String video_1_social) {
        this.video_1_social = video_1_social;
    }

    public String getVideo_1_no_social() {
        return video_1_no_social;
    }

    public void setVideo_1_no_social(String video_1_no_social) {
        this.video_1_no_social = video_1_no_social;
    }

    public String getVideo_2_social() {
        return video_2_social;
    }

    public void setVideo_2_social(String video_2_social) {
        this.video_2_social = video_2_social;
    }

    public String getVideo_2_no_social() {
        return video_2_no_social;
    }

    public void setVideo_2_no_social(String video_2_no_social) {
        this.video_2_no_social = video_2_no_social;
    }

    public String getVideo_3_social() {
        return video_3_social;
    }

    public void setVideo_3_social(String video_3_social) {
        this.video_3_social = video_3_social;
    }

    public String getVideo_3_no_social() {
        return video_3_no_social;
    }

    public void setVideo_3_no_social(String video_3_no_social) {
        this.video_3_no_social = video_3_no_social;
    }

    public String getVideo_4_social() {
        return video_4_social;
    }

    public void setVideo_4_social(String video_4_social) {
        this.video_4_social = video_4_social;
    }

    public String getVideo_4_no_social() {
        return video_4_no_social;
    }

    public void setVideo_4_no_social(String video_4_no_social) {
        this.video_4_no_social = video_4_no_social;
    }

    public String getVideoDemo_social() {
        return videoDemo_social;
    }

    public void setVideoDemo_social(String videoDemo_social) {
        this.videoDemo_social = videoDemo_social;
    }

    public String getVideoDemo_no_social() {
        return videoDemo_no_social;
    }

    public void setVideoDemo_no_social(String videoDemo_no_social) {
        this.videoDemo_no_social = videoDemo_no_social;
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

    public String getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(String isDeleted) {
        this.isDeleted = isDeleted;
    }
}
