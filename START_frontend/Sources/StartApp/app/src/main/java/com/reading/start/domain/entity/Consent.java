package com.reading.start.domain.entity;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Presents table for save consent form
 */
@RealmClass
public class Consent extends RealmObject {
    public static final String FILED_ID = "id";
    public static final String FILED_ENGLISH = "english";
    public static final String FILED_HINDI = "hindi";

    @PrimaryKey
    @SerializedName(FILED_ID)
    private int id = 0;

    @SerializedName(FILED_ENGLISH)
    private String english = "";

    @SerializedName(FILED_HINDI)
    private String hindi = "";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    public String getHindi() {
        return hindi;
    }

    public void setHindi(String hindi) {
        this.hindi = hindi;
    }
}
