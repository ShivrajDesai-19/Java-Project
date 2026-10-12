package com.petcare.model;

import java.time.LocalDate;

/**
 * Unit 2: Subclass extending ServiceItem
 */
public class GroomingBooking extends ServiceItem {
    private String groomerStation;

    public GroomingBooking() {
        super();
        this.serviceType = "GROOMING";
    }

    public GroomingBooking(int id, int petId, LocalDate bookingDate, LocalDate serviceDate,
                           String station, String packageDetails, String specialInstructions,
                           double cost, String status) {
        super(id, petId, "GROOMING", bookingDate, serviceDate, packageDetails, specialInstructions, cost, status);
        this.groomerStation = station;
    }

    public String getGroomerStation() {
        return groomerStation;
    }

    public void setGroomerStation(String groomerStation) {
        this.groomerStation = groomerStation;
    }

    @Override
    public String getItemDescription() {
        return "Grooming Service: " + packageDetails + (groomerStation != null ? " [Station: " + groomerStation + "]" : "");
    }
}
