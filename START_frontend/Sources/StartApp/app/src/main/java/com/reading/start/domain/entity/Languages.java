package com.reading.start.domain.entity;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Presents table for save languages
 */
@RealmClass
public class Languages extends RealmObject {
    public static final String FILED_ID = "id";
    public static final String FILED_ID_SERVER = "idServer";
    public static final String FILED_NAME_ENGLISH = "nameEnglish";
    public static final String FILED_NAME_HINDI = "nameHindi";

    @PrimaryKey
    @SerializedName(FILED_ID)
    private int id = 0;

    @SerializedName(FILED_ID_SERVER)
    private String idServer;

    @SerializedName(FILED_NAME_ENGLISH)
    private String nameEnglish = "";

    @SerializedName(FILED_NAME_HINDI)
    private String nameHindi = "";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdServer() {
        return idServer;
    }

    public void setIdServer(String idServer) {
        this.idServer = idServer;
    }

    public String getNameEnglish() {
        return nameEnglish;
    }

    public void setNameEnglish(String nameEnglish) {
        this.nameEnglish = nameEnglish;
    }

    public String getNameHindi() {
        return nameHindi;
    }

    public void setNameHindi(String nameHindi) {
        this.nameHindi = nameHindi;
    }
}
