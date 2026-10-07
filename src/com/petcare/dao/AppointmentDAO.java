package com.petcare.dao;

import com.petcare.exception.DatabaseException;
import com.petcare.model.Appointment;
import com.petcare.model.AppointmentStatus;
import com.petcare.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Appointment Data Access Object
 */
public class AppointmentDAO implements GenericDAO<Appointment> {

    @Override
    public void insert(Appointment apt) throws DatabaseException {
        String sql = "INSERT INTO `appointments` (`pet_id`, `vet_id`, `appointment_date`, `appointment_time`, `purpose`, `status`, `notes`) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, apt.getPetId());
            ps.setInt(2, apt.getVetId());
            ps.setDate(3, apt.getAppointmentDate() != null ? Date.valueOf(apt.getAppointmentDate()) : new Date(System.currentTimeMillis()));
            ps.setString(4, apt.getAppointmentTime());
            ps.setString(5, apt.getPurpose());
            ps.setString(6, apt.getStatus() != null ? apt.getStatus().name() : AppointmentStatus.SCHEDULED.name());
            ps.setString(7, apt.getNotes());
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                apt.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to schedule appointment: " + e.getMessage(), e);
        } finally {
            DBConnection.closeQuietly(conn, ps, rs);
        }
    }

    @Override
    public void update(Appointment apt) throws DatabaseException {
        String sql = "UPDATE `appointments` SET `pet_id` = ?, `vet_id` = ?, `appointment_date` = ?, " +
                     "`appointment_time` = ?, `purpose` = ?, `status` = ?, `notes` = ? WHERE `id` = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, apt.getPetId());
            ps.setInt(2, apt.getVetId());
            ps.setDate(3, apt.getAppointmentDate() != null ? Date.valueOf(apt.getAppointmentDate()) : new Date(System.currentTimeMillis()));
            ps.setString(4, apt.getAppointmentTime());
            ps.setString(5, apt.getPurpose());
            ps.setString(6, apt.getStatus().name());
            ps.setString(7, apt.getNotes());
            ps.setInt(8, apt.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update appointment: " + e.getMessage(), e);
        } finally {
            DBConnection.closeQuietly(conn, ps, null);
        }
    }

    public void updateStatus(int appointmentId, AppointmentStatus status) throws DatabaseException {
        String sql = "UPDATE `appointments` SET `status` = ? WHERE `id` = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, status.name());
            ps.setInt(2, appointmentId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update appointment status: " + e.getMessage(), e);
        } finally {
            DBConnection.closeQuietly(conn, ps, null);
        }
    }

    @Override
    public void delete(int id) throws DatabaseException {
        String sql = "DELETE FROM `appointments` WHERE `id` = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete appointment: " + e.getMessage(), e);
        } finally {
            DBConnection.closeQuietly(conn, ps, null);
        }
    }

    @Override
    public Appointment findById(int id) throws DatabaseException {
        String sql = baseQuery() + " WHERE a.id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return mapAppointment(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find appointment: " + e.getMessage(), e);
        } finally {
            DBConnection.closeQuietly(conn, ps, rs);
        }
    }

    @Override
    public List<Appointment> findAll() throws DatabaseException {
        String sql = baseQuery() + " ORDER BY a.appointment_date DESC, a.appointment_time ASC";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Appointment> list = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                list.add(mapAppointment(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch all appointments: " + e.getMessage(), e);
        } finally {
            DBConnection.closeQuietly(conn, stmt, rs);
        }
    }

    public List<Appointment> findByDate(LocalDate date) throws DatabaseException {
        String sql = baseQuery() + " WHERE a.appointment_date = ? ORDER BY a.appointment_time ASC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Appointment> list = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setDate(1, Date.valueOf(date));
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapAppointment(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch appointments by date: " + e.getMessage(), e);
        } finally {
            DBConnection.closeQuietly(conn, ps, rs);
        }
    }

    public int countUpcomingToday() throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM `appointments` WHERE `appointment_date` = CURDATE() AND `status` = 'SCHEDULED'";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count appointments: " + e.getMessage(), e);
        } finally {
            DBConnection.closeQuietly(conn, stmt, rs);
        }
    }

    private String baseQuery() {
        return "SELECT a.*, p.name AS pet_name, o.full_name AS owner_name, u.full_name AS vet_name " +
               "FROM `appointments` a " +
               "JOIN `pets` p ON a.pet_id = p.id " +
               "JOIN `pet_owners` o ON p.owner_id = o.id " +
               "JOIN `users` u ON a.vet_id = u.id ";
    }

    private Appointment mapAppointment(ResultSet rs) throws SQLException {
        Appointment apt = new Appointment();
        apt.setId(rs.getInt("id"));
        apt.setPetId(rs.getInt("pet_id"));
        apt.setPetName(rs.getString("pet_name"));
        apt.setOwnerName(rs.getString("owner_name"));
        apt.setVetId(rs.getInt("vet_id"));
        apt.setVetName(rs.getString("vet_name"));
        Date d = rs.getDate("appointment_date");
        if (d != null) {
            apt.setAppointmentDate(d.toLocalDate());
        }
        apt.setAppointmentTime(rs.getString("appointment_time"));
        apt.setPurpose(rs.getString("purpose"));
        try {
            apt.setStatus(AppointmentStatus.valueOf(rs.getString("status")));
        } catch (Exception e) {
            apt.setStatus(AppointmentStatus.SCHEDULED);
        }
        apt.setNotes(rs.getString("notes"));
        return apt;
    }
}
