package com.petcare.ui.dialogs;

import com.petcare.model.Medicine;
import com.petcare.service.PharmacyService;
import com.petcare.util.UIStyleUtil;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.time.LocalDate;

/**
 * Dialog for adding or updating Pharmacy Medicines
 */
public class MedicineDialog extends JDialog {

    private final PharmacyService pharmacyService;
    private final Medicine existingMed;
    private boolean saved = false;

    private final JTextField nameField;
    private final JComboBox<String> categoryCombo;
    private final JTextField batchField;
    private final JTextField stockField;
    private final JTextField minStockField;
    private final JTextField priceField;
    private final JTextField expiryField;
    private final JTextField supplierField;

    public MedicineDialog(Window parent, PharmacyService pharmacyService, Medicine existingMed) {
        super(parent, existingMed == null ? "Add Medicine to Inventory" : "Update Medicine Stock", ModalityType.APPLICATION_MODAL);
        this.pharmacyService = pharmacyService;
        this.existingMed = existingMed;

        setSize(520, 520);
        setResizable(true);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        nameField = new JTextField(existingMed != null ? existingMed.getName() : "", 20);
        String[] cats = {"Antibiotic", "Painkiller / NSAID", "Vaccine", "Steroid", "Antiseptic", "Vitamin & Supplement", "Antiparasitic", "Ophthalmic / Ear Drops"};
        categoryCombo = new JComboBox<>(cats);
        if (existingMed != null && existingMed.getCategory() != null) {
            categoryCombo.setSelectedItem(existingMed.getCategory());
        }
        batchField = new JTextField(existingMed != null ? existingMed.getBatchNumber() : "BAT-" + System.currentTimeMillis() % 10000, 20);
        stockField = new JTextField(existingMed != null ? String.valueOf(existingMed.getStockQuantity()) : "20", 20);
        minStockField = new JTextField(existingMed != null ? String.valueOf(existingMed.getMinStockAlert()) : "10", 20);
        priceField = new JTextField(existingMed != null ? String.valueOf(existingMed.getUnitPrice()) : "250.0", 20);
        expiryField = new JTextField(existingMed != null && existingMed.getExpiryDate() != null ? 
            existingMed.getExpiryDate().toString() : LocalDate.now().plusYears(1).toString(), 20);
        supplierField = new JTextField(existingMed != null ? existingMed.getSupplier() : "PetCare Pharma Ltd", 20);

        int row = 0;
        addFormRow(formPanel, gbc, row++, "Medicine Name *:", nameField);
        addFormRow(formPanel, gbc, row++, "Category *:", categoryCombo);
        addFormRow(formPanel, gbc, row++, "Batch Number *:", batchField);
        addFormRow(formPanel, gbc, row++, "Stock Quantity *:", stockField);
        addFormRow(formPanel, gbc, row++, "Low Stock Alert Level:", minStockField);
        addFormRow(formPanel, gbc, row++, "Unit Price (₹) *:", priceField);
        addFormRow(formPanel, gbc, row++, "Expiry Date (YYYY-MM-DD):", expiryField);
        addFormRow(formPanel, gbc, row++, "Supplier / Manufacturer:", supplierField);

        JScrollPane formScroll = new JScrollPane(formPanel);
        formScroll.setBorder(null);
        formScroll.getVerticalScrollBar().setUnitIncrement(12);
        add(formScroll, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = UIStyleUtil.createPrimaryButton("Save Medicine");
        JButton cancelBtn = UIStyleUtil.createSecondaryButton("Cancel");

        saveBtn.addActionListener(e -> saveMedicine());
        cancelBtn.addActionListener(e -> dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, javax.swing.JComponent comp) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        panel.add(comp, gbc);
    }

    private void saveMedicine() {
        try {
            String name = nameField.getText().trim();
            String cat = (String) categoryCombo.getSelectedItem();
            String batch = batchField.getText().trim();
            int stock = Integer.parseInt(stockField.getText().trim());
            int minStock = Integer.parseInt(minStockField.getText().trim());
            double price = Double.parseDouble(priceField.getText().trim());
            LocalDate expiry = LocalDate.parse(expiryField.getText().trim());
            String supplier = supplierField.getText().trim();

            if (existingMed == null) {
                Medicine med = new Medicine(0, name, cat, batch, stock, minStock, price, expiry, supplier);
                pharmacyService.addMedicine(med);
            } else {
                existingMed.setName(name);
                existingMed.setCategory(cat);
                existingMed.setBatchNumber(batch);
                existingMed.setStockQuantity(stock);
                existingMed.setMinStockAlert(minStock);
                existingMed.setUnitPrice(price);
                existingMed.setExpiryDate(expiry);
                existingMed.setSupplier(supplier);
                pharmacyService.updateMedicine(existingMed);
            }

            saved = true;
            JOptionPane.showMessageDialog(this, "Medicine record saved successfully!", "Saved", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Validation / Save Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
