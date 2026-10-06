package com.petcare.ui.panels;

import com.petcare.model.PetOwner;
import com.petcare.service.PetManagementService;
import com.petcare.ui.dialogs.OwnerDialog;
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
 * Pet Owners Management Panel
 */
public class OwnersPanel extends JPanel {

    private final PetManagementService service;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JTextField searchField;
    private List<PetOwner> currentOwners = new ArrayList<>();

    public OwnersPanel(PetManagementService service) {
        this.service = service;
        setLayout(new BorderLayout(12, 12));
        setBackground(UIStyleUtil.COLOR_BG_LIGHT);
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Top Toolbar Panel (Vertical stack guarantees search and action buttons never overlap)
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        searchPanel.setOpaque(false);
        searchPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        searchPanel.add(new JLabel("Search Owners:"));
        searchField = new JTextField(18);
        searchPanel.add(searchField);
        JButton searchBtn = UIStyleUtil.createSecondaryButton("Search");
        searchBtn.addActionListener(e -> performSearch());
        searchField.addActionListener(e -> performSearch());
        searchPanel.add(searchBtn);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        actionPanel.setOpaque(false);
        actionPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        JButton addBtn = UIStyleUtil.createPrimaryButton("+ Add Owner");
        JButton editBtn = UIStyleUtil.createSecondaryButton("Edit Profile");
        JButton deleteBtn = UIStyleUtil.createDangerButton("Delete");
        JButton refreshBtn = UIStyleUtil.createSecondaryButton("Refresh");

        addBtn.addActionListener(e -> openAddDialog());
        editBtn.addActionListener(e -> openEditDialog());
        deleteBtn.addActionListener(e -> deleteSelectedOwner());
        refreshBtn.addActionListener(e -> loadOwners());

        actionPanel.add(addBtn);
        actionPanel.add(editBtn);
        actionPanel.add(deleteBtn);
        actionPanel.add(refreshBtn);

        topPanel.add(searchPanel);
        topPanel.add(Box.createVerticalStrut(4));
        topPanel.add(actionPanel);
        add(topPanel, BorderLayout.NORTH);

        // Owners Table
        String[] columns = {"#", "ID", "Full Name", "Phone Number", "Email Address", "Emergency Contact", "Registered Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        UIStyleUtil.styleTable(table);
        if (table.getColumnModel().getColumnCount() > 0) {
            table.getColumnModel().getColumn(0).setPreferredWidth(35);
            table.getColumnModel().getColumn(0).setMaxWidth(50);
        }

        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(UIStyleUtil.COLOR_BG_CARD);
        tableContainer.setBorder(BorderFactory.createLineBorder(UIStyleUtil.COLOR_BORDER, 1));
        tableContainer.add(new JScrollPane(table), BorderLayout.CENTER);

        add(tableContainer, BorderLayout.CENTER);

        loadOwners();
    }

    public void loadOwners() {
        try {
            currentOwners = service.getAllOwners();
            populateTable(currentOwners);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load owners: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performSearch() {
        String query = searchField.getText().trim();
        try {
            currentOwners = service.searchOwners(query);
            populateTable(currentOwners);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Search failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateTable(List<PetOwner> list) {
        tableModel.setRowCount(0);
        int srNo = 1;
        for (PetOwner o : list) {
            tableModel.addRow(new Object[]{
                srNo++,
                o.getId(),
                o.getFullName(),
                o.getPhone(),
                o.getEmail(),
                o.getEmergencyContact(),
                o.getRegisteredDate()
            });
        }
    }

    private void openAddDialog() {
        OwnerDialog dlg = new OwnerDialog(SwingUtilities.getWindowAncestor(this), service, null);
        dlg.setVisible(true);
        if (dlg.isSaved()) {
            loadOwners();
        }
    }

    private void openEditDialog() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= currentOwners.size()) {
            JOptionPane.showMessageDialog(this, "Please select an owner row to edit.", "Selection Required", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        PetOwner selected = currentOwners.get(row);
        OwnerDialog dlg = new OwnerDialog(SwingUtilities.getWindowAncestor(this), service, selected);
        dlg.setVisible(true);
        if (dlg.isSaved()) {
            loadOwners();
        }
    }

    private void deleteSelectedOwner() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= currentOwners.size()) {
            JOptionPane.showMessageDialog(this, "Please select an owner to delete.", "Selection Required", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        PetOwner selected = currentOwners.get(row);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete owner '" + selected.getFullName() + "'?\nWarning: This will also remove all associated pets!",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                service.deleteOwner(selected.getId());
                loadOwners();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Delete failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
