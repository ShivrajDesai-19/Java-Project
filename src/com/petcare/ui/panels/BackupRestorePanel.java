package com.petcare.ui.panels;

import com.petcare.ui.dialogs.DBConfigDialog;
import com.petcare.util.DatabaseBackupUtil;
import com.petcare.util.UIStyleUtil;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Database Backup & Restore Utility Panel
 * Demonstrates background threading (Unit 4) and file I/O operations (Unit 2).
 */
public class BackupRestorePanel extends JPanel {

    private final JTextArea logArea;
    private final JProgressBar progressBar;

    public BackupRestorePanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(UIStyleUtil.COLOR_BG_LIGHT);
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Header Title
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new javax.swing.BoxLayout(headerPanel, javax.swing.BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        JLabel title = new JLabel("Database Maintenance: Backup & Restore");
        title.setFont(UIStyleUtil.FONT_HEADER_LARGE);
        title.setForeground(UIStyleUtil.COLOR_PRIMARY);

        JLabel sub = new JLabel("Create standalone SQL backup archives or restore databases safely from previous snapshots.");
        sub.setFont(UIStyleUtil.FONT_BODY);
        sub.setForeground(UIStyleUtil.COLOR_TEXT_MUTED);

        headerPanel.add(title);
        headerPanel.add(javax.swing.Box.createVerticalStrut(4));
        headerPanel.add(sub);
        add(headerPanel, BorderLayout.NORTH);

        // Actions and Progress
        JPanel centerCard = new JPanel(new BorderLayout(15, 15));
        centerCard.setBackground(UIStyleUtil.COLOR_BG_CARD);
        centerCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIStyleUtil.COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        buttonRow.setOpaque(false);

        JButton backupBtn = UIStyleUtil.createPrimaryButton("Create Backup Archive (.sql)");
        JButton restoreBtn = UIStyleUtil.createDangerButton("Restore From Backup (.sql)");
        JButton configBtn = UIStyleUtil.createSecondaryButton("MySQL Configuration");

        backupBtn.addActionListener(e -> performBackup());
        restoreBtn.addActionListener(e -> performRestore());
        configBtn.addActionListener(e -> new DBConfigDialog(SwingUtilities.getWindowAncestor(this)).setVisible(true));

        buttonRow.add(backupBtn);
        buttonRow.add(restoreBtn);
        buttonRow.add(configBtn);

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(false);
        progressBar.setStringPainted(true);
        progressBar.setString("Ready");

        JPanel topControls = new JPanel(new BorderLayout(10, 10));
        topControls.setOpaque(false);
        topControls.add(buttonRow, BorderLayout.NORTH);
        topControls.add(progressBar, BorderLayout.SOUTH);
        centerCard.add(topControls, BorderLayout.NORTH);

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new java.awt.Font("Consolas", java.awt.Font.PLAIN, 12));
        logArea.setBackground(new java.awt.Color(250, 250, 252));
        logArea.setText("--- System Backup & Restore Log Initialized ---\n");

        centerCard.add(new JScrollPane(logArea), BorderLayout.CENTER);
        add(centerCard, BorderLayout.CENTER);
    }

    private void log(String msg) {
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
        SwingUtilities.invokeLater(() -> {
            logArea.append("[" + time + "] " + msg + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    /**
     * Executes database backup in a separate thread so the GUI does not freeze (Unit 4)
     */
    private void performBackup() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Directory to Save SQL Backup");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File dest = chooser.getSelectedFile();
            progressBar.setIndeterminate(true);
            progressBar.setString("Exporting SQL Dump...");
            log("Initiating database backup to folder: " + dest.getAbsolutePath());

            // Unit 4 Syllabus: Background Thread Execution
            new Thread(() -> {
                try {
                    File file = DatabaseBackupUtil.createBackup(dest);
                    log("SUCCESS! Database backup created: " + file.getName());
                    log("Full location: " + file.getAbsolutePath());
                    SwingUtilities.invokeLater(() -> {
                        progressBar.setIndeterminate(false);
                        progressBar.setString("Backup Completed Successfully");
                        JOptionPane.showMessageDialog(this, "Backup file generated:\n" + file.getAbsolutePath(), "Backup Successful", JOptionPane.INFORMATION_MESSAGE);
                    });
                } catch (Exception ex) {
                    log("ERROR: Backup failed: " + ex.getMessage());
                    SwingUtilities.invokeLater(() -> {
                        progressBar.setIndeterminate(false);
                        progressBar.setString("Backup Failed");
                        JOptionPane.showMessageDialog(this, "Backup operation failed:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    });
                }
            }, "DatabaseBackupThread").start();
        }
    }

    /**
     * Executes database restore in a separate thread
     */
    private void performRestore() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select SQL Backup File to Restore");
        chooser.setFileFilter(new FileNameExtensionFilter("SQL Backup Files (*.sql)", "sql"));

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();

            int confirm = JOptionPane.showConfirmDialog(this,
                "Warning: Restoring will overwrite existing records with data from '" + file.getName() + "'.\nContinue?",
                "Confirm Restore", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                progressBar.setIndeterminate(true);
                progressBar.setString("Restoring Database...");
                log("Starting database restoration from: " + file.getAbsolutePath());

                // Unit 4 Background Worker Thread
                new Thread(() -> {
                    try {
                        DatabaseBackupUtil.restoreBackup(file);
                        log("SUCCESS! Database records successfully restored from backup.");
                        SwingUtilities.invokeLater(() -> {
                            progressBar.setIndeterminate(false);
                            progressBar.setString("Restore Completed");
                            JOptionPane.showMessageDialog(this, "Database restoration completed successfully!", "Restored", JOptionPane.INFORMATION_MESSAGE);
                        });
                    } catch (Exception ex) {
                        log("ERROR: Restore failed: " + ex.getMessage());
                        SwingUtilities.invokeLater(() -> {
                            progressBar.setIndeterminate(false);
                            progressBar.setString("Restore Failed");
                            JOptionPane.showMessageDialog(this, "Restore operation failed:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        });
                    }
                }, "DatabaseRestoreThread").start();
            }
        }
    }
}
