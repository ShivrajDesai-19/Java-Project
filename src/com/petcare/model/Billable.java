package com.petcare.model;

/**
 * Unit 2 Syllabus Concept: Interface
 * An interface serves as a contract/blueprint for classes that generate chargeable bills.
 * Multiple classes (Medicine, GroomingBooking, BoardingBooking) implement this interface.
 */
public interface Billable {
    // Abstract methods implicitly public and abstract
    String getItemDescription();
    double getUnitPrice();
    int getQuantity();
    double calculateTotalPrice();
    String getItemType();
}
