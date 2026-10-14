package com.petcare.thread;

import com.petcare.model.Appointment;
import com.petcare.model.Medicine;
import com.petcare.model.VaccinationRecord;

import javax.swing.JLabel;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Unit 4 Syllabus Demonstration: Grouping Threads
 * "Every Java thread is a member of a thread group. Thread groups provide a mechanism
 * for collecting multiple threads into a single object and manipulating those threads all at once.
 * Java thread groups are implemented by the ThreadGroup class in java.lang."
 */
public class BackgroundServiceManager {

    private final ThreadGroup petCareThreadGroup;
    private StockAlertThread stockAlertThread;
    private LiveClockThread liveClockThread;
    private Thread appointmentWorkerThread;
    private AppointmentReminderThread appointmentReminderRunnable;

    public BackgroundServiceManager() {
        // Unit 4: Creating our own ThreadGroup
        this.petCareThreadGroup = new ThreadGroup("SmartPetCareServicesGroup");
    }

    public void startServices(JLabel statusClockLabel, 
                              Consumer<List<Medicine>> stockCallback,
                              BiConsumer<List<Appointment>, List<VaccinationRecord>> reminderCallback) {
        
        // 1. Start Live Clock Thread (Extends Thread)
        if (statusClockLabel != null) {
            liveClockThread = new LiveClockThread(petCareThreadGroup, statusClockLabel);
            liveClockThread.start();
        }

        // 2. Start Stock Alert Thread (Extends Thread)
        stockAlertThread = new StockAlertThread(petCareThreadGroup, "StockAlertDaemon", stockCallback, 20000);
        stockAlertThread.start();

        // 3. Start Appointment Reminder Thread (Implements Runnable)
        appointmentReminderRunnable = new AppointmentReminderThread(reminderCallback, 30000);
        appointmentWorkerThread = new Thread(petCareThreadGroup, appointmentReminderRunnable, "AptReminderWorker");
        appointmentWorkerThread.setDaemon(true);
        appointmentWorkerThread.setPriority(Thread.NORM_PRIORITY);
        appointmentWorkerThread.start();

        // Print thread group active statistics (Unit 4 slide 45)
        System.out.println("[BackgroundServiceManager] ThreadGroup: " + petCareThreadGroup.getName());
        System.out.println("[BackgroundServiceManager] Active threads in group: " + petCareThreadGroup.activeCount());
    }

    public void shutdownAll() {
        System.out.println("[BackgroundServiceManager] Shutting down all grouped services...");
        if (stockAlertThread != null) {
            stockAlertThread.stopMonitoring();
        }
        if (liveClockThread != null) {
            liveClockThread.stopClock();
        }
        if (appointmentReminderRunnable != null) {
            appointmentReminderRunnable.stopWorker();
        }
        if (appointmentWorkerThread != null) {
            appointmentWorkerThread.interrupt();
        }
        // Unit 4: ThreadGroup active count inspect
        System.out.println("[BackgroundServiceManager] Remaining active threads: " + petCareThreadGroup.activeCount());
    }

    public ThreadGroup getThreadGroup() {
        return petCareThreadGroup;
    }
}
