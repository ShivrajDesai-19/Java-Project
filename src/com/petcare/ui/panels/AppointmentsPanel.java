package com.petcare.ui.panels;

import com.petcare.model.Appointment;
import com.petcare.model.AppointmentStatus;
import com.petcare.service.AppointmentService;
import com.petcare.service.PetManagementService;
import com.petcare.ui.dialogs.AppointmentDialog;
import com.petcare.util.UIStyleUtil;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Appointments Scheduling and Management Panel
 */
public class AppointmentsPanel extends JPanel {

    private final AppointmentService aptService;
    private final PetManagementService petService;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JComboBox<String> statusFilterCombo;
    private List<Appointment> currentApts = new ArrayList<>();

    public AppointmentsPanel(AppointmentService aptService, PetManagementService petService) {
        this.aptService = aptService;
        this.petService = petService;

        setLayout(new BorderLayout(12, 12));
        setBackground(UIStyleUtil.COLOR_BG_LIGHT);
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Toolbar (Vertical stack guarantees filters and action buttons never overlap)
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filterPanel.setOpaque(false);
        filterPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        filterPanel.add(new JLabel("Status Filter:"));
        statusFilterCombo = new JComboBox<>(new String[]{"ALL", "SCHEDULED", "IN_PROGRESS", "COMPLETED", "CANCELLED"});
        statusFilterCombo.addActionListener(e -> applyFilter());
        filterPanel.add(statusFilterCombo);

        JButton todayBtn = UIStyleUtil.createSecondaryButton("Today's Appointments");
        todayBtn.addActionListener(e -> filterToday());
        filterPanel.add(todayBtn);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        actionPanel.setOpaque(false);
        actionPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        JButton addBtn = UIStyleUtil.createPrimaryButton("+ Book Appointment");
        JButton editBtn = UIStyleUtil.createSecondaryButton("Edit");
        JButton completeBtn = UIStyleUtil.createSuccessButton("Mark Completed");
        JButton cancelBtn = UIStyleUtil.createDangerButton("Cancel Visit");
        JButton refreshBtn = UIStyleUtil.createSecondaryButton("Refresh");

        addBtn.addActionListener(e -> openAddDialog());
        editBtn.addActionListener(e -> openEditDialog());
        completeBtn.addActionListener(e -> updateSelectedStatus(AppointmentStatus.COMPLETED));
        cancelBtn.addActionListener(e -> updateSelectedStatus(AppointmentStatus.CANCELLED));
        refreshBtn.addActionListener(e -> loadAppointments());

        actionPanel.add(addBtn);
        actionPanel.add(editBtn);
        actionPanel.add(completeBtn);
        actionPanel.add(cancelBtn);
        actionPanel.add(refreshBtn);

        topPanel.add(filterPanel);
        topPanel.add(Box.createVerticalStrut(4));
        topPanel.add(actionPanel);
        add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Pet Name", "Owner Name", "Veterinarian", "Date", "Time Slot", "Purpose", "Status", "Notes"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        UIStyleUtil.styleTable(table);

        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(UIStyleUtil.COLOR_BG_CARD);
        tableContainer.setBorder(BorderFactory.createLineBorder(UIStyleUtil.COLOR_BORDER, 1));
        tableContainer.add(new JScrollPane(table), BorderLayout.CENTER);

        add(tableContainer, BorderLayout.CENTER);

        loadAppointments();
    }

    public void loadAppointments() {
        try {
            currentApts = aptService.getAllAppointments();
            applyFilter();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load appointments: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterToday() {
        try {
            currentApts = aptService.getAppointmentsByDate(LocalDate.now());
            statusFilterCombo.setSelectedItem("ALL");
            populateTable(currentApts);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error fetching today's appointments: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyFilter() {
        String filter = (String) statusFilterCombo.getSelectedItem();
        List<Appointment> filtered = new ArrayList<>();
        for (Appointment a : currentApts) {
            if ("ALL".equalsIgnoreCase(filter) || a.getStatus().name().equalsIgnoreCase(filter)) {
                filtered.add(a);
            }
        }
        populateTable(filtered);
    }

    private void populateTable(List<Appointment> list) {
        tableModel.setRowCount(0);
        for (Appointment a : list) {
            tableModel.addRow(new Object[]{
                a.getId(),
                a.getPetName(),
                a.getOwnerName(),
                a.getVetName(),
                a.getAppointmentDate(),
                a.getAppointmentTime(),
                a.getPurpose(),
                a.getStatus().name(),
                a.getNotes()
            });
        }
    }

    private void openAddDialog() {
        AppointmentDialog dlg = new AppointmentDialog(SwingUtilities.getWindowAncestor(this), aptService, petService, null);
        dlg.setVisible(true);
        if (dlg.isSaved()) {
            loadAppointments();
        }
    }

    private void openEditDialog() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= currentApts.size()) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to edit.", "Selection Required", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Appointment selected = currentApts.get(row);
        AppointmentDialog dlg = new AppointmentDialog(SwingUtilities.getWindowAncestor(this), aptService, petService, selected);
        dlg.setVisible(true);
        if (dlg.isSaved()) {
            loadAppointments();
        }
    }

    private void updateSelectedStatus(AppointmentStatus newStatus) {
        int row = table.getSelectedRow();
        if (row < 0 || row >= currentApts.size()) {
            JOptionPane.showMessageDialog(this, "Please select an appointment.", "Selection Required", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Appointment selected = currentApts.get(row);
        try {
            aptService.updateStatus(selected.getId(), newStatus);
            loadAppointments();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to update appointment: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
