package com.reading.start.tests.test_parent.data.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DataAssessmentItemChoices {
    public static final String FILED_CHOICES_ENGLISH = "english";
    public static final String FILED_CHOICES_HINDI = "hindi";

    @SerializedName(FILED_CHOICES_ENGLISH)
    private ArrayList<String> choicesEnglish = null;

    @SerializedName(FILED_CHOICES_HINDI)
    private ArrayList<String> choicesHindi = null;

    public ArrayList<String> getChoicesEnglish() {
        return choicesEnglish;
    }

    public ArrayList<String> getChoicesHindi() {
        return choicesHindi;
    }
}
