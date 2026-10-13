package com.petcare.dao;

import com.petcare.exception.DatabaseException;
import com.petcare.model.Invoice;
import com.petcare.model.InvoiceItem;
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
 * Invoice Data Access Object
 * Unit 3 Syllabus Demonstration:
 * Implements JDBC Transaction Management with commit() and rollback() inside catch blocks.
 */
public class InvoiceDAO {

    /**
     * Inserts an invoice and all its items atomically inside a database transaction.
     */
    public void createInvoice(Invoice invoice) throws DatabaseException {
        String insertInvSql = "INSERT INTO `invoices` (`invoice_number`, `pet_id`, `owner_id`, `invoice_date`, " +
                              "`subtotal`, `tax_amount`, `discount_amount`, `total_amount`, `payment_method`, `payment_status`) " +
                              "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String insertItemSql = "INSERT INTO `invoice_items` (`invoice_id`, `description`, `item_type`, `quantity`, `unit_price`, `total_price`) " +
                               "VALUES (?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement psInv = null;
        PreparedStatement psItem = null;
        ResultSet rsKeys = null;

        try {
            conn = DBConnection.getConnection();
            // Start Transaction
            conn.setAutoCommit(false);

            psInv = conn.prepareStatement(insertInvSql, Statement.RETURN_GENERATED_KEYS);
            psInv.setString(1, invoice.getInvoiceNumber());
            psInv.setInt(2, invoice.getPetId());
            psInv.setInt(3, invoice.getOwnerId());
            psInv.setDate(4, invoice.getInvoiceDate() != null ? Date.valueOf(invoice.getInvoiceDate()) : new Date(System.currentTimeMillis()));
            psInv.setDouble(5, invoice.getSubtotal());
            psInv.setDouble(6, invoice.getTaxAmount());
            psInv.setDouble(7, invoice.getDiscountAmount());
            psInv.setDouble(8, invoice.getTotalAmount());
            psInv.setString(9, invoice.getPaymentMethod());
            psInv.setString(10, invoice.getPaymentStatus());
            psInv.executeUpdate();

            rsKeys = psInv.getGeneratedKeys();
            int invoiceId;
            if (rsKeys.next()) {
                invoiceId = rsKeys.getInt(1);
                invoice.setId(invoiceId);
            } else {
                throw new SQLException("Failed to retrieve generated invoice ID.");
            }

            // Insert line items
            psItem = conn.prepareStatement(insertItemSql);
            for (InvoiceItem item : invoice.getItems()) {
                psItem.setInt(1, invoiceId);
                psItem.setString(2, item.getDescription());
                psItem.setString(3, item.getItemType());
                psItem.setInt(4, item.getQuantity());
                psItem.setDouble(5, item.getUnitPrice());
                psItem.setDouble(6, item.getTotalPrice());
                psItem.addBatch();
            }
            psItem.executeBatch();

            // Commit transaction
            conn.commit();

        } catch (SQLException e) {
            // Rollback transaction on failure (Unit 3 concept)
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("[InvoiceDAO] Transaction rolled back due to error: " + e.getMessage());
                } catch (SQLException rollbackEx) {
                    System.err.println("[InvoiceDAO] Rollback failed: " + rollbackEx.getMessage());
                }
            }
            throw new DatabaseException("Failed to generate invoice transaction: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
            }
            DBConnection.closeQuietly(null, psItem, null);
            DBConnection.closeQuietly(conn, psInv, rsKeys);
        }
    }

    public List<Invoice> findAll() throws DatabaseException {
        String sql = "SELECT i.*, p.name AS pet_name, o.full_name AS owner_name FROM `invoices` i " +
                     "JOIN `pets` p ON i.pet_id = p.id " +
                     "JOIN `pet_owners` o ON i.owner_id = o.id " +
                     "ORDER BY i.invoice_date DESC, i.id DESC";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Invoice> list = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                list.add(mapInvoice(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch invoices: " + e.getMessage(), e);
        } finally {
            DBConnection.closeQuietly(conn, stmt, rs);
        }
    }

    public Invoice findById(int id) throws DatabaseException {
        String sql = "SELECT i.*, p.name AS pet_name, o.full_name AS owner_name FROM `invoices` i " +
                     "JOIN `pets` p ON i.pet_id = p.id " +
                     "JOIN `pet_owners` o ON i.owner_id = o.id WHERE i.id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                Invoice inv = mapInvoice(rs);
                inv.setItems(findItemsByInvoiceId(inv.getId(), conn));
                return inv;
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find invoice: " + e.getMessage(), e);
        } finally {
            DBConnection.closeQuietly(conn, ps, rs);
        }
    }

    public List<InvoiceItem> findItemsByInvoiceId(int invoiceId, Connection existingConn) throws SQLException {
        String sql = "SELECT * FROM `invoice_items` WHERE `invoice_id` = ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<InvoiceItem> items = new ArrayList<>();

        try {
            ps = existingConn.prepareStatement(sql);
            ps.setInt(1, invoiceId);
            rs = ps.executeQuery();
            while (rs.next()) {
                InvoiceItem item = new InvoiceItem(
                    rs.getInt("id"),
                    rs.getInt("invoice_id"),
                    rs.getString("description"),
                    rs.getString("item_type"),
                    rs.getInt("quantity"),
                    rs.getDouble("unit_price"),
                    rs.getDouble("total_price")
                );
                items.add(item);
            }
            return items;
        } finally {
            DBConnection.closeQuietly(null, ps, rs);
        }
    }

    public double calculateTotalRevenue() throws DatabaseException {
        String sql = "SELECT SUM(total_amount) FROM `invoices` WHERE `payment_status` = 'PAID'";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getDouble(1);
            }
            return 0.0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to calculate total revenue: " + e.getMessage(), e);
        } finally {
            DBConnection.closeQuietly(conn, stmt, rs);
        }
    }

    private Invoice mapInvoice(ResultSet rs) throws SQLException {
        Invoice inv = new Invoice();
        inv.setId(rs.getInt("id"));
        inv.setInvoiceNumber(rs.getString("invoice_number"));
        inv.setPetId(rs.getInt("pet_id"));
        inv.setPetName(rs.getString("pet_name"));
        inv.setOwnerId(rs.getInt("owner_id"));
        inv.setOwnerName(rs.getString("owner_name"));
        Date d = rs.getDate("invoice_date");
        if (d != null) {
            inv.setInvoiceDate(d.toLocalDate());
        }
        inv.setSubtotal(rs.getDouble("subtotal"));
        inv.setTaxAmount(rs.getDouble("tax_amount"));
        inv.setDiscountAmount(rs.getDouble("discount_amount"));
        inv.setTotalAmount(rs.getDouble("total_amount"));
        inv.setPaymentMethod(rs.getString("payment_method"));
        inv.setPaymentStatus(rs.getString("payment_status"));
        return inv;
    }
}
