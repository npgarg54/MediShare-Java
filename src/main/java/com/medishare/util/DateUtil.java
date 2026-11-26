package com.medishare.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class DateUtil {
    public static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

    private DateUtil() {
    }

    public static LocalDate parseIsoDate(String value) {
        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException | NullPointerException ex) {
            throw new IllegalArgumentException("Enter the date in yyyy-MM-dd format (example: 2027-04-30).");
        }
    }

    public static String display(LocalDate date) {
        return date == null ? "-" : date.format(DISPLAY_FORMAT);
    }
}
