package com.reading.start.tests.test_parent.data;

import com.reading.start.tests.test_parent.domain.entity.ParentTestContent;
import com.reading.start.tests.test_parent.domain.entity.ParentTestContentChoices;
import com.reading.start.tests.test_parent.domain.entity.ParentTestSurveyResult;

import io.realm.annotations.RealmModule;

@RealmModule(library = true, classes = {ParentTestSurveyResult.class, ParentTestContent.class, ParentTestContentChoices.class})
public class ParentTestModule {
}
