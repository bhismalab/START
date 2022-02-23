package com.reading.start.tests.test_parent_child_play.domain.entity;

import com.reading.start.tests.ITestModuleResult;

public class ParentChildPlayTestSurveyResultInfo implements ITestModuleResult {
    private int mIndex = 1;

    private long mTestTime = 0;

    private String mInfo = null;

    private ParentChildPlayTestSurveyResult mSurveyResult;

    public ParentChildPlayTestSurveyResultInfo(int index, long testTime, String info, ParentChildPlayTestSurveyResult result) {
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

    public ParentChildPlayTestSurveyResult getSurveyResult() {
        return mSurveyResult;
    }
}
