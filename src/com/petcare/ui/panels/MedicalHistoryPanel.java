package com.petcare.ui.panels;

import com.petcare.model.DiagnosisRecord;
import com.petcare.model.LabTestRecord;
import com.petcare.model.Pet;
import com.petcare.model.VaccinationRecord;
import com.petcare.service.MedicalService;
import com.petcare.service.PetManagementService;
import com.petcare.ui.dialogs.MedicalRecordDialog;
import com.petcare.util.UIStyleUtil;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

/**
 * Medical History, Consultations, Vaccinations & Diagnostics Panel
 */
public class MedicalHistoryPanel extends JPanel {

    private final MedicalService medicalService;
    private final PetManagementService petService;

    private final JComboBox<Pet> petSelectorCombo;
    private final DefaultTableModel diagTableModel;
    private final DefaultTableModel vaccTableModel;
    private final DefaultTableModel labTableModel;

    public MedicalHistoryPanel(MedicalService medicalService, PetManagementService petService) {
        this.medicalService = medicalService;
        this.petService = petService;

        setLayout(new BorderLayout(12, 12));
        setBackground(UIStyleUtil.COLOR_BG_LIGHT);
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Top Toolbar (Vertical stack guarantees selector and action buttons never overlap)
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);

        JPanel selectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        selectPanel.setOpaque(false);
        selectPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        selectPanel.add(new JLabel("Select Pet Patient:"));
        petSelectorCombo = new JComboBox<>();
        petSelectorCombo.addActionListener(e -> loadSelectedPetHistory());
        selectPanel.add(petSelectorCombo);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        actionPanel.setOpaque(false);
        actionPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        JButton addBtn = UIStyleUtil.createPrimaryButton("+ Add Clinical Record");
        JButton printBtn = UIStyleUtil.createSecondaryButton("Print Medical Summary");
        JButton refreshBtn = UIStyleUtil.createSecondaryButton("Refresh");

        addBtn.addActionListener(e -> openAddDialog());
        printBtn.addActionListener(e -> printMedicalSummary());
        refreshBtn.addActionListener(e -> reloadPets());

        actionPanel.add(addBtn);
        actionPanel.add(printBtn);
        actionPanel.add(refreshBtn);

        topPanel.add(selectPanel);
        topPanel.add(Box.createVerticalStrut(4));
        topPanel.add(actionPanel);
        add(topPanel, BorderLayout.NORTH);

        // Sub Tabbed Pane
        JTabbedPane tabs = new JTabbedPane();

        // 1. Diagnoses Table
        String[] diagCols = {"Visit Date", "Doctor", "Diagnosis", "Symptoms", "Treatment Plan", "Temp (°C)", "Pulse"};
        diagTableModel = new DefaultTableModel(diagCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable diagTable = new JTable(diagTableModel);
        UIStyleUtil.styleTable(diagTable);
        tabs.addTab("Clinical Consultations & Diagnoses", new JScrollPane(diagTable));

        // 2. Vaccinations Table
        String[] vaccCols = {"Vaccine Name", "Batch No", "Administered Date", "Next Booster Due", "Status", "Attending Vet", "Remarks"};
        vaccTableModel = new DefaultTableModel(vaccCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable vaccTable = new JTable(vaccTableModel);
        UIStyleUtil.styleTable(vaccTable);
        tabs.addTab("Vaccinations & Immunizations", new JScrollPane(vaccTable));

        // 3. Lab Tests Table
        String[] labCols = {"Test Name", "Category", "Test Date", "Results", "Reference Range", "Conducted By", "Status"};
        labTableModel = new DefaultTableModel(labCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable labTable = new JTable(labTableModel);
        UIStyleUtil.styleTable(labTable);
        tabs.addTab("Laboratory Diagnostic Tests", new JScrollPane(labTable));

        add(tabs, BorderLayout.CENTER);

        reloadPets();
    }

    public void reloadPets() {
        petSelectorCombo.removeAllItems();
        try {
            List<Pet> pets = petService.getAllPets();
            for (Pet p : pets) {
                petSelectorCombo.addItem(p);
            }
            loadSelectedPetHistory();
        } catch (Exception ex) {
            System.err.println("Could not load pets: " + ex.getMessage());
        }
    }

    public void loadSelectedPetHistory() {
        diagTableModel.setRowCount(0);
        vaccTableModel.setRowCount(0);
        labTableModel.setRowCount(0);

        Pet pet = (Pet) petSelectorCombo.getSelectedItem();
        if (pet == null) return;

        try {
            // Load Diagnoses
            List<DiagnosisRecord> diagnoses = medicalService.getDiagnosesForPet(pet.getId());
            for (DiagnosisRecord d : diagnoses) {
                diagTableModel.addRow(new Object[]{
                    d.getVisitDate(),
                    d.getVetName(),
                    d.getDiagnosis(),
                    d.getSymptoms(),
                    d.getTreatmentPlan(),
                    d.getTemperatureC(),
                    d.getPulseRate()
                });
            }

            // Load Vaccinations
            List<VaccinationRecord> vaccinations = medicalService.getVaccinationsForPet(pet.getId());
            for (VaccinationRecord v : vaccinations) {
                vaccTableModel.addRow(new Object[]{
                    v.getVaccineName(),
                    v.getBatchNo(),
                    v.getDateAdministered(),
                    v.getNextDueDate(),
                    v.getStatus(),
                    v.getVetName(),
                    v.getRemarks()
                });
            }

            // Load Lab Tests
            List<LabTestRecord> labTests = medicalService.getLabTestsForPet(pet.getId());
            for (LabTestRecord l : labTests) {
                labTableModel.addRow(new Object[]{
                    l.getTestName(),
                    l.getTestCategory(),
                    l.getVisitDate(),
                    l.getResults(),
                    l.getNormalRange(),
                    l.getConductedBy(),
                    l.getStatus()
                });
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load medical history: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openAddDialog() {
        Pet pet = (Pet) petSelectorCombo.getSelectedItem();
        int petId = pet != null ? pet.getId() : 0;
        MedicalRecordDialog dlg = new MedicalRecordDialog(SwingUtilities.getWindowAncestor(this), medicalService, petService, petId);
        dlg.setVisible(true);
        if (dlg.isSaved()) {
            loadSelectedPetHistory();
        }
    }

    private void printMedicalSummary() {
        Pet pet = (Pet) petSelectorCombo.getSelectedItem();
        if (pet == null) {
            JOptionPane.showMessageDialog(this, "Please select a pet patient first.", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Unit 2 Concept: Using StringBuilder to construct clinical summary document
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='font-family: Arial, sans-serif; padding: 15px;'>");
        sb.append("<h2 style='color: #184C78;'>CLINICAL HEALTH SUMMARY - ").append(pet.getName()).append("</h2>");
        sb.append("<p><b>Species / Breed:</b> ").append(pet.getSpecies()).append(" (").append(pet.getBreed()).append(")<br>");
        sb.append("<b>Owner:</b> ").append(pet.getOwnerName()).append(" | <b>Weight:</b> ").append(pet.getWeightKg()).append(" kg<br>");
        sb.append("<b>Allergies:</b> ").append(pet.getAllergies()).append(" | <b>Blood Group:</b> ").append(pet.getBloodGroup()).append("</p>");
        sb.append("<hr style='border: 1px solid #184C78;'>");

        sb.append("<h3>Recent Consultations</h3><ul>");
        for (int i = 0; i < diagTableModel.getRowCount(); i++) {
            sb.append("<li><b>").append(diagTableModel.getValueAt(i, 0)).append("</b> - Diagnosis: ")
              .append(diagTableModel.getValueAt(i, 2)).append("<br>Treatment: ")
              .append(diagTableModel.getValueAt(i, 4)).append("</li>");
        }
        if (diagTableModel.getRowCount() == 0) sb.append("<li>No consultation records.</li>");
        sb.append("</ul>");

        sb.append("<h3>Vaccination History</h3><ul>");
        for (int i = 0; i < vaccTableModel.getRowCount(); i++) {
            sb.append("<li><b>").append(vaccTableModel.getValueAt(i, 0)).append("</b> (Given: ")
              .append(vaccTableModel.getValueAt(i, 2)).append(" | Next Due: ")
              .append(vaccTableModel.getValueAt(i, 3)).append(") - ")
              .append(vaccTableModel.getValueAt(i, 4)).append("</li>");
        }
        if (vaccTableModel.getRowCount() == 0) sb.append("<li>No vaccinations on record.</li>");
        sb.append("</ul>");

        sb.append("</body></html>");

        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Pet Medical Summary", JDialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(520, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JEditorPane editor = new JEditorPane("text/html", sb.toString());
        editor.setEditable(false);
        dialog.add(new JScrollPane(editor), BorderLayout.CENTER);

        JButton close = new JButton("Close");
        close.addActionListener(e -> dialog.dispose());
        JPanel bp = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bp.add(close);
        dialog.add(bp, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}
