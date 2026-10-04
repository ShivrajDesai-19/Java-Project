package com.petcare.exception;

/**
 * Unit 3 Syllabus Concept: Custom Exception
 * "We can also create our own class for Exception which must extend the Exception class."
 * Base application exception for Smart Pet Care System.
 */
public class PetCareException extends Exception {

    public PetCareException(String message) {
        super(message);
    }

    public PetCareException(String message, Throwable cause) {
        super(message, cause);
    }
}
