package com.petcare.model;

/**
 * Unit 2: Subclass extending User (Hierarchical Inheritance)
 * Demonstrates 'super' constructor call and method overriding.
 */
public class Administrator extends User {

    public Administrator() {
        super();
        setRole(UserRole.ADMINISTRATOR);
    }

    public Administrator(int id, String username, String password, String fullName, 
                         String email, String phone, String specialization, String status) {
        super(id, username, password, fullName, UserRole.ADMINISTRATOR, email, phone, specialization, status);
    }

    public Administrator(String username, String password, String fullName, String email, String phone) {
        super(username, password, fullName, UserRole.ADMINISTRATOR, email, phone, "System Administration");
    }

    @Override
    public boolean canManageStaff() {
        return true;
    }

    @Override
    public boolean canDiagnoseAndPrescribe() {
        return true;
    }

    @Override
    public boolean canManageBilling() {
        return true;
    }

    @Override
    public boolean canPerformDatabaseBackup() {
        return true;
    }
}
