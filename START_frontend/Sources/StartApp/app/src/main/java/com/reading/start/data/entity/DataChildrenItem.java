package com.reading.start.data.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Represent response data with child info
 */
public class DataChildrenItem {
    public static final String FILED_ID = "id";
    public static final String FILED_NAME = "name";
    public static final String FILED_SURNAME = "surname";
    public static final String FILED_PATRONYMIC = "patronymic";
    public static final String FILED_STATE = "state";
    public static final String FILED_ADDRESS = "address";
    public static final String FILED_GENDER = "gender";
    public static final String FILED_BIRTH_DATE = "birth_date";
    public static final String FILED_LATITUDE = "latitude";
    public static final String FILED_LONGITUDE = "longitude";
    public static final String FILED_DIAGNOSIS = "diagnosis";
    public static final String FILED_DIAGNOSIS_CLINIC = "diagnosis_clinic";
    public static final String FILED_DIAGNOSIS_DATE = "diagnosis_datetime";
    public static final String FILED_ADD_DATETIME = "add_datetime";
    public static final String FILED_ADD_BY = "add_by";
    public static final String FILED_MOD_DATETIME = "mod_datetime";
    public static final String FILED_MOD_BY = "mod_by";
    public static final String FILED_PHOTO = "photo";
    public static final String FILED_PARENTS = "parents";
    public static final String FILED_IS_DELETED = "is_deleted";
    public static final String FILED_HAND = "hand";

    @SerializedName(FILED_ID)
    private int id = 0;

    @SerializedName(FILED_NAME)
    private String name = "";

    @SerializedName(FILED_SURNAME)
    private String surname = "";

    @SerializedName(FILED_PATRONYMIC)
    private String patronymic = "";

    @SerializedName(FILED_STATE)
    private String state = "";

    @SerializedName(FILED_ADDRESS)
    private String address = "";

    @SerializedName(FILED_GENDER)
    private String gender = "";

    @SerializedName(FILED_BIRTH_DATE)
    private String birthDate = null;

    @SerializedName(FILED_LATITUDE)
    private String latitude = "";

    @SerializedName(FILED_LONGITUDE)
    private String longitude = "";

    @SerializedName(FILED_DIAGNOSIS)
    private String diagnosis = "";

    @SerializedName(FILED_DIAGNOSIS_CLINIC)
    private String diagnosisClinic = "";

    @SerializedName(FILED_DIAGNOSIS_DATE)
    private String diagnosisDate = "";

    @SerializedName(FILED_ADD_DATETIME)
    private String addDateTime = null;

    @SerializedName(FILED_ADD_BY)
    private String addBy = "";

    @SerializedName(FILED_MOD_DATETIME)
    private String modDateTime = "";

    @SerializedName(FILED_MOD_BY)
    private String modBy = "";

    @SerializedName(FILED_PHOTO)
    private String photo = "";

    @SerializedName(FILED_PARENTS)
    private ArrayList<DataParentItem> parents;

    @SerializedName(FILED_IS_DELETED)
    private String isDeleted = "0";

    @SerializedName(FILED_HAND)
    private String hand = "";

    public DataChildrenItem() {
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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getDiagnosisClinic() {
        return diagnosisClinic;
    }

    public void setDiagnosisClinic(String diagnosisClinic) {
        this.diagnosisClinic = diagnosisClinic;
    }

    public String getDiagnosisDate() {
        return diagnosisDate;
    }

    public void setDiagnosisDate(String diagnosisDate) {
        this.diagnosisDate = diagnosisDate;
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

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public ArrayList<DataParentItem> getParents() {
        return parents;
    }

    public void setParents(ArrayList<DataParentItem> parents) {
        this.parents = parents;
    }

    public String isDeleted() {
        return isDeleted;
    }

    public void setDeleted(String deleted) {
        isDeleted = deleted;
    }

    public String getHand() {
        return hand;
    }

    public void setHand(String hand) {
        this.hand = hand;
    }
}
