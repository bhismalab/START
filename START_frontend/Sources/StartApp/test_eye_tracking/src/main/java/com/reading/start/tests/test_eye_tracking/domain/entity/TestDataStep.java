package com.reading.start.tests.test_eye_tracking.domain.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TestDataStep {
    public static final String FILED_NAME = "name";
    public static final String FILED_ITEMS = "itemsStimulus";
    public static final String FILED_VIDEO_NAME = "videoName";

    @SerializedName(FILED_NAME)
    private String mName = null;

    @SerializedName(FILED_ITEMS)
    private ArrayList<TestDataStimulus> mItems = new ArrayList<>();

    @SerializedName(FILED_VIDEO_NAME)
    private String mVideoName;

    public TestDataStep(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public ArrayList<TestDataStimulus> getItems() {
        return mItems;
    }

    public String getVideoName() {
        return mVideoName;
    }

    public void setVideoName(String videoName) {
        mVideoName = videoName;
    }
}
