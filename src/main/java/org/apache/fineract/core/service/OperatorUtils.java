package org.apache.fineract.core.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class OperatorUtils {

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat(DATETIME_PATTERN);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_PATTERN);
    private static final SimpleDateFormat UTC_DATETIME_FORMAT;

    static {
        UTC_DATETIME_FORMAT = new SimpleDateFormat(DATETIME_PATTERN);
        UTC_DATETIME_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public static String strip(String str) {
        return str.replaceAll("^\"|\"$", "");
    }

    public static SimpleDateFormat dateFormat() {
        return DATE_FORMAT;
    }

    public static SimpleDateFormat dateTimeFormat() {
        return DATETIME_FORMAT;
    }

    public static SimpleDateFormat utcDateTimeFormat() {
        return UTC_DATETIME_FORMAT;
    }

    public static String formatDate(Date input) {
        if (input == null) {
            return null;
        }
        return dateFormat().format(input);
    }

    public static String formatDateTime(Date input) {
        if (input == null) {
            return null;
        }
        return dateTimeFormat().format(input);
    }

    public static String formatUtcDateTime(Date input) {
        if (input == null) {
            return null;
        }
        return utcDateTimeFormat().format(input);
    }
}
