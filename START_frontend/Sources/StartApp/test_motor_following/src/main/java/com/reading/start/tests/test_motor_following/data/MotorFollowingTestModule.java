package com.reading.start.tests.test_motor_following.data;

import com.reading.start.tests.test_motor_following.domain.entity.MotorFollowingTestContent;
import com.reading.start.tests.test_motor_following.domain.entity.MotorFollowingTestSurveyAttachments;
import com.reading.start.tests.test_motor_following.domain.entity.MotorFollowingTestSurveyResult;

import io.realm.annotations.RealmModule;

@RealmModule(library = true, classes = {MotorFollowingTestSurveyResult.class, MotorFollowingTestContent.class, MotorFollowingTestSurveyAttachments.class})
public class MotorFollowingTestModule {
}
