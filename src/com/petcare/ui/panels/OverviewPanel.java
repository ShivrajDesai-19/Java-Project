package com.petcare.ui.panels;

import com.petcare.dao.AppointmentDAO;
import com.petcare.dao.MedicineDAO;
import com.petcare.dao.PetDAO;
import com.petcare.dao.PetOwnerDAO;
import com.petcare.model.Appointment;
import com.petcare.model.Medicine;
import com.petcare.model.User;
import com.petcare.service.BillingService;
import com.petcare.util.UIStyleUtil;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.List;

/**
 * Overview & Live Dashboard Panel
 */
public class OverviewPanel extends JPanel {

    private final User currentUser;
    private final PetDAO petDAO = new PetDAO();
    private final PetOwnerDAO ownerDAO = new PetOwnerDAO();
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final MedicineDAO medicineDAO = new MedicineDAO();
    private final BillingService billingService = new BillingService();

    private final DefaultTableModel aptTableModel;
    private final DefaultTableModel stockTableModel;

    private JPanel statsCardsPanel;

    public OverviewPanel(User currentUser) {
        this.currentUser = currentUser;
        setLayout(new BorderLayout(15, 15));
        setBackground(UIStyleUtil.COLOR_BG_LIGHT);
        setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        // Header Welcome Banner
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel welcomeLabel = new JLabel("Welcome back, " + currentUser.getFullName() + "!");
        welcomeLabel.setFont(UIStyleUtil.FONT_HEADER_LARGE);
        welcomeLabel.setForeground(UIStyleUtil.COLOR_TEXT_DARK);

        JLabel roleLabel = new JLabel("Role: " + currentUser.getRole().getDisplayName() + " | Smart Pet Care Clinic Rajkot");
        roleLabel.setFont(UIStyleUtil.FONT_BODY);
        roleLabel.setForeground(UIStyleUtil.COLOR_TEXT_MUTED);

        headerPanel.add(welcomeLabel, BorderLayout.NORTH);
        headerPanel.add(roleLabel, BorderLayout.SOUTH);

        // Stats Cards Panel (FlowLayout allows natural card size and prevents text compression)
        statsCardsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        statsCardsPanel.setOpaque(false);
        statsCardsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setOpaque(false);
        topContainer.add(headerPanel, BorderLayout.NORTH);
        topContainer.add(statsCardsPanel, BorderLayout.CENTER);

        add(topContainer, BorderLayout.NORTH);

        // Tables for quick activity: Today's Appointments & Low Stock Alerts
        String[] aptCols = {"Time", "Pet Patient", "Owner", "Doctor", "Purpose", "Status"};
        aptTableModel = new DefaultTableModel(aptCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable aptTable = new JTable(aptTableModel);
        UIStyleUtil.styleTable(aptTable);

        JPanel aptBox = new JPanel(new BorderLayout(5, 5));
        aptBox.setBackground(UIStyleUtil.COLOR_BG_CARD);
        aptBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIStyleUtil.COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        JLabel aptTitle = new JLabel("Today's Scheduled Appointments");
        aptTitle.setFont(UIStyleUtil.FONT_HEADER_MED);
        aptTitle.setForeground(UIStyleUtil.COLOR_PRIMARY);
        aptBox.add(aptTitle, BorderLayout.NORTH);
        aptBox.add(new JScrollPane(aptTable), BorderLayout.CENTER);

        String[] stockCols = {"Medicine", "Batch", "Available", "Min Alert", "Price (₹)"};
        stockTableModel = new DefaultTableModel(stockCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable stockTable = new JTable(stockTableModel);
        UIStyleUtil.styleTable(stockTable);

        JPanel stockBox = new JPanel(new BorderLayout(5, 5));
        stockBox.setBackground(UIStyleUtil.COLOR_BG_CARD);
        stockBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIStyleUtil.COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        JLabel stockTitle = new JLabel("Pharmacy Low Stock Warnings");
        stockTitle.setFont(UIStyleUtil.FONT_HEADER_MED);
        stockTitle.setForeground(UIStyleUtil.COLOR_DANGER);
        stockBox.add(stockTitle, BorderLayout.NORTH);
        stockBox.add(new JScrollPane(stockTable), BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, aptBox, stockBox);
        splitPane.setResizeWeight(0.58);
        splitPane.setDividerSize(8);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);

        add(splitPane, BorderLayout.CENTER);

        refreshData();
    }

    public void refreshData() {
        statsCardsPanel.removeAll();
        aptTableModel.setRowCount(0);
        stockTableModel.setRowCount(0);

        int totalPets = 0;
        int totalOwners = 0;
        int todayApts = 0;
        int lowStock = 0;
        double revenue = 0.0;

        try {
            totalPets = petDAO.findAll().size();
            totalOwners = ownerDAO.findAll().size();
            todayApts = appointmentDAO.countUpcomingToday();
            List<Medicine> lowMed = medicineDAO.findLowStockMedicines();
            lowStock = lowMed.size();
            revenue = billingService.getTotalRevenue();

            List<Appointment> apts = appointmentDAO.findByDate(LocalDate.now());
            for (Appointment a : apts) {
                aptTableModel.addRow(new Object[]{
                    a.getAppointmentTime(),
                    a.getPetName(),
                    a.getOwnerName(),
                    a.getVetName(),
                    a.getPurpose(),
                    a.getStatus().name()
                });
            }

            for (Medicine m : lowMed) {
                stockTableModel.addRow(new Object[]{
                    m.getName(),
                    m.getBatchNumber(),
                    m.getStockQuantity(),
                    m.getMinStockAlert(),
                    m.getUnitPrice()
                });
            }

        } catch (Exception ex) {
            System.err.println("[OverviewPanel] Could not fetch live metrics: " + ex.getMessage());
        }

        statsCardsPanel.add(UIStyleUtil.createStatCard("TOTAL PATIENTS", String.valueOf(totalPets), "Active Pets", UIStyleUtil.COLOR_SECONDARY));
        statsCardsPanel.add(UIStyleUtil.createStatCard("PET OWNERS", String.valueOf(totalOwners), "Registered Clients", UIStyleUtil.COLOR_PRIMARY));
        statsCardsPanel.add(UIStyleUtil.createStatCard("APPOINTMENTS TODAY", String.valueOf(todayApts), "Scheduled Visits", UIStyleUtil.COLOR_SUCCESS));
        statsCardsPanel.add(UIStyleUtil.createStatCard("LOW STOCK ALERTS", String.valueOf(lowStock), "Medicines Due", UIStyleUtil.COLOR_DANGER));
        statsCardsPanel.add(UIStyleUtil.createStatCard("TOTAL REVENUE", "₹" + String.format("%.0f", revenue), "Invoices Settled", UIStyleUtil.COLOR_ACCENT));

        statsCardsPanel.revalidate();
        statsCardsPanel.repaint();
    }
}
