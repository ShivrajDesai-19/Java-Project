package com.petcare.service;

import com.petcare.exception.DatabaseException;
import com.petcare.util.DBConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * Service generating system analytics and statistical aggregates for Reports Dashboard
 */
public class ReportService {

    public static class AnalyticsSummary {
        public int totalOwners;
        public int totalPets;
        public int scheduledAppointments;
        public int lowStockCount;
        public double totalRevenue;
        public Map<String, Integer> speciesDistribution = new HashMap<>();
        public Map<String, Integer> appointmentStatusCount = new HashMap<>();
    }

    public AnalyticsSummary getClinicAnalytics() throws DatabaseException {
        AnalyticsSummary summary = new AnalyticsSummary();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();

            // Total Owners
            rs = stmt.executeQuery("SELECT COUNT(*) FROM `pet_owners`");
            if (rs.next()) summary.totalOwners = rs.getInt(1);
            rs.close();

            // Total Pets
            rs = stmt.executeQuery("SELECT COUNT(*) FROM `pets`");
            if (rs.next()) summary.totalPets = rs.getInt(1);
            rs.close();

            // Scheduled Appointments
            rs = stmt.executeQuery("SELECT COUNT(*) FROM `appointments` WHERE `status` = 'SCHEDULED'");
            if (rs.next()) summary.scheduledAppointments = rs.getInt(1);
            rs.close();

            // Low Stock Medicines
            rs = stmt.executeQuery("SELECT COUNT(*) FROM `medicines` WHERE `stock_quantity` <= `min_stock_alert`");
            if (rs.next()) summary.lowStockCount = rs.getInt(1);
            rs.close();

            // Total Revenue
            rs = stmt.executeQuery("SELECT COALESCE(SUM(total_amount), 0.0) FROM `invoices` WHERE `payment_status` = 'PAID'");
            if (rs.next()) summary.totalRevenue = rs.getDouble(1);
            rs.close();

            // Species Distribution
            rs = stmt.executeQuery("SELECT species, COUNT(*) FROM `pets` GROUP BY species");
            while (rs.next()) {
                summary.speciesDistribution.put(rs.getString(1), rs.getInt(2));
            }
            rs.close();

            // Appointment status distribution
            rs = stmt.executeQuery("SELECT status, COUNT(*) FROM `appointments` GROUP BY status");
            while (rs.next()) {
                summary.appointmentStatusCount.put(rs.getString(1), rs.getInt(2));
            }

            return summary;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to generate analytics summary: " + e.getMessage(), e);
        } finally {
            DBConnection.closeQuietly(conn, stmt, rs);
        }
    }
}
