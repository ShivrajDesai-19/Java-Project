package com.petcare.ui.panels;

import com.petcare.model.BoardingBooking;
import com.petcare.model.GroomingBooking;
import com.petcare.model.ServiceItem;
import com.petcare.service.PetManagementService;
import com.petcare.service.ServiceBookingService;
import com.petcare.ui.dialogs.ServiceBookingDialog;
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
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * Grooming and Pet Boarding Services Management Panel
 */
public class ServicesPanel extends JPanel {

    private final ServiceBookingService service;
    private final PetManagementService petService;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JComboBox<String> typeFilterCombo;
    private List<ServiceItem> currentBookings = new ArrayList<>();

    public ServicesPanel(ServiceBookingService service, PetManagementService petService) {
        this.service = service;
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
        filterPanel.add(new JLabel("Service Type:"));
        typeFilterCombo = new JComboBox<>(new String[]{"ALL", "GROOMING", "BOARDING"});
        typeFilterCombo.addActionListener(e -> applyFilter());
        filterPanel.add(typeFilterCombo);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        actionPanel.setOpaque(false);
        actionPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        JButton addBtn = UIStyleUtil.createPrimaryButton("+ Book Service");
        JButton completeBtn = UIStyleUtil.createSuccessButton("Mark Completed");
        JButton cancelBtn = UIStyleUtil.createDangerButton("Cancel Booking");
        JButton refreshBtn = UIStyleUtil.createSecondaryButton("Refresh");

        addBtn.addActionListener(e -> openAddDialog());
        completeBtn.addActionListener(e -> updateSelectedStatus("COMPLETED"));
        cancelBtn.addActionListener(e -> updateSelectedStatus("CANCELLED"));
        refreshBtn.addActionListener(e -> loadBookings());

        actionPanel.add(addBtn);
        actionPanel.add(completeBtn);
        actionPanel.add(cancelBtn);
        actionPanel.add(refreshBtn);

        topPanel.add(filterPanel);
        topPanel.add(Box.createVerticalStrut(4));
        topPanel.add(actionPanel);
        add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Pet Name", "Type", "Start Date", "End Date", "Station / Cage", "Package Details", "Cost (₹)", "Status"};
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

        loadBookings();
    }

    public void loadBookings() {
        try {
            currentBookings = service.getAllBookings();
            applyFilter();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load service bookings: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyFilter() {
        String filter = (String) typeFilterCombo.getSelectedItem();
        List<ServiceItem> filtered = new ArrayList<>();
        for (ServiceItem b : currentBookings) {
            if ("ALL".equalsIgnoreCase(filter) || b.getServiceType().equalsIgnoreCase(filter)) {
                filtered.add(b);
            }
        }
        populateTable(filtered);
    }

    private void populateTable(List<ServiceItem> list) {
        tableModel.setRowCount(0);
        for (ServiceItem s : list) {
            String endDateStr = "-";
            String stationOrCage = "-";
            if (s instanceof BoardingBooking) {
                BoardingBooking bb = (BoardingBooking) s;
                endDateStr = bb.getEndDate() != null ? bb.getEndDate().toString() : "-";
                stationOrCage = bb.getCageOrRoomNo();
            } else if (s instanceof GroomingBooking) {
                GroomingBooking gb = (GroomingBooking) s;
                stationOrCage = gb.getGroomerStation();
            }

            tableModel.addRow(new Object[]{
                s.getId(),
                s.getPetName(),
                s.getServiceType(),
                s.getStartDate(),
                endDateStr,
                stationOrCage,
                s.getPackageDetails(),
                s.calculateTotalPrice(),
                s.getStatus()
            });
        }
    }

    private void openAddDialog() {
        ServiceBookingDialog dlg = new ServiceBookingDialog(SwingUtilities.getWindowAncestor(this), service, petService);
        dlg.setVisible(true);
        if (dlg.isSaved()) {
            loadBookings();
        }
    }

    private void updateSelectedStatus(String newStatus) {
        int row = table.getSelectedRow();
        if (row < 0 || row >= currentBookings.size()) {
            JOptionPane.showMessageDialog(this, "Please select a booking to update.", "Selection Required", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        ServiceItem selected = currentBookings.get(row);
        try {
            service.updateStatus(selected.getId(), newStatus);
            loadBookings();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to update status: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
