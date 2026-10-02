package com.petcare.model;

import java.sql.Timestamp;

/**
 * Unit 1 & Unit 2 Syllabus Concepts:
 * - Encapsulation (private data members, public accessors)
 * - Constructor Overloading (default and parameterized)
 * - Superclass for Hierarchical Inheritance
 */
public class User {
    // Encapsulated instance variables
    private int id;
    private String username;
    private String password;
    private String fullName;
    private UserRole role;
    private String email;
    private String phone;
    private String specialization;
    private String status;
    private Timestamp createdAt;

    // Unit 1: Default Constructor
    public User() {
        this.status = "ACTIVE";
    }

    // Unit 1: Parameterized Constructor demonstrating 'this' keyword
    public User(int id, String username, String password, String fullName, UserRole role, 
                String email, String phone, String specialization, String status) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
        this.email = email;
        this.phone = phone;
        this.specialization = specialization;
        this.status = status;
    }

    // Constructor Overloading (without id for new records)
    public User(String username, String password, String fullName, UserRole role, 
                String email, String phone, String specialization) {
        this(0, username, password, fullName, role, email, phone, specialization, "ACTIVE");
    }

    // Role capability checks - overridden in specific subclasses
    public boolean canManageStaff() {
        return this.role == UserRole.ADMINISTRATOR;
    }

    public boolean canDiagnoseAndPrescribe() {
        return this.role == UserRole.ADMINISTRATOR || this.role == UserRole.VETERINARIAN;
    }

    public boolean canManageBilling() {
        return this.role == UserRole.ADMINISTRATOR || this.role == UserRole.RECEPTIONIST;
    }

    public boolean canManageInventory() {
        return true; // All internal roles can view/manage inventory
    }

    public boolean canPerformDatabaseBackup() {
        return this.role == UserRole.ADMINISTRATOR;
    }

    // Getters and Setters (Encapsulation)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return fullName + " (" + (role != null ? role.getDisplayName() : "User") + ")";
    }
}
