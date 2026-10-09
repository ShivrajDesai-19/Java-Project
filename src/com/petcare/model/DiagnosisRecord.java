package com.petcare.model;

import java.time.LocalDate;

/**
 * Unit 2: Concrete subclass extending MedicalRecord
 * Implements abstract methods and demonstrates 'super' keyword.
 */
public class DiagnosisRecord extends MedicalRecord {
    private String symptoms;
    private String diagnosis;
    private String treatmentPlan;
    private double temperatureC;
    private int pulseRate;

    public DiagnosisRecord() {
        super();
        this.recordType = "DIAGNOSIS";
        this.temperatureC = 38.5;
        this.pulseRate = 80;
    }

    public DiagnosisRecord(int id, int petId, int vetId, LocalDate visitDate,
                           String symptoms, String diagnosis, String treatmentPlan,
                           double temperatureC, int pulseRate) {
        super(id, petId, vetId, visitDate, "DIAGNOSIS");
        this.symptoms = symptoms;
        this.diagnosis = diagnosis;
        this.treatmentPlan = treatmentPlan;
        this.temperatureC = temperatureC;
        this.pulseRate = pulseRate;
    }

    @Override
    public String getRecordCategory() {
        return "Clinical Diagnosis";
    }

    @Override
    public String getSummaryDescription() {
        return "Diagnosis: " + (diagnosis != null ? diagnosis : "None") + 
               " | Symptoms: " + (symptoms != null ? symptoms : "None");
    }

    // Getters and Setters
    public String getSymptoms() { return symptoms; }
    public void setSymptoms(String symptoms) { this.symptoms = symptoms; }

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    public String getTreatmentPlan() { return treatmentPlan; }
    public void setTreatmentPlan(String treatmentPlan) { this.treatmentPlan = treatmentPlan; }

    public double getTemperatureC() { return temperatureC; }
    public void setTemperatureC(double temperatureC) { this.temperatureC = temperatureC; }

    public int getPulseRate() { return pulseRate; }
    public void setPulseRate(int pulseRate) { this.pulseRate = pulseRate; }
}
