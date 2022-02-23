package com.reading.start.domain.entity;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Presents table for save parents
 */
@RealmClass
public class Parent extends RealmObject {
    public static final String FILED_ID = "id";
    public static final String FILED_ID_SERVER = "idServer";
    public static final String FILED_CHILD_ID = "childId";
    public static final String FILED_CHILD_RELATIONSHIP = "childRelationship";
    public static final String FILED_NAME = "name";
    public static final String FILED_SURNAME = "surname";
    public static final String FILED_PATRONYMIC = "patronymic";
    public static final String FILED_STATE = "state";
    public static final String FILED_ADDRESS = "address";
    public static final String FILED_GENDER = "gender";
    public static final String FILED_BIRTH_DATE = "birthDate";
    public static final String FILED_SPOKEN_LANGUAGE = "spokenLanguage";
    public static final String FILED_PHONE = "phone";
    public static final String FILED_EMAIL = "email";
    public static final String FILED_SIGNATURE_SCAN = "signatureScan";
    public static final String FILED_ADD_DATETIME = "addDateTime";
    public static final String FILED_ADD_BY = "addBy";
    public static final String FILED_MOD_DATETIME = "modDateTime";
    public static final String FILED_MOD_BY = "modBy";
    public static final String FILED_IS_DELETED = "isDeleted";
    public static final String FILED_PREFERABLE_CONTACT = "preferableContact";

    @PrimaryKey
    @SerializedName(FILED_ID)
    private int id;

    @SerializedName(FILED_ID_SERVER)
    private int idServer;

    @SerializedName(FILED_CHILD_ID)
    private int childId;

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
    private long birthDate;

    @SerializedName(FILED_SPOKEN_LANGUAGE)
    private String spokenLanguage;

    @SerializedName(FILED_PHONE)
    private String phone;

    @SerializedName(FILED_EMAIL)
    private String email;

    @SerializedName(FILED_SIGNATURE_SCAN)
    private String signatureScan;

    @SerializedName(FILED_ADD_DATETIME)
    private long addDateTime;

    @SerializedName(FILED_ADD_BY)
    private String addBy;

    @SerializedName(FILED_MOD_DATETIME)
    private long modDateTime;

    @SerializedName(FILED_MOD_BY)
    private String modBy;

    @SerializedName(FILED_IS_DELETED)
    private boolean isDeleted;

    @SerializedName(FILED_PREFERABLE_CONTACT)
    private String preferableContact;

    public Parent() {
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

    public int getChildId() {
        return childId;
    }

    public void setChildId(int childId) {
        this.childId = childId;
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

    public long getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(long birthDate) {
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

    public long getModDateTime() {
        return modDateTime;
    }

    public void setModDateTime(long modDateTime) {
        this.modDateTime = modDateTime;
    }

    public String getModBy() {
        return modBy;
    }

    public void setModBy(String modBy) {
        this.modBy = modBy;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getPreferableContact() {
        return preferableContact;
    }

    public void setPreferableContact(String preferableContact) {
        this.preferableContact = preferableContact;
    }
}
