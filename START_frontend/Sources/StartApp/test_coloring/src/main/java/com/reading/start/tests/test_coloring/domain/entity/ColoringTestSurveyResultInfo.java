package com.reading.start.tests.test_coloring.domain.entity;

import com.reading.start.tests.ITestModuleResult;

public class ColoringTestSurveyResultInfo implements ITestModuleResult {
    private int mIndex = 1;

    private long mTestTime = 0;

    private String mInfo = null;

    private boolean mIsInterrupted = false;

    private ColoringTestSurveyResult mSurveyResult;

    public ColoringTestSurveyResultInfo(int index, long testTime, String info, boolean interrupted,
                                        ColoringTestSurveyResult result) {
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

    public ColoringTestSurveyResult getSurveyResult() {
        return mSurveyResult;
    }
}
