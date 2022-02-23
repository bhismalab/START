package com.reading.start.tests.test_eye_tracking.domain.entity;

import com.reading.start.tests.ITestModuleResult;

public class EyeTrackingTestSurveyResultInfo implements ITestModuleResult {
    private int mIndex = 1;

    private long mTestTime = 0;

    private String mInfo = null;

    private EyeTrackingTestSurveyResult mSurveyResult;

    public EyeTrackingTestSurveyResultInfo(int index, long testTime, String info, EyeTrackingTestSurveyResult result) {
        mIndex = index;
        mTestTime = testTime;
        mInfo = info;
        mSurveyResult = result;
    }

    public int getIndex() {
        return mIndex;
    }

    public long getTestTime() {
        return mTestTime;
    }

    public String getInfo() {
        return mInfo;
    }

    @Override
    public boolean isInterrupted() {
        return false;
    }

    public EyeTrackingTestSurveyResult getSurveyResult() {
        return mSurveyResult;
    }
}
