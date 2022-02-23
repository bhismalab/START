package com.reading.start.data.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Represent response data with social worker info
 */
public class DataSocialWorker {
    public static final String FILED_ID = "id";
    public static final String FILED_EMAIL = "email";
    public static final String FILED_PASSWORD = "password";
    public static final String FILED_FULL_NAME = "full_name";
    public static final String FILED_COLOR = "color";
    public static final String FILED_GENDER = "gender";
    public static final String FILED_BIRTH_DATE = "birth_date";
    public static final String FILED_REG_DATE = "reg_date";
    public static final String FILED_REG_BY = "reg_by";
    public static final String FILED_IS_BLOCKED = "is_blocked";
    public static final String FILED_IS_DELETED = "is_deleted";

    @SerializedName(FILED_ID)
    private int id;

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

    public DataSocialWorker() {
    }

    public DataSocialWorker(int id, String email, String password, String fullName, String color,
                            String gender, String birthDate, String regDate, String regBy,
                            String isBlocked, String isDeleted) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.color = color;
        this.gender = gender;
        this.birthDate = birthDate;
        this.regDate = regDate;
        this.regBy = regBy;
        this.isBlocked = isBlocked;
        this.isDeleted = isDeleted;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}
