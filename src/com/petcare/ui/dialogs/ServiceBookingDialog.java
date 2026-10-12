package com.petcare.ui.dialogs;

import com.petcare.model.BoardingBooking;
import com.petcare.model.GroomingBooking;
import com.petcare.model.Pet;
import com.petcare.model.ServiceItem;
import com.petcare.service.PetManagementService;
import com.petcare.service.ServiceBookingService;
import com.petcare.util.UIStyleUtil;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
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
 * Dialog for scheduling Grooming or Boarding service bookings
 */
public class ServiceBookingDialog extends JDialog {

    private final ServiceBookingService service;
    private boolean saved = false;

    private final JComboBox<Pet> petCombo;
    private final JRadioButton groomingRadio;
    private final JRadioButton boardingRadio;
    private final JTextField startDateField;
    private final JTextField endDateField;
    private final JTextField roomOrStationField;
    private final JTextField packageField;
    private final JTextField costField;
    private final JTextArea notesArea;

    public ServiceBookingDialog(Window parent, ServiceBookingService service, PetManagementService petService) {
        super(parent, "Book Grooming or Pet Boarding Service", ModalityType.APPLICATION_MODAL);
        this.service = service;

        setSize(540, 560);
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
            }
        } catch (Exception ignored) {}

        groomingRadio = new JRadioButton("Grooming & Spa", true);
        boardingRadio = new JRadioButton("Pet Boarding & Kennels");
        ButtonGroup group = new ButtonGroup();
        group.add(groomingRadio);
        group.add(boardingRadio);
        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        typePanel.add(groomingRadio);
        typePanel.add(boardingRadio);

        startDateField = new JTextField(LocalDate.now().toString(), 20);
        endDateField = new JTextField(LocalDate.now().plusDays(2).toString(), 20);
        endDateField.setEnabled(false); // Only active for boarding

        roomOrStationField = new JTextField("Grooming-Station-1", 20);
        packageField = new JTextField("Full Spa Grooming (Bath, Trim, Nails, Ear Clean)", 20);
        costField = new JTextField("1200.0", 20);
        notesArea = new JTextArea(3, 20);
        notesArea.setLineWrap(true);

        groomingRadio.addActionListener(e -> {
            endDateField.setEnabled(false);
            roomOrStationField.setText("Grooming-Station-1");
            packageField.setText("Full Spa Grooming (Bath, Trim, Nails, Ear Clean)");
            costField.setText("1200.0");
        });

        boardingRadio.addActionListener(e -> {
            endDateField.setEnabled(true);
            roomOrStationField.setText("Kennel-Suite-A3");
            packageField.setText("Deluxe Boarding Suite with Meals & 2 Daily Walks");
            costField.setText("1600.0");
        });

        int r = 0;
        addFormRow(formPanel, gbc, r++, "Select Pet *:", petCombo);
        addFormRow(formPanel, gbc, r++, "Service Type *:", typePanel);
        addFormRow(formPanel, gbc, r++, "Start Date (YYYY-MM-DD):", startDateField);
        addFormRow(formPanel, gbc, r++, "End Date (Boarding only):", endDateField);
        addFormRow(formPanel, gbc, r++, "Station / Room No:", roomOrStationField);
        addFormRow(formPanel, gbc, r++, "Package Description *:", packageField);
        addFormRow(formPanel, gbc, r++, "Total Service Cost (₹) *:", costField);

        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0.35;
        formPanel.add(new JLabel("Special Instructions:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        formPanel.add(new JScrollPane(notesArea), gbc);

        JScrollPane formScroll = new JScrollPane(formPanel);
        formScroll.setBorder(null);
        formScroll.getVerticalScrollBar().setUnitIncrement(12);
        add(formScroll, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = UIStyleUtil.createPrimaryButton("Confirm Booking");
        JButton cancelBtn = UIStyleUtil.createSecondaryButton("Cancel");

        saveBtn.addActionListener(e -> saveBooking());
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

    private void saveBooking() {
        try {
            Pet pet = (Pet) petCombo.getSelectedItem();
            if (pet == null) {
                JOptionPane.showMessageDialog(this, "Please select a registered pet.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            LocalDate start = LocalDate.parse(startDateField.getText().trim());
            double cost = Double.parseDouble(costField.getText().trim());
            String roomOrStation = roomOrStationField.getText().trim();
            String pkg = packageField.getText().trim();
            String notes = notesArea.getText().trim();

            ServiceItem booking;
            if (boardingRadio.isSelected()) {
                LocalDate end = LocalDate.parse(endDateField.getText().trim());
                booking = new BoardingBooking(
                    0, pet.getId(), LocalDate.now(), start, end,
                    roomOrStation, pkg, notes, cost, "CONFIRMED"
                );
            } else {
                booking = new GroomingBooking(
                    0, pet.getId(), LocalDate.now(), start,
                    roomOrStation, pkg, notes, cost, "CONFIRMED"
                );
            }

            service.createBooking(booking);
            saved = true;
            JOptionPane.showMessageDialog(this, "Service booked successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving booking: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
