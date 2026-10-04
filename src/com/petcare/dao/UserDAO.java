package com.petcare.dao;

import com.petcare.exception.AuthenticationException;
import com.petcare.exception.DatabaseException;
import com.petcare.model.Administrator;
import com.petcare.model.Receptionist;
import com.petcare.model.StaffMember;
import com.petcare.model.User;
import com.petcare.model.UserRole;
import com.petcare.model.Veterinarian;
import com.petcare.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * User Data Access Object
 * Demonstrates Polymorphic Factory Creation: Instantiates specific subclasses
 * (Administrator, Veterinarian, Receptionist, StaffMember) based on role column.
 */
public class UserDAO implements GenericDAO<User> {

    /**
     * Authenticates user credentials and returns polymorphic User subclass instance.
     */
    public User authenticate(String username, String password) throws DatabaseException, AuthenticationException {
        String sql = "SELECT * FROM `users` WHERE `username` = ? AND `password` = ? AND `status` = 'ACTIVE'";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            rs = ps.executeQuery();

            if (rs.next()) {
                return mapUser(rs);
            } else {
                throw new AuthenticationException("Invalid username or password, or account is inactive.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database error during authentication: " + e.getMessage(), e);
        } finally {
            DBConnection.closeQuietly(conn, ps, rs);
        }
    }

    @Override
    public void insert(User user) throws DatabaseException {
        String sql = "INSERT INTO `users` (`username`, `password`, `full_name`, `role`, `email`, `phone`, `specialization`, `status`) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getRole().name());
            ps.setString(5, user.getEmail());
            ps.setString(6, user.getPhone());
            ps.setString(7, user.getSpecialization());
            ps.setString(8, user.getStatus() != null ? user.getStatus() : "ACTIVE");
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                user.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to insert user: " + e.getMessage(), e);
        } finally {
            DBConnection.closeQuietly(conn, ps, rs);
        }
    }

    @Override
    public void update(User user) throws DatabaseException {
        String sql = "UPDATE `users` SET `full_name` = ?, `role` = ?, `email` = ?, `phone` = ?, " +
                     "`specialization` = ?, `status` = ?" +
                     (user.getPassword() != null && !user.getPassword().isEmpty() ? ", `password` = ? " : " ") +
                     "WHERE `id` = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            int idx = 1;
            ps.setString(idx++, user.getFullName());
            ps.setString(idx++, user.getRole().name());
            ps.setString(idx++, user.getEmail());
            ps.setString(idx++, user.getPhone());
            ps.setString(idx++, user.getSpecialization());
            ps.setString(idx++, user.getStatus());
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                ps.setString(idx++, user.getPassword());
            }
            ps.setInt(idx, user.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update user: " + e.getMessage(), e);
        } finally {
            DBConnection.closeQuietly(conn, ps, null);
        }
    }

    @Override
    public void delete(int id) throws DatabaseException {
        String sql = "DELETE FROM `users` WHERE `id` = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete user: " + e.getMessage(), e);
        } finally {
            DBConnection.closeQuietly(conn, ps, null);
        }
    }

    @Override
    public User findById(int id) throws DatabaseException {
        String sql = "SELECT * FROM `users` WHERE `id` = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return mapUser(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find user by ID: " + e.getMessage(), e);
        } finally {
            DBConnection.closeQuietly(conn, ps, rs);
        }
    }

    @Override
    public List<User> findAll() throws DatabaseException {
        String sql = "SELECT * FROM `users` ORDER BY `id` ASC";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<User> list = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                list.add(mapUser(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch all users: " + e.getMessage(), e);
        } finally {
            DBConnection.closeQuietly(conn, stmt, rs);
        }
    }

    public List<User> findByRole(UserRole role) throws DatabaseException {
        String sql = "SELECT * FROM `users` WHERE `role` = ? AND `status` = 'ACTIVE' ORDER BY `full_name` ASC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<User> list = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, role.name());
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapUser(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch users by role: " + e.getMessage(), e);
        } finally {
            DBConnection.closeQuietly(conn, ps, rs);
        }
    }

    /**
     * Polymorphic Object Factory instantiation (Unit 2 concept)
     */
    private User mapUser(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String username = rs.getString("username");
        String pass = rs.getString("password");
        String name = rs.getString("full_name");
        String roleStr = rs.getString("role");
        String email = rs.getString("email");
        String phone = rs.getString("phone");
        String spec = rs.getString("specialization");
        String status = rs.getString("status");

        UserRole role;
        try {
            role = UserRole.valueOf(roleStr);
        } catch (IllegalArgumentException e) {
            role = UserRole.STAFF;
        }

        // Return specific subclass based on role
        User user;
        switch (role) {
            case ADMINISTRATOR:
                user = new Administrator(id, username, pass, name, email, phone, spec, status);
                break;
            case VETERINARIAN:
                user = new Veterinarian(id, username, pass, name, email, phone, spec, status);
                break;
            case RECEPTIONIST:
                user = new Receptionist(id, username, pass, name, email, phone, spec, status);
                break;
            case STAFF:
            default:
                user = new StaffMember(id, username, pass, name, email, phone, spec, status);
                break;
        }
        user.setCreatedAt(rs.getTimestamp("created_at"));
        return user;
    }
}
