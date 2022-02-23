package com.reading.start.tests;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import java.util.ArrayList;

/**
 * Represent test module.
 */
public interface ITestModule {
    /**
     * Return unique string that represent test.
     */
    String getSurveyType();

    /**
     * Return resource that represent name of the test.
     */
    int getNameResource();

    /**
     * Start test.
     */
    void startTest(Activity context, int surveyId);

    /**
     * Start test.
     */
    void startTest(Activity context, int surveyId, Bundle data);

    /**
     * Show test result.
     */
    void showResult(Activity context, int surveyId, int attempt);

    /**
     * Indicates whether test have result for displaying in separate screen.
     */
    boolean hasExtendedResult(Context context, int surveyId, int attempt);

    /**
     * Download resources for test.
     */
    boolean downloadTestData(String token, String sinceDateTime);

    /**
     * Download result of the test from the server.
     */
    boolean downloadResult(String token, int surveyId, int surveyIdServer);

    /**
     * Upload result of test to server.
     */
    UploadResult uploadResult(String token, int surveyId, int surveyIdServer);

    /**
     * Get result of the test from the memory.
     */
    ArrayList<ITestModuleResult> getTestResults(int surveyId);

    /**
     * Get result of the test from the database.
     */
    ArrayList<ITestModuleResult> fetchTestResults(int surveyId);

    /**
     * Delete test result from database.
     */
    void deleteResults(int surveyId);

    /**
     * Indicates whether test competed.
     */
    boolean isCompleted(int surveyId);

    /**
     * Indicates whether test uploaded to server.
     */
    boolean isUploaded(int surveyId);

    /**
     * Get index of the test in displaying list. Uses for sorting of the tests.
     */
    int getIndexDisplaying();

    /**
     * Get index of the test in processing list. Uses for sorting of the tests.
     */
    int getIndexProcessing();

    /**
     * Save dump of the test result to sdcard.
     */
    boolean makeDump(Context context, int surveyId, int attempt, String path);
}
