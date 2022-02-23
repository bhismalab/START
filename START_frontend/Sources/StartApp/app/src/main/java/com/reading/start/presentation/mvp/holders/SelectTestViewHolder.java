package com.reading.start.presentation.mvp.holders;

import android.view.View;

public class SelectTestViewHolder {
    private final View mEyeTrackingTest;
    private final View mChoiceTouchingTest;
    private final View mMotorFollowingTest;
    private final View mJabFingerBubbles;
    private final View mMultipleChoiceQuestions;
    private final View mParentChildPlayTest;

    public SelectTestViewHolder(View eyeTrackingTest, View choiceTouchingTest, View motorFollowingTest,
                                View jabFingerBubbles, View multipleChoiceQuestions, View parentChildPlayTest) {
        mEyeTrackingTest = eyeTrackingTest;
        mChoiceTouchingTest = choiceTouchingTest;
        mMotorFollowingTest = motorFollowingTest;
        mJabFingerBubbles = jabFingerBubbles;
        mMultipleChoiceQuestions = multipleChoiceQuestions;
        mParentChildPlayTest = parentChildPlayTest;
    }

    public View getEyeTrackingTest() {
        return mEyeTrackingTest;
    }

    public View getChoiceTouchingTest() {
        return mChoiceTouchingTest;
    }

    public View getMotorFollowingTest() {
        return mMotorFollowingTest;
    }

    public View getJabFingerBubbles() {
        return mJabFingerBubbles;
    }

    public View getMultipleChoiceQuestions() {
        return mMultipleChoiceQuestions;
    }

    public View getParentChildPlayTest() {
        return mParentChildPlayTest;
    }
}
