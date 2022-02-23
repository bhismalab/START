package com.reading.start.tests.test_parent.domain.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TestDataItemChoices implements IParentTestItem {
    public static final String FILED_CHOICES_ENGLISH = "english";
    public static final String FILED_CHOICES_HINDI = "hindi";

    @SerializedName(FILED_CHOICES_ENGLISH)
    private ArrayList<String> mChoicesEnglish = null;

    @SerializedName(FILED_CHOICES_HINDI)
    private ArrayList<String> mChoicesHindi = null;

    public TestDataItemChoices() {
        mChoicesEnglish = new ArrayList<>();
        mChoicesHindi = new ArrayList<>();
    }

    public ArrayList<String> getChoicesEnglish() {
        return mChoicesEnglish;
    }

    public ArrayList<String> getChoicesHindi() {
        return mChoicesHindi;
    }

    public void setChoicesEnglish(ArrayList<String> choicesEnglish) {
        mChoicesEnglish = choicesEnglish;
    }

    public void setChoicesHindi(ArrayList<String> choicesHindi) {
        mChoicesHindi = choicesHindi;
    }
}
