package com.petcare.ui.dialogs;

import com.petcare.model.Pet;
import com.petcare.model.PetOwner;
import com.petcare.service.PetManagementService;
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
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Dialog for registering or updating a Pet Patient
 */
public class PetDialog extends JDialog {

    private final PetManagementService service;
    private final Pet existingPet;
    private boolean saved = false;

    private final JComboBox<PetOwner> ownerCombo;
    private final JTextField nameField;
    private final JComboBox<String> speciesCombo;
    private final JTextField breedField;
    private final JComboBox<String> genderCombo;
    private final JTextField dobField;
    private final JTextField weightField;
    private final JTextField colorField;
    private final JTextField chipField;
    private final JTextField allergyField;
    private final JTextField bloodField;

    public PetDialog(Window parent, PetManagementService service, Pet existingPet) {
        super(parent, existingPet == null ? "Register New Pet Patient" : "Edit Pet Information", ModalityType.APPLICATION_MODAL);
        this.service = service;
        this.existingPet = existingPet;

        setSize(540, 620);
        setResizable(true);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        ownerCombo = new JComboBox<>();
        try {
            List<PetOwner> owners = service.getAllOwners();
            for (PetOwner o : owners) {
                ownerCombo.addItem(o);
                if (existingPet != null && o.getId() == existingPet.getOwnerId()) {
                    ownerCombo.setSelectedItem(o);
                }
            }
        } catch (Exception ignored) {}

        nameField = new JTextField(existingPet != null ? existingPet.getName() : "", 20);
        speciesCombo = new JComboBox<>(new String[]{"Dog", "Cat", "Bird", "Rabbit", "Hamster", "Reptile", "Other"});
        if (existingPet != null && existingPet.getSpecies() != null) {
            speciesCombo.setSelectedItem(existingPet.getSpecies());
        }
        breedField = new JTextField(existingPet != null ? existingPet.getBreed() : "", 20);
        genderCombo = new JComboBox<>(new String[]{"Male", "Female"});
        if (existingPet != null && existingPet.getGender() != null) {
            genderCombo.setSelectedItem(existingPet.getGender());
        }
        dobField = new JTextField(existingPet != null && existingPet.getDateOfBirth() != null ? existingPet.getDateOfBirth().toString() : "2023-01-01", 20);
        weightField = new JTextField(existingPet != null ? String.valueOf(existingPet.getWeightKg()) : "5.0", 20);
        colorField = new JTextField(existingPet != null ? existingPet.getColor() : "", 20);
        chipField = new JTextField(existingPet != null ? existingPet.getMicrochipId() : "", 20);
        allergyField = new JTextField(existingPet != null ? existingPet.getAllergies() : "None", 20);
        bloodField = new JTextField(existingPet != null ? existingPet.getBloodGroup() : "Unknown", 20);

        int row = 0;
        addFormRow(formPanel, gbc, row++, "Select Owner *:", ownerCombo);
        addFormRow(formPanel, gbc, row++, "Pet Name *:", nameField);
        addFormRow(formPanel, gbc, row++, "Species *:", speciesCombo);
        addFormRow(formPanel, gbc, row++, "Breed:", breedField);
        addFormRow(formPanel, gbc, row++, "Gender *:", genderCombo);
        addFormRow(formPanel, gbc, row++, "Birth Date (YYYY-MM-DD):", dobField);
        addFormRow(formPanel, gbc, row++, "Weight (kg):", weightField);
        addFormRow(formPanel, gbc, row++, "Color / Markings:", colorField);
        addFormRow(formPanel, gbc, row++, "Microchip ID:", chipField);
        addFormRow(formPanel, gbc, row++, "Known Allergies:", allergyField);
        addFormRow(formPanel, gbc, row++, "Blood Group:", bloodField);

        JScrollPane formScroll = new JScrollPane(formPanel);
        formScroll.setBorder(null);
        formScroll.getVerticalScrollBar().setUnitIncrement(12);
        add(formScroll, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = UIStyleUtil.createPrimaryButton("Save Pet");
        JButton cancelBtn = UIStyleUtil.createSecondaryButton("Cancel");

        saveBtn.addActionListener(e -> savePet());
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

    private void savePet() {
        try {
            PetOwner selectedOwner = (PetOwner) ownerCombo.getSelectedItem();
            if (selectedOwner == null) {
                JOptionPane.showMessageDialog(this, "Please select an owner.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            LocalDate dob = null;
            if (!dobField.getText().trim().isEmpty()) {
                try {
                    dob = LocalDate.parse(dobField.getText().trim());
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.", "Date Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            double weight = 0.0;
            if (!weightField.getText().trim().isEmpty()) {
                weight = Double.parseDouble(weightField.getText().trim());
            }

            if (existingPet == null) {
                Pet pet = new Pet(
                    selectedOwner.getId(),
                    nameField.getText().trim(),
                    (String) speciesCombo.getSelectedItem(),
                    breedField.getText().trim(),
                    (String) genderCombo.getSelectedItem(),
                    dob,
                    weight,
                    colorField.getText().trim(),
                    chipField.getText().trim(),
                    allergyField.getText().trim(),
                    bloodField.getText().trim()
                );
                service.registerPet(pet);
            } else {
                existingPet.setOwnerId(selectedOwner.getId());
                existingPet.setName(nameField.getText().trim());
                existingPet.setSpecies((String) speciesCombo.getSelectedItem());
                existingPet.setBreed(breedField.getText().trim());
                existingPet.setGender((String) genderCombo.getSelectedItem());
                existingPet.setDateOfBirth(dob);
                existingPet.setWeightKg(weight);
                existingPet.setColor(colorField.getText().trim());
                existingPet.setMicrochipId(chipField.getText().trim());
                existingPet.setAllergies(allergyField.getText().trim());
                existingPet.setBloodGroup(bloodField.getText().trim());
                service.updatePet(existingPet);
            }

            saved = true;
            JOptionPane.showMessageDialog(this, "Pet record saved successfully!", "Saved", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving pet: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
