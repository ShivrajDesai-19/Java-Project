package com.petcare.model;

import java.time.LocalDate;

/**
 * Unit 1: Encapsulated Java Bean with constructors and 'this' keyword
 * Unit 2: Implements Searchable interface
 */
public class PetOwner implements Searchable {
    private int id;
    private String fullName;
    private String phone;
    private String email;
    private String address;
    private String emergencyContact;
    private LocalDate registeredDate;

    // Unit 1: Default Constructor
    public PetOwner() {
        this.registeredDate = LocalDate.now();
    }

    // Unit 1: Parameterized Constructor
    public PetOwner(int id, String fullName, String phone, String email, 
                    String address, String emergencyContact, LocalDate registeredDate) {
        this.id = id;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.emergencyContact = emergencyContact;
        this.registeredDate = (registeredDate != null) ? registeredDate : LocalDate.now();
    }

    // Constructor Overloading (for creating new owner records)
    public PetOwner(String fullName, String phone, String email, String address, String emergencyContact) {
        this(0, fullName, phone, email, address, emergencyContact, LocalDate.now());
    }

    // Unit 2: Implementation of interface method
    @Override
    public boolean matchesSearch(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return true;
        }
        String lower = keyword.toLowerCase();
        return (fullName != null && fullName.toLowerCase().contains(lower)) ||
               (phone != null && phone.contains(lower)) ||
               (email != null && email.toLowerCase().contains(lower)) ||
               (address != null && address.toLowerCase().contains(lower));
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }

    public LocalDate getRegisteredDate() { return registeredDate; }
    public void setRegisteredDate(LocalDate registeredDate) { this.registeredDate = registeredDate; }

    @Override
    public String toString() {
        return fullName + " (" + phone + ")";
    }
}
