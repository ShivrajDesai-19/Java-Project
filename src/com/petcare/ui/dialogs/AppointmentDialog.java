package com.petcare.ui.dialogs;

import com.petcare.dao.UserDAO;
import com.petcare.model.Appointment;
import com.petcare.model.AppointmentStatus;
import com.petcare.model.Pet;
import com.petcare.model.User;
import com.petcare.model.UserRole;
import com.petcare.service.AppointmentService;
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
 * Dialog for scheduling or modifying appointments
 */
public class AppointmentDialog extends JDialog {

    private final AppointmentService aptService;
    private final Appointment existingApt;
    private boolean saved = false;

    private final JComboBox<Pet> petCombo;
    private final JComboBox<User> vetCombo;
    private final JTextField dateField;
    private final JComboBox<String> timeCombo;
    private final JComboBox<String> purposeCombo;
    private final JComboBox<AppointmentStatus> statusCombo;
    private final JTextArea notesArea;

    public AppointmentDialog(Window parent, AppointmentService aptService, PetManagementService petService, Appointment existingApt) {
        super(parent, existingApt == null ? "Schedule New Appointment" : "Edit Appointment", ModalityType.APPLICATION_MODAL);
        this.aptService = aptService;
        this.existingApt = existingApt;

        setSize(540, 540);
        setResizable(true);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        petCombo = new JComboBox<>();
        try {
            List<Pet> pets = petService.getAllPets();
            for (Pet p : pets) {
                petCombo.addItem(p);
                if (existingApt != null && p.getId() == existingApt.getPetId()) {
                    petCombo.setSelectedItem(p);
                }
            }
        } catch (Exception ignored) {}

        vetCombo = new JComboBox<>();
        try {
            List<User> vets = new UserDAO().findByRole(UserRole.VETERINARIAN);
            for (User v : vets) {
                vetCombo.addItem(v);
                if (existingApt != null && v.getId() == existingApt.getVetId()) {
                    vetCombo.setSelectedItem(v);
                }
            }
        } catch (Exception ignored) {}

        dateField = new JTextField(existingApt != null && existingApt.getAppointmentDate() != null ? 
            existingApt.getAppointmentDate().toString() : LocalDate.now().toString(), 20);

        String[] times = {"09:00 AM", "09:30 AM", "10:00 AM", "10:30 AM", "11:00 AM", "11:30 AM", 
                          "02:00 PM", "02:30 PM", "03:00 PM", "03:30 PM", "04:00 PM", "04:30 PM", "05:00 PM"};
        timeCombo = new JComboBox<>(times);
        if (existingApt != null && existingApt.getAppointmentTime() != null) {
            timeCombo.setSelectedItem(existingApt.getAppointmentTime());
        }

        String[] purposes = {"Routine Checkup & Wellness", "Vaccination Booster", "Surgery & Operation", 
                             "Dermatology & Skin Care", "Dental Cleaning", "Emergency Care", "Follow-up Consultation"};
        purposeCombo = new JComboBox<>(purposes);
        if (existingApt != null && existingApt.getPurpose() != null) {
            purposeCombo.setSelectedItem(existingApt.getPurpose());
        }

        statusCombo = new JComboBox<>(AppointmentStatus.values());
        if (existingApt != null && existingApt.getStatus() != null) {
            statusCombo.setSelectedItem(existingApt.getStatus());
        }

        notesArea = new JTextArea(existingApt != null ? existingApt.getNotes() : "", 3, 20);
        notesArea.setLineWrap(true);

        int row = 0;
        addFormRow(formPanel, gbc, row++, "Select Pet Patient *:", petCombo);
        addFormRow(formPanel, gbc, row++, "Assigned Veterinarian *:", vetCombo);
        addFormRow(formPanel, gbc, row++, "Appointment Date (YYYY-MM-DD):", dateField);
        addFormRow(formPanel, gbc, row++, "Time Slot *:", timeCombo);
        addFormRow(formPanel, gbc, row++, "Purpose / Reason *:", purposeCombo);
        addFormRow(formPanel, gbc, row++, "Appointment Status:", statusCombo);

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        formPanel.add(new JLabel("Notes / Symptoms:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        formPanel.add(new JScrollPane(notesArea), gbc);

        JScrollPane formScroll = new JScrollPane(formPanel);
        formScroll.setBorder(null);
        formScroll.getVerticalScrollBar().setUnitIncrement(12);
        add(formScroll, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = UIStyleUtil.createPrimaryButton("Confirm Appointment");
        JButton cancelBtn = UIStyleUtil.createSecondaryButton("Cancel");

        saveBtn.addActionListener(e -> saveAppointment());
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

    private void saveAppointment() {
        try {
            Pet pet = (Pet) petCombo.getSelectedItem();
            User vet = (User) vetCombo.getSelectedItem();
            if (pet == null) {
                JOptionPane.showMessageDialog(this, "Please select a registered pet.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (vet == null) {
                JOptionPane.showMessageDialog(this, "Please select an attending veterinarian doctor.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            LocalDate date = LocalDate.parse(dateField.getText().trim());
            String time = (String) timeCombo.getSelectedItem();
            String purpose = (String) purposeCombo.getSelectedItem();
            AppointmentStatus status = (AppointmentStatus) statusCombo.getSelectedItem();
            String notes = notesArea.getText().trim();

            if (existingApt == null) {
                Appointment apt = new Appointment(0, pet.getId(), vet.getId(), date, time, purpose, status, notes);
                aptService.scheduleAppointment(apt);
            } else {
                existingApt.setPetId(pet.getId());
                existingApt.setVetId(vet.getId());
                existingApt.setAppointmentDate(date);
                existingApt.setAppointmentTime(time);
                existingApt.setPurpose(purpose);
                existingApt.setStatus(status);
                existingApt.setNotes(notes);
                aptService.updateAppointment(existingApt);
            }

            saved = true;
            JOptionPane.showMessageDialog(this, "Appointment scheduled successfully!", "Scheduled", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving appointment: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
