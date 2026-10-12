package com.petcare.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Unit 2: Subclass extending ServiceItem
 * Overrides calculateTotalPrice() dynamically based on days stayed.
 */
public class BoardingBooking extends ServiceItem {
    private LocalDate endDate;
    private String cageOrRoomNo;
    private double dailyRate;

    public BoardingBooking() {
        super();
        this.serviceType = "BOARDING";
        this.endDate = LocalDate.now().plusDays(1);
        this.dailyRate = 800.0;
    }

    public BoardingBooking(int id, int petId, LocalDate bookingDate, LocalDate startDate,
                           LocalDate endDate, String cageOrRoomNo, String packageDetails,
                           String specialInstructions, double dailyRate, String status) {
        super(id, petId, "BOARDING", bookingDate, startDate, packageDetails, specialInstructions, 0.0, status);
        this.endDate = endDate;
        this.cageOrRoomNo = cageOrRoomNo;
        this.dailyRate = dailyRate;
        this.cost = calculateTotalPrice();
    }

    public long getNumberOfDays() {
        if (startDate == null || endDate == null) return 1;
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        return days <= 0 ? 1 : days;
    }

    @Override
    public int getQuantity() {
        return (int) getNumberOfDays();
    }

    @Override
    public double getUnitPrice() {
        return dailyRate;
    }

    @Override
    public double calculateTotalPrice() {
        return dailyRate * getNumberOfDays();
    }

    @Override
    public String getItemDescription() {
        return "Boarding Room/Cage: " + cageOrRoomNo + " (" + getNumberOfDays() + " days @ ₹" + dailyRate + "/day)";
    }

    // Getters and Setters
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { 
        this.endDate = endDate; 
        this.cost = calculateTotalPrice();
    }

    public String getCageOrRoomNo() { return cageOrRoomNo; }
    public void setCageOrRoomNo(String cageOrRoomNo) { this.cageOrRoomNo = cageOrRoomNo; }

    public double getDailyRate() { return dailyRate; }
    public void setDailyRate(double dailyRate) { 
        this.dailyRate = dailyRate; 
        this.cost = calculateTotalPrice();
    }
}
