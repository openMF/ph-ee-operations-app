package org.apache.fineract.core.service;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OperatorUtils {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public static String strip(String str) {
        return str.replaceAll("^\"|\"$", "");
    }

    public static SimpleDateFormat dateFormat() {
        return DATE_FORMAT;
    }

    public static String formatDate(Date input) {
        if (input == null) {
            return null;
        }
        return dateFormat().format(input);
    }
}
