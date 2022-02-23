package com.reading.start.tests.test_jabble.data;

import com.reading.start.tests.test_jabble.domain.entity.JabbleTestContent;
import com.reading.start.tests.test_jabble.domain.entity.JabbleTestSurveyResult;

import io.realm.annotations.RealmModule;

@RealmModule(library = true, classes = {JabbleTestSurveyResult.class, JabbleTestContent.class})
public class JabbleTestModule {
}
