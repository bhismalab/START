package com.reading.start.tests.test_parent_child_play.data;

import com.reading.start.tests.test_parent_child_play.domain.entity.ParentChildPlayTestContent;
import com.reading.start.tests.test_parent_child_play.domain.entity.ParentChildPlayTestSurveyResult;

import io.realm.annotations.RealmModule;

@RealmModule(library = true, classes = {ParentChildPlayTestSurveyResult.class, ParentChildPlayTestContent.class})
public class ParentChildPlayTestModule {
}
