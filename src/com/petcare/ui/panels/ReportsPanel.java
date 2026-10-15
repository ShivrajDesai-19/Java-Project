package com.petcare.ui.panels;

import com.petcare.service.ReportService;
import com.petcare.util.UIStyleUtil;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Map;

/**
 * Reports and Analytics Dashboard Panel
 */
public class ReportsPanel extends JPanel {

    private final ReportService reportService = new ReportService();

    private final JPanel summaryCardsPanel;
    private final DefaultTableModel speciesTableModel;
    private final DefaultTableModel aptStatusModel;

    public ReportsPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(UIStyleUtil.COLOR_BG_LIGHT);
        setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        // Top Toolbar (Vertical stack prevents title colliding with action buttons)
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);

        JLabel titleLbl = new JLabel("Clinic Analytics & Reports Dashboard");
        titleLbl.setFont(UIStyleUtil.FONT_HEADER_LARGE);
        titleLbl.setForeground(UIStyleUtil.COLOR_PRIMARY);
        titleLbl.setAlignmentX(JLabel.LEFT_ALIGNMENT);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        actionPanel.setOpaque(false);
        actionPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        JButton printReportBtn = UIStyleUtil.createPrimaryButton("Export Full Clinic Report");
        JButton refreshBtn = UIStyleUtil.createSecondaryButton("Refresh Analytics");

        printReportBtn.addActionListener(e -> exportFullReport());
        refreshBtn.addActionListener(e -> loadAnalytics());

        actionPanel.add(printReportBtn);
        actionPanel.add(refreshBtn);

        topPanel.add(titleLbl);
        topPanel.add(Box.createVerticalStrut(6));
        topPanel.add(actionPanel);

        // Stats summary cards (FlowLayout prevents text compression)
        summaryCardsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        summaryCardsPanel.setOpaque(false);
        summaryCardsPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        JPanel northStack = new JPanel(new BorderLayout());
        northStack.setOpaque(false);
        northStack.add(topPanel, BorderLayout.NORTH);
        northStack.add(summaryCardsPanel, BorderLayout.CENTER);

        add(northStack, BorderLayout.NORTH);

        // Charts / Breakdown Grid
        JPanel centerGrid = new JPanel(new GridLayout(1, 2, 15, 15));
        centerGrid.setOpaque(false);

        // Species Breakdown Table
        String[] spCols = {"Pet Species Category", "Total Count", "Share (%)"};
        speciesTableModel = new DefaultTableModel(spCols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable spTable = new JTable(speciesTableModel);
        UIStyleUtil.styleTable(spTable);

        JPanel spBox = new JPanel(new BorderLayout(6, 6));
        spBox.setBackground(UIStyleUtil.COLOR_BG_CARD);
        spBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIStyleUtil.COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));
        JLabel spTitle = new JLabel("Patient Species Distribution");
        spTitle.setFont(UIStyleUtil.FONT_HEADER_MED);
        spTitle.setForeground(UIStyleUtil.COLOR_PRIMARY);
        spBox.add(spTitle, BorderLayout.NORTH);
        spBox.add(new JScrollPane(spTable), BorderLayout.CENTER);
        centerGrid.add(spBox);

        // Appointment Status Breakdown Table
        String[] aptCols = {"Appointment Status", "Total Visits", "Share (%)"};
        aptStatusModel = new DefaultTableModel(aptCols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable aptTable = new JTable(aptStatusModel);
        UIStyleUtil.styleTable(aptTable);

        JPanel aptBox = new JPanel(new BorderLayout(6, 6));
        aptBox.setBackground(UIStyleUtil.COLOR_BG_CARD);
        aptBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIStyleUtil.COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));
        JLabel aptTitle = new JLabel("Appointment Status Fulfillment");
        aptTitle.setFont(UIStyleUtil.FONT_HEADER_MED);
        aptTitle.setForeground(UIStyleUtil.COLOR_PRIMARY);
        aptBox.add(aptTitle, BorderLayout.NORTH);
        aptBox.add(new JScrollPane(aptTable), BorderLayout.CENTER);
        centerGrid.add(aptBox);

        add(centerGrid, BorderLayout.CENTER);

        loadAnalytics();
    }

    public void loadAnalytics() {
        summaryCardsPanel.removeAll();
        speciesTableModel.setRowCount(0);
        aptStatusModel.setRowCount(0);

        try {
            ReportService.AnalyticsSummary s = reportService.getClinicAnalytics();

            summaryCardsPanel.add(UIStyleUtil.createStatCard("ACTIVE PATIENTS", String.valueOf(s.totalPets), "Registered Pets", UIStyleUtil.COLOR_PRIMARY));
            summaryCardsPanel.add(UIStyleUtil.createStatCard("PET OWNERS", String.valueOf(s.totalOwners), "Client Database", UIStyleUtil.COLOR_SECONDARY));
            summaryCardsPanel.add(UIStyleUtil.createStatCard("MEDICINE ALERTS", String.valueOf(s.lowStockCount), "Low Stock Items", UIStyleUtil.COLOR_DANGER));
            summaryCardsPanel.add(UIStyleUtil.createStatCard("GROSS REVENUE", "₹" + String.format("%.2f", s.totalRevenue), "Total Earnings", UIStyleUtil.COLOR_SUCCESS));

            // Populate Species Breakdown
            for (Map.Entry<String, Integer> e : s.speciesDistribution.entrySet()) {
                double pct = s.totalPets > 0 ? (e.getValue() * 100.0 / s.totalPets) : 0.0;
                speciesTableModel.addRow(new Object[]{
                    e.getKey(),
                    e.getValue(),
                    String.format("%.1f%%", pct)
                });
            }

            // Populate Appointment Breakdown
            int totalApts = 0;
            for (int count : s.appointmentStatusCount.values()) totalApts += count;

            for (Map.Entry<String, Integer> e : s.appointmentStatusCount.entrySet()) {
                double pct = totalApts > 0 ? (e.getValue() * 100.0 / totalApts) : 0.0;
                aptStatusModel.addRow(new Object[]{
                    e.getKey(),
                    e.getValue(),
                    String.format("%.1f%%", pct)
                });
            }

            summaryCardsPanel.revalidate();
            summaryCardsPanel.repaint();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to compute analytics: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportFullReport() {
        try {
            ReportService.AnalyticsSummary s = reportService.getClinicAnalytics();

            // Unit 2 Concept: StringBuilder for report compilation
            StringBuilder sb = new StringBuilder();
            sb.append("<html><body style='font-family: Arial, sans-serif; padding: 20px;'>");
            sb.append("<h1 style='color: #184C78;'>SMART PET CARE & VETERINARY CLINIC</h1>");
            sb.append("<h3 style='color: #555;'>Executive Operations & Clinical Analytics Report</h3>");
            sb.append("<hr style='border: 1px solid #184C78;'>");

            sb.append("<h4>Core Clinic Metrics</h4>");
            sb.append("<table border='1' cellpadding='8' style='border-collapse: collapse; width: 100%;'>");
            sb.append("<tr style='background-color: #f2f2f2;'><th>Metric</th><th>Recorded Total</th></tr>");
            sb.append("<tr><td>Total Registered Pet Patients</td><td><b>").append(s.totalPets).append("</b></td></tr>");
            sb.append("<tr><td>Total Pet Owners</td><td><b>").append(s.totalOwners).append("</b></td></tr>");
            sb.append("<tr><td>Pending Scheduled Appointments</td><td><b>").append(s.scheduledAppointments).append("</b></td></tr>");
            sb.append("<tr><td>Low Stock Pharmacy Items</td><td><b style='color: red;'>").append(s.lowStockCount).append("</b></td></tr>");
            sb.append("<tr><td>Gross Realized Revenue</td><td><b style='color: green;'>₹").append(String.format("%.2f", s.totalRevenue)).append("</b></td></tr>");
            sb.append("</table>");

            sb.append("<h4>Patient Demographics by Species</h4><ul>");
            for (Map.Entry<String, Integer> e : s.speciesDistribution.entrySet()) {
                sb.append("<li>").append(e.getKey()).append(": <b>").append(e.getValue()).append("</b> patients</li>");
            }
            sb.append("</ul>");

            sb.append("<p style='font-size: 11px; color: #888;'>Smart Pet Care System - Academic Project Evaluation Build</p>");
            sb.append("</body></html>");

            JDialog dialog = new JDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this), "Executive Clinic Report", true);
            dialog.setSize(560, 560);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout());

            JEditorPane pane = new JEditorPane("text/html", sb.toString());
            pane.setEditable(false);
            dialog.add(new JScrollPane(pane), BorderLayout.CENTER);

            JPanel bp = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton print = new JButton("Print");
            print.addActionListener(e -> {
                try { pane.print(); } catch (Exception ignored) {}
            });
            JButton close = new JButton("Close");
            close.addActionListener(e -> dialog.dispose());
            bp.add(print);
            bp.add(close);
            dialog.add(bp, BorderLayout.SOUTH);

            dialog.setVisible(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Report generation failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
