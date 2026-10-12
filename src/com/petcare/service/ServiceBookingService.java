package com.petcare.service;

import com.petcare.dao.ServiceBookingDAO;
import com.petcare.exception.DatabaseException;
import com.petcare.exception.InvalidInputException;
import com.petcare.model.ServiceItem;
import com.petcare.util.InputValidator;

import java.util.List;

/**
 * Service managing Grooming and Boarding reservations
 */
public class ServiceBookingService {

    private final ServiceBookingDAO bookingDAO;

    public ServiceBookingService() {
        this.bookingDAO = new ServiceBookingDAO();
    }

    public void createBooking(ServiceItem booking) throws InvalidInputException, DatabaseException {
        if (booking.getPetId() <= 0) {
            throw new InvalidInputException("Please select a registered pet for service.");
        }
        InputValidator.validateNotEmpty(booking.getPackageDetails(), "Package Details");
        if (booking.calculateTotalPrice() <= 0) {
            throw new InvalidInputException("Service cost must be greater than zero.");
        }
        bookingDAO.insert(booking);
    }

    public List<ServiceItem> getAllBookings() throws DatabaseException {
        return bookingDAO.findAll();
    }

    public void updateStatus(int bookingId, String status) throws DatabaseException {
        bookingDAO.updateStatus(bookingId, status);
    }
}
