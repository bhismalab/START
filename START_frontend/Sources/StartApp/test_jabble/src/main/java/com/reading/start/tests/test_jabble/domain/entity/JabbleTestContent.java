package com.reading.start.tests.test_jabble.domain.entity;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class JabbleTestContent extends RealmObject {
    public static final String FILED_ID = "id";
    public static final String FILED_NAME = "name";
    public static final String FILED_IMG_1 = "img_1";
    public static final String FILED_IMG_2 = "img_2";
    public static final String FILED_IMG_3 = "img_3";
    public static final String FILED_ADD_DATETIME = "add_datetime";
    public static final String FILED_ADD_BY = "add_by";
    public static final String FILED_MOD_DATETIME = "mod_datetime";
    public static final String FILED_MOD_BY = "mod_by";
    public static final String FILED_IS_DELETED = "is_deleted";

    @PrimaryKey
    @SerializedName(FILED_ID)
    private String id;

    @SerializedName(FILED_NAME)
    private String name;

    @SerializedName(FILED_IMG_1)
    private String img1;

    @SerializedName(FILED_IMG_2)
    private String img2;

    @SerializedName(FILED_IMG_3)
    private String img3;

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

    public JabbleTestContent() {
    }

    public JabbleTestContent(String id, String name, String img1, String img2, String img3,
                             String addDateTime, String addBy, String modDateTime, String modBy,
                             String isDeleted) {
        this.id = id;
        this.name = name;
        this.img1 = img1;
        this.img2 = img2;
        this.img3 = img3;
        this.addDateTime = addDateTime;
        this.addBy = addBy;
        this.modDateTime = modDateTime;
        this.modBy = modBy;
        this.isDeleted = isDeleted;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg1() {
        return img1;
    }

    public void setImg1(String img1) {
        this.img1 = img1;
    }

    public String getImg2() {
        return img2;
    }

    public void setImg2(String img2) {
        this.img2 = img2;
    }

    public String getImg3() {
        return img3;
    }

    public void setImg3(String img3) {
        this.img3 = img3;
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
