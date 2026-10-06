package com.petcare.dao;

import com.petcare.exception.DatabaseException;
import com.petcare.model.PetOwner;
import com.petcare.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * PetOwner Data Access Object
 * Demonstrates GenericDAO implementation and Method Overloading for search operations.
 */
public class PetOwnerDAO implements GenericDAO<PetOwner> {

    @Override
    public void insert(PetOwner owner) throws DatabaseException {
        String sql = "INSERT INTO `pet_owners` (`full_name`, `phone`, `email`, `address`, `emergency_contact`, `registered_date`) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, owner.getFullName());
            ps.setString(2, owner.getPhone());
            ps.setString(3, owner.getEmail());
            ps.setString(4, owner.getAddress());
            ps.setString(5, owner.getEmergencyContact());
            ps.setDate(6, owner.getRegisteredDate() != null ? Date.valueOf(owner.getRegisteredDate()) : new Date(System.currentTimeMillis()));
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                owner.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to register pet owner: " + e.getMessage(), e);
        } finally {
            DBConnection.closeQuietly(conn, ps, rs);
        }
    }

    @Override
    public void update(PetOwner owner) throws DatabaseException {
        String sql = "UPDATE `pet_owners` SET `full_name` = ?, `phone` = ?, `email` = ?, " +
                     "`address` = ?, `emergency_contact` = ? WHERE `id` = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, owner.getFullName());
            ps.setString(2, owner.getPhone());
            ps.setString(3, owner.getEmail());
            ps.setString(4, owner.getAddress());
            ps.setString(5, owner.getEmergencyContact());
            ps.setInt(6, owner.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update pet owner: " + e.getMessage(), e);
        } finally {
            DBConnection.closeQuietly(conn, ps, null);
        }
    }

    @Override
    public void delete(int id) throws DatabaseException {
        String sql = "DELETE FROM `pet_owners` WHERE `id` = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete pet owner: " + e.getMessage(), e);
        } finally {
            DBConnection.closeQuietly(conn, ps, null);
        }
    }

    @Override
    public PetOwner findById(int id) throws DatabaseException {
        String sql = "SELECT * FROM `pet_owners` WHERE `id` = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return mapOwner(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find pet owner: " + e.getMessage(), e);
        } finally {
            DBConnection.closeQuietly(conn, ps, rs);
        }
    }

    @Override
    public List<PetOwner> findAll() throws DatabaseException {
        String sql = "SELECT * FROM `pet_owners` ORDER BY `full_name` ASC";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<PetOwner> list = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                list.add(mapOwner(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch pet owners: " + e.getMessage(), e);
        } finally {
            DBConnection.closeQuietly(conn, stmt, rs);
        }
    }

    /**
     * Unit 2 Syllabus Demonstration: Method Overloading (Search by single keyword)
     */
    public List<PetOwner> search(String keyword) throws DatabaseException {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }

        String sql = "SELECT * FROM `pet_owners` WHERE `full_name` LIKE ? OR `phone` LIKE ? OR `email` LIKE ? ORDER BY `full_name` ASC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<PetOwner> list = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            String term = "%" + keyword.trim() + "%";
            ps.setString(1, term);
            ps.setString(2, term);
            ps.setString(3, term);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapOwner(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Search error for pet owners: " + e.getMessage(), e);
        } finally {
            DBConnection.closeQuietly(conn, ps, rs);
        }
    }

    /**
     * Unit 2 Syllabus Demonstration: Method Overloading (Search with specific name and phone criteria)
     */
    public List<PetOwner> search(String name, String phone) throws DatabaseException {
        String sql = "SELECT * FROM `pet_owners` WHERE `full_name` LIKE ? AND `phone` LIKE ? ORDER BY `full_name` ASC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<PetOwner> list = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + (name != null ? name : "") + "%");
            ps.setString(2, "%" + (phone != null ? phone : "") + "%");
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapOwner(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Overloaded search error: " + e.getMessage(), e);
        } finally {
            DBConnection.closeQuietly(conn, ps, rs);
        }
    }

    private PetOwner mapOwner(ResultSet rs) throws SQLException {
        PetOwner owner = new PetOwner();
        owner.setId(rs.getInt("id"));
        owner.setFullName(rs.getString("full_name"));
        owner.setPhone(rs.getString("phone"));
        owner.setEmail(rs.getString("email"));
        owner.setAddress(rs.getString("address"));
        owner.setEmergencyContact(rs.getString("emergency_contact"));
        Date regDate = rs.getDate("registered_date");
        if (regDate != null) {
            owner.setRegisteredDate(regDate.toLocalDate());
        }
        return owner;
    }
}
