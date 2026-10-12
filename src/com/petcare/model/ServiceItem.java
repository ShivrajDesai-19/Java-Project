package com.petcare.model;

import java.time.LocalDate;

/**
 * Unit 2: Superclass for Grooming and Boarding services
 * Implements Billable and Searchable interfaces.
 */
public class ServiceItem implements Billable, Searchable {
    protected int id;
    protected int petId;
    protected String petName;
    protected String serviceType; // GROOMING, BOARDING
    protected LocalDate bookingDate;
    protected LocalDate startDate;
    protected String packageDetails;
    protected String specialInstructions;
    protected double cost;
    protected String status; // CONFIRMED, CHECKED_IN, COMPLETED, CANCELLED

    public ServiceItem() {
        this.bookingDate = LocalDate.now();
        this.startDate = LocalDate.now();
        this.status = "CONFIRMED";
    }

    public ServiceItem(int id, int petId, String serviceType, LocalDate bookingDate,
                       LocalDate startDate, String packageDetails, String specialInstructions,
                       double cost, String status) {
        this.id = id;
        this.petId = petId;
        this.serviceType = serviceType;
        this.bookingDate = (bookingDate != null) ? bookingDate : LocalDate.now();
        this.startDate = (startDate != null) ? startDate : LocalDate.now();
        this.packageDetails = packageDetails;
        this.specialInstructions = specialInstructions;
        this.cost = cost;
        this.status = (status != null) ? status : "CONFIRMED";
    }

    // Billable implementation
    @Override
    public String getItemDescription() {
        return serviceType + " - " + packageDetails;
    }

    @Override
    public double getUnitPrice() {
        return cost;
    }

    @Override
    public int getQuantity() {
        return 1;
    }

    @Override
    public double calculateTotalPrice() {
        return cost;
    }

    @Override
    public String getItemType() {
        return serviceType;
    }

    // Searchable implementation
    @Override
    public boolean matchesSearch(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return true;
        }
        String lower = keyword.toLowerCase();
        return (petName != null && petName.toLowerCase().contains(lower)) ||
               (serviceType != null && serviceType.toLowerCase().contains(lower)) ||
               (packageDetails != null && packageDetails.toLowerCase().contains(lower)) ||
               (status != null && status.toLowerCase().contains(lower));
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPetId() { return petId; }
    public void setPetId(int petId) { this.petId = petId; }

    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }

    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public LocalDate getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDate bookingDate) { this.bookingDate = bookingDate; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public String getPackageDetails() { return packageDetails; }
    public void setPackageDetails(String packageDetails) { this.packageDetails = packageDetails; }

    public String getSpecialInstructions() { return specialInstructions; }
    public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }

    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
