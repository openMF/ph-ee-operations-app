package org.apache.fineract.utils;

import org.apache.commons.text.CaseUtils;
import org.json.JSONArray;

import java.util.ArrayList;
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

    public static List<String> jsonArrayToStringList(String s) {
        if (s == null || s.isBlank()) {
            return Collections.emptyList();
        } else {
            JSONArray jsonArray = new JSONArray(s);
            List<String> result = new ArrayList<>();
            jsonArray.forEach(o -> {
                result.add(o.toString());
            });
            return result;
        }
    }

    public static String submitterVariableName(String taskName) {
        return "submitter" + taskName.replaceAll(" ", "");
    }
}
