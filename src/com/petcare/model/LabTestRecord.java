package com.petcare.model;

import java.time.LocalDate;

/**
 * Unit 2 Syllabus Demonstration:
 * Extends MedicalRecord and implements Billable interface.
 */
public class LabTestRecord extends MedicalRecord implements Billable {
    private String testName;
    private String testCategory; // Hematology, Biochemistry, Radiology, Urinalysis
    private String results;
    private String normalRange;
    private String status; // REQUESTED, IN_ANALYSIS, COMPLETED
    private String conductedBy;
    private double standardTestFee = 450.0;

    public LabTestRecord() {
        super();
        this.recordType = "LAB_TEST";
        this.status = "COMPLETED";
    }

    public LabTestRecord(int id, int petId, int vetId, LocalDate testDate,
                         String testName, String testCategory, String results,
                         String normalRange, String status, String conductedBy) {
        super(id, petId, vetId, testDate, "LAB_TEST");
        this.testName = testName;
        this.testCategory = testCategory;
        this.results = results;
        this.normalRange = normalRange;
        this.status = status;
        this.conductedBy = conductedBy;
    }

    @Override
    public String getRecordCategory() {
        return "Laboratory Diagnostics";
    }

    @Override
    public String getSummaryDescription() {
        return "Lab Test: " + testName + " [" + testCategory + "] - " + status;
    }

    // Billable methods
    @Override
    public String getItemDescription() {
        return "Lab Diagnostic: " + testName + " (" + testCategory + ")";
    }

    @Override
    public double getUnitPrice() {
        return standardTestFee;
    }

    @Override
    public int getQuantity() {
        return 1;
    }

    @Override
    public double calculateTotalPrice() {
        return standardTestFee;
    }

    @Override
    public String getItemType() {
        return "LAB_TEST";
    }

    // Getters and Setters
    public String getTestName() { return testName; }
    public void setTestName(String testName) { this.testName = testName; }

    public String getTestCategory() { return testCategory; }
    public void setTestCategory(String testCategory) { this.testCategory = testCategory; }

    public String getResults() { return results; }
    public void setResults(String results) { this.results = results; }

    public String getNormalRange() { return normalRange; }
    public void setNormalRange(String normalRange) { this.normalRange = normalRange; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getConductedBy() { return conductedBy; }
    public void setConductedBy(String conductedBy) { this.conductedBy = conductedBy; }

    public double getStandardTestFee() { return standardTestFee; }
    public void setStandardTestFee(double fee) { this.standardTestFee = fee; }
}
