package com.reading.start.tests.test_motor_following.domain.entity;

import com.reading.start.tests.ITestModuleResult;

public class MotorFollowingTestSurveyResultInfo implements ITestModuleResult {
    private int mIndex = 1;

    private long mTestTime = 0;

    private String mInfo = null;

    private boolean mIsInterrupted = false;

    private MotorFollowingTestSurveyResult mSurveyResult;

    public MotorFollowingTestSurveyResultInfo(int index, long testTime, String info, boolean interrupted,
                                              MotorFollowingTestSurveyResult result) {
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

    public MotorFollowingTestSurveyResult getSurveyResult() {
        return mSurveyResult;
    }
}
