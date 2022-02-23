package com.reading.start.data.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Represent response data with list of child info
 */
public class DataChildren {
    @SerializedName("children")
    private ArrayList<DataChildrenItem> mChildren;

    public ArrayList<DataChildrenItem> getChildren() {
        return mChildren;
    }
}
