package com.reading.start.domain.entity;

/**
 * Holder for child info
 */
public class ChildrenInfo {
    private long mSurveyDate = 0;

    private boolean mIsFinished = false;

    private int mSurveyNumber = 0;

    public ChildrenInfo() {
        mSurveyDate = 0;
        mIsFinished = false;
        mSurveyNumber = 0;
    }

    public ChildrenInfo(long surveyDate, boolean isFinished, int surveyNumber) {
        mSurveyDate = surveyDate;
        mIsFinished = isFinished;
        mSurveyNumber = surveyNumber;
    }

    public long getSurveyDate() {
        return mSurveyDate;
    }

    public void setSurveyDate(long surveyDate) {
        mSurveyDate = surveyDate;
    }

    public boolean isIsFinished() {
        return mIsFinished;
    }

    public void setIsFinished(boolean isFinished) {
        mIsFinished = isFinished;
    }

    public int getSurveyNumber() {
        return mSurveyNumber;
    }

    public void setSurveyNumber(int surveyNumber) {
        mSurveyNumber = surveyNumber;
    }
}
