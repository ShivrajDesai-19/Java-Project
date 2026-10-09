package com.petcare.ui.dialogs;

import com.petcare.dao.UserDAO;
import com.petcare.model.DiagnosisRecord;
import com.petcare.model.LabTestRecord;
import com.petcare.model.Pet;
import com.petcare.model.User;
import com.petcare.model.UserRole;
import com.petcare.model.VaccinationRecord;
import com.petcare.service.MedicalService;
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
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.time.LocalDate;
import java.util.List;

/**
 * Dialog for adding Clinical Diagnoses, Vaccinations, and Lab Tests
 */
public class MedicalRecordDialog extends JDialog {

    private final MedicalService medicalService;
    private boolean saved = false;

    private final JComboBox<Pet> petCombo;
    private final JComboBox<User> vetCombo;

    // Diagnosis tab components
    private final JTextArea symptomsArea;
    private final JTextArea diagnosisArea;
    private final JTextArea treatmentArea;
    private final JTextField tempField;
    private final JTextField pulseField;

    // Vaccination tab components
    private final JTextField vaccineNameField;
    private final JTextField batchField;
    private final JTextField vaccDateField;
    private final JTextField nextDueField;
    private final JTextField vaccRemarksField;

    // Lab Test tab components
    private final JTextField testNameField;
    private final JComboBox<String> testCategoryCombo;
    private final JTextArea resultsArea;
    private final JTextField normalRangeField;
    private final JTextField conductedByField;

    private final JTabbedPane tabbedPane;

    public MedicalRecordDialog(Window parent, MedicalService medicalService, PetManagementService petService, int defaultPetId) {
        super(parent, "New Clinical Medical Record", ModalityType.APPLICATION_MODAL);
        this.medicalService = medicalService;

        setSize(620, 640);
        setResizable(true);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // Header Panel (Select Pet and Attending Vet)
        JPanel headerPanel = new JPanel(new GridBagLayout());
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UIStyleUtil.COLOR_BORDER),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        GridBagConstraints gbcH = new GridBagConstraints();
        gbcH.insets = new Insets(4, 6, 4, 6);
        gbcH.fill = GridBagConstraints.HORIZONTAL;

        petCombo = new JComboBox<>();
        try {
            List<Pet> pets = petService.getAllPets();
            for (Pet p : pets) {
                petCombo.addItem(p);
                if (p.getId() == defaultPetId) {
                    petCombo.setSelectedItem(p);
                }
            }
        } catch (Exception ignored) {}

        vetCombo = new JComboBox<>();
        try {
            List<User> vets = new UserDAO().findByRole(UserRole.VETERINARIAN);
            for (User v : vets) {
                vetCombo.addItem(v);
            }
        } catch (Exception ignored) {}

        gbcH.gridx = 0; gbcH.gridy = 0; gbcH.weightx = 0.2;
        headerPanel.add(new JLabel("Pet Patient:"), gbcH);
        gbcH.gridx = 1; gbcH.weightx = 0.8;
        headerPanel.add(petCombo, gbcH);

        gbcH.gridx = 0; gbcH.gridy = 1; gbcH.weightx = 0.2;
        headerPanel.add(new JLabel("Veterinarian:"), gbcH);
        gbcH.gridx = 1; gbcH.weightx = 0.8;
        headerPanel.add(vetCombo, gbcH);

        add(headerPanel, BorderLayout.NORTH);

        // Tabbed Panel
        tabbedPane = new JTabbedPane();

        // 1. Diagnosis Tab
        JPanel diagPanel = new JPanel(new GridBagLayout());
        diagPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        GridBagConstraints gbcD = new GridBagConstraints();
        gbcD.insets = new Insets(4, 4, 4, 4);
        gbcD.fill = GridBagConstraints.HORIZONTAL;

        symptomsArea = new JTextArea(3, 20); symptomsArea.setLineWrap(true);
        diagnosisArea = new JTextArea(3, 20); diagnosisArea.setLineWrap(true);
        treatmentArea = new JTextArea(3, 20); treatmentArea.setLineWrap(true);
        tempField = new JTextField("38.5", 10);
        pulseField = new JTextField("80", 10);

        int r = 0;
        gbcD.gridx = 0; gbcD.gridy = r; gbcD.weightx = 0.3; diagPanel.add(new JLabel("Presenting Symptoms:"), gbcD);
        gbcD.gridx = 1; gbcD.weightx = 0.7; diagPanel.add(new JScrollPane(symptomsArea), gbcD);
        r++;
        gbcD.gridx = 0; gbcD.gridy = r; gbcD.weightx = 0.3; diagPanel.add(new JLabel("Clinical Diagnosis *:"), gbcD);
        gbcD.gridx = 1; gbcD.weightx = 0.7; diagPanel.add(new JScrollPane(diagnosisArea), gbcD);
        r++;
        gbcD.gridx = 0; gbcD.gridy = r; gbcD.weightx = 0.3; diagPanel.add(new JLabel("Treatment & Prescription:"), gbcD);
        gbcD.gridx = 1; gbcD.weightx = 0.7; diagPanel.add(new JScrollPane(treatmentArea), gbcD);
        r++;
        gbcD.gridx = 0; gbcD.gridy = r; gbcD.weightx = 0.3; diagPanel.add(new JLabel("Temperature (°C):"), gbcD);
        gbcD.gridx = 1; gbcD.weightx = 0.7; diagPanel.add(tempField, gbcD);
        r++;
        gbcD.gridx = 0; gbcD.gridy = r; gbcD.weightx = 0.3; diagPanel.add(new JLabel("Pulse Rate (bpm):"), gbcD);
        gbcD.gridx = 1; gbcD.weightx = 0.7; diagPanel.add(pulseField, gbcD);

        JScrollPane diagScroll = new JScrollPane(diagPanel);
        diagScroll.setBorder(null);
        diagScroll.getVerticalScrollBar().setUnitIncrement(12);
        tabbedPane.addTab("Clinical Diagnosis", diagScroll);

        // 2. Vaccination Tab
        JPanel vaccPanel = new JPanel(new GridBagLayout());
        vaccPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        GridBagConstraints gbcV = new GridBagConstraints();
        gbcV.insets = new Insets(5, 5, 5, 5);
        gbcV.fill = GridBagConstraints.HORIZONTAL;

        vaccineNameField = new JTextField(20);
        batchField = new JTextField("BAT-VAC-" + (System.currentTimeMillis() % 1000), 20);
        vaccDateField = new JTextField(LocalDate.now().toString(), 20);
        nextDueField = new JTextField(LocalDate.now().plusYears(1).toString(), 20);
        vaccRemarksField = new JTextField("Pet tolerated vaccine well without adverse reaction.", 20);

        int vr = 0;
        addFormRow(vaccPanel, gbcV, vr++, "Vaccine Name *:", vaccineNameField);
        addFormRow(vaccPanel, gbcV, vr++, "Batch Number:", batchField);
        addFormRow(vaccPanel, gbcV, vr++, "Administered Date:", vaccDateField);
        addFormRow(vaccPanel, gbcV, vr++, "Next Due Date *:", nextDueField);
        addFormRow(vaccPanel, gbcV, vr++, "Clinical Remarks:", vaccRemarksField);

        JScrollPane vaccScroll = new JScrollPane(vaccPanel);
        vaccScroll.setBorder(null);
        vaccScroll.getVerticalScrollBar().setUnitIncrement(12);
        tabbedPane.addTab("Vaccination Tracking", vaccScroll);

        // 3. Lab Diagnostic Test Tab
        JPanel labPanel = new JPanel(new GridBagLayout());
        labPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        GridBagConstraints gbcL = new GridBagConstraints();
        gbcL.insets = new Insets(5, 5, 5, 5);
        gbcL.fill = GridBagConstraints.HORIZONTAL;

        testNameField = new JTextField("Complete Blood Count (CBC)", 20);
        testCategoryCombo = new JComboBox<>(new String[]{"Hematology", "Biochemistry", "Radiology / X-Ray", "Urinalysis", "Microbiology & Culture"});
        resultsArea = new JTextArea(3, 20); resultsArea.setLineWrap(true);
        normalRangeField = new JTextField("Standard reference range normal", 20);
        conductedByField = new JTextField("In-Clinic Pathology Laboratory", 20);

        int lr = 0;
        addFormRow(labPanel, gbcL, lr++, "Test Name *:", testNameField);
        addFormRow(labPanel, gbcL, lr++, "Test Category *:", testCategoryCombo);
        gbcL.gridx = 0; gbcL.gridy = lr; gbcL.weightx = 0.35; labPanel.add(new JLabel("Test Results:"), gbcL);
        gbcL.gridx = 1; gbcL.weightx = 0.65; labPanel.add(new JScrollPane(resultsArea), gbcL);
        lr++;
        addFormRow(labPanel, gbcL, lr++, "Normal Range:", normalRangeField);
        addFormRow(labPanel, gbcL, lr++, "Conducted By:", conductedByField);

        JScrollPane labScroll = new JScrollPane(labPanel);
        labScroll.setBorder(null);
        labScroll.getVerticalScrollBar().setUnitIncrement(12);
        tabbedPane.addTab("Laboratory Test", labScroll);

        add(tabbedPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = UIStyleUtil.createPrimaryButton("Save Record");
        JButton cancelBtn = UIStyleUtil.createSecondaryButton("Cancel");

        saveBtn.addActionListener(e -> saveRecord());
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

    private void saveRecord() {
        try {
            Pet pet = (Pet) petCombo.getSelectedItem();
            User vet = (User) vetCombo.getSelectedItem();
            if (pet == null) {
                JOptionPane.showMessageDialog(this, "Please select a registered pet patient.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int vetId = vet != null ? vet.getId() : 1;

            int selectedTab = tabbedPane.getSelectedIndex();

            if (selectedTab == 0) {
                // Diagnosis Record
                double temp = Double.parseDouble(tempField.getText().trim());
                int pulse = Integer.parseInt(pulseField.getText().trim());
                DiagnosisRecord diag = new DiagnosisRecord(
                    0, pet.getId(), vetId, LocalDate.now(),
                    symptomsArea.getText().trim(),
                    diagnosisArea.getText().trim(),
                    treatmentArea.getText().trim(),
                    temp, pulse
                );
                medicalService.recordDiagnosis(diag);

            } else if (selectedTab == 1) {
                // Vaccination
                VaccinationRecord vacc = new VaccinationRecord(
                    0, pet.getId(), vetId,
                    vaccineNameField.getText().trim(),
                    batchField.getText().trim(),
                    LocalDate.parse(vaccDateField.getText().trim()),
                    LocalDate.parse(nextDueField.getText().trim()),
                    "COMPLETED",
                    vaccRemarksField.getText().trim()
                );
                medicalService.recordVaccination(vacc);

            } else {
                // Lab Test
                LabTestRecord lab = new LabTestRecord(
                    0, pet.getId(), vetId, LocalDate.now(),
                    testNameField.getText().trim(),
                    (String) testCategoryCombo.getSelectedItem(),
                    resultsArea.getText().trim(),
                    normalRangeField.getText().trim(),
                    "COMPLETED",
                    conductedByField.getText().trim()
                );
                medicalService.recordLabTest(lab);
            }

            saved = true;
            JOptionPane.showMessageDialog(this, "Medical record created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving medical record: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
