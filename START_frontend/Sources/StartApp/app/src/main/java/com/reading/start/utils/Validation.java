package com.reading.start.utils;

import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;

/**
 * Helper call using for validation different value.
 */
public class Validation {

    /**
     * Validate of the mail.
     */
    public static boolean isValidEmail(String target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    /**
     * Validate of phone number.
     */
    public static boolean isValidPhoneNumber(String target) {
        return !TextUtils.isEmpty(target) && PhoneNumberUtils.isGlobalPhoneNumber(target);
    }
}
