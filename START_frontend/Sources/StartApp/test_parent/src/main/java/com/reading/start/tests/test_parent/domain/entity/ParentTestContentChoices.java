package com.reading.start.tests.test_parent.domain.entity;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class ParentTestContentChoices extends RealmObject {
    public static final String FILED_ID = "id";
    public static final String FILED_ID_CONTENT = "idContent";
    public static final String FILED_CHOICE_ENGLISH = "choiceEnglish";
    public static final String FILED_CHOICE_HINDI = "choiceHindi";

    @PrimaryKey
    @SerializedName(FILED_ID)
    private int id;

    @SerializedName(FILED_ID_CONTENT)
    private int idContent;

    @SerializedName(FILED_CHOICE_ENGLISH)
    private String choiceEnglish;

    @SerializedName(FILED_CHOICE_HINDI)
    private String choiceHindi;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdContent() {
        return idContent;
    }

    public void setIdContent(int idContent) {
        this.idContent = idContent;
    }

    public String getChoiceEnglish() {
        return choiceEnglish;
    }

    public void setChoiceEnglish(String choice) {
        this.choiceEnglish = choice;
    }

    public String getChoiceHindi() {
        return choiceHindi;
    }

    public void setChoiceHindi(String choiceHindi) {
        this.choiceHindi = choiceHindi;
    }
}
