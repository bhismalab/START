package com.reading.start.tests.test_eye_tracking.data;

import com.reading.start.tests.test_eye_tracking.domain.entity.EyeTrackingTestContentPairs;
import com.reading.start.tests.test_eye_tracking.domain.entity.EyeTrackingTestContentSlide;
import com.reading.start.tests.test_eye_tracking.domain.entity.EyeTrackingTestSurveyResult;

import io.realm.annotations.RealmModule;

@RealmModule(library = true, classes = {EyeTrackingTestSurveyResult.class, EyeTrackingTestContentPairs.class, EyeTrackingTestContentSlide.class})
public class EyeTrackingTestModule {
}
