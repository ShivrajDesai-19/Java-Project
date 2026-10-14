package com.petcare.thread;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Unit 4: Live clock thread updating status bar every second.
 * Extends Thread class and uses sleep() method.
 */
public class LiveClockThread extends Thread {

    private final JLabel clockLabel;
    private volatile boolean active = true;
    private final SimpleDateFormat formatter = new SimpleDateFormat("EEEE, dd MMM yyyy  |  hh:mm:ss a");

    public LiveClockThread(ThreadGroup group, JLabel clockLabel) {
        super(group, "LiveClockThread");
        this.clockLabel = clockLabel;
        this.setDaemon(true);
        this.setPriority(Thread.MIN_PRIORITY); // Lower priority so it doesn't burden CPU
    }

    @Override
    public void run() {
        while (active && !isInterrupted()) {
            final String currentFormatted = formatter.format(new Date());
            SwingUtilities.invokeLater(() -> {
                if (clockLabel != null) {
                    clockLabel.setText(currentFormatted);
                }
            });

            try {
                Thread.sleep(1000); // 1-second interval
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public void stopClock() {
        this.active = false;
        this.interrupt();
    }
}
