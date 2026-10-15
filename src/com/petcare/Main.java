package com.petcare;

import com.petcare.ui.LoginFrame;
import com.petcare.util.DBConnection;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Unit 1 & Unit 2 Syllabus Demonstration:
 * - Java Program Structure: public class, main(String[] args)
 * - Command Line Arguments: args.length, iterating args (Unit 2, slide 37-38)
 * - Static Initializer Block & Static method calls
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("==================================================================");
        System.out.println("     SMART PET CARE & VETERINARY CLINIC MANAGEMENT SYSTEM         ");
        System.out.println("          BCA Semester 3 - Java Programming Final Project         ");
        System.out.println("==================================================================");

        // Unit 2 Syllabus Demonstration: Processing Command Line Arguments
        if (args != null && args.length > 0) {
            System.out.println("[Main] Number of Command-Line Arguments = " + args.length);
            for (int i = 0; i < args.length; i++) {
                System.out.println("[Main] Arg " + (i + 1) + ": " + args[i]);
            }
        }

        // Configure native System Look & Feel for crisp fonts, correct button padding, and no clipping
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception ignored) {}
        }

        // Attempt background initial connection check
        new Thread(() -> {
            try {
                DBConnection.initializeDatabase();
                System.out.println("[Main] Database connection & schema verified successfully.");
            } catch (Exception e) {
                System.out.println("[Main] Database connection notice: " + e.getMessage());
            }
        }, "DBInitThread").start();

        // Launch UI on Event Dispatch Thread (Swing best practice)
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
