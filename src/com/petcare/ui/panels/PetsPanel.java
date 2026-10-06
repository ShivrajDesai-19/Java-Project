package com.petcare.ui.panels;

import com.petcare.model.Pet;
import com.petcare.service.PetManagementService;
import com.petcare.ui.dialogs.PetDialog;
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
 * Pet Patients Management Panel
 */
public class PetsPanel extends JPanel {

    private final PetManagementService service;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JTextField searchField;
    private List<Pet> currentPets = new ArrayList<>();

    public PetsPanel(PetManagementService service) {
        this.service = service;
        setLayout(new BorderLayout(12, 12));
        setBackground(UIStyleUtil.COLOR_BG_LIGHT);
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Toolbar (Vertical stack guarantees search and action buttons never overlap)
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        searchPanel.setOpaque(false);
        searchPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        searchPanel.add(new JLabel("Search Pets:"));
        searchField = new JTextField(18);
        searchPanel.add(searchField);
        JButton searchBtn = UIStyleUtil.createSecondaryButton("Search");
        searchBtn.addActionListener(e -> performSearch());
        searchField.addActionListener(e -> performSearch());
        searchPanel.add(searchBtn);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        actionPanel.setOpaque(false);
        actionPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        JButton addBtn = UIStyleUtil.createPrimaryButton("+ Register Pet");
        JButton editBtn = UIStyleUtil.createSecondaryButton("Edit Pet");
        JButton deleteBtn = UIStyleUtil.createDangerButton("Delete");
        JButton refreshBtn = UIStyleUtil.createSecondaryButton("Refresh");

        addBtn.addActionListener(e -> openAddDialog());
        editBtn.addActionListener(e -> openEditDialog());
        deleteBtn.addActionListener(e -> deleteSelectedPet());
        refreshBtn.addActionListener(e -> loadPets());

        actionPanel.add(addBtn);
        actionPanel.add(editBtn);
        actionPanel.add(deleteBtn);
        actionPanel.add(refreshBtn);

        topPanel.add(searchPanel);
        topPanel.add(Box.createVerticalStrut(4));
        topPanel.add(actionPanel);
        add(topPanel, BorderLayout.NORTH);

        // Pets Table
        String[] columns = {"#", "ID", "Pet Name", "Species", "Breed", "Gender", "Birth Date", "Weight (kg)", "Color", "Microchip ID", "Owner Name"};
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

        loadPets();
    }

    public void loadPets() {
        try {
            currentPets = service.getAllPets();
            populateTable(currentPets);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load pets: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performSearch() {
        String query = searchField.getText().trim();
        try {
            currentPets = service.searchPets(query);
            populateTable(currentPets);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Search failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateTable(List<Pet> list) {
        tableModel.setRowCount(0);
        int srNo = 1;
        for (Pet p : list) {
            tableModel.addRow(new Object[]{
                srNo++,
                p.getId(),
                p.getName(),
                p.getSpecies(),
                p.getBreed(),
                p.getGender(),
                p.getDateOfBirth() != null ? p.getDateOfBirth().toString() : "N/A",
                p.getWeightKg(),
                p.getColor(),
                p.getMicrochipId(),
                p.getOwnerName()
            });
        }
    }

    private void openAddDialog() {
        PetDialog dlg = new PetDialog(SwingUtilities.getWindowAncestor(this), service, null);
        dlg.setVisible(true);
        if (dlg.isSaved()) {
            loadPets();
        }
    }

    private void openEditDialog() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= currentPets.size()) {
            JOptionPane.showMessageDialog(this, "Please select a pet to edit.", "Selection Required", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Pet selected = currentPets.get(row);
        PetDialog dlg = new PetDialog(SwingUtilities.getWindowAncestor(this), service, selected);
        dlg.setVisible(true);
        if (dlg.isSaved()) {
            loadPets();
        }
    }

    private void deleteSelectedPet() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= currentPets.size()) {
            JOptionPane.showMessageDialog(this, "Please select a pet to delete.", "Selection Required", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Pet selected = currentPets.get(row);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete pet patient '" + selected.getName() + "'?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                service.deletePet(selected.getId());
                loadPets();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Delete failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
