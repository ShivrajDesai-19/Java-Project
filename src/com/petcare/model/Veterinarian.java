package com.petcare.model;

/**
 * Unit 2: Subclass extending User (Hierarchical Inheritance)
 */
public class Veterinarian extends User {

    public Veterinarian() {
        super();
        setRole(UserRole.VETERINARIAN);
    }

    public Veterinarian(int id, String username, String password, String fullName, 
                        String email, String phone, String specialization, String status) {
        super(id, username, password, fullName, UserRole.VETERINARIAN, email, phone, specialization, status);
    }

    public Veterinarian(String username, String password, String fullName, String email, String phone, String specialization) {
        super(username, password, fullName, UserRole.VETERINARIAN, email, phone, specialization);
    }

    @Override
    public boolean canManageStaff() {
        return false;
    }

    @Override
    public boolean canDiagnoseAndPrescribe() {
        return true;
    }

    @Override
    public boolean canManageBilling() {
        return false;
    }

    @Override
    public boolean canPerformDatabaseBackup() {
        return false;
    }
}
