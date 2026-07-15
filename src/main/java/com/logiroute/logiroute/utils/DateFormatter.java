package com.logiroute.logiroute.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DateFormatter {

    private DateFormatter() {
    }

    public static String format(LocalDateTime date, String pattern) {
        if (date == null) return "";
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String toIso(LocalDateTime date) {
        if (date == null) return "";
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
