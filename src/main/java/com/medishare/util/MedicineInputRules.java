package com.medishare.util;

import com.medishare.exception.ValidationException;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/** Shared formatting and validation rules for medicine master data. */
public final class MedicineInputRules {
    private static final Pattern CODE = Pattern.compile("[A-Z0-9][A-Z0-9_-]{2,19}");
    private static final Pattern STRENGTH = Pattern.compile(
            "(?i)^\\d+(?:\\.\\d+)?\\s*(?:mcg|µg|mg|g|kg|ml|l|iu|units?|meq|mmol|%)" +
            "(?:/\\s*(?:\\d+(?:\\.\\d+)?\\s*)?(?:mcg|µg|mg|g|kg|ml|l|iu|units?|meq|mmol|%))?$");
    private static final List<String> DOSAGE_FORMS = List.of(
            "Tablet", "Capsule", "Syrup", "Solution", "Suspension", "Sachet",
            "Injection", "Cream", "Ointment", "Drops", "Inhaler", "Other");

    private MedicineInputRules() { }

    public static String normalizeCode(String value) {
        String code = collapseSpaces(value).replace(" ", "").toUpperCase(Locale.ROOT);
        if (!CODE.matcher(code).matches()) {
            throw new ValidationException("Medicine code must be 3-20 characters and contain only letters, numbers, hyphens or underscores.");
        }
        return code;
    }

    public static String normalizeTitle(String value) {
        String cleaned = collapseSpaces(value).toLowerCase(Locale.ROOT);
        StringBuilder result = new StringBuilder(cleaned.length());
        boolean capitalize = true;
        for (char ch : cleaned.toCharArray()) {
            if (Character.isLetter(ch) && capitalize) {
                result.append(Character.toUpperCase(ch));
                capitalize = false;
            } else {
                result.append(ch);
                capitalize = Character.isWhitespace(ch) || ch == '-' || ch == '/';
            }
        }
        return result.toString();
    }

    public static String normalizeBatchNumber(String value) {
        String normalized = collapseSpaces(value).replace(" ", "").toUpperCase(Locale.ROOT);
        if (normalized.isBlank()) throw new ValidationException("Batch number is required.");
        return normalized;
    }

    public static String normalizeStrength(String value) {
        String cleaned = collapseSpaces(value);
        if (cleaned.equalsIgnoreCase("not specified")) return "Not specified";
        cleaned = cleaned.replaceAll("\\s*/\\s*", "/");
        if (!STRENGTH.matcher(cleaned).matches()) {
            throw new ValidationException("Enter a numeric strength with a unit (for example 500 mg, 100 mg/5 mL, 0.9%, 1000 IU), or use 'Not specified'.");
        }
        return canonicalizeUnits(cleaned);
    }

    public static List<String> dosageForms() {
        return DOSAGE_FORMS;
    }

    public static String normalizeDosageForm(String value) {
        for (String form : DOSAGE_FORMS) {
            if (form.equalsIgnoreCase(value == null ? "" : value.trim())) return form;
        }
        String custom = normalizeTitle(value);
        if (custom.isBlank()) throw new ValidationException("Dosage form is required.");
        return custom;
    }

    private static String canonicalizeUnits(String value) {
        String result = value;
        result = result.replaceAll("(?i)(\\d)(?=(?:mcg|µg|mg|kg|g|ml|l|iu|units?|meq|mmol)\\b)", "$1 ");
        result = result.replaceAll("(?i)\\bml\\b", "mL");
        result = result.replaceAll("(?i)\\bl\\b", "L");
        result = result.replaceAll("(?i)\\biu\\b", "IU");
        result = result.replaceAll("(?i)\\bmeq\\b", "mEq");
        result = result.replaceAll("(?i)\\bmcg\\b", "mcg");
        result = result.replaceAll("(?i)\\bmmol\\b", "mmol");
        result = result.replaceAll("(?i)\\bunits?\\b", "units");
        result = result.replaceAll("(?i)\\bmg\\b", "mg");
        result = result.replaceAll("(?i)\\bkg\\b", "kg");
        result = result.replaceAll("(?i)\\bg\\b", "g");
        return collapseSpaces(result).replaceAll("\\s*/\\s*", "/");
    }

    private static String collapseSpaces(String value) {
        return value == null ? "" : value.trim().replaceAll("\\s+", " ");
    }
}
