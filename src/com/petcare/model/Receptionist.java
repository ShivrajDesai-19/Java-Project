package com.petcare.model;

/**
 * Unit 2: Subclass extending User (Hierarchical Inheritance)
 */
public class Receptionist extends User {

    public Receptionist() {
        super();
        setRole(UserRole.RECEPTIONIST);
    }

    public Receptionist(int id, String username, String password, String fullName, 
                        String email, String phone, String specialization, String status) {
        super(id, username, password, fullName, UserRole.RECEPTIONIST, email, phone, specialization, status);
    }

    public Receptionist(String username, String password, String fullName, String email, String phone) {
        super(username, password, fullName, UserRole.RECEPTIONIST, email, phone, "Front Desk");
    }

    @Override
    public boolean canManageStaff() {
        return false;
    }

    @Override
    public boolean canDiagnoseAndPrescribe() {
        return false;
    }

    @Override
    public boolean canManageBilling() {
        return true;
    }

    @Override
    public boolean canPerformDatabaseBackup() {
        return false;
    }
}
