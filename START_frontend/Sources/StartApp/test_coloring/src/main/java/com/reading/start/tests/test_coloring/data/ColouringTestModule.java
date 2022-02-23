package com.reading.start.tests.test_coloring.data;

import com.reading.start.tests.test_coloring.domain.entity.ColoringTestContent;
import com.reading.start.tests.test_coloring.domain.entity.ColoringTestSurveyAttachments;
import com.reading.start.tests.test_coloring.domain.entity.ColoringTestSurveyResult;

import io.realm.annotations.RealmModule;

@RealmModule(library = true, classes = {ColoringTestSurveyResult.class, ColoringTestSurveyAttachments.class, ColoringTestContent.class})
public class ColouringTestModule {
}
