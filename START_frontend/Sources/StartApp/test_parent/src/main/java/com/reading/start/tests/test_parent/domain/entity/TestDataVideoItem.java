package com.reading.start.tests.test_parent.domain.entity;

import com.google.gson.annotations.SerializedName;

public class TestDataVideoItem implements IParentTestItem {
    public static final String FILED_TITLE = "title";
    public static final String FILED_QUESTION = "question";
    public static final String FILED_QUESTION_HINDI = "question_hindi";
    public static final String FILED_SELECTED_CHOICE = "selected_choice";
    public static final String FILED_CHOICES = "choices";
    public static final String FILED_LEFT_VIDEO = "leftVideo";
    public static final String FILED_RIGHT_VIDEO = "rightVideo";

    @SerializedName(FILED_TITLE)
    private String mTitle;

    @SerializedName(FILED_QUESTION)
    private String mText;

    @SerializedName(FILED_QUESTION_HINDI)
    private String mTextHindi;

    @SerializedName(FILED_LEFT_VIDEO)
    private String mLeftVideoUrl;

    @SerializedName(FILED_RIGHT_VIDEO)
    private String mRightVideoUrl;

    @SerializedName(FILED_SELECTED_CHOICE)
    private int mSelectedChoice = -1;

    @SerializedName(FILED_CHOICES)
    private TestDataItemChoices mChoices = null;

    public TestDataVideoItem(String text, String textHindi, String leftVideo, String rightVideo, TestDataItemChoices choices) {
        mText = text;
        mTextHindi = textHindi;
        mLeftVideoUrl = leftVideo;
        mRightVideoUrl = rightVideo;
        mChoices = choices;
        mSelectedChoice = -1;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public String getTextHindi() {
        return mTextHindi;
    }

    public void setTextHindi(String textHindi) {
        mTextHindi = textHindi;
    }

    public int getSelectedChoice() {
        return mSelectedChoice;
    }

    public void setSelectedChoice(int selectedChoice) {
        mSelectedChoice = selectedChoice;
    }

    public TestDataItemChoices getChoices() {
        return mChoices;
    }

    public void setChoices(TestDataItemChoices choices) {
        mChoices = choices;
    }

    public String getLeftVideoUrl() {
        return mLeftVideoUrl;
    }

    public void setLeftVideoUrl(String leftVideoUrl) {
        mLeftVideoUrl = leftVideoUrl;
    }

    public String getRightVideoUrl() {
        return mRightVideoUrl;
    }

    public void setRightVideoUrl(String rightVideoUrl) {
        mRightVideoUrl = rightVideoUrl;
    }
}
