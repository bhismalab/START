package com.reading.start.data.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Represent response data with parent info
 */
public class DataParentItem {
    public static final String FILED_ID = "id";
    public static final String FILED_CHILD_RELATIONSHIP = "child_relationship";
    public static final String FILED_NAME = "name";
    public static final String FILED_SURNAME = "surname";
    public static final String FILED_PATRONYMIC = "patronymic";
    public static final String FILED_STATE = "state";
    public static final String FILED_ADDRESS = "address";
    public static final String FILED_GENDER = "gender";
    public static final String FILED_BIRTH_DATE = "birth_date";
    public static final String FILED_SPOKEN_LANGUAGE = "spoken_language";
    public static final String FILED_PHONE = "phone";
    public static final String FILED_EMAIL = "email";
    public static final String FILED_SIGNATURE_SCAN = "signature_scan";
    public static final String FILED_ADD_DATETIME = "add_datetime";
    public static final String FILED_ADD_BY = "add_by";
    public static final String FILED_MOD_DATETIME = "mod_datetime";
    public static final String FILED_MOD_BY = "mod_by";
    public static final String FILED_PREFERABLE_CONTACT = "preferable_contact";

    @SerializedName(FILED_ID)
    private int id;

    @SerializedName(FILED_CHILD_RELATIONSHIP)
    private String childRelationship;

    @SerializedName(FILED_NAME)
    private String name;

    @SerializedName(FILED_SURNAME)
    private String surname;

    @SerializedName(FILED_PATRONYMIC)
    private String patronymic;

    @SerializedName(FILED_STATE)
    private String state;

    @SerializedName(FILED_ADDRESS)
    private String address;

    @SerializedName(FILED_GENDER)
    private String gender;

    @SerializedName(FILED_BIRTH_DATE)
    private String birthDate;

    @SerializedName(FILED_SPOKEN_LANGUAGE)
    private String spokenLanguage;

    @SerializedName(FILED_PHONE)
    private String phone;

    @SerializedName(FILED_EMAIL)
    private String email;

    @SerializedName(FILED_SIGNATURE_SCAN)
    private String signatureScan;

    @SerializedName(FILED_ADD_DATETIME)
    private String addDateTime;

    @SerializedName(FILED_ADD_BY)
    private String addBy;

    @SerializedName(FILED_MOD_DATETIME)
    private String modDateTime;

    @SerializedName(FILED_MOD_BY)
    private String modBy;

    @SerializedName(FILED_PREFERABLE_CONTACT)
    private String preferableContact;

    public DataParentItem() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getChildRelationship() {
        return childRelationship;
    }

    public void setChildRelationship(String childRelationship) {
        this.childRelationship = childRelationship;
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

    public String getSpokenLanguage() {
        return spokenLanguage;
    }

    public void setSpokenLanguage(String spokenLanguage) {
        this.spokenLanguage = spokenLanguage;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSignatureScan() {
        return signatureScan;
    }

    public void setSignatureScan(String signatureScan) {
        this.signatureScan = signatureScan;
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

    public String getPreferableContact() {
        return preferableContact;
    }

    public void setPreferableContact(String preferableContact) {
        this.preferableContact = preferableContact;
    }
}
