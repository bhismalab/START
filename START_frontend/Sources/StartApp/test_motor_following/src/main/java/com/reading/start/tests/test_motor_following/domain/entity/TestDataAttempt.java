package com.reading.start.tests.test_motor_following.domain.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TestDataAttempt {
    public static final String FILED_ITEMS = "items";

    @SerializedName(FILED_ITEMS)
    private ArrayList<TestDataItem> mItems = new ArrayList<>();

    public ArrayList<TestDataItem> getItems() {
        return mItems;
    }
}
