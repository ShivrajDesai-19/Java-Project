package com.petcare.service;

import com.petcare.dao.AppointmentDAO;
import com.petcare.exception.DatabaseException;
import com.petcare.exception.InvalidInputException;
import com.petcare.model.Appointment;
import com.petcare.model.AppointmentStatus;
import com.petcare.util.InputValidator;

import java.time.LocalDate;
import java.util.List;

/**
 * Service managing Appointment booking and scheduling workflows.
 */
public class AppointmentService {

    private final AppointmentDAO appointmentDAO;

    public AppointmentService() {
        this.appointmentDAO = new AppointmentDAO();
    }

    public void scheduleAppointment(Appointment apt) throws InvalidInputException, DatabaseException {
        if (apt.getPetId() <= 0) {
            throw new InvalidInputException("Please select a valid registered pet.");
        }
        if (apt.getVetId() <= 0) {
            throw new InvalidInputException("Please assign a veterinarian doctor.");
        }
        if (apt.getAppointmentDate() == null) {
            throw new InvalidInputException("Please specify an appointment date.");
        }
        InputValidator.validateNotEmpty(apt.getAppointmentTime(), "Appointment Time");
        InputValidator.validateNotEmpty(apt.getPurpose(), "Appointment Purpose");

        appointmentDAO.insert(apt);
    }

    public void updateAppointment(Appointment apt) throws InvalidInputException, DatabaseException {
        InputValidator.validateNotEmpty(apt.getAppointmentTime(), "Appointment Time");
        InputValidator.validateNotEmpty(apt.getPurpose(), "Appointment Purpose");
        appointmentDAO.update(apt);
    }

    public void updateStatus(int appointmentId, AppointmentStatus status) throws DatabaseException {
        appointmentDAO.updateStatus(appointmentId, status);
    }

    public void cancelAppointment(int appointmentId) throws DatabaseException {
        appointmentDAO.updateStatus(appointmentId, AppointmentStatus.CANCELLED);
    }

    public void deleteAppointment(int appointmentId) throws DatabaseException {
        appointmentDAO.delete(appointmentId);
    }

    public List<Appointment> getAllAppointments() throws DatabaseException {
        return appointmentDAO.findAll();
    }

    public List<Appointment> getAppointmentsByDate(LocalDate date) throws DatabaseException {
        return appointmentDAO.findByDate(date);
    }

    public int getScheduledCountToday() throws DatabaseException {
        return appointmentDAO.countUpcomingToday();
    }
}
