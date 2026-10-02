package com.petcare.model;

/**
 * Unit 1 Syllabus Concept: Java Enum
 * Enums provide type safety, constants (static & final implicitly),
 * and can be seamlessly utilized in switch expressions.
 */
public enum UserRole {
    ADMINISTRATOR("Administrator", "Full system access including staff and backups"),
    VETERINARIAN("Veterinarian", "Clinical diagnoses, prescriptions and medical records"),
    RECEPTIONIST("Receptionist", "Client check-in, appointments, and billing"),
    STAFF("Staff", "Inventory monitoring, pet care, grooming and boarding");

    private final String displayName;
    private final String description;

    UserRole(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
