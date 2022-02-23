package com.reading.start.tests;

/**
 * Contains all global constants of module.
 */
public class Constants {
    // ---- DATA BASE ----
    public static final long DATA_BASE_VERSION = 7;

    // Activity constants
    public static final int ACTIVITY_SURVEY = 200;
    public static final int ACTIVITY_RESULT_BACK = 201;
    public static final int ACTIVITY_RESULT_HOME = 202;
    public static final int ACTIVITY_RESULT_TRY_AGAIN = 203;
    public static final int ACTIVITY_RESULT_START_NEXT_SURVEY = 204;
    public static final int ACTIVITY_RESULT_OPEN_CHILD_TESTS = 205;

    // Intent data
    public static final String SURVEY_ID = "SURVEY_ID";
    public static final String SURVEY_NUMBER = "SURVEY_NUMBER";
    public static final String SURVEY_TYPE = "SURVEY_TYPE";
    public static final String SURVEY_SHOW_RESULT = "SURVEY_SHOW_RESULT";
    public static final String SURVEY_RESULT_ATTEMPT = "SURVEY_RESULT_ATTEMPT";
    public static final String SURVEY_DATA = "SURVEY_DATA";
    public static final String SURVEY_PARENT_1 = "SURVEY_PARENT_1";
    public static final String SURVEY_PARENT_2 = "SURVEY_PARENT_2";

    // General tests parameters
    public static final int ATTEMPT_COUNT = 3;

    public static final String LOG_FILE = "AppLog.txt";
    public static final String LOG_FOLDER = "StartApp";
    public static final String SERVER_LOG_FILE = "ServerLog.txt";
    public static final String SERVER_LOG_FOLDER = "StartApp";
    public static final String CAMERA_LOG_FILE = "CameraLog.txt";
    public static final String CAMERA_LOG_FOLDER = "StartApp";
    public static final String DATABASE_LOG_FILE = "DatabaseLog.txt";
    public static final String DATABASE_LOG_FOLDER = "StartApp";
    public static final int OK_HTTP_CLIENT_TIMEOUT = 120; // 120 sec
    public static final int OK_HTTP_CLIENT_BUFFER_SIZE = 256 * 1024;
}