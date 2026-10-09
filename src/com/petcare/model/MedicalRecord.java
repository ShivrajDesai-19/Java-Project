package com.petcare.model;

import java.time.LocalDate;

/**
 * Unit 2 Syllabus Concept: Abstract Class
 * An abstract class cannot be instantiated directly and serves as a base class
 * containing both concrete common properties and abstract methods that subclasses must implement.
 */
public abstract class MedicalRecord implements Searchable {
    protected int id;
    protected int petId;
    protected String petName;
    protected int vetId;
    protected String vetName;
    protected LocalDate visitDate;
    protected String recordType;

    public MedicalRecord() {
        this.visitDate = LocalDate.now();
    }

    public MedicalRecord(int id, int petId, int vetId, LocalDate visitDate, String recordType) {
        this.id = id;
        this.petId = petId;
        this.vetId = vetId;
        this.visitDate = (visitDate != null) ? visitDate : LocalDate.now();
        this.recordType = recordType;
    }

    // Abstract methods that MUST be overridden by subclasses (Unit 2 concept)
    public abstract String getRecordCategory();
    public abstract String getSummaryDescription();

    @Override
    public boolean matchesSearch(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return true;
        }
        String lower = keyword.toLowerCase();
        return (petName != null && petName.toLowerCase().contains(lower)) ||
               (vetName != null && vetName.toLowerCase().contains(lower)) ||
               (recordType != null && recordType.toLowerCase().contains(lower)) ||
               (getSummaryDescription() != null && getSummaryDescription().toLowerCase().contains(lower));
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPetId() { return petId; }
    public void setPetId(int petId) { this.petId = petId; }

    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }

    public int getVetId() { return vetId; }
    public void setVetId(int vetId) { this.vetId = vetId; }

    public String getVetName() { return vetName; }
    public void setVetName(String vetName) { this.vetName = vetName; }

    public LocalDate getVisitDate() { return visitDate; }
    public void setVisitDate(LocalDate visitDate) { this.visitDate = visitDate; }

    public String getRecordType() { return recordType; }
    public void setRecordType(String recordType) { this.recordType = recordType; }
}
