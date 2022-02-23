package com.reading.start.tests.test_wheel.data;

import com.reading.start.tests.test_wheel.domain.entity.WheelTestContent;
import com.reading.start.tests.test_wheel.domain.entity.WheelTestSurveyResult;

import io.realm.annotations.RealmModule;

@RealmModule(library = true, classes = {WheelTestSurveyResult.class, WheelTestContent.class})
public class WheelTestModule {
}
