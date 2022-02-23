package com.reading.start.presentation.mvp.views;

import com.reading.start.domain.entity.Survey;

public interface MainActivityView {
    /**
     * Raises when added new survey
     */
    void onAddSurvey(int childId, Survey survey, int surveyNumber);
}
