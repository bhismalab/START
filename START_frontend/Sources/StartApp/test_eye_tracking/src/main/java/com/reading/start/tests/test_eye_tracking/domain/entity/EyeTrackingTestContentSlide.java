package com.reading.start.tests.test_eye_tracking.domain.entity;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class EyeTrackingTestContentSlide extends RealmObject {
    public static final String FILED_ID = "id";
    public static final String FILED_NAME = "name";
    public static final String FILED_MOVING_FRAGMENT = "moving_fragment";
    public static final String FILED_SLIDE = "slide";
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

    @SerializedName(FILED_MOVING_FRAGMENT)
    private String movingFragment;

    @SerializedName(FILED_SLIDE)
    private String slide;

    @SerializedName(FILED_ADD_DATETIME)
    private long addDateTime;

    @SerializedName(FILED_ADD_BY)
    private String addBy;

    @SerializedName(FILED_MOD_DATETIME)
    private long modDatetime;

    @SerializedName(FILED_MOD_BY)
    private String modBy;

    @SerializedName(FILED_IS_DELETED)
    private String isDeleted;

    public EyeTrackingTestContentSlide() {
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

    public String getMovingFragment() {
        return movingFragment;
    }

    public void setMovingFragment(String movingFragment) {
        this.movingFragment = movingFragment;
    }

    public String getSlide() {
        return slide;
    }

    public void setSlide(String slide) {
        this.slide = slide;
    }

    public long getAddDateTime() {
        return addDateTime;
    }

    public void setAddDateTime(long addDateTime) {
        this.addDateTime = addDateTime;
    }

    public String getAddBy() {
        return addBy;
    }

    public void setAddBy(String addBy) {
        this.addBy = addBy;
    }

    public long getModDatetime() {
        return modDatetime;
    }

    public void setModDatetime(long modDatetime) {
        this.modDatetime = modDatetime;
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
