package com.petcare.dao;

import com.petcare.exception.DatabaseException;
import com.petcare.model.BoardingBooking;
import com.petcare.model.GroomingBooking;
import com.petcare.model.ServiceItem;
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
 * ServiceBookingDAO for Grooming and Boarding reservations
 */
public class ServiceBookingDAO {

    public void insert(ServiceItem booking) throws DatabaseException {
        String sql = "INSERT INTO `service_bookings` (`pet_id`, `service_type`, `booking_date`, `start_date`, " +
                     "`end_date`, `cage_or_room_no`, `package_details`, `special_instructions`, `cost`, `status`) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, booking.getPetId());
            ps.setString(2, booking.getServiceType());
            ps.setDate(3, booking.getBookingDate() != null ? Date.valueOf(booking.getBookingDate()) : new Date(System.currentTimeMillis()));
            ps.setDate(4, booking.getStartDate() != null ? Date.valueOf(booking.getStartDate()) : new Date(System.currentTimeMillis()));

            if (booking instanceof BoardingBooking) {
                BoardingBooking bb = (BoardingBooking) booking;
                ps.setDate(5, bb.getEndDate() != null ? Date.valueOf(bb.getEndDate()) : null);
                ps.setString(6, bb.getCageOrRoomNo());
            } else if (booking instanceof GroomingBooking) {
                GroomingBooking gb = (GroomingBooking) booking;
                ps.setDate(5, booking.getStartDate() != null ? Date.valueOf(booking.getStartDate()) : null);
                ps.setString(6, gb.getGroomerStation());
            } else {
                ps.setDate(5, null);
                ps.setString(6, null);
            }

            ps.setString(7, booking.getPackageDetails());
            ps.setString(8, booking.getSpecialInstructions());
            ps.setDouble(9, booking.calculateTotalPrice());
            ps.setString(10, booking.getStatus() != null ? booking.getStatus() : "CONFIRMED");
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                booking.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to register service booking: " + e.getMessage(), e);
        } finally {
            DBConnection.closeQuietly(conn, ps, rs);
        }
    }

    public List<ServiceItem> findAll() throws DatabaseException {
        String sql = "SELECT s.*, p.name AS pet_name FROM `service_bookings` s " +
                     "JOIN `pets` p ON s.pet_id = p.id ORDER BY s.booking_date DESC, s.id DESC";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<ServiceItem> list = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                list.add(mapService(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch service bookings: " + e.getMessage(), e);
        } finally {
            DBConnection.closeQuietly(conn, stmt, rs);
        }
    }

    public void updateStatus(int bookingId, String newStatus) throws DatabaseException {
        String sql = "UPDATE `service_bookings` SET `status` = ? WHERE `id` = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, newStatus);
            ps.setInt(2, bookingId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update booking status: " + e.getMessage(), e);
        } finally {
            DBConnection.closeQuietly(conn, ps, null);
        }
    }

    private ServiceItem mapService(ResultSet rs) throws SQLException {
        String type = rs.getString("service_type");
        int id = rs.getInt("id");
        int petId = rs.getInt("pet_id");
        String petName = rs.getString("pet_name");
        Date bDate = rs.getDate("booking_date");
        Date sDate = rs.getDate("start_date");
        Date eDate = rs.getDate("end_date");
        String stationOrCage = rs.getString("cage_or_room_no");
        String details = rs.getString("package_details");
        String notes = rs.getString("special_instructions");
        double cost = rs.getDouble("cost");
        String status = rs.getString("status");

        if ("BOARDING".equalsIgnoreCase(type)) {
            BoardingBooking bb = new BoardingBooking(
                id, petId,
                bDate != null ? bDate.toLocalDate() : LocalDate.now(),
                sDate != null ? sDate.toLocalDate() : LocalDate.now(),
                eDate != null ? eDate.toLocalDate() : LocalDate.now().plusDays(1),
                stationOrCage, details, notes, 800.0, status
            );
            bb.setPetName(petName);
            bb.setCost(cost);
            return bb;
        } else {
            GroomingBooking gb = new GroomingBooking(
                id, petId,
                bDate != null ? bDate.toLocalDate() : LocalDate.now(),
                sDate != null ? sDate.toLocalDate() : LocalDate.now(),
                stationOrCage, details, notes, cost, status
            );
            gb.setPetName(petName);
            return gb;
        }
    }
}
