package com.reading.start.tests.test_parent.domain.entity;

import com.reading.start.tests.ITestModuleResult;

public class ParentTestSurveyResultInfo implements ITestModuleResult {
    private int mIndex = 1;

    private long mTestTime = 0;

    private String mInfo = null;

    private ParentTestSurveyResult mSurveyResult;

    public ParentTestSurveyResultInfo(int index, long testTime, String info, ParentTestSurveyResult result) {
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

    public ParentTestSurveyResult getSurveyResult() {
        return mSurveyResult;
    }
}
