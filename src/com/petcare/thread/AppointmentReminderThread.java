package com.petcare.thread;

import com.petcare.dao.AppointmentDAO;
import com.petcare.dao.MedicalRecordDAO;
import com.petcare.model.Appointment;
import com.petcare.model.VaccinationRecord;

import java.time.LocalDate;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Unit 4 Syllabus Demonstration:
 * Creating thread by implementing Runnable interface:
 * class AppointmentReminderThread implements Runnable
 */
public class AppointmentReminderThread implements Runnable {

    private final AppointmentDAO appointmentDAO;
    private final MedicalRecordDAO medicalRecordDAO;
    private final BiConsumer<List<Appointment>, List<VaccinationRecord>> reminderCallback;
    private volatile boolean running = true;
    private final long intervalMillis;

    public AppointmentReminderThread(BiConsumer<List<Appointment>, List<VaccinationRecord>> reminderCallback, long intervalMillis) {
        this.appointmentDAO = new AppointmentDAO();
        this.medicalRecordDAO = new MedicalRecordDAO();
        this.reminderCallback = reminderCallback;
        this.intervalMillis = intervalMillis;
    }

    @Override
    public void run() {
        System.out.println("[AppointmentReminderThread] Runnable started running on thread: " + 
                           Thread.currentThread().getName());

        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                List<Appointment> todayApts = appointmentDAO.findByDate(LocalDate.now());
                List<VaccinationRecord> dueVaccs = medicalRecordDAO.findUpcomingDueVaccinations();

                if (reminderCallback != null) {
                    reminderCallback.accept(todayApts, dueVaccs);
                }

                // Sleep for configured interval
                Thread.sleep(intervalMillis);

            } catch (InterruptedException e) {
                System.out.println("[AppointmentReminderThread] Thread interrupted. Exiting loop.");
                break;
            } catch (Exception e) {
                System.err.println("[AppointmentReminderThread] Check error: " + e.getMessage());
                try {
                    Thread.sleep(15000);
                } catch (InterruptedException ie) {
                    break;
                }
            }
        }
    }

    public void stopWorker() {
        this.running = false;
    }
}
