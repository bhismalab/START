package com.reading.start.tests.test_eye_tracking.domain.entity;

import com.google.gson.annotations.SerializedName;

public class TestDataMerged {
    public static final String FILED_TIME = "time";
    public static final String FILED_GAZE_X = "gazeX";
    public static final String FILED_GAZE_Y = "gazeY";
    public static final String FILED_GAZE_VALID = "gazeDetect";
    public static final String FILED_GAZE_OUT = "gazeOut";

    public static final String FILED_STIMULUS_NAME = "stimulusName";
    public static final String FILED_STIMULUS_VIDEO_NAME = "stimulusVideoName";
    public static final String FILED_STIMULUS_APPEAR = "stimulusAppear";
    public static final String FILED_STIMULUS_SIDE = "stimulusSide";

    @SerializedName(FILED_TIME)
    private long mTime;

    @SerializedName(FILED_GAZE_X)
    private float mGazeX;

    @SerializedName(FILED_GAZE_Y)
    private float mGazeY;

    @SerializedName(FILED_GAZE_VALID)
    private int mGazeValid;

    @SerializedName(FILED_GAZE_OUT)
    private int mGazeOut;

    @SerializedName(FILED_STIMULUS_NAME)
    private String mStimulusName = null;

    @SerializedName(FILED_STIMULUS_VIDEO_NAME)
    private String mStimulusVideoName;

    @SerializedName(FILED_STIMULUS_APPEAR)
    private String mStimulusAppear;

    @SerializedName(FILED_STIMULUS_SIDE)
    private String mStimulusSide;

    public TestDataMerged(long time, float gazeX, float gazeY, int gazeValid, int gazeOut,
                          String stimulusName, String stimulusVideoName, String stimulusAppear,
                          String stimulusSide) {
        mTime = time;
        mGazeX = gazeX;
        mGazeY = gazeY;
        mGazeValid = gazeValid;
        mGazeOut = gazeOut;
        mStimulusName = stimulusName;
        mStimulusVideoName = stimulusVideoName;
        mStimulusAppear = stimulusAppear;
        mStimulusSide = stimulusSide;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public float getGazeX() {
        return mGazeX;
    }

    public void setGazeX(float gazeX) {
        mGazeX = gazeX;
    }

    public float getGazeY() {
        return mGazeY;
    }

    public void setGazeY(float gazeY) {
        mGazeY = gazeY;
    }

    public int getGazeValid() {
        return mGazeValid;
    }

    public void setGazeValid(int gazeValid) {
        mGazeValid = gazeValid;
    }

    public int getGazeOut() {
        return mGazeOut;
    }

    public void setGazeOut(int gazeOut) {
        mGazeOut = gazeOut;
    }

    public String getStimulusName() {
        return mStimulusName;
    }

    public void setStimulusName(String stimulusName) {
        mStimulusName = stimulusName;
    }

    public String getStimulusVideoName() {
        return mStimulusVideoName;
    }

    public void setStimulusVideoName(String stimulusVideoName) {
        mStimulusVideoName = stimulusVideoName;
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
