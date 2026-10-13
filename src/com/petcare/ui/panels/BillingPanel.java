package com.petcare.ui.panels;

import com.petcare.dao.MedicineDAO;
import com.petcare.dao.PetOwnerDAO;
import com.petcare.model.Invoice;
import com.petcare.model.InvoiceItem;
import com.petcare.model.Medicine;
import com.petcare.model.Pet;
import com.petcare.model.PetOwner;
import com.petcare.service.BillingService;
import com.petcare.service.PetManagementService;
import com.petcare.util.ReceiptPrinter;
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
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Billing, Invoicing & POS Payment Processing Panel
 */
public class BillingPanel extends JPanel {

    private final BillingService billingService;
    private final PetManagementService petService;
    private final MedicineDAO medicineDAO = new MedicineDAO();
    private final PetOwnerDAO ownerDAO = new PetOwnerDAO();

    // Active Invoice creation state
    private final Invoice activeInvoice = new Invoice();
    private final DefaultTableModel currentItemsModel;
    private final JTable currentItemsTable;

    private final JComboBox<Pet> petCombo;
    private final JLabel ownerNameLabel;
    private final JComboBox<String> itemTypeCombo;
    private final JComboBox<Medicine> medicineCombo;
    private final JTextField customDescField;
    private final JTextField itemPriceField;
    private final JTextField itemQtyField;

    private final JLabel subtotalLabel;
    private final JLabel taxLabel;
    private final JTextField discountField;
    private final JLabel grandTotalLabel;
    private final JComboBox<String> paymentMethodCombo;

    // Past Invoices Table
    private final DefaultTableModel pastInvoicesModel;
    private final JTable pastInvoicesTable;
    private List<Invoice> allInvoices = new ArrayList<>();

    public BillingPanel(BillingService billingService, PetManagementService petService) {
        this.billingService = billingService;
        this.petService = petService;

        setLayout(new BorderLayout(12, 12));
        setBackground(UIStyleUtil.COLOR_BG_LIGHT);
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // LEFT PANEL: Invoice Builder / POS Terminal
        JPanel posPanel = new JPanel(new BorderLayout(10, 10));
        posPanel.setBackground(UIStyleUtil.COLOR_BG_CARD);
        posPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIStyleUtil.COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        JLabel posTitle = new JLabel("Create New Invoice / Bill");
        posTitle.setFont(UIStyleUtil.FONT_HEADER_MED);
        posTitle.setForeground(UIStyleUtil.COLOR_PRIMARY);
        posPanel.add(posTitle, BorderLayout.NORTH);

        // Pet & Owner selector
        JPanel clientPanel = new JPanel(new GridBagLayout());
        clientPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        petCombo = new JComboBox<>();
        petCombo.addActionListener(e -> updateSelectedPet());
        ownerNameLabel = new JLabel("Owner: -");
        ownerNameLabel.setFont(UIStyleUtil.FONT_BODY_BOLD);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3; clientPanel.add(new JLabel("Pet Patient:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7; clientPanel.add(petCombo, gbc);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; clientPanel.add(ownerNameLabel, gbc);
        gbc.gridwidth = 1;

        // Item Adder Panel
        JPanel itemAddPanel = new JPanel(new GridBagLayout());
        itemAddPanel.setBorder(BorderFactory.createTitledBorder("Add Billable Item"));
        itemAddPanel.setOpaque(false);

        itemTypeCombo = new JComboBox<>(new String[]{"CONSULTATION", "MEDICINE", "VACCINATION", "LAB_TEST", "GROOMING", "BOARDING"});
        medicineCombo = new JComboBox<>();
        medicineCombo.setEnabled(false);
        customDescField = new JTextField("Veterinary Health Consultation", 16);
        itemPriceField = new JTextField("500.0", 8);
        itemQtyField = new JTextField("1", 4);

        itemTypeCombo.addActionListener(e -> handleItemTypeSelection());
        medicineCombo.addActionListener(e -> handleMedicineSelection());

        int ir = 0;
        gbc.gridx = 0; gbc.gridy = ir; gbc.weightx = 0.3; itemAddPanel.add(new JLabel("Item Type:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7; itemAddPanel.add(itemTypeCombo, gbc);
        ir++;
        gbc.gridx = 0; gbc.gridy = ir; gbc.weightx = 0.3; itemAddPanel.add(new JLabel("Pharmacy Med:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7; itemAddPanel.add(medicineCombo, gbc);
        ir++;
        gbc.gridx = 0; gbc.gridy = ir; gbc.weightx = 0.3; itemAddPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7; itemAddPanel.add(customDescField, gbc);
        ir++;
        gbc.gridx = 0; gbc.gridy = ir; gbc.weightx = 0.3; itemAddPanel.add(new JLabel("Price (₹):"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7; itemAddPanel.add(itemPriceField, gbc);
        ir++;
        gbc.gridx = 0; gbc.gridy = ir; gbc.weightx = 0.3; itemAddPanel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7; itemAddPanel.add(itemQtyField, gbc);
        ir++;
        JButton addItemBtn = UIStyleUtil.createSecondaryButton("+ Add to Invoice");
        addItemBtn.addActionListener(e -> addItemToActiveInvoice());
        gbc.gridx = 1; gbc.gridy = ir; itemAddPanel.add(addItemBtn, gbc);

        JPanel topPosStack = new JPanel(new BorderLayout(5, 5));
        topPosStack.setOpaque(false);
        topPosStack.add(clientPanel, BorderLayout.NORTH);
        topPosStack.add(itemAddPanel, BorderLayout.CENTER);
        posPanel.add(topPosStack, BorderLayout.NORTH);

        // Current items table
        String[] itemCols = {"Description", "Type", "Qty", "Price", "Total"};
        currentItemsModel = new DefaultTableModel(itemCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        currentItemsTable = new JTable(currentItemsModel);
        UIStyleUtil.styleTable(currentItemsTable);

        JPanel itemsTablePanel = new JPanel(new BorderLayout(0, 4));
        itemsTablePanel.setPreferredSize(new Dimension(360, 160));
        itemsTablePanel.setMinimumSize(new Dimension(300, 120));
        itemsTablePanel.add(new JScrollPane(currentItemsTable), BorderLayout.CENTER);
        JButton removeLineBtn = UIStyleUtil.createSecondaryButton("Remove Selected Item");
        removeLineBtn.addActionListener(e -> removeSelectedItem());
        JPanel removeBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        removeBtnPanel.setOpaque(false);
        removeBtnPanel.add(removeLineBtn);
        itemsTablePanel.add(removeBtnPanel, BorderLayout.SOUTH);
        posPanel.add(itemsTablePanel, BorderLayout.CENTER);

        // Financial totals & Payment panel with GridBagLayout (prevents grid overlapping)
        JPanel totalsPanel = new JPanel(new GridBagLayout());
        totalsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Bill Summary"),
            BorderFactory.createEmptyBorder(6, 8, 8, 8)
        ));
        totalsPanel.setOpaque(false);
        GridBagConstraints gbcT = new GridBagConstraints();
        gbcT.insets = new Insets(4, 6, 4, 6);
        gbcT.fill = GridBagConstraints.HORIZONTAL;

        subtotalLabel = new JLabel("₹0.00");
        subtotalLabel.setFont(UIStyleUtil.FONT_BODY_BOLD);
        taxLabel = new JLabel("₹0.00");
        discountField = new JTextField("0.0", 8);
        discountField.addActionListener(e -> updateDiscount());
        grandTotalLabel = new JLabel("₹0.00");
        grandTotalLabel.setFont(UIStyleUtil.FONT_HEADER_MED);
        grandTotalLabel.setForeground(UIStyleUtil.COLOR_PRIMARY);

        paymentMethodCombo = new JComboBox<>(new String[]{"Cash", "UPI", "Credit Card", "Debit Card", "NetBanking"});

        int tr = 0;
        gbcT.gridx = 0; gbcT.gridy = tr; gbcT.weightx = 0.4; totalsPanel.add(new JLabel("Subtotal:"), gbcT);
        gbcT.gridx = 1; gbcT.weightx = 0.6; totalsPanel.add(subtotalLabel, gbcT);
        tr++;
        gbcT.gridx = 0; gbcT.gridy = tr; gbcT.weightx = 0.4; totalsPanel.add(new JLabel("Tax (5% GST):"), gbcT);
        gbcT.gridx = 1; gbcT.weightx = 0.6; totalsPanel.add(taxLabel, gbcT);
        tr++;
        gbcT.gridx = 0; gbcT.gridy = tr; gbcT.weightx = 0.4; totalsPanel.add(new JLabel("Discount (₹):"), gbcT);
        gbcT.gridx = 1; gbcT.weightx = 0.6; totalsPanel.add(discountField, gbcT);
        tr++;
        gbcT.gridx = 0; gbcT.gridy = tr; gbcT.weightx = 0.4; totalsPanel.add(new JLabel("Grand Total:"), gbcT);
        gbcT.gridx = 1; gbcT.weightx = 0.6; totalsPanel.add(grandTotalLabel, gbcT);
        tr++;
        gbcT.gridx = 0; gbcT.gridy = tr; gbcT.weightx = 0.4; totalsPanel.add(new JLabel("Payment Mode:"), gbcT);
        gbcT.gridx = 1; gbcT.weightx = 0.6; totalsPanel.add(paymentMethodCombo, gbcT);

        JButton generateInvoiceBtn = UIStyleUtil.createSuccessButton("Generate & Settle Invoice");
        generateInvoiceBtn.addActionListener(e -> saveAndSettleInvoice());

        JPanel bottomPos = new JPanel(new BorderLayout(0, 8));
        bottomPos.setOpaque(false);
        bottomPos.add(totalsPanel, BorderLayout.CENTER);
        bottomPos.add(generateInvoiceBtn, BorderLayout.SOUTH);
        posPanel.add(bottomPos, BorderLayout.SOUTH);

        // RIGHT PANEL: Invoices Archive / History
        JPanel historyPanel = new JPanel(new BorderLayout(10, 10));
        historyPanel.setBackground(UIStyleUtil.COLOR_BG_CARD);
        historyPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIStyleUtil.COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        JPanel histHeader = new JPanel();
        histHeader.setLayout(new BoxLayout(histHeader, BoxLayout.Y_AXIS));
        histHeader.setOpaque(false);

        JLabel histTitle = new JLabel("Invoice History & Surcharges");
        histTitle.setFont(UIStyleUtil.FONT_HEADER_MED);
        histTitle.setForeground(UIStyleUtil.COLOR_PRIMARY);
        histTitle.setAlignmentX(JLabel.LEFT_ALIGNMENT);

        JPanel histActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        histActions.setOpaque(false);
        histActions.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        JButton viewReceiptBtn = UIStyleUtil.createPrimaryButton("View / Print Receipt");
        JButton refreshHistBtn = UIStyleUtil.createSecondaryButton("Refresh");

        viewReceiptBtn.addActionListener(e -> viewSelectedInvoiceReceipt());
        refreshHistBtn.addActionListener(e -> loadPastInvoices());

        histActions.add(viewReceiptBtn);
        histActions.add(refreshHistBtn);

        histHeader.add(histTitle);
        histHeader.add(Box.createVerticalStrut(6));
        histHeader.add(histActions);
        historyPanel.add(histHeader, BorderLayout.NORTH);

        String[] invCols = {"Invoice No", "Date", "Pet Patient", "Owner", "Mode", "Total (₹)", "Status"};
        pastInvoicesModel = new DefaultTableModel(invCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        pastInvoicesTable = new JTable(pastInvoicesModel);
        UIStyleUtil.styleTable(pastInvoicesTable);
        historyPanel.add(new JScrollPane(pastInvoicesTable), BorderLayout.CENTER);

        JScrollPane posScroll = new JScrollPane(posPanel);
        posScroll.setBorder(null);
        posScroll.getVerticalScrollBar().setUnitIncrement(16);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, posScroll, historyPanel);
        splitPane.setResizeWeight(0.48);
        splitPane.setDividerSize(8);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);

        add(splitPane, BorderLayout.CENTER);

        loadInitialData();
    }

    public void loadInitialData() {
        petCombo.removeAllItems();
        medicineCombo.removeAllItems();
        try {
            List<Pet> pets = petService.getAllPets();
            for (Pet p : pets) petCombo.addItem(p);

            List<Medicine> meds = medicineDAO.findAll();
            for (Medicine m : meds) medicineCombo.addItem(m);

            updateSelectedPet();
            loadPastInvoices();
        } catch (Exception ex) {
            System.err.println("Error initializing billing panel: " + ex.getMessage());
        }
    }

    private void updateSelectedPet() {
        Pet pet = (Pet) petCombo.getSelectedItem();
        if (pet != null) {
            ownerNameLabel.setText("Owner: " + (pet.getOwnerName() != null ? pet.getOwnerName() : "ID: " + pet.getOwnerId()));
            activeInvoice.setPetId(pet.getId());
            activeInvoice.setPetName(pet.getName());
            activeInvoice.setOwnerId(pet.getOwnerId());
            activeInvoice.setOwnerName(pet.getOwnerName());
        }
    }

    private void handleItemTypeSelection() {
        String type = (String) itemTypeCombo.getSelectedItem();
        if ("MEDICINE".equals(type)) {
            medicineCombo.setEnabled(true);
            handleMedicineSelection();
        } else {
            medicineCombo.setEnabled(false);
            if ("CONSULTATION".equals(type)) {
                customDescField.setText("Veterinary Health Consultation & Checkup");
                itemPriceField.setText("500.0");
            } else if ("VACCINATION".equals(type)) {
                customDescField.setText("Core Vaccine Administration (Rabies/DHPP)");
                itemPriceField.setText("350.0");
            } else if ("LAB_TEST".equals(type)) {
                customDescField.setText("Hematology Diagnostic CBC Test");
                itemPriceField.setText("450.0");
            } else if ("GROOMING".equals(type)) {
                customDescField.setText("Standard Pet Spa & Bath Service");
                itemPriceField.setText("800.0");
            } else if ("BOARDING".equals(type)) {
                customDescField.setText("Pet Kennel Boarding Stay (1 Day)");
                itemPriceField.setText("800.0");
            }
        }
    }

    private void handleMedicineSelection() {
        Medicine m = (Medicine) medicineCombo.getSelectedItem();
        if (m != null) {
            customDescField.setText(m.getName() + " (" + m.getCategory() + ")");
            itemPriceField.setText(String.valueOf(m.getUnitPrice()));
        }
    }

    private void addItemToActiveInvoice() {
        try {
            String desc = customDescField.getText().trim();
            String type = (String) itemTypeCombo.getSelectedItem();
            double price = Double.parseDouble(itemPriceField.getText().trim());
            int qty = Integer.parseInt(itemQtyField.getText().trim());

            if (desc.isEmpty() || price < 0 || qty <= 0) {
                JOptionPane.showMessageDialog(this, "Please enter valid item description, positive price and quantity.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            InvoiceItem item = new InvoiceItem(desc, type, qty, price);
            activeInvoice.addItem(item);

            currentItemsModel.addRow(new Object[]{
                item.getDescription(),
                item.getItemType(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getTotalPrice()
            });

            updateDisplayTotals();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Price and Quantity must be valid numeric values.", "Number Format Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeSelectedItem() {
        int row = currentItemsTable.getSelectedRow();
        if (row >= 0) {
            activeInvoice.removeItem(row);
            currentItemsModel.removeRow(row);
            updateDisplayTotals();
        }
    }

    private void updateDiscount() {
        try {
            double disc = Double.parseDouble(discountField.getText().trim());
            activeInvoice.setDiscountAmount(disc);
            updateDisplayTotals();
        } catch (NumberFormatException ignored) {}
    }

    private void updateDisplayTotals() {
        activeInvoice.recalculateTotals();
        subtotalLabel.setText("₹" + String.format("%.2f", activeInvoice.getSubtotal()));
        taxLabel.setText("₹" + String.format("%.2f", activeInvoice.getTaxAmount()));
        grandTotalLabel.setText("₹" + String.format("%.2f", activeInvoice.getTotalAmount()));
    }

    private void saveAndSettleInvoice() {
        try {
            if (activeInvoice.getItems().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please add items to invoice before generating bill.", "Invoice Empty", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Pet pet = (Pet) petCombo.getSelectedItem();
            if (pet == null) {
                JOptionPane.showMessageDialog(this, "Please select a pet patient.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            activeInvoice.setPetId(pet.getId());
            activeInvoice.setOwnerId(pet.getOwnerId());
            activeInvoice.setPaymentMethod((String) paymentMethodCombo.getSelectedItem());
            activeInvoice.setPaymentStatus("PAID");
            activeInvoice.setInvoiceDate(LocalDate.now());

            billingService.processInvoice(activeInvoice);

            JOptionPane.showMessageDialog(this, "Invoice " + activeInvoice.getInvoiceNumber() + " successfully generated and saved!", "Invoice Settled", JOptionPane.INFORMATION_MESSAGE);

            // Open Receipt Dialog immediately for printing
            ReceiptPrinter.showReceiptDialog(SwingUtilities.getWindowAncestor(this), activeInvoice);

            // Reset active invoice
            currentItemsModel.setRowCount(0);
            activeInvoice.getItems().clear();
            discountField.setText("0.0");
            updateDisplayTotals();
            loadPastInvoices();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to process invoice: " + ex.getMessage(), "Billing Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadPastInvoices() {
        try {
            allInvoices = billingService.getAllInvoices();
            pastInvoicesModel.setRowCount(0);
            for (Invoice inv : allInvoices) {
                pastInvoicesModel.addRow(new Object[]{
                    inv.getInvoiceNumber(),
                    inv.getInvoiceDate(),
                    inv.getPetName(),
                    inv.getOwnerName(),
                    inv.getPaymentMethod(),
                    inv.getTotalAmount(),
                    inv.getPaymentStatus()
                });
            }
        } catch (Exception ex) {
            System.err.println("Could not load invoices: " + ex.getMessage());
        }
    }

    private void viewSelectedInvoiceReceipt() {
        int row = pastInvoicesTable.getSelectedRow();
        if (row < 0 || row >= allInvoices.size()) {
            JOptionPane.showMessageDialog(this, "Please select an invoice from history to view receipt.", "Selection Required", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        try {
            Invoice inv = billingService.getInvoiceById(allInvoices.get(row).getId());
            if (inv != null) {
                ReceiptPrinter.showReceiptDialog(SwingUtilities.getWindowAncestor(this), inv);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error viewing receipt: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
