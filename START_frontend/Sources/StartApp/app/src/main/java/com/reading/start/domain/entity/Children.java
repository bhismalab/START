package com.reading.start.domain.entity;

import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Presents table for save children
 */
@RealmClass
public class Children extends RealmObject implements IChild, Serializable {
    public static final String FILED_ID = "id";
    public static final String FILED_ID_SERVER = "idServer";
    public static final String FILED_ID_WORKER = "idWorker";
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
    public static final String FILED_DIAGNOSIS_CLINIC = "diagnosisClinic";
    public static final String FILED_DIAGNOSIS_DATE = "diagnosisDate";
    public static final String FILED_ADD_DATETIME = "addDateTime";
    public static final String FILED_ADD_BY = "modBy";
    public static final String FILED_MOD_DATETIME = "modDateTime";
    public static final String FILED_MOD_BY = "modBy";
    public static final String FILED_IS_DELETED = "isDeleted";
    public static final String FILED_PARENT_1 = "parentFirst";
    public static final String FILED_PARENT_2 = "parentSecond";
    public static final String FILED_PHOTO = "photo";
    public static final String FILED_HAND = "hand";

    @PrimaryKey
    @SerializedName(FILED_ID)
    private int id = 0;

    @SerializedName(FILED_ID_SERVER)
    private int idServer = -1;

    @SerializedName(FILED_ID_WORKER)
    private int idWorker = -1;

    @SerializedName(FILED_NAME)
    private String name = "";

    @SerializedName(FILED_SURNAME)
    private String surname = "";

    @SerializedName(FILED_PATRONYMIC)
    private String patronymic = "";

    @SerializedName(FILED_STATE)
    private String state = "-1";

    @SerializedName(FILED_ADDRESS)
    private String address = "";

    @SerializedName(FILED_GENDER)
    private String gender = "";

    @SerializedName(FILED_BIRTH_DATE)
    private long birthDate = 0;

    @SerializedName(FILED_LATITUDE)
    private String latitude = "0.0";

    @SerializedName(FILED_LONGITUDE)
    private String longitude = "0.0";

    @SerializedName(FILED_DIAGNOSIS)
    private String diagnosis = "";

    @SerializedName(FILED_DIAGNOSIS_CLINIC)
    private String diagnosisClinic = "";

    @SerializedName(FILED_DIAGNOSIS_DATE)
    private long diagnosisDate = 0;

    @SerializedName(FILED_ADD_DATETIME)
    private long addDateTime = 0;

    @SerializedName(FILED_ADD_BY)
    private String addBy = "";

    @SerializedName(FILED_MOD_DATETIME)
    private long modDateTime = 0;

    @SerializedName(FILED_MOD_BY)
    private String modBy = "";

    @SerializedName(FILED_IS_DELETED)
    private boolean isDeleted = false;

    @SerializedName(FILED_PARENT_1)
    private Parent parentFirst;

    @SerializedName(FILED_PARENT_2)
    private Parent parentSecond;

    @SerializedName(FILED_PHOTO)
    private String photo = "";

    @SerializedName(FILED_HAND)
    private String hand = "";

    @Ignore
    private File photoPath = null;

    @Ignore
    private ChildrenInfo childrenInfo;

    public Children() {
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

    public int getIdWorker() {
        return idWorker;
    }

    public void setIdWorker(int idWorker) {
        this.idWorker = idWorker;
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

    public long getDiagnosisDate() {
        return diagnosisDate;
    }

    public void setDiagnosisDate(long diagnosisDate) {
        this.diagnosisDate = diagnosisDate;
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

    public Parent getParentFirst() {
        return parentFirst;
    }

    public void setParentFirst(Parent parentFirst) {
        this.parentFirst = parentFirst;
    }

    public Parent getParentSecond() {
        return parentSecond;
    }

    public void setParentSecond(Parent parentSecond) {
        this.parentSecond = parentSecond;
    }

    public ChildrenInfo getChildrenInfo() {
        return childrenInfo;
    }

    public void setChildrenInfo(ChildrenInfo childrenInfo) {
        this.childrenInfo = childrenInfo;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public File getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(File photoPath) {
        this.photoPath = photoPath;
    }

    public String getHand() {
        return hand;
    }

    public void setHand(String hand) {
        this.hand = hand;
    }
}
