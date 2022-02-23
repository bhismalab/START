package com.reading.start.tests.test_wheel.domain.entity;

import com.google.gson.annotations.SerializedName;

public class TestDataStimulus {
    public static final String FILED_STIMULUS_ACTION = "stimulus_action";

    public static final String FILED_TIME = "time";

    @SerializedName(FILED_STIMULUS_ACTION)
    private String mAction;

    @SerializedName(FILED_TIME)
    private long mTime;

    public TestDataStimulus(String action, long time) {
        mAction = action;
        mTime = time;
    }

    public String getAction() {
        return mAction;
    }

    public long getTime() {
        return mTime;
    }
}
