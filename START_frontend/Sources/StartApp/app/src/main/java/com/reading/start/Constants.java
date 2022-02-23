package com.reading.start;

import java.util.ArrayList;

/**
 * Contains all global constants of application.
 */
public class Constants {
    // ---- GENERAL ----
    public static final String PHOTO_BASE64_ENCODED = "data:image/jpg;base64, ";

    // ---- LANGUAGE ----
    public static final int LANGUAGE_VALUE_SYSTEM = 0;
    public static final int LANGUAGE_VALUE_EN = 1;
    public static final int LANGUAGE_VALUE_HI = 2;

    // ---- DATE FORMAT ----
    public static final String DATE_FORMAT = "dd-MMM-yyyy";
    public static final String DATE_FORMAT_CHILD = "d 'of' MMM yyyy";
    public static final long DEFAULT_DATE_PASSWORD = 631152000000l; //"01-Jan-1990";
    public static final long DEFAULT_DATE_PARENT = 631152000000l; //"01-Jan-1990";
    public static final long DEFAULT_DATE_CHILD = 1388534400000l; //"01-Jan-2014";
    public static final long DEFAULT_DATE_ASSESSMENT = 1388534400000l; //"01-Jan-2014";
    public static final String ATTEMPT_TIME_FORMAT = "%02d:%02d";
    public static final String IMAGE_FILE_FORMAT = "yyyyMMdd_HHmmss";

    // ---- DEFAULT STRINGS ----
    public static final String MALE = "Male";
    public static final String FEMALE = "Female";
    public static final String PARENT = "Parent";
    public static final String GUARDIAN = "Guardian";
    public static final String HAND_RIGHT = "Right";
    public static final String HAND_LEFT = "Left";
    public static final String HAND_AMBIDEXTER = "Ambidextrous";

    // ---- LOGIN ----

    /**
     * Number of attempt for login.
     */
    public static final int DEFAULT_LOGIN_ATTEMPT = 5;

    /**
     * Timeout of lock of login.
     */
    public static final int DEFAULT_LOGIN_ATTEMPT_FAIL_WAITING_TIME = 1000 * 60 * 30; //30 minutes

    // ---- GENERAL AND RXJAVA CONSTANTS ----

    // Default time for execute tasks
    public static final int DEFAULT_EXECUTE_TIME = 15;

    // Small time for execute tasks
    public static final int SMALL_EXECUTE_TIME = 5;

    // Important value for uploading
    public static final int UPLOADING_COUNT_RETRY = 3;
    public static final int UPLOADING_TIMEOUT = 60; // 30 min

    public static final int NETWORK_CHECK_TIMEOUT = 3000; // 3 sec

    // ---- UI CONSTANTS ----

    /**
     * Count of items on My survey screen for all general devices.
     */
    public static final double MY_SURVEY_ITEMS_COLUMN_DEFAULT = 4;

    /**
     * Count of items on My survey screen for narrow devices.
     */
    public static final double MY_SURVEY_ITEMS_COLUMN_NARROW = 5;

    /**
     * Count of items on Child information screen for all general devices.
     */
    public static final int CHILD_INFO_ITEMS_COLUMN_DEFAULT = 3;

    /**
     * Count of items on Child information screen for narrow devices.
     */
    public static final int CHILD_INFO_ITEMS_COLUMN_NARROW = 4;

    /**
     * Count row of items on Child information screen for narrow devices.
     */
    public static final double CHILD_INFO_SURVEYS_ITEMS_ROWS = 2.5;

    // ---- PHOTO ----

    /**
     * Size for photo which displaying in UI.
     */
    public static final int PHOTO_HEIGHT = 600;

    /**
     * How mny place take a photo on the screen.
     */
    public static final int PHOTO_SIZE_UI_PERCENT = 45;

    // ---- TEST ----

    /**
     * Indicates if need make dump of the tests. Using for testing.
     */
    public static final boolean TEST_MAKE_DUMP = BuildConfig.MAKE_TEST_DATA_DUMP;

    /**
     * Folder on sd card for saving dumps of the tests.
     */
    public static final String TEST_DUMP_FOLDER = "StartApp";

    // ---- CONSTANTS ----

    /**
     * Default language when register new child.
     */
    public static final String DEFAULT_LANGUAGE = "Hindi";

    /**
     * Default state when register new child.
     */
    public static final String DEFAULT_STATE = "Delhi";

    /**
     * List of value. Uses in create/edit children.
     */
    public static final ArrayList<Integer> HAND_LIST = new ArrayList<Integer>() {
        {
            add(R.string.child_hand_right);
            add(R.string.child_hand_left);
            add(R.string.child_hand_ambidexter);
        }
    };

    /**
     * List of preferable contact method. Uses in create/edit parent.
     */
    public static final ArrayList<Integer> PREFERABLE_CONTACT_LIST = new ArrayList<Integer>() {
        {
            add(R.string.parent_preferable_contact_not_specified);
            add(R.string.parent_preferable_contact_post);
            add(R.string.parent_preferable_contact_phone);
            add(R.string.parent_preferable_contact_email);
        }
    };
}
