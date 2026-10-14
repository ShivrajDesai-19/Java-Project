package com.petcare.ui;

import com.petcare.model.Appointment;
import com.petcare.model.Medicine;
import com.petcare.model.User;
import com.petcare.model.UserRole;
import com.petcare.model.VaccinationRecord;
import com.petcare.service.AppointmentService;
import com.petcare.service.AuthService;
import com.petcare.service.BillingService;
import com.petcare.service.MedicalService;
import com.petcare.service.PetManagementService;
import com.petcare.service.PharmacyService;
import com.petcare.service.ServiceBookingService;
import com.petcare.thread.BackgroundServiceManager;
import com.petcare.ui.panels.AppointmentsPanel;
import com.petcare.ui.panels.BackupRestorePanel;
import com.petcare.ui.panels.BillingPanel;
import com.petcare.ui.panels.MedicalHistoryPanel;
import com.petcare.ui.panels.OverviewPanel;
import com.petcare.ui.panels.OwnersPanel;
import com.petcare.ui.panels.PetsPanel;
import com.petcare.ui.panels.PharmacyPanel;
import com.petcare.ui.panels.ReportsPanel;
import com.petcare.ui.panels.ServicesPanel;
import com.petcare.ui.panels.StaffPanel;
import com.petcare.util.UIStyleUtil;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Main Application Dashboard Frame
 * Integrates all modules, enforces Role-based permissions,
 * and hosts the Unit 4 Multithreading Background Services.
 */
public class MainDashboardFrame extends JFrame {

    private final User currentUser;

    // Services
    private final PetManagementService petService = new PetManagementService();
    private final AppointmentService aptService = new AppointmentService();
    private final MedicalService medicalService = new MedicalService();
    private final PharmacyService pharmacyService = new PharmacyService();
    private final ServiceBookingService serviceBookingService = new ServiceBookingService();
    private final BillingService billingService = new BillingService();

    // Background Threading Manager (Unit 4)
    private final BackgroundServiceManager backgroundManager = new BackgroundServiceManager();

    // UI Structure
    private final JPanel contentArea;
    private final CardLayout cardLayout;
    private final List<JButton> navButtons = new ArrayList<>();

    // Sub-panels
    private OverviewPanel overviewPanel;
    private OwnersPanel ownersPanel;
    private PetsPanel petsPanel;
    private AppointmentsPanel appointmentsPanel;
    private MedicalHistoryPanel medicalHistoryPanel;
    private PharmacyPanel pharmacyPanel;
    private ServicesPanel servicesPanel;
    private BillingPanel billingPanel;
    private StaffPanel staffPanel;
    private ReportsPanel reportsPanel;
    private BackupRestorePanel backupRestorePanel;

    // Status bar labels
    private JLabel threadStatusLabel;
    private JLabel clockLabel;

    public MainDashboardFrame(User user) {
        this.currentUser = user;
        setTitle("Smart Pet Care & Veterinary Management System - [" + user.getFullName() + " | " + user.getRole().getDisplayName() + "]");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1240, 780);
        setMinimumSize(new Dimension(1080, 680));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. Sidebar Navigation
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // 2. Central Multi-Panel Card Container
        cardLayout = new CardLayout();
        contentArea = new JPanel(cardLayout);
        contentArea.setBackground(UIStyleUtil.COLOR_BG_LIGHT);

        buildPanels();
        add(contentArea, BorderLayout.CENTER);

        // 3. Status Bar with Unit 4 Live Clock and Thread Status
        JPanel statusBar = createStatusBar();
        add(statusBar, BorderLayout.SOUTH);

        // 4. Start Unit 4 Multithreading Services
        startBackgroundThreads();

        // Safe Shutdown Hook on Window Close
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                backgroundManager.shutdownAll();
            }
        });
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout(0, 10));
        sidebar.setBackground(UIStyleUtil.COLOR_SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(230, getHeight()));
        sidebar.setBorder(BorderFactory.createEmptyBorder(18, 10, 15, 10));

        // Clinic Branding Header
        JPanel brandPanel = new JPanel();
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setOpaque(false);

        JLabel appTitle = new JLabel("SMART PET CARE");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        appTitle.setForeground(Color.WHITE);
        appTitle.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        JLabel userBadge = new JLabel(currentUser.getFullName());
        userBadge.setFont(UIStyleUtil.FONT_BODY_BOLD);
        userBadge.setForeground(UIStyleUtil.COLOR_SECONDARY);
        userBadge.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        JLabel roleBadge = new JLabel("[" + currentUser.getRole().getDisplayName() + "]");
        roleBadge.setFont(UIStyleUtil.FONT_SMALL);
        roleBadge.setForeground(new Color(160, 180, 205));
        roleBadge.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        brandPanel.add(appTitle);
        brandPanel.add(Box.createVerticalStrut(8));
        brandPanel.add(userBadge);
        brandPanel.add(Box.createVerticalStrut(2));
        brandPanel.add(roleBadge);
        brandPanel.add(Box.createVerticalStrut(15));
        sidebar.add(brandPanel, BorderLayout.NORTH);

        // Center: Scrollable navigation buttons container
        JPanel navListPanel = new JPanel();
        navListPanel.setLayout(new BoxLayout(navListPanel, BoxLayout.Y_AXIS));
        navListPanel.setOpaque(false);

        addNavButton(navListPanel, "•  Dashboard", "OVERVIEW");
        addNavButton(navListPanel, "•  Pet Owners", "OWNERS");
        addNavButton(navListPanel, "•  Pet Patients", "PETS");
        addNavButton(navListPanel, "•  Appointments", "APPOINTMENTS");

        if (currentUser.canDiagnoseAndPrescribe()) {
            addNavButton(navListPanel, "•  Medical & Clinical", "MEDICAL");
        }

        addNavButton(navListPanel, "•  Pharmacy Stock", "PHARMACY");
        addNavButton(navListPanel, "•  Grooming & Boarding", "SERVICES");

        if (currentUser.canManageBilling()) {
            addNavButton(navListPanel, "•  Invoicing & Billing", "BILLING");
        }

        if (currentUser.canManageStaff()) {
            addNavButton(navListPanel, "•  Staff Management", "STAFF");
        }

        addNavButton(navListPanel, "•  Reports & Analytics", "REPORTS");

        if (currentUser.canPerformDatabaseBackup()) {
            addNavButton(navListPanel, "•  Backup & Restore", "BACKUP");
        }

        JScrollPane navScroll = new JScrollPane(navListPanel);
        navScroll.setOpaque(false);
        navScroll.getViewport().setOpaque(false);
        navScroll.setBorder(null);
        navScroll.getVerticalScrollBar().setUnitIncrement(14);
        sidebar.add(navScroll, BorderLayout.CENTER);

        // Logout Button at South
        JButton logoutBtn = UIStyleUtil.createDangerButton("Sign Out");
        logoutBtn.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        logoutBtn.setMaximumSize(new Dimension(200, 36));
        logoutBtn.setPreferredSize(new Dimension(200, 36));
        logoutBtn.addActionListener(e -> handleLogout());

        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
        logoutPanel.setOpaque(false);
        logoutPanel.add(logoutBtn);
        sidebar.add(logoutPanel, BorderLayout.SOUTH);

        return sidebar;
    }

    private void addNavButton(JPanel container, String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btn.setFont(UIStyleUtil.FONT_BODY_BOLD);
        btn.setForeground(Color.WHITE);
        btn.setBackground(UIStyleUtil.COLOR_SIDEBAR_BG);
        btn.setMaximumSize(new Dimension(205, 36));
        btn.setPreferredSize(new Dimension(205, 36));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 0));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> {
            cardLayout.show(contentArea, cardName);
            setActiveNavButton(btn);
            refreshCardData(cardName);
        });

        navButtons.add(btn);
        container.add(btn);
        container.add(Box.createVerticalStrut(4));

        if (navButtons.size() == 1) {
            btn.setBackground(UIStyleUtil.COLOR_SIDEBAR_ACTIVE);
        }
    }

    private void setActiveNavButton(JButton activeBtn) {
        for (JButton btn : navButtons) {
            if (btn == activeBtn) {
                btn.setBackground(UIStyleUtil.COLOR_SIDEBAR_ACTIVE);
            } else {
                btn.setBackground(UIStyleUtil.COLOR_SIDEBAR_BG);
            }
        }
    }

    private void buildPanels() {
        overviewPanel = new OverviewPanel(currentUser);
        ownersPanel = new OwnersPanel(petService);
        petsPanel = new PetsPanel(petService);
        appointmentsPanel = new AppointmentsPanel(aptService, petService);
        medicalHistoryPanel = new MedicalHistoryPanel(medicalService, petService);
        pharmacyPanel = new PharmacyPanel(pharmacyService);
        servicesPanel = new ServicesPanel(serviceBookingService, petService);
        billingPanel = new BillingPanel(billingService, petService);
        staffPanel = new StaffPanel();
        reportsPanel = new ReportsPanel();
        backupRestorePanel = new BackupRestorePanel();

        contentArea.add(overviewPanel, "OVERVIEW");
        contentArea.add(ownersPanel, "OWNERS");
        contentArea.add(petsPanel, "PETS");
        contentArea.add(appointmentsPanel, "APPOINTMENTS");
        contentArea.add(medicalHistoryPanel, "MEDICAL");
        contentArea.add(pharmacyPanel, "PHARMACY");
        contentArea.add(servicesPanel, "SERVICES");
        contentArea.add(billingPanel, "BILLING");
        contentArea.add(staffPanel, "STAFF");
        contentArea.add(reportsPanel, "REPORTS");
        contentArea.add(backupRestorePanel, "BACKUP");
    }

    private void refreshCardData(String cardName) {
        if ("OVERVIEW".equals(cardName)) overviewPanel.refreshData();
        else if ("OWNERS".equals(cardName)) ownersPanel.loadOwners();
        else if ("PETS".equals(cardName)) petsPanel.loadPets();
        else if ("APPOINTMENTS".equals(cardName)) appointmentsPanel.loadAppointments();
        else if ("MEDICAL".equals(cardName)) medicalHistoryPanel.reloadPets();
        else if ("PHARMACY".equals(cardName)) pharmacyPanel.loadMedicines();
        else if ("SERVICES".equals(cardName)) servicesPanel.loadBookings();
        else if ("BILLING".equals(cardName)) billingPanel.loadInitialData();
        else if ("STAFF".equals(cardName)) staffPanel.loadStaff();
        else if ("REPORTS".equals(cardName)) reportsPanel.loadAnalytics();
    }

    private JPanel createStatusBar() {
        JPanel status = new JPanel(new BorderLayout());
        status.setBackground(Color.WHITE);
        status.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, UIStyleUtil.COLOR_BORDER),
            BorderFactory.createEmptyBorder(6, 16, 6, 16)
        ));

        // Left: User info and active thread group count (Unit 4 viva demonstration)
        threadStatusLabel = new JLabel("Status: Connected  |  Active Background Threads: 3");
        threadStatusLabel.setFont(UIStyleUtil.FONT_SMALL);
        threadStatusLabel.setForeground(UIStyleUtil.COLOR_TEXT_MUTED);

        // Right: Live Clock banner
        clockLabel = new JLabel("Initializing clock...");
        clockLabel.setFont(UIStyleUtil.FONT_SMALL);
        clockLabel.setForeground(UIStyleUtil.COLOR_PRIMARY);

        status.add(threadStatusLabel, BorderLayout.CENTER);
        status.add(clockLabel, BorderLayout.EAST);
        return status;
    }

    /**
     * Unit 4 Syllabus Feature: Multithreading Background Monitoring
     */
    private void startBackgroundThreads() {
        backgroundManager.startServices(
            clockLabel,
            // Low Stock callback
            lowMedicines -> SwingUtilities.invokeLater(() -> {
                threadStatusLabel.setText("Status: Online  |  Threads in Group: " + 
                    backgroundManager.getThreadGroup().activeCount() + 
                    "  |  ⚠ Low Stock Alert: " + lowMedicines.size() + " medicines");
            }),
            // Appointment Reminder callback
            (todayApts, dueVaccs) -> SwingUtilities.invokeLater(() -> {
                threadStatusLabel.setText("Status: Online  |  Threads in Group: " + 
                    backgroundManager.getThreadGroup().activeCount() + 
                    "  |  Visits Today: " + todayApts.size() + 
                    "  |  Vaccines Due: " + dueVaccs.size());
            })
        );
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to log out?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            backgroundManager.shutdownAll();
            AuthService.getInstance().logout();
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        }
    }
}
