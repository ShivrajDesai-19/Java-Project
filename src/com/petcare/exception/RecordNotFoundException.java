package com.petcare.exception;

/**
 * Unit 3: Custom Exception for missing database records
 */
public class RecordNotFoundException extends PetCareException {

    public RecordNotFoundException(String entityName, int id) {
        super(entityName + " record with ID " + id + " was not found in the system.");
    }

    public RecordNotFoundException(String message) {
        super(message);
    }
}
