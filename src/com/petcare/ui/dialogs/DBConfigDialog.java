package com.petcare.ui.dialogs;

import com.petcare.util.DBConnection;
import com.petcare.util.UIStyleUtil;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;

/**
 * Dialog for testing and configuring XAMPP MySQL database connectivity.
 */
public class DBConfigDialog extends JDialog {

    private final JTextField hostField;
    private final JTextField portField;
    private final JTextField dbField;
    private final JTextField userField;
    private final JPasswordField passField;

    public DBConfigDialog(Window parent) {
        super(parent, "Database Configuration (XAMPP MySQL)", ModalityType.APPLICATION_MODAL);
        setSize(520, 390);
        setResizable(true);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        hostField = new JTextField(DBConnection.getHost(), 20);
        portField = new JTextField(String.valueOf(DBConnection.getPort()), 20);
        dbField = new JTextField(DBConnection.getDatabase(), 20);
        userField = new JTextField(DBConnection.getUsername(), 20);
        passField = new JPasswordField(DBConnection.getPassword(), 20);

        int row = 0;
        addFormRow(formPanel, gbc, row++, "Host:", hostField);
        addFormRow(formPanel, gbc, row++, "Port (XAMPP Default 3306):", portField);
        addFormRow(formPanel, gbc, row++, "Database Name:", dbField);
        addFormRow(formPanel, gbc, row++, "Username (Default 'root'):", userField);
        addFormRow(formPanel, gbc, row++, "Password (Default empty):", passField);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton testBtn = UIStyleUtil.createSecondaryButton("Test Connection");
        JButton saveBtn = UIStyleUtil.createPrimaryButton("Save & Initialize");
        JButton cancelBtn = UIStyleUtil.createSecondaryButton("Cancel");

        testBtn.addActionListener(e -> {
            try {
                int port = Integer.parseInt(portField.getText().trim());
                boolean ok = DBConnection.testConnection(
                    hostField.getText().trim(),
                    port,
                    userField.getText().trim(),
                    new String(passField.getPassword())
                );
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Success! MySQL server is reachable.", "Connection OK", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Could not reach MySQL server. Is MySQL started in XAMPP?\nDetail: " + DBConnection.getLastError(), "Connection Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid port number: " + ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        saveBtn.addActionListener(e -> {
            try {
                int port = Integer.parseInt(portField.getText().trim());
                DBConnection.saveConfig(
                    hostField.getText().trim(),
                    port,
                    dbField.getText().trim(),
                    userField.getText().trim(),
                    new String(passField.getPassword())
                );
                DBConnection.initializeDatabase();
                JOptionPane.showMessageDialog(this, "Configuration saved and database initialized successfully!", "Initialized", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Saved config, but database setup gave an error:\n" + ex.getMessage() + "\n\nMake sure XAMPP MySQL is running.", "Warning", JOptionPane.WARNING_MESSAGE);
                dispose();
            }
        });

        cancelBtn.addActionListener(e -> dispose());

        buttonPanel.add(testBtn);
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, JTextField field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(field, gbc);
    }
}
