package com.petcare.ui.panels;

import com.petcare.dao.UserDAO;
import com.petcare.model.User;
import com.petcare.ui.dialogs.StaffDialog;
import com.petcare.util.UIStyleUtil;

import javax.swing.BorderFactory;
import javax.swing.JButton;
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
 * Staff & Employee Management Panel (Administrator Access Only)
 */
public class StaffPanel extends JPanel {

    private final UserDAO userDAO = new UserDAO();
    private final DefaultTableModel tableModel;
    private final JTable table;
    private List<User> staffList = new ArrayList<>();

    public StaffPanel() {
        setLayout(new BorderLayout(12, 12));
        setBackground(UIStyleUtil.COLOR_BG_LIGHT);
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        topPanel.setOpaque(false);
        JButton addBtn = UIStyleUtil.createPrimaryButton("+ Add Staff Member");
        JButton editBtn = UIStyleUtil.createSecondaryButton("Edit Staff");
        JButton deleteBtn = UIStyleUtil.createDangerButton("Deactivate / Delete");
        JButton refreshBtn = UIStyleUtil.createSecondaryButton("Refresh");

        addBtn.addActionListener(e -> openAddDialog());
        editBtn.addActionListener(e -> openEditDialog());
        deleteBtn.addActionListener(e -> deleteSelected());
        refreshBtn.addActionListener(e -> loadStaff());

        topPanel.add(addBtn);
        topPanel.add(editBtn);
        topPanel.add(deleteBtn);
        topPanel.add(refreshBtn);

        add(topPanel, BorderLayout.NORTH);

        String[] columns = {"#", "ID", "Username", "Full Name", "Role", "Email", "Phone", "Specialization / Duty", "Status"};
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

        loadStaff();
    }

    public void loadStaff() {
        try {
            staffList = userDAO.findAll();
            tableModel.setRowCount(0);
            int srNo = 1;
            for (User u : staffList) {
                tableModel.addRow(new Object[]{
                    srNo++,
                    u.getId(),
                    u.getUsername(),
                    u.getFullName(),
                    u.getRole().getDisplayName(),
                    u.getEmail(),
                    u.getPhone(),
                    u.getSpecialization(),
                    u.getStatus()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load staff list: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openAddDialog() {
        StaffDialog dlg = new StaffDialog(SwingUtilities.getWindowAncestor(this), userDAO, null);
        dlg.setVisible(true);
        if (dlg.isSaved()) {
            loadStaff();
        }
    }

    private void openEditDialog() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= staffList.size()) {
            JOptionPane.showMessageDialog(this, "Please select an employee to edit.", "Selection Required", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        User selected = staffList.get(row);
        StaffDialog dlg = new StaffDialog(SwingUtilities.getWindowAncestor(this), userDAO, selected);
        dlg.setVisible(true);
        if (dlg.isSaved()) {
            loadStaff();
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= staffList.size()) {
            JOptionPane.showMessageDialog(this, "Please select an employee to delete.", "Selection Required", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        User selected = staffList.get(row);
        if ("admin".equalsIgnoreCase(selected.getUsername())) {
            JOptionPane.showMessageDialog(this, "The primary administrator account cannot be deleted.", "Protected Account", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to remove user '" + selected.getFullName() + "'?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                userDAO.delete(selected.getId());
                loadStaff();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to delete: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
