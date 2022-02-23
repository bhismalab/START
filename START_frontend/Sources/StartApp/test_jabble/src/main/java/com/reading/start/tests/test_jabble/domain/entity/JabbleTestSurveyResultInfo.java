package com.reading.start.tests.test_jabble.domain.entity;

import com.reading.start.tests.ITestModuleResult;

public class JabbleTestSurveyResultInfo implements ITestModuleResult {
    private int mIndex = 1;

    private long mTestTime = 0;

    private String mInfo = null;

    private boolean mIsInterrupted = false;

    private JabbleTestSurveyResult mSurveyResult;

    public JabbleTestSurveyResultInfo(int index, long testTime, String info, boolean interrupted,
                                      JabbleTestSurveyResult result) {
        mIndex = index;
        mTestTime = testTime;
        mInfo = info;
        mIsInterrupted = interrupted;
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

    public boolean isInterrupted() {
        return mIsInterrupted;
    }

    public JabbleTestSurveyResult getSurveyResult() {
        return mSurveyResult;
    }
}
