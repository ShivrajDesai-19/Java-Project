package com.petcare.ui;

import com.petcare.model.User;
import com.petcare.model.UserRole;
import com.petcare.service.AuthService;
import com.petcare.ui.dialogs.DBConfigDialog;
import com.petcare.util.DBConnection;
import com.petcare.util.UIStyleUtil;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

/**
 * Modern Login Screen with Role-based Authentication & Viva Demo Quick-Fill Shortcuts
 */
public class LoginFrame extends JFrame {

    private final AuthService authService = AuthService.getInstance();

    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JLabel statusLabel;

    public LoginFrame() {
        setTitle("Smart Pet Care & Veterinary Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(920, 640);
        setMinimumSize(new Dimension(880, 600));
        setLocationRelativeTo(null);
        setResizable(true);
        setLayout(new BorderLayout());

        // Left Branding Banner
        JPanel leftBrandPanel = new JPanel();
        leftBrandPanel.setBackground(UIStyleUtil.COLOR_SIDEBAR_BG);
        leftBrandPanel.setPreferredSize(new Dimension(340, 640));
        leftBrandPanel.setLayout(new BoxLayout(leftBrandPanel, BoxLayout.Y_AXIS));
        leftBrandPanel.setBorder(BorderFactory.createEmptyBorder(45, 25, 30, 25));

        JLabel logoIcon = new JLabel("+");
        logoIcon.setFont(new Font("Segoe UI", Font.BOLD, 54));
        logoIcon.setForeground(UIStyleUtil.COLOR_SECONDARY);
        logoIcon.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        JLabel brandTitle = new JLabel("Smart Pet Care");
        brandTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        brandTitle.setForeground(Color.WHITE);
        brandTitle.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        JLabel brandSub = new JLabel("Veterinary Clinic System");
        brandSub.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        brandSub.setForeground(new Color(175, 195, 215));
        brandSub.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        JLabel feature1 = new JLabel("• Complete Patient & Owner Records");
        feature1.setFont(UIStyleUtil.FONT_BODY);
        feature1.setForeground(new Color(200, 220, 240));
        feature1.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        JLabel feature2 = new JLabel("• Appointments & Vaccine Trackers");
        feature2.setFont(UIStyleUtil.FONT_BODY);
        feature2.setForeground(new Color(200, 220, 240));
        feature2.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        JLabel feature3 = new JLabel("• Pharmacy, Grooming & Billing POS");
        feature3.setFont(UIStyleUtil.FONT_BODY);
        feature3.setForeground(new Color(200, 220, 240));
        feature3.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        JLabel syllabusBadge = new JLabel("Marwadi University - BCA Sem 3");
        syllabusBadge.setFont(UIStyleUtil.FONT_SMALL);
        syllabusBadge.setForeground(new Color(130, 160, 190));
        syllabusBadge.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        leftBrandPanel.add(logoIcon);
        leftBrandPanel.add(Box.createVerticalStrut(6));
        leftBrandPanel.add(brandTitle);
        leftBrandPanel.add(Box.createVerticalStrut(4));
        leftBrandPanel.add(brandSub);
        leftBrandPanel.add(Box.createVerticalStrut(36));
        leftBrandPanel.add(feature1);
        leftBrandPanel.add(Box.createVerticalStrut(10));
        leftBrandPanel.add(feature2);
        leftBrandPanel.add(Box.createVerticalStrut(10));
        leftBrandPanel.add(feature3);
        leftBrandPanel.add(Box.createVerticalGlue());
        leftBrandPanel.add(syllabusBadge);

        add(leftBrandPanel, BorderLayout.WEST);

        // Right Login Form Panel (Scrollable to prevent any vertical squishing)
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(UIStyleUtil.COLOR_BG_CARD);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 25, 40));

        JLabel loginHeader = new JLabel("Sign In to Your Account");
        loginHeader.setFont(UIStyleUtil.FONT_HEADER_LARGE);
        loginHeader.setForeground(UIStyleUtil.COLOR_TEXT_DARK);
        loginHeader.setAlignmentX(JLabel.LEFT_ALIGNMENT);

        JLabel subHeader = new JLabel("Enter your credentials or select a quick role below.");
        subHeader.setFont(UIStyleUtil.FONT_BODY);
        subHeader.setForeground(UIStyleUtil.COLOR_TEXT_MUTED);
        subHeader.setAlignmentX(JLabel.LEFT_ALIGNMENT);

        rightPanel.add(loginHeader);
        rightPanel.add(Box.createVerticalStrut(4));
        rightPanel.add(subHeader);
        rightPanel.add(Box.createVerticalStrut(20));

        // Form Fields in GridBagLayout with generous insets
        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setOpaque(false);
        formCard.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 4, 6, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        usernameField = new JTextField("admin", 18);
        usernameField.setPreferredSize(new Dimension(240, 36));
        usernameField.setFont(UIStyleUtil.FONT_BODY);

        passwordField = new JPasswordField("admin123", 18);
        passwordField.setPreferredSize(new Dimension(240, 36));
        passwordField.setFont(UIStyleUtil.FONT_BODY);

        JButton loginBtn = UIStyleUtil.createPrimaryButton("Login to Dashboard");
        loginBtn.addActionListener(e -> attemptLogin());
        passwordField.addActionListener(e -> attemptLogin());

        statusLabel = new JLabel(" ");
        statusLabel.setFont(UIStyleUtil.FONT_SMALL);
        statusLabel.setForeground(UIStyleUtil.COLOR_DANGER);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3; formCard.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7; formCard.add(usernameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3; formCard.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7; formCard.add(passwordField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; formCard.add(Box.createVerticalStrut(6), gbc);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; formCard.add(loginBtn, gbc);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; formCard.add(statusLabel, gbc);

        rightPanel.add(formCard);
        rightPanel.add(Box.createVerticalStrut(15));

        // Quick Demo Accounts Panel (Great for Viva Demonstrations)
        JPanel demoBox = new JPanel(new GridLayout(2, 2, 10, 10));
        demoBox.setOpaque(false);
        demoBox.setBorder(BorderFactory.createTitledBorder("Quick Demo Logins (Viva Evaluation)"));
        demoBox.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        demoBox.setMaximumSize(new Dimension(420, 110));

        JButton demoAdmin = new JButton("Administrator");
        JButton demoVet = new JButton("Veterinarian");
        JButton demoRec = new JButton("Receptionist");
        JButton demoStaff = new JButton("Staff Member");

        demoAdmin.setFocusPainted(false);
        demoVet.setFocusPainted(false);
        demoRec.setFocusPainted(false);
        demoStaff.setFocusPainted(false);

        demoAdmin.addActionListener(e -> fillCredentials("admin", "admin123"));
        demoVet.addActionListener(e -> fillCredentials("dr.priya", "vet123"));
        demoRec.addActionListener(e -> fillCredentials("receptionist", "rec123"));
        demoStaff.addActionListener(e -> fillCredentials("staff", "staff123"));

        demoBox.add(demoAdmin);
        demoBox.add(demoVet);
        demoBox.add(demoRec);
        demoBox.add(demoStaff);

        rightPanel.add(demoBox);
        rightPanel.add(Box.createVerticalStrut(12));

        JButton dbBtn = new JButton("Configure Database (XAMPP MySQL)");
        dbBtn.setBorderPainted(false);
        dbBtn.setContentAreaFilled(false);
        dbBtn.setForeground(UIStyleUtil.COLOR_PRIMARY);
        dbBtn.setFont(UIStyleUtil.FONT_BODY_BOLD);
        dbBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        dbBtn.setAlignmentX(JButton.LEFT_ALIGNMENT);
        dbBtn.addActionListener(e -> new DBConfigDialog(this).setVisible(true));
        rightPanel.add(dbBtn);

        JScrollPane rightScroll = new JScrollPane(rightPanel);
        rightScroll.setBorder(null);
        rightScroll.getVerticalScrollBar().setUnitIncrement(12);

        add(rightScroll, BorderLayout.CENTER);
    }

    private void fillCredentials(String user, String pass) {
        usernameField.setText(user);
        passwordField.setText(pass);
        statusLabel.setText("Selected " + user + ". Click Login to enter.");
        statusLabel.setForeground(UIStyleUtil.COLOR_SUCCESS);
    }

    private void attemptLogin() {
        String u = usernameField.getText().trim();
        String p = new String(passwordField.getPassword()).trim();

        statusLabel.setText("Authenticating...");
        statusLabel.setForeground(UIStyleUtil.COLOR_PRIMARY);

        try {
            // First ensure DB is initialized
            try {
                DBConnection.initializeDatabase();
            } catch (Exception ignored) {}

            User user = authService.login(u, p);
            dispose();

            SwingUtilities.invokeLater(() -> {
                MainDashboardFrame dashboard = new MainDashboardFrame(user);
                dashboard.setVisible(true);
            });

        } catch (Exception ex) {
            statusLabel.setText(ex.getMessage());
            statusLabel.setForeground(UIStyleUtil.COLOR_DANGER);
            JOptionPane.showMessageDialog(this, 
                "Login Error: " + ex.getMessage() + "\n\nMake sure XAMPP MySQL is running on port 3306,\nor click 'Database Setup' below to verify settings.", 
                "Authentication / Connection Failed", JOptionPane.WARNING_MESSAGE);
        }
    }
}
