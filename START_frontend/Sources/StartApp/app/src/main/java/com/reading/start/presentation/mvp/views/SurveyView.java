package com.reading.start.presentation.mvp.views;

import com.reading.start.domain.entity.Children;
import com.reading.start.tests.ITestModule;

public interface SurveyView {

    /**
     * Raises when pressed navigation menu
     */
    void onNavigationMenuPressed();

    /**
     * Raises when pressed back button
     */
    void onBackPressed();

    /**
     * Raises when pressed home button
     */
    void onHomePressed();

    /**
     * Raises when caused error
     */
    void onError(String message);

    /**
     * Raises when pressed delete button
     */
    void onDeleteSurvey();

    /**
     * Raises when survey deleted success
     */
    void onDeletedSurvey();

    /**
     * Raises when press start survey
     */
    void onStartSurvey(ITestModule module);

    /**
     * Raises when press open survey
     */
    void onOpenSurvey(ITestModule module, int attempt);

    /**
     * Raises when need to updated child info
     */
    void onChildInfoUpdated(Children value);
}
