package com.petcare.util;

import com.petcare.exception.InvalidInputException;

import java.util.regex.Pattern;

/**
 * Unit 1 & Unit 3 Syllabus Concepts:
 * - Static Methods and Logic Validation
 * - Custom Exception throwing (InvalidInputException)
 */
public class InputValidator {

    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^[0-9+\\-\\s()]{7,15}$");

    private InputValidator() {}

    public static void validateNotEmpty(String value, String fieldName) throws InvalidInputException {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidInputException(fieldName + " cannot be empty.");
        }
    }

    public static void validatePhone(String phone, String fieldName) throws InvalidInputException {
        validateNotEmpty(phone, fieldName);
        if (!PHONE_PATTERN.matcher(phone.trim()).matches()) {
            throw new InvalidInputException(fieldName + " must be a valid phone number (digits and optional + or -).");
        }
    }

    public static void validateEmail(String email, String fieldName) throws InvalidInputException {
        if (email != null && !email.trim().isEmpty()) {
            if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
                throw new InvalidInputException(fieldName + " is not a valid email address format.");
            }
        }
    }

    public static double validatePositiveDouble(String numberStr, String fieldName) throws InvalidInputException {
        validateNotEmpty(numberStr, fieldName);
        try {
            double val = Double.parseDouble(numberStr.trim());
            if (val < 0) {
                throw new InvalidInputException(fieldName + " cannot be negative.");
            }
            return val;
        } catch (NumberFormatException e) {
            throw new InvalidInputException(fieldName + " must be a valid numeric amount.", e);
        }
    }

    public static int validatePositiveInt(String numberStr, String fieldName) throws InvalidInputException {
        validateNotEmpty(numberStr, fieldName);
        try {
            int val = Integer.parseInt(numberStr.trim());
            if (val < 0) {
                throw new InvalidInputException(fieldName + " cannot be negative.");
            }
            return val;
        } catch (NumberFormatException e) {
            throw new InvalidInputException(fieldName + " must be a valid whole integer number.", e);
        }
    }
}
