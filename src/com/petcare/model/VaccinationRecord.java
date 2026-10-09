package com.petcare.model;

import java.time.LocalDate;

/**
 * Unit 2 Syllabus Demonstration:
 * A class extending a superclass AND implementing an interface simultaneously:
 * class VaccinationRecord extends MedicalRecord implements Billable
 */
public class VaccinationRecord extends MedicalRecord implements Billable {
    private String vaccineName;
    private String batchNo;
    private LocalDate dateAdministered;
    private LocalDate nextDueDate;
    private String status; // COMPLETED, DUE, OVERDUE
    private String remarks;
    private double standardVaccineFee = 350.0;

    public VaccinationRecord() {
        super();
        this.recordType = "VACCINATION";
        this.dateAdministered = LocalDate.now();
        this.nextDueDate = LocalDate.now().plusYears(1);
        this.status = "COMPLETED";
    }

    public VaccinationRecord(int id, int petId, int vetId, String vaccineName, String batchNo,
                             LocalDate dateAdministered, LocalDate nextDueDate, String status, String remarks) {
        super(id, petId, vetId, dateAdministered, "VACCINATION");
        this.vaccineName = vaccineName;
        this.batchNo = batchNo;
        this.dateAdministered = dateAdministered;
        this.nextDueDate = nextDueDate;
        this.status = status;
        this.remarks = remarks;
    }

    // Overriding MedicalRecord abstract methods
    @Override
    public String getRecordCategory() {
        return "Immunization / Vaccine";
    }

    @Override
    public String getSummaryDescription() {
        return "Vaccine: " + vaccineName + " (Batch: " + batchNo + ") Next Due: " + nextDueDate;
    }

    // Overriding Billable interface methods
    @Override
    public String getItemDescription() {
        return "Vaccination Administration: " + vaccineName;
    }

    @Override
    public double getUnitPrice() {
        return standardVaccineFee;
    }

    @Override
    public int getQuantity() {
        return 1;
    }

    @Override
    public double calculateTotalPrice() {
        return standardVaccineFee;
    }

    @Override
    public String getItemType() {
        return "VACCINATION";
    }

    // Getters and Setters
    public String getVaccineName() { return vaccineName; }
    public void setVaccineName(String vaccineName) { this.vaccineName = vaccineName; }

    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }

    public LocalDate getDateAdministered() { return dateAdministered; }
    public void setDateAdministered(LocalDate dateAdministered) { this.dateAdministered = dateAdministered; }

    public LocalDate getNextDueDate() { return nextDueDate; }
    public void setNextDueDate(LocalDate nextDueDate) { this.nextDueDate = nextDueDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public double getStandardVaccineFee() { return standardVaccineFee; }
    public void setStandardVaccineFee(double fee) { this.standardVaccineFee = fee; }
}
