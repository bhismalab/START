package com.reading.start.tests.test_choice_touching.data;

import com.reading.start.tests.test_choice_touching.domain.entity.ChoiceTouchingTestContent;
import com.reading.start.tests.test_choice_touching.domain.entity.ChoiceTouchingTestSurveyResult;

import io.realm.annotations.RealmModule;

@RealmModule(library = true, classes = {ChoiceTouchingTestSurveyResult.class, ChoiceTouchingTestContent.class})
public class ChoiceTouchingTestModule {
}
