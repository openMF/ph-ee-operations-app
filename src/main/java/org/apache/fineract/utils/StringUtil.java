package org.apache.fineract.utils;

import org.apache.commons.text.CaseUtils;

import java.util.Collections;
import java.util.List;

public class StringUtil {

    public static List<String> commaSeparatedStringToList(String s) {
        if (s == null || s.isBlank()) {
            return Collections.emptyList();
        } else {
            return s.contains(",") ? List.of(s.split(",")) : Collections.singletonList(s);
        }
    }

    public static String submitterVariableName(String taskName) {
        return "submitter" + taskName.replaceAll(" ", "");
    }
}
