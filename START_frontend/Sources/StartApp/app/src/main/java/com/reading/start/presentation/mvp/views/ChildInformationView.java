package com.reading.start.presentation.mvp.views;

import com.reading.start.domain.entity.Survey;

public interface ChildInformationView {

    /**
     * Raises when survey added
     */
    void onSurveyAdd(Survey survey, int surveyNumber);

    /**
     * Raises when survey need to open
     */
    void onSurveyOpen(boolean restore, Survey survey, int surveyNumber);

    /**
     * Raises when survey need to download
     */
    void onSurveyDownload(Survey survey, int surveyNumber);

    /**
     * Raises when need edit child
     */
    void onEditChild();

    /**
     * Raises when navigation menu pressed
     */
    void onNavigationMenuPressed();

    /**
     * Raises when back button pressed
     */
    void onBackPressed();

    /**
     * Raises when home button pressed
     */
    void onHomePressed();

    /**
     * Raises when caused some error
     */
    void onError(String message);
}
