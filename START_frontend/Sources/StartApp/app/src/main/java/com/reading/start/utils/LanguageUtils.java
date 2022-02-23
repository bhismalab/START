package com.reading.start.utils;

import com.reading.start.Constants;

/**
 * Represent all languages which support application.
 */
public class LanguageUtils {
    /**
     * Get all languages which supported.
     */
    public static String getLanguageString(int value) {
        String result = null;

        switch (value) {
            case Constants.LANGUAGE_VALUE_SYSTEM: {
                result = null;
                break;
            }
            case Constants.LANGUAGE_VALUE_EN: {
                result = "en";
                break;
            }
            case Constants.LANGUAGE_VALUE_HI: {
                result = "hi";
                break;
            }
            default: {
                result = "en";
                break;
            }
        }

        return result;
    }
}
