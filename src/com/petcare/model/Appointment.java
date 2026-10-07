package com.petcare.model;

import java.time.LocalDate;

/**
 * Unit 1 & Unit 2: Encapsulated Model and Searchable
 */
public class Appointment implements Searchable {
    private int id;
    private int petId;
    private String petName;
    private String ownerName;
    private int vetId;
    private String vetName;
    private LocalDate appointmentDate;
    private String appointmentTime;
    private String purpose;
    private AppointmentStatus status;
    private String notes;

    public Appointment() {
        this.appointmentDate = LocalDate.now();
        this.status = AppointmentStatus.SCHEDULED;
    }

    public Appointment(int id, int petId, int vetId, LocalDate appointmentDate, 
                       String appointmentTime, String purpose, AppointmentStatus status, String notes) {
        this.id = id;
        this.petId = petId;
        this.vetId = vetId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.purpose = purpose;
        this.status = (status != null) ? status : AppointmentStatus.SCHEDULED;
        this.notes = notes;
    }

    // Constructor Overloading
    public Appointment(int petId, int vetId, LocalDate appointmentDate, 
                       String appointmentTime, String purpose, String notes) {
        this(0, petId, vetId, appointmentDate, appointmentTime, purpose, AppointmentStatus.SCHEDULED, notes);
    }

    @Override
    public boolean matchesSearch(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return true;
        }
        String lower = keyword.toLowerCase();
        return (petName != null && petName.toLowerCase().contains(lower)) ||
               (ownerName != null && ownerName.toLowerCase().contains(lower)) ||
               (vetName != null && vetName.toLowerCase().contains(lower)) ||
               (purpose != null && purpose.toLowerCase().contains(lower)) ||
               (status != null && status.name().toLowerCase().contains(lower));
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPetId() { return petId; }
    public void setPetId(int petId) { this.petId = petId; }

    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public int getVetId() { return vetId; }
    public void setVetId(int vetId) { this.vetId = vetId; }

    public String getVetName() { return vetName; }
    public void setVetName(String vetName) { this.vetName = vetName; }

    public LocalDate getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; }

    public String getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(String appointmentTime) { this.appointmentTime = appointmentTime; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public AppointmentStatus getStatus() { return status; }
    public void setStatus(AppointmentStatus status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
