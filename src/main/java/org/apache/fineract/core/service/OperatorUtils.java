package org.apache.fineract.core.service;

import java.text.SimpleDateFormat;

public final class OperatorUtils {

    private OperatorUtils() {}

    public static String strip(String str) {
        return str.replaceAll("^\"|\"$", "");
    }

    public static SimpleDateFormat dateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
}
