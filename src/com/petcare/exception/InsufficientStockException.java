package com.petcare.exception;

/**
 * Unit 3: Custom Exception for pharmacy inventory checks
 */
public class InsufficientStockException extends PetCareException {

    private final String medicineName;
    private final int requested;
    private final int available;

    public InsufficientStockException(String medicineName, int requested, int available) {
        super("Insufficient stock for medicine '" + medicineName + "'. Requested: " + requested + ", Available: " + available);
        this.medicineName = medicineName;
        this.requested = requested;
        this.available = available;
    }

    public String getMedicineName() { return medicineName; }
    public int getRequested() { return requested; }
    public int getAvailable() { return available; }
}
