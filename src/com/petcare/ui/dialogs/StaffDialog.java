package com.petcare.ui.dialogs;

import com.petcare.dao.UserDAO;
import com.petcare.model.Administrator;
import com.petcare.model.Receptionist;
import com.petcare.model.StaffMember;
import com.petcare.model.User;
import com.petcare.model.UserRole;
import com.petcare.model.Veterinarian;
import com.petcare.util.UIStyleUtil;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;

/**
 * Dialog for adding or updating Staff and Veterinary doctors (Admin only)
 */
public class StaffDialog extends JDialog {

    private final UserDAO userDAO;
    private final User existingUser;
    private boolean saved = false;

    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JTextField nameField;
    private final JComboBox<UserRole> roleCombo;
    private final JTextField emailField;
    private final JTextField phoneField;
    private final JTextField specField;
    private final JComboBox<String> statusCombo;

    public StaffDialog(Window parent, UserDAO userDAO, User existingUser) {
        super(parent, existingUser == null ? "Add Clinic Employee / Staff" : "Edit Staff Member", ModalityType.APPLICATION_MODAL);
        this.userDAO = userDAO;
        this.existingUser = existingUser;

        setSize(520, 520);
        setResizable(true);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        usernameField = new JTextField(existingUser != null ? existingUser.getUsername() : "", 20);
        passwordField = new JPasswordField(existingUser != null ? existingUser.getPassword() : "", 20);
        nameField = new JTextField(existingUser != null ? existingUser.getFullName() : "", 20);
        roleCombo = new JComboBox<>(UserRole.values());
        if (existingUser != null && existingUser.getRole() != null) {
            roleCombo.setSelectedItem(existingUser.getRole());
        }
        emailField = new JTextField(existingUser != null ? existingUser.getEmail() : "", 20);
        phoneField = new JTextField(existingUser != null ? existingUser.getPhone() : "", 20);
        specField = new JTextField(existingUser != null ? existingUser.getSpecialization() : "", 20);
        statusCombo = new JComboBox<>(new String[]{"ACTIVE", "INACTIVE"});
        if (existingUser != null && existingUser.getStatus() != null) {
            statusCombo.setSelectedItem(existingUser.getStatus());
        }

        int r = 0;
        addFormRow(formPanel, gbc, r++, "Username *:", usernameField);
        addFormRow(formPanel, gbc, r++, "Password *:", passwordField);
        addFormRow(formPanel, gbc, r++, "Full Name *:", nameField);
        addFormRow(formPanel, gbc, r++, "System Role *:", roleCombo);
        addFormRow(formPanel, gbc, r++, "Email Address:", emailField);
        addFormRow(formPanel, gbc, r++, "Phone Number:", phoneField);
        addFormRow(formPanel, gbc, r++, "Specialization / Duty:", specField);
        addFormRow(formPanel, gbc, r++, "Account Status:", statusCombo);

        JScrollPane formScroll = new JScrollPane(formPanel);
        formScroll.setBorder(null);
        formScroll.getVerticalScrollBar().setUnitIncrement(12);
        add(formScroll, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = UIStyleUtil.createPrimaryButton("Save Staff");
        JButton cancelBtn = UIStyleUtil.createSecondaryButton("Cancel");

        saveBtn.addActionListener(e -> saveStaff());
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

    private void saveStaff() {
        try {
            String u = usernameField.getText().trim();
            String p = new String(passwordField.getPassword()).trim();
            String n = nameField.getText().trim();
            UserRole r = (UserRole) roleCombo.getSelectedItem();
            String em = emailField.getText().trim();
            String ph = phoneField.getText().trim();
            String sp = specField.getText().trim();
            String st = (String) statusCombo.getSelectedItem();

            if (u.isEmpty() || p.isEmpty() || n.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username, password and full name are required.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (existingUser == null) {
                User user;
                switch (r) {
                    case ADMINISTRATOR: user = new Administrator(0, u, p, n, em, ph, sp, st); break;
                    case VETERINARIAN:  user = new Veterinarian(0, u, p, n, em, ph, sp, st); break;
                    case RECEPTIONIST:  user = new Receptionist(0, u, p, n, em, ph, sp, st); break;
                    default:            user = new StaffMember(0, u, p, n, em, ph, sp, st); break;
                }
                userDAO.insert(user);
            } else {
                existingUser.setFullName(n);
                existingUser.setPassword(p);
                existingUser.setRole(r);
                existingUser.setEmail(em);
                existingUser.setPhone(ph);
                existingUser.setSpecialization(sp);
                existingUser.setStatus(st);
                userDAO.update(existingUser);
            }

            saved = true;
            JOptionPane.showMessageDialog(this, "Staff user saved successfully!", "Saved", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving staff member: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
