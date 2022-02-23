package com.reading.start.data.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Represent response data with languages
 */
public class DataLanguages {
    @SerializedName("languages")
    private ArrayList<DataLanguageItem> mLanguages;

    public ArrayList<DataLanguageItem> getLanguages() {
        return mLanguages;
    }
}
