package com.reading.start.tests.test_coloring.domain.entity;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class ColoringTestContent extends RealmObject {
    public static final String FILED_ID = "id";
    public static final String FILED_NAME = "name";
    public static final String FILED_IMG_1 = "img_1";
    public static final String FILED_IMG_NAME_1 = "img_name_1";
    public static final String FILED_IMG_1_DUMP = "img_1_dump";
    public static final String FILED_IMG_2 = "img_2";
    public static final String FILED_IMG_NAME_2 = "img_name_2";
    public static final String FILED_IMG_2_DUMP = "img_2_dump";
    public static final String FILED_IMG_3 = "img_3";
    public static final String FILED_IMG_NAME_3 = "img_name_3";
    public static final String FILED_IMG_3_DUMP = "img_3_dump";
    public static final String FILED_IMG_4 = "img_4";
    public static final String FILED_IMG_NAME_4 = "img_name_4";
    public static final String FILED_IMG_4_DUMP = "img_4_dump";
    public static final String FILED_IMG_5 = "img_5";
    public static final String FILED_IMG_NAME_5 = "img_name_5";
    public static final String FILED_IMG_5_DUMP = "img_5_dump";
    public static final String FILED_IMG_6 = "img_6";
    public static final String FILED_IMG_NAME_6 = "img_name_6";
    public static final String FILED_IMG_6_DUMP = "img_6_dump";
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

    @SerializedName(FILED_IMG_1)
    private String img1;

    @SerializedName(FILED_IMG_NAME_1)
    private String imgName1;

    @SerializedName(FILED_IMG_1_DUMP)
    private String img1Dump;

    @SerializedName(FILED_IMG_2)
    private String img2;

    @SerializedName(FILED_IMG_NAME_2)
    private String imgName2;

    @SerializedName(FILED_IMG_2_DUMP)
    private String img2Dump;

    @SerializedName(FILED_IMG_3)
    private String img3;

    @SerializedName(FILED_IMG_NAME_3)
    private String imgName3;

    @SerializedName(FILED_IMG_3_DUMP)
    private String img3Dump;

    @SerializedName(FILED_IMG_4)
    private String img4;

    @SerializedName(FILED_IMG_NAME_4)
    private String imgName4;

    @SerializedName(FILED_IMG_4_DUMP)
    private String img4Dump;

    @SerializedName(FILED_IMG_5)
    private String img5;

    @SerializedName(FILED_IMG_NAME_5)
    private String imgName5;

    @SerializedName(FILED_IMG_5_DUMP)
    private String img5Dump;

    @SerializedName(FILED_IMG_6)
    private String img6;

    @SerializedName(FILED_IMG_NAME_6)
    private String imgName6;

    @SerializedName(FILED_IMG_6_DUMP)
    private String img6Dump;

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

    public ColoringTestContent() {
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

    public String getImg4() {
        return img4;
    }

    public void setImg4(String img4) {
        this.img4 = img4;
    }

    public String getImg5() {
        return img5;
    }

    public void setImg5(String img5) {
        this.img5 = img5;
    }

    public String getImg6() {
        return img6;
    }

    public void setImg6(String img6) {
        this.img6 = img6;
    }

    public String getImg1Dump() {
        return img1Dump;
    }

    public void setImg1Dump(String img1Dump) {
        this.img1Dump = img1Dump;
    }

    public String getImg2Dump() {
        return img2Dump;
    }

    public void setImg2Dump(String img2Dump) {
        this.img2Dump = img2Dump;
    }

    public String getImg3Dump() {
        return img3Dump;
    }

    public void setImg3Dump(String img3Dump) {
        this.img3Dump = img3Dump;
    }

    public String getImg4Dump() {
        return img4Dump;
    }

    public void setImg4Dump(String img4Dump) {
        this.img4Dump = img4Dump;
    }

    public String getImg5Dump() {
        return img5Dump;
    }

    public void setImg5Dump(String img5Dump) {
        this.img5Dump = img5Dump;
    }

    public String getImg6Dump() {
        return img6Dump;
    }

    public void setImg6Dump(String img6Dump) {
        this.img6Dump = img6Dump;
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

    public String getImgName1() {
        return imgName1;
    }

    public void setImgName1(String imgName1) {
        this.imgName1 = imgName1;
    }

    public String getImgName2() {
        return imgName2;
    }

    public void setImgName2(String imgName2) {
        this.imgName2 = imgName2;
    }

    public String getImgName3() {
        return imgName3;
    }

    public void setImgName3(String imgName3) {
        this.imgName3 = imgName3;
    }

    public String getImgName4() {
        return imgName4;
    }

    public void setImgName4(String imgName4) {
        this.imgName4 = imgName4;
    }

    public String getImgName5() {
        return imgName5;
    }

    public void setImgName5(String imgName5) {
        this.imgName5 = imgName5;
    }

    public String getImgName6() {
        return imgName6;
    }

    public void setImgName6(String imgName6) {
        this.imgName6 = imgName6;
    }
}
