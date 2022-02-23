package com.reading.start.tests.test_parent.domain.entity;

import com.google.gson.annotations.SerializedName;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class ParentTestContent extends RealmObject {
    public static final String FILED_ID = "id";
    public static final String FILED_TYPE = "type";
    public static final String FILED_QUESTION = "question";
    public static final String FILED_QUESTION_HINDI = "question_hindi";
    public static final String FILED_VIDEO_1 = "video_1";
    public static final String FILED_VIDEO_2 = "video_2";
    public static final String FILED_ADD_DATETIME = "add_datetime";
    public static final String FILED_ADD_BY = "add_by";
    public static final String FILED_MOD_DATETIME = "mod_datetime";
    public static final String FILED_MOD_BY = "mod_by";
    public static final String FILED_IS_DELETED = "is_deleted";

    @PrimaryKey
    @SerializedName(FILED_ID)
    private int id;

    @SerializedName(FILED_TYPE)
    private String type;

    @SerializedName(FILED_QUESTION)
    private String question;

    @SerializedName(FILED_QUESTION_HINDI)
    private String question_hindi;

    @SerializedName(FILED_VIDEO_1)
    private String video1;

    @SerializedName(FILED_VIDEO_2)
    private String video2;

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

    public ParentTestContent() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getQuestion_hindi() {
        return question_hindi;
    }

    public void setQuestion_hindi(String question_hindi) {
        this.question_hindi = question_hindi;
    }

    public String getVideo1() {
        return video1;
    }

    public void setVideo1(String video1) {
        this.video1 = video1;
    }

    public String getVideo2() {
        return video2;
    }

    public void setVideo2(String video2) {
        this.video2 = video2;
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
