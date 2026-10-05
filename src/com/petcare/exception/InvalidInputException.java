package com.petcare.exception;

/**
 * Unit 3: Custom Exception for input validations
 */
public class InvalidInputException extends PetCareException {

    public InvalidInputException(String message) {
        super(message);
    }

    public InvalidInputException(String message, Throwable cause) {
        super(message, cause);
    }
}
