package com.petcare.thread;

import com.petcare.dao.MedicineDAO;
import com.petcare.model.Medicine;

import java.util.List;
import java.util.function.Consumer;

/**
 * Unit 4 Syllabus Demonstration:
 * (1) Creating thread by extending Thread class:
 *     class StockAlertThread extends Thread
 * (2) Setting thread priority: setPriority(Thread.NORM_PRIORITY)
 * (3) Participating in ThreadGroup
 * (4) Thread lifecycle methods: start(), sleep(), interrupt(), isAlive()
 */
public class StockAlertThread extends Thread {

    private final MedicineDAO medicineDAO;
    private final Consumer<List<Medicine>> alertCallback;
    private volatile boolean running = true;
    private final long intervalMillis;

    public StockAlertThread(ThreadGroup group, String name, Consumer<List<Medicine>> alertCallback, long intervalMillis) {
        // Calling super(group, name) demonstrates ThreadGroup participation (Unit 4, slide 44-48)
        super(group, name);
        this.medicineDAO = new MedicineDAO();
        this.alertCallback = alertCallback;
        this.intervalMillis = intervalMillis;

        // Unit 4 Syllabus: Setting Thread Priority
        this.setPriority(Thread.NORM_PRIORITY);
        this.setDaemon(true); // Daemon thread runs quietly in background
    }

    @Override
    public void run() {
        System.out.println("[StockAlertThread] Started running in thread group: " + 
                           getThreadGroup().getName() + " with priority: " + getPriority());

        while (running && !isInterrupted()) {
            try {
                List<Medicine> lowStockItems = medicineDAO.findLowStockMedicines();
                if (alertCallback != null && !lowStockItems.isEmpty()) {
                    alertCallback.accept(lowStockItems);
                }

                // Unit 4: sleep() causes thread to temporarily cease execution
                Thread.sleep(intervalMillis);

            } catch (InterruptedException e) {
                System.out.println("[StockAlertThread] Interrupted. Exiting run loop.");
                break;
            } catch (Exception e) {
                System.err.println("[StockAlertThread] Check cycle warning: " + e.getMessage());
                try {
                    Thread.sleep(10000); // Wait before retrying after DB error
                } catch (InterruptedException ie) {
                    break;
                }
            }
        }
        System.out.println("[StockAlertThread] Terminated safely.");
    }

    public void stopMonitoring() {
        this.running = false;
        this.interrupt();
    }
}
