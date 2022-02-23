package com.reading.start.data.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Represent response data with list of states
 */
public class DataStates {
    @SerializedName("states")
    private ArrayList<DataStateItem> mStates;

    public ArrayList<DataStateItem> getStates() {
        return mStates;
    }
}
