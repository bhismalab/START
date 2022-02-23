package com.reading.start.domain.entity;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Presents table for save children
 */
@RealmClass
public class SocialWorker extends RealmObject {
    public static final String FILED_ID = "id";
    public static final String FILED_ID_SERVER = "idServer";
    public static final String FILED_EMAIL = "email";
    public static final String FILED_PASSWORD = "password";
    public static final String FILED_FULL_NAME = "fullName";
    public static final String FILED_COLOR = "color";
    public static final String FILED_GENDER = "gender";
    public static final String FILED_BIRTH_DATE = "birthDate";
    public static final String FILED_REG_DATE = "regDate";
    public static final String FILED_REG_BY = "regBy";
    public static final String FILED_IS_BLOCKED = "isBlocked";
    public static final String FILED_IS_DELETED = "isDeleted";
    public static final String FILED_TOKEN = "token";
    public static final String FILED_SYNC_TIME = "syncTime";
    public static final String FILED_SYNC_TIME_X = "syncTimeX";

    @PrimaryKey
    @SerializedName(FILED_ID)
    private int id;

    @SerializedName(FILED_ID_SERVER)
    private int idServer = -1;

    @SerializedName(FILED_EMAIL)
    private String email;

    @SerializedName(FILED_PASSWORD)
    private String password;

    @SerializedName(FILED_FULL_NAME)
    private String fullName;

    @SerializedName(FILED_COLOR)
    private String color;

    @SerializedName(FILED_GENDER)
    private String gender;

    @SerializedName(FILED_BIRTH_DATE)
    private String birthDate;

    @SerializedName(FILED_REG_DATE)
    private String regDate;

    @SerializedName(FILED_REG_BY)
    private String regBy;

    @SerializedName(FILED_IS_BLOCKED)
    private String isBlocked;

    @SerializedName(FILED_IS_DELETED)
    private String isDeleted;

    @SerializedName(FILED_TOKEN)
    private String token;

    @SerializedName(FILED_SYNC_TIME)
    private long syncTime;

    @SerializedName(FILED_SYNC_TIME_X)
    private long syncTimeX;

    public SocialWorker() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdServer() {
        return idServer;
    }

    public void setIdServer(int idServer) {
        this.idServer = idServer;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public String getRegBy() {
        return regBy;
    }

    public void setRegBy(String regBy) {
        this.regBy = regBy;
    }

    public String getIsBlocked() {
        return isBlocked;
    }

    public void setIsBlocked(String isBlocked) {
        this.isBlocked = isBlocked;
    }

    public String getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(String isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getSyncTime() {
        return syncTime;
    }

    public void setSyncTime(long syncTime) {
        this.syncTime = syncTime;
    }

    public long getSyncTimeX() {
        return syncTimeX;
    }

    public void setSyncTimeX(long syncTimeX) {
        this.syncTimeX = syncTimeX;
    }
}
