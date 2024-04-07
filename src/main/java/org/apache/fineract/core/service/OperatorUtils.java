package org.apache.fineract.core.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class OperatorUtils {

    private static final String PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(PATTERN);
    private static final SimpleDateFormat UTC_DATE_FORMAT;

    static {
        UTC_DATE_FORMAT = new SimpleDateFormat(PATTERN);
        UTC_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public static String strip(String str) {
        return str.replaceAll("^\"|\"$", "");
    }

    public static SimpleDateFormat dateFormat() {
        return DATE_FORMAT;
    }

    public static SimpleDateFormat utcDateFormat() {
        return UTC_DATE_FORMAT;
    }

    public static String formatDate(Date input) {
        if (input == null) {
            return null;
        }
        return dateFormat().format(input);
    }

    public static String formatUtcDate(Date input) {
        if (input == null) {
            return null;
        }
        return utcDateFormat().format(input);
    }
}
