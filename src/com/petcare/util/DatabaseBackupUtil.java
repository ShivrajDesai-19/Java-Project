package com.petcare.util;

import com.petcare.exception.DatabaseException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Unit 2 Syllabus Demonstration:
 * java.io package (File, BufferedReader, BufferedWriter, FileReader, FileWriter)
 * Unit 3 Syllabus Demonstration:
 * Exception Handling & Chained Exceptions
 *
 * Provides database backup (dumping schema + data into SQL file)
 * and database restore (reading and executing SQL statements from backup file).
 */
public class DatabaseBackupUtil {

    private DatabaseBackupUtil() {}

    /**
     * Creates an SQL backup file containing table data and structure.
     */
    public static File createBackup(File targetDirectory) throws DatabaseException {
        if (!targetDirectory.exists()) {
            targetDirectory.mkdirs();
        }

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File backupFile = new File(targetDirectory, "smart_pet_care_backup_" + timestamp + ".sql");

        Connection conn = null;
        BufferedWriter writer = null;

        try {
            conn = DBConnection.getConnection();
            writer = new BufferedWriter(new FileWriter(backupFile));

            writer.write("-- ==================================================\n");
            writer.write("-- Smart Pet Care System - Database Backup Dump\n");
            writer.write("-- Generated at: " + new Date() + "\n");
            writer.write("-- ==================================================\n\n");
            writer.write("SET FOREIGN_KEY_CHECKS = 0;\n\n");

            // List of tables in dependency order
            String[] tables = {
                "users", "pet_owners", "pets", "appointments", "medical_records",
                "medicines", "prescriptions", "vaccinations", "lab_tests",
                "service_bookings", "invoices", "invoice_items"
            };

            for (String tableName : tables) {
                dumpTableData(conn, tableName, writer);
            }

            writer.write("SET FOREIGN_KEY_CHECKS = 1;\n");
            writer.flush();
            return backupFile;

        } catch (SQLException | IOException e) {
            throw new DatabaseException("Failed to generate database backup: " + e.getMessage(), e);
        } finally {
            if (writer != null) {
                try { writer.close(); } catch (IOException ignored) {}
            }
            DBConnection.closeQuietly(conn, null, null);
        }
    }

    private static void dumpTableData(Connection conn, String tableName, BufferedWriter writer) throws SQLException, IOException {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM `" + tableName + "`");
            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();

            writer.write("-- Dumping data for table `" + tableName + "`\n");
            writer.write("DELETE FROM `" + tableName + "`;\n");

            while (rs.next()) {
                StringBuilder rowSql = new StringBuilder("INSERT INTO `" + tableName + "` VALUES (");
                for (int i = 1; i <= colCount; i++) {
                    Object val = rs.getObject(i);
                    if (val == null) {
                        rowSql.append("NULL");
                    } else if (val instanceof Number) {
                        rowSql.append(val);
                    } else {
                        String cleanStr = val.toString().replace("'", "''").replace("\\", "\\\\");
                        rowSql.append("'").append(cleanStr).append("'");
                    }
                    if (i < colCount) {
                        rowSql.append(", ");
                    }
                }
                rowSql.append(");\n");
                writer.write(rowSql.toString());
            }
            writer.write("\n");
        } finally {
            DBConnection.closeQuietly(null, stmt, rs);
        }
    }

    /**
     * Restores database from a given SQL backup file.
     */
    public static void restoreBackup(File backupFile) throws DatabaseException {
        if (!backupFile.exists()) {
            throw new DatabaseException("Backup file does not exist: " + backupFile.getAbsolutePath());
        }

        Connection conn = null;
        Statement stmt = null;
        BufferedReader reader = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            reader = new BufferedReader(new FileReader(backupFile));

            StringBuilder sqlBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("--") || line.startsWith("/*")) {
                    continue;
                }
                sqlBuilder.append(line);
                if (line.endsWith(";")) {
                    String query = sqlBuilder.toString().replace(";", "").trim();
                    if (!query.isEmpty()) {
                        try {
                            stmt.execute(query);
                        } catch (SQLException ex) {
                            System.err.println("[Restore Warning] Query failed: " + query + " - " + ex.getMessage());
                        }
                    }
                    sqlBuilder.setLength(0);
                }
            }

        } catch (SQLException | IOException e) {
            throw new DatabaseException("Failed to restore database from backup: " + e.getMessage(), e);
        } finally {
            if (reader != null) {
                try { reader.close(); } catch (IOException ignored) {}
            }
            DBConnection.closeQuietly(conn, stmt, null);
        }
    }
}
