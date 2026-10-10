package com.petcare.dao;

import com.petcare.exception.DatabaseException;
import com.petcare.exception.InsufficientStockException;
import com.petcare.model.Medicine;
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
 * Medicine DAO for Pharmacy Inventory Management
 * Unit 4 Syllabus Demonstration:
 * Utilizes synchronized method to control concurrent stock dispensing.
 */
public class MedicineDAO implements GenericDAO<Medicine> {

    @Override
    public void insert(Medicine med) throws DatabaseException {
        String sql = "INSERT INTO `medicines` (`name`, `category`, `batch_number`, `stock_quantity`, `min_stock_alert`, `unit_price`, `expiry_date`, `supplier`) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, med.getName());
            ps.setString(2, med.getCategory());
            ps.setString(3, med.getBatchNumber());
            ps.setInt(4, med.getStockQuantity());
            ps.setInt(5, med.getMinStockAlert());
            ps.setDouble(6, med.getUnitPrice());
            ps.setDate(7, med.getExpiryDate() != null ? Date.valueOf(med.getExpiryDate()) : Date.valueOf(LocalDate.now().plusYears(1)));
            ps.setString(8, med.getSupplier());
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                med.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to add medicine to inventory: " + e.getMessage(), e);
        } finally {
            DBConnection.closeQuietly(conn, ps, rs);
        }
    }

    @Override
    public void update(Medicine med) throws DatabaseException {
        String sql = "UPDATE `medicines` SET `name` = ?, `category` = ?, `batch_number` = ?, " +
                     "`stock_quantity` = ?, `min_stock_alert` = ?, `unit_price` = ?, " +
                     "`expiry_date` = ?, `supplier` = ? WHERE `id` = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, med.getName());
            ps.setString(2, med.getCategory());
            ps.setString(3, med.getBatchNumber());
            ps.setInt(4, med.getStockQuantity());
            ps.setInt(5, med.getMinStockAlert());
            ps.setDouble(6, med.getUnitPrice());
            ps.setDate(7, med.getExpiryDate() != null ? Date.valueOf(med.getExpiryDate()) : null);
            ps.setString(8, med.getSupplier());
            ps.setInt(9, med.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update medicine inventory: " + e.getMessage(), e);
        } finally {
            DBConnection.closeQuietly(conn, ps, null);
        }
    }

    @Override
    public void delete(int id) throws DatabaseException {
        String sql = "DELETE FROM `medicines` WHERE `id` = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete medicine: " + e.getMessage(), e);
        } finally {
            DBConnection.closeQuietly(conn, ps, null);
        }
    }

    @Override
    public Medicine findById(int id) throws DatabaseException {
        String sql = "SELECT * FROM `medicines` WHERE `id` = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return mapMedicine(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find medicine: " + e.getMessage(), e);
        } finally {
            DBConnection.closeQuietly(conn, ps, rs);
        }
    }

    @Override
    public List<Medicine> findAll() throws DatabaseException {
        String sql = "SELECT * FROM `medicines` ORDER BY `name` ASC";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Medicine> list = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                list.add(mapMedicine(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch medicines: " + e.getMessage(), e);
        } finally {
            DBConnection.closeQuietly(conn, stmt, rs);
        }
    }

    public List<Medicine> search(String keyword) throws DatabaseException {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }

        String sql = "SELECT * FROM `medicines` WHERE `name` LIKE ? OR `category` LIKE ? OR `batch_number` LIKE ? ORDER BY `name` ASC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Medicine> list = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            String term = "%" + keyword.trim() + "%";
            ps.setString(1, term);
            ps.setString(2, term);
            ps.setString(3, term);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapMedicine(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Search error for medicines: " + e.getMessage(), e);
        } finally {
            DBConnection.closeQuietly(conn, ps, rs);
        }
    }

    /**
     * Finds medicines that are below or at their minStockAlert threshold.
     * Queried by the background StockAlertThread.
     */
    public List<Medicine> findLowStockMedicines() throws DatabaseException {
        String sql = "SELECT * FROM `medicines` WHERE `stock_quantity` <= `min_stock_alert` ORDER BY `stock_quantity` ASC";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Medicine> list = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                list.add(mapMedicine(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to check low stock medicines: " + e.getMessage(), e);
        } finally {
            DBConnection.closeQuietly(conn, stmt, rs);
        }
    }

    /**
     * Unit 4 Syllabus Demonstration:
     * Synchronized method prevents race conditions when dispensing stock across multiple threads.
     */
    public synchronized void dispenseMedicine(int medicineId, int quantity) 
            throws DatabaseException, InsufficientStockException {
        Medicine med = findById(medicineId);
        if (med == null) {
            throw new DatabaseException("Medicine not found with ID: " + medicineId);
        }
        if (med.getStockQuantity() < quantity) {
            throw new InsufficientStockException(med.getName(), quantity, med.getStockQuantity());
        }

        String sql = "UPDATE `medicines` SET `stock_quantity` = `stock_quantity` - ? WHERE `id` = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, quantity);
            ps.setInt(2, medicineId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error dispensing medicine stock: " + e.getMessage(), e);
        } finally {
            DBConnection.closeQuietly(conn, ps, null);
        }
    }

    private Medicine mapMedicine(ResultSet rs) throws SQLException {
        Medicine med = new Medicine();
        med.setId(rs.getInt("id"));
        med.setName(rs.getString("name"));
        med.setCategory(rs.getString("category"));
        med.setBatchNumber(rs.getString("batch_number"));
        med.setStockQuantity(rs.getInt("stock_quantity"));
        med.setMinStockAlert(rs.getInt("min_stock_alert"));
        med.setUnitPrice(rs.getDouble("unit_price"));
        Date exp = rs.getDate("expiry_date");
        if (exp != null) {
            med.setExpiryDate(exp.toLocalDate());
        }
        med.setSupplier(rs.getString("supplier"));
        return med;
    }
}
