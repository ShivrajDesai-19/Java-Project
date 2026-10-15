package com.petcare;

import com.petcare.model.Administrator;
import com.petcare.ui.LoginFrame;
import com.petcare.ui.MainDashboardFrame;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

public class RenderTest {
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            try {
                // 1. Render LoginFrame
                LoginFrame login = new LoginFrame();
                login.pack();
                login.setSize(920, 640);
                login.doLayout();
                renderFrame(login, "login_preview.png");
                login.dispose();

                // 2. Render DashboardFrame with Admin
                Administrator admin = new Administrator(1, "admin", "admin123", "Dr. Ramesh Sharma", "admin@petcare.com", "9876543210", "Chief Vet", "ACTIVE");
                MainDashboardFrame dash = new MainDashboardFrame(admin);
                dash.pack();
                dash.setSize(1240, 780);
                dash.doLayout();
                renderFrame(dash, "dash_preview.png");
                dash.dispose();

                System.out.println("RENDER_SUCCESS");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        System.exit(0);
    }

    private static void renderFrame(JFrame frame, String fileName) throws Exception {
        frame.getContentPane().setSize(frame.getWidth(), frame.getHeight());
        frame.getContentPane().validate();
        frame.getContentPane().doLayout();
        BufferedImage img = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        g2.setColor(java.awt.Color.WHITE);
        g2.fillRect(0, 0, frame.getWidth(), frame.getHeight());
        frame.getContentPane().paint(g2);
        g2.dispose();
        ImageIO.write(img, "png", new File(fileName));
    }
}
