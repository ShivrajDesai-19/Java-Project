package com.petcare.ui.panels;

import com.petcare.exception.InsufficientStockException;
import com.petcare.model.Medicine;
import com.petcare.service.PharmacyService;
import com.petcare.ui.dialogs.MedicineDialog;
import com.petcare.util.UIStyleUtil;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Pharmacy Inventory & Stock Management Panel
 */
public class PharmacyPanel extends JPanel {

    private final PharmacyService pharmacyService;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JTextField searchField;
    private final JLabel lowStockAlertBanner;
    private List<Medicine> currentMedicines = new ArrayList<>();

    public PharmacyPanel(PharmacyService pharmacyService) {
        this.pharmacyService = pharmacyService;

        setLayout(new BorderLayout(12, 12));
        setBackground(UIStyleUtil.COLOR_BG_LIGHT);
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Top Toolbar (Vertical stack guarantees search and action buttons never overlap)
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        searchPanel.setOpaque(false);
        searchPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        searchPanel.add(new JLabel("Search Inventory:"));
        searchField = new JTextField(18);
        searchPanel.add(searchField);
        JButton searchBtn = UIStyleUtil.createSecondaryButton("Search");
        searchBtn.addActionListener(e -> performSearch());
        searchField.addActionListener(e -> performSearch());
        searchPanel.add(searchBtn);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        actionPanel.setOpaque(false);
        actionPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        JButton addBtn = UIStyleUtil.createPrimaryButton("+ Add Medicine");
        JButton editBtn = UIStyleUtil.createSecondaryButton("Edit Stock");
        JButton dispenseBtn = UIStyleUtil.createSuccessButton("Dispense Medicine");
        JButton deleteBtn = UIStyleUtil.createDangerButton("Delete");
        JButton refreshBtn = UIStyleUtil.createSecondaryButton("Refresh");

        addBtn.addActionListener(e -> openAddDialog());
        editBtn.addActionListener(e -> openEditDialog());
        dispenseBtn.addActionListener(e -> dispenseSelected());
        deleteBtn.addActionListener(e -> deleteSelected());
        refreshBtn.addActionListener(e -> loadMedicines());

        actionPanel.add(addBtn);
        actionPanel.add(editBtn);
        actionPanel.add(dispenseBtn);
        actionPanel.add(deleteBtn);
        actionPanel.add(refreshBtn);

        topPanel.add(searchPanel);
        topPanel.add(Box.createVerticalStrut(4));
        topPanel.add(actionPanel);

        // Low stock warning banner
        lowStockAlertBanner = new JLabel("  Pharmacy Stock Status: Checking...");
        lowStockAlertBanner.setFont(UIStyleUtil.FONT_BODY_BOLD);
        lowStockAlertBanner.setOpaque(true);
        lowStockAlertBanner.setBackground(new java.awt.Color(254, 237, 237));
        lowStockAlertBanner.setForeground(UIStyleUtil.COLOR_DANGER);
        lowStockAlertBanner.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JPanel northContainer = new JPanel(new BorderLayout(0, 10));
        northContainer.setOpaque(false);
        northContainer.add(topPanel, BorderLayout.NORTH);
        northContainer.add(lowStockAlertBanner, BorderLayout.SOUTH);

        add(northContainer, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Medicine Name", "Category", "Batch Number", "In Stock", "Alert Level", "Unit Price (₹)", "Expiry Date", "Supplier"};
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

        loadMedicines();
    }

    public void loadMedicines() {
        try {
            currentMedicines = pharmacyService.getAllMedicines();
            populateTable(currentMedicines);
            checkLowStock();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load medicines: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void checkLowStock() {
        try {
            List<Medicine> lowList = pharmacyService.getLowStockMedicines();
            if (lowList.isEmpty()) {
                lowStockAlertBanner.setText("  All pharmacy medicines are adequately stocked.");
                lowStockAlertBanner.setBackground(new java.awt.Color(235, 247, 238));
                lowStockAlertBanner.setForeground(UIStyleUtil.COLOR_SUCCESS);
            } else {
                lowStockAlertBanner.setText("  WARNING: " + lowList.size() + " medicine(s) are at or below minimum threshold! Re-order recommended.");
                lowStockAlertBanner.setBackground(new java.awt.Color(254, 237, 237));
                lowStockAlertBanner.setForeground(UIStyleUtil.COLOR_DANGER);
            }
        } catch (Exception ignored) {}
    }

    private void performSearch() {
        String query = searchField.getText().trim();
        try {
            currentMedicines = pharmacyService.searchMedicines(query);
            populateTable(currentMedicines);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Search error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateTable(List<Medicine> list) {
        tableModel.setRowCount(0);
        for (Medicine m : list) {
            tableModel.addRow(new Object[]{
                m.getId(),
                m.getName(),
                m.getCategory(),
                m.getBatchNumber(),
                m.getStockQuantity(),
                m.getMinStockAlert(),
                m.getUnitPrice(),
                m.getExpiryDate(),
                m.getSupplier()
            });
        }
    }

    private void openAddDialog() {
        MedicineDialog dlg = new MedicineDialog(SwingUtilities.getWindowAncestor(this), pharmacyService, null);
        dlg.setVisible(true);
        if (dlg.isSaved()) {
            loadMedicines();
        }
    }

    private void openEditDialog() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= currentMedicines.size()) {
            JOptionPane.showMessageDialog(this, "Please select a medicine row to edit.", "Selection Required", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Medicine selected = currentMedicines.get(row);
        MedicineDialog dlg = new MedicineDialog(SwingUtilities.getWindowAncestor(this), pharmacyService, selected);
        dlg.setVisible(true);
        if (dlg.isSaved()) {
            loadMedicines();
        }
    }

    private void dispenseSelected() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= currentMedicines.size()) {
            JOptionPane.showMessageDialog(this, "Please select a medicine to dispense.", "Selection Required", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Medicine selected = currentMedicines.get(row);
        String input = JOptionPane.showInputDialog(this, 
            "Enter quantity to dispense for '" + selected.getName() + "':\n(Current Available: " + selected.getStockQuantity() + ")", 
            "1");

        if (input != null && !input.trim().isEmpty()) {
            try {
                int qty = Integer.parseInt(input.trim());
                if (qty <= 0) {
                    JOptionPane.showMessageDialog(this, "Quantity must be greater than zero.", "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                // Unit 4 Syllabus: Calling synchronized dispense method
                pharmacyService.dispense(selected.getId(), qty);
                JOptionPane.showMessageDialog(this, "Successfully dispensed " + qty + " units of " + selected.getName() + "!", "Dispensed", JOptionPane.INFORMATION_MESSAGE);
                loadMedicines();
            } catch (InsufficientStockException ise) {
                // Unit 3 Custom Exception handling
                JOptionPane.showMessageDialog(this, ise.getMessage(), "Insufficient Stock Alert", JOptionPane.WARNING_MESSAGE);
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Please enter a valid numeric integer quantity.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Dispense error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= currentMedicines.size()) {
            JOptionPane.showMessageDialog(this, "Please select a medicine to delete.", "Selection Required", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Medicine selected = currentMedicines.get(row);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to remove '" + selected.getName() + "' from inventory?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                pharmacyService.deleteMedicine(selected.getId());
                loadMedicines();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Delete failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
