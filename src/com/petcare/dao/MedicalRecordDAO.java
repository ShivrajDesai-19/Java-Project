package com.petcare.dao;

import com.petcare.exception.DatabaseException;
import com.petcare.model.DiagnosisRecord;
import com.petcare.model.LabTestRecord;
import com.petcare.model.VaccinationRecord;
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
 * Data Access Object for Clinical Diagnoses, Vaccinations, and Lab Tests
 */
public class MedicalRecordDAO {

    // 1. Clinical Diagnosis Records
    public void insertDiagnosis(DiagnosisRecord record) throws DatabaseException {
        String sql = "INSERT INTO `medical_records` (`pet_id`, `vet_id`, `visit_date`, `record_type`, " +
                     "`symptoms`, `diagnosis`, `treatment_plan`, `temperature_c`, `pulse_rate`) " +
                     "VALUES (?, ?, ?, 'DIAGNOSIS', ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, record.getPetId());
            ps.setInt(2, record.getVetId());
            ps.setDate(3, record.getVisitDate() != null ? Date.valueOf(record.getVisitDate()) : new Date(System.currentTimeMillis()));
            ps.setString(4, record.getSymptoms());
            ps.setString(5, record.getDiagnosis());
            ps.setString(6, record.getTreatmentPlan());
            ps.setDouble(7, record.getTemperatureC());
            ps.setInt(8, record.getPulseRate());
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                record.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save diagnosis record: " + e.getMessage(), e);
        } finally {
            DBConnection.closeQuietly(conn, ps, rs);
        }
    }

    public List<DiagnosisRecord> findDiagnosesByPetId(int petId) throws DatabaseException {
        String sql = "SELECT m.*, p.name AS pet_name, u.full_name AS vet_name FROM `medical_records` m " +
                     "JOIN `pets` p ON m.pet_id = p.id " +
                     "JOIN `users` u ON m.vet_id = u.id " +
                     "WHERE m.pet_id = ? AND m.record_type = 'DIAGNOSIS' ORDER BY m.visit_date DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<DiagnosisRecord> list = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, petId);
            rs = ps.executeQuery();
            while (rs.next()) {
                DiagnosisRecord d = new DiagnosisRecord(
                    rs.getInt("id"),
                    rs.getInt("pet_id"),
                    rs.getInt("vet_id"),
                    rs.getDate("visit_date") != null ? rs.getDate("visit_date").toLocalDate() : LocalDate.now(),
                    rs.getString("symptoms"),
                    rs.getString("diagnosis"),
                    rs.getString("treatment_plan"),
                    rs.getDouble("temperature_c"),
                    rs.getInt("pulse_rate")
                );
                d.setPetName(rs.getString("pet_name"));
                d.setVetName(rs.getString("vet_name"));
                list.add(d);
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch medical diagnoses: " + e.getMessage(), e);
        } finally {
            DBConnection.closeQuietly(conn, ps, rs);
        }
    }

    // 2. Vaccinations
    public void insertVaccination(VaccinationRecord vacc) throws DatabaseException {
        String sql = "INSERT INTO `vaccinations` (`pet_id`, `vaccine_name`, `batch_no`, `date_administered`, `next_due_date`, `administered_by`, `status`, `remarks`) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, vacc.getPetId());
            ps.setString(2, vacc.getVaccineName());
            ps.setString(3, vacc.getBatchNo());
            ps.setDate(4, Date.valueOf(vacc.getDateAdministered()));
            ps.setDate(5, Date.valueOf(vacc.getNextDueDate()));
            ps.setInt(6, vacc.getVetId());
            ps.setString(7, vacc.getStatus());
            ps.setString(8, vacc.getRemarks());
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                vacc.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to record vaccination: " + e.getMessage(), e);
        } finally {
            DBConnection.closeQuietly(conn, ps, rs);
        }
    }

    public List<VaccinationRecord> findVaccinationsByPetId(int petId) throws DatabaseException {
        String sql = "SELECT v.*, p.name AS pet_name, u.full_name AS vet_name FROM `vaccinations` v " +
                     "JOIN `pets` p ON v.pet_id = p.id " +
                     "LEFT JOIN `users` u ON v.administered_by = u.id " +
                     "WHERE v.pet_id = ? ORDER BY v.date_administered DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<VaccinationRecord> list = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, petId);
            rs = ps.executeQuery();
            while (rs.next()) {
                VaccinationRecord v = new VaccinationRecord(
                    rs.getInt("id"),
                    rs.getInt("pet_id"),
                    rs.getInt("administered_by"),
                    rs.getString("vaccine_name"),
                    rs.getString("batch_no"),
                    rs.getDate("date_administered") != null ? rs.getDate("date_administered").toLocalDate() : LocalDate.now(),
                    rs.getDate("next_due_date") != null ? rs.getDate("next_due_date").toLocalDate() : LocalDate.now(),
                    rs.getString("status"),
                    rs.getString("remarks")
                );
                v.setPetName(rs.getString("pet_name"));
                v.setVetName(rs.getString("vet_name"));
                list.add(v);
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch vaccinations: " + e.getMessage(), e);
        } finally {
            DBConnection.closeQuietly(conn, ps, rs);
        }
    }

    public List<VaccinationRecord> findUpcomingDueVaccinations() throws DatabaseException {
        String sql = "SELECT v.*, p.name AS pet_name, u.full_name AS vet_name FROM `vaccinations` v " +
                     "JOIN `pets` p ON v.pet_id = p.id " +
                     "LEFT JOIN `users` u ON v.administered_by = u.id " +
                     "WHERE v.next_due_date <= DATE_ADD(CURDATE(), INTERVAL 14 DAY) " +
                     "ORDER BY v.next_due_date ASC";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<VaccinationRecord> list = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                VaccinationRecord v = new VaccinationRecord(
                    rs.getInt("id"),
                    rs.getInt("pet_id"),
                    rs.getInt("administered_by"),
                    rs.getString("vaccine_name"),
                    rs.getString("batch_no"),
                    rs.getDate("date_administered") != null ? rs.getDate("date_administered").toLocalDate() : LocalDate.now(),
                    rs.getDate("next_due_date") != null ? rs.getDate("next_due_date").toLocalDate() : LocalDate.now(),
                    rs.getString("status"),
                    rs.getString("remarks")
                );
                v.setPetName(rs.getString("pet_name"));
                v.setVetName(rs.getString("vet_name"));
                list.add(v);
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch due vaccinations: " + e.getMessage(), e);
        } finally {
            DBConnection.closeQuietly(conn, stmt, rs);
        }
    }

    // 3. Laboratory Diagnostic Tests
    public void insertLabTest(LabTestRecord test) throws DatabaseException {
        String sql = "INSERT INTO `lab_tests` (`pet_id`, `test_name`, `test_date`, `test_category`, `results`, `normal_range`, `status`, `conducted_by`) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, test.getPetId());
            ps.setString(2, test.getTestName());
            ps.setDate(3, Date.valueOf(test.getVisitDate()));
            ps.setString(4, test.getTestCategory());
            ps.setString(5, test.getResults());
            ps.setString(6, test.getNormalRange());
            ps.setString(7, test.getStatus());
            ps.setString(8, test.getConductedBy());
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                test.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to record lab test: " + e.getMessage(), e);
        } finally {
            DBConnection.closeQuietly(conn, ps, rs);
        }
    }

    public List<LabTestRecord> findLabTestsByPetId(int petId) throws DatabaseException {
        String sql = "SELECT l.*, p.name AS pet_name FROM `lab_tests` l " +
                     "JOIN `pets` p ON l.pet_id = p.id " +
                     "WHERE l.pet_id = ? ORDER BY l.test_date DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<LabTestRecord> list = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, petId);
            rs = ps.executeQuery();
            while (rs.next()) {
                LabTestRecord l = new LabTestRecord(
                    rs.getInt("id"),
                    rs.getInt("pet_id"),
                    0,
                    rs.getDate("test_date") != null ? rs.getDate("test_date").toLocalDate() : LocalDate.now(),
                    rs.getString("test_name"),
                    rs.getString("test_category"),
                    rs.getString("results"),
                    rs.getString("normal_range"),
                    rs.getString("status"),
                    rs.getString("conducted_by")
                );
                l.setPetName(rs.getString("pet_name"));
                list.add(l);
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch lab tests: " + e.getMessage(), e);
        } finally {
            DBConnection.closeQuietly(conn, ps, rs);
        }
    }
}
