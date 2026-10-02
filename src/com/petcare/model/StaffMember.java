package com.petcare.model;

/**
 * Unit 2: Subclass extending User (Hierarchical Inheritance)
 */
public class StaffMember extends User {

    public StaffMember() {
        super();
        setRole(UserRole.STAFF);
    }

    public StaffMember(int id, String username, String password, String fullName, 
                       String email, String phone, String specialization, String status) {
        super(id, username, password, fullName, UserRole.STAFF, email, phone, specialization, status);
    }

    public StaffMember(String username, String password, String fullName, String email, String phone) {
        super(username, password, fullName, UserRole.STAFF, email, phone, "Clinic Operations & Care");
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
        return false;
    }

    @Override
    public boolean canPerformDatabaseBackup() {
        return false;
    }
}
