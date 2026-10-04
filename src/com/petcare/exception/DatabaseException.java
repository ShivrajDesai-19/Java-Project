package com.petcare.exception;

/**
 * Unit 3 Syllabus Concept: Chained Exceptions
 * "Whenever in a program the first exception causes another exception,
 * that is termed as Chained Exception (nesting exception).
 * The syntax for using a chained exception wraps the lower cause into the new exception."
 */
public class DatabaseException extends PetCareException {

    public DatabaseException(String message) {
        super(message);
    }

    // Chained Exception Constructor
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
