package com.reading.start.tests.test_eye_tracking.domain.entity;

import com.google.gson.annotations.SerializedName;

public class TestDataStimulus {
    public static final String FILED_TIME = "time";
    public static final String FILED_STIMULUS_APPEAR = "stimulusAppear";
    public static final String FILED_STIMULUS_SIDE = "stimulusSide";

    @SerializedName(FILED_TIME)
    private long mTime;

    @SerializedName(FILED_STIMULUS_APPEAR)
    private String mStimulusAppear;

    @SerializedName(FILED_STIMULUS_SIDE)
    private String mStimulusSide;

    public TestDataStimulus(long time, String stimulusAppear, String side) {
        mTime = time;
        mStimulusAppear = stimulusAppear;
        mStimulusSide = side;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public String getStimulusAppear() {
        return mStimulusAppear;
    }

    public void setStimulusAppear(String stimulusAppear) {
        mStimulusAppear = stimulusAppear;
    }

    public String getStimulusSide() {
        return mStimulusSide;
    }

    public void setStimulusSide(String stimulusSide) {
        mStimulusSide = stimulusSide;
    }
}
