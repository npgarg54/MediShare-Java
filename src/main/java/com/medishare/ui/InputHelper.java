package com.medishare.ui;

import com.medishare.exception.LogoutRequestedException;
import com.medishare.exception.OperationCancelledException;
import com.medishare.util.DateUtil;

import java.time.LocalDate;
import java.util.Scanner;

/** Reads console input and recognizes global navigation commands. */
public class InputHelper {
    private final Scanner scanner;

    public InputHelper(Scanner scanner) {
        this.scanner = scanner;
    }

    public String readRequired(String prompt) {
        while (true) {
            String value = readCommandAware(prompt);
            if (!value.isBlank()) return value;
            System.out.println("This field is required. Please try again.");
        }
    }

    /** Used for pauses and other optional prompts while retaining global navigation commands. */
    public String readOptional(String prompt) {
        return readCommandAware(prompt);
    }

    public int readInt(String prompt, int minimum, int maximum) {
        while (true) {
            String input = readCommandAware(prompt);
            try {
                int value = Integer.parseInt(input);
                if (value < minimum || value > maximum) {
                    System.out.println("Enter a value from " + minimum + " to " + maximum + ".");
                } else return value;
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    public LocalDate readDate(String prompt) {
        while (true) {
            String value = readCommandAware(prompt);
            try {
                return DateUtil.parseIsoDate(value);
            } catch (IllegalArgumentException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private String readCommandAware(String prompt) {
        System.out.print(prompt);
        String value = scanner.nextLine().trim();
        if (value.equalsIgnoreCase(":cancel") || value.equalsIgnoreCase(":back")) {
            throw new OperationCancelledException();
        }
        if (value.equalsIgnoreCase(":logout")) {
            throw new LogoutRequestedException();
        }
        return value;
    }
}
