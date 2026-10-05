package com.petcare.ui.dialogs;

import com.petcare.model.PetOwner;
import com.petcare.service.PetManagementService;
import com.petcare.util.UIStyleUtil;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;

/**
 * Dialog for adding or updating a Pet Owner
 */
public class OwnerDialog extends JDialog {

    private final PetManagementService service;
    private final PetOwner existingOwner;
    private boolean saved = false;

    private final JTextField nameField;
    private final JTextField phoneField;
    private final JTextField emailField;
    private final JTextArea addressArea;
    private final JTextField emergencyField;

    public OwnerDialog(Window parent, PetManagementService service, PetOwner existingOwner) {
        super(parent, existingOwner == null ? "Register New Pet Owner" : "Edit Pet Owner Profile", ModalityType.APPLICATION_MODAL);
        this.service = service;
        this.existingOwner = existingOwner;

        setSize(500, 460);
        setResizable(true);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        nameField = new JTextField(existingOwner != null ? existingOwner.getFullName() : "", 20);
        phoneField = new JTextField(existingOwner != null ? existingOwner.getPhone() : "", 20);
        emailField = new JTextField(existingOwner != null ? existingOwner.getEmail() : "", 20);
        addressArea = new JTextArea(existingOwner != null ? existingOwner.getAddress() : "", 3, 20);
        addressArea.setLineWrap(true);
        emergencyField = new JTextField(existingOwner != null ? existingOwner.getEmergencyContact() : "", 20);

        int row = 0;
        addFormRow(formPanel, gbc, row++, "Full Name *:", nameField);
        addFormRow(formPanel, gbc, row++, "Phone Number *:", phoneField);
        addFormRow(formPanel, gbc, row++, "Email Address:", emailField);
        addFormRow(formPanel, gbc, row++, "Emergency Contact:", emergencyField);

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        formPanel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(new JScrollPane(addressArea), gbc);

        JScrollPane formScroll = new JScrollPane(formPanel);
        formScroll.setBorder(null);
        formScroll.getVerticalScrollBar().setUnitIncrement(12);
        add(formScroll, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = UIStyleUtil.createPrimaryButton("Save Owner");
        JButton cancelBtn = UIStyleUtil.createSecondaryButton("Cancel");

        saveBtn.addActionListener(e -> saveOwner());
        cancelBtn.addActionListener(e -> dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, JTextField field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        panel.add(field, gbc);
    }

    private void saveOwner() {
        try {
            if (existingOwner == null) {
                PetOwner owner = new PetOwner(
                    nameField.getText().trim(),
                    phoneField.getText().trim(),
                    emailField.getText().trim(),
                    addressArea.getText().trim(),
                    emergencyField.getText().trim()
                );
                service.registerOwner(owner);
            } else {
                existingOwner.setFullName(nameField.getText().trim());
                existingOwner.setPhone(phoneField.getText().trim());
                existingOwner.setEmail(emailField.getText().trim());
                existingOwner.setAddress(addressArea.getText().trim());
                existingOwner.setEmergencyContact(emergencyField.getText().trim());
                service.updateOwner(existingOwner);
            }
            saved = true;
            JOptionPane.showMessageDialog(this, "Pet owner saved successfully!", "Saved", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Validation / Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
