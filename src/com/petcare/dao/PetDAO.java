package com.petcare.dao;

import com.petcare.exception.DatabaseException;
import com.petcare.model.Pet;
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
 * Pet Data Access Object
 * Demonstrates SQL joins, Method Overloading, and Exception handling.
 */
public class PetDAO implements GenericDAO<Pet> {

    @Override
    public void insert(Pet pet) throws DatabaseException {
        String sql = "INSERT INTO `pets` (`owner_id`, `name`, `species`, `breed`, `gender`, " +
                     "`date_of_birth`, `weight_kg`, `color`, `microchip_id`, `allergies`, `blood_group`, `registered_date`) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, pet.getOwnerId());
            ps.setString(2, pet.getName());
            ps.setString(3, pet.getSpecies());
            ps.setString(4, pet.getBreed());
            ps.setString(5, pet.getGender());
            ps.setDate(6, pet.getDateOfBirth() != null ? Date.valueOf(pet.getDateOfBirth()) : null);
            ps.setDouble(7, pet.getWeightKg());
            ps.setString(8, pet.getColor());
            ps.setString(9, pet.getMicrochipId());
            ps.setString(10, pet.getAllergies());
            ps.setString(11, pet.getBloodGroup());
            ps.setDate(12, pet.getRegisteredDate() != null ? Date.valueOf(pet.getRegisteredDate()) : new Date(System.currentTimeMillis()));
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                pet.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to register pet: " + e.getMessage(), e);
        } finally {
            DBConnection.closeQuietly(conn, ps, rs);
        }
    }

    @Override
    public void update(Pet pet) throws DatabaseException {
        String sql = "UPDATE `pets` SET `owner_id` = ?, `name` = ?, `species` = ?, `breed` = ?, " +
                     "`gender` = ?, `date_of_birth` = ?, `weight_kg` = ?, `color` = ?, " +
                     "`microchip_id` = ?, `allergies` = ?, `blood_group` = ? WHERE `id` = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, pet.getOwnerId());
            ps.setString(2, pet.getName());
            ps.setString(3, pet.getSpecies());
            ps.setString(4, pet.getBreed());
            ps.setString(5, pet.getGender());
            ps.setDate(6, pet.getDateOfBirth() != null ? Date.valueOf(pet.getDateOfBirth()) : null);
            ps.setDouble(7, pet.getWeightKg());
            ps.setString(8, pet.getColor());
            ps.setString(9, pet.getMicrochipId());
            ps.setString(10, pet.getAllergies());
            ps.setString(11, pet.getBloodGroup());
            ps.setInt(12, pet.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update pet: " + e.getMessage(), e);
        } finally {
            DBConnection.closeQuietly(conn, ps, null);
        }
    }

    @Override
    public void delete(int id) throws DatabaseException {
        String sql = "DELETE FROM `pets` WHERE `id` = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete pet: " + e.getMessage(), e);
        } finally {
            DBConnection.closeQuietly(conn, ps, null);
        }
    }

    @Override
    public Pet findById(int id) throws DatabaseException {
        String sql = "SELECT p.*, o.full_name AS owner_name FROM `pets` p " +
                     "JOIN `pet_owners` o ON p.owner_id = o.id WHERE p.id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return mapPet(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find pet: " + e.getMessage(), e);
        } finally {
            DBConnection.closeQuietly(conn, ps, rs);
        }
    }

    @Override
    public List<Pet> findAll() throws DatabaseException {
        String sql = "SELECT p.*, o.full_name AS owner_name FROM `pets` p " +
                     "JOIN `pet_owners` o ON p.owner_id = o.id ORDER BY p.name ASC";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Pet> list = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                list.add(mapPet(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch all pets: " + e.getMessage(), e);
        } finally {
            DBConnection.closeQuietly(conn, stmt, rs);
        }
    }

    public List<Pet> findByOwnerId(int ownerId) throws DatabaseException {
        String sql = "SELECT p.*, o.full_name AS owner_name FROM `pets` p " +
                     "JOIN `pet_owners` o ON p.owner_id = o.id WHERE p.owner_id = ? ORDER BY p.name ASC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Pet> list = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, ownerId);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapPet(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch pets for owner: " + e.getMessage(), e);
        } finally {
            DBConnection.closeQuietly(conn, ps, rs);
        }
    }

    public List<Pet> search(String keyword) throws DatabaseException {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }

        String sql = "SELECT p.*, o.full_name AS owner_name FROM `pets` p " +
                     "JOIN `pet_owners` o ON p.owner_id = o.id " +
                     "WHERE p.name LIKE ? OR p.species LIKE ? OR p.breed LIKE ? OR o.full_name LIKE ? " +
                     "ORDER BY p.name ASC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Pet> list = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            String term = "%" + keyword.trim() + "%";
            ps.setString(1, term);
            ps.setString(2, term);
            ps.setString(3, term);
            ps.setString(4, term);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapPet(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Search error for pets: " + e.getMessage(), e);
        } finally {
            DBConnection.closeQuietly(conn, ps, rs);
        }
    }

    private Pet mapPet(ResultSet rs) throws SQLException {
        Pet pet = new Pet();
        pet.setId(rs.getInt("id"));
        pet.setOwnerId(rs.getInt("owner_id"));
        pet.setOwnerName(rs.getString("owner_name"));
        pet.setName(rs.getString("name"));
        pet.setSpecies(rs.getString("species"));
        pet.setBreed(rs.getString("breed"));
        pet.setGender(rs.getString("gender"));
        Date dob = rs.getDate("date_of_birth");
        if (dob != null) {
            pet.setDateOfBirth(dob.toLocalDate());
        }
        pet.setWeightKg(rs.getDouble("weight_kg"));
        pet.setColor(rs.getString("color"));
        pet.setMicrochipId(rs.getString("microchip_id"));
        pet.setAllergies(rs.getString("allergies"));
        pet.setBloodGroup(rs.getString("blood_group"));
        Date regDate = rs.getDate("registered_date");
        if (regDate != null) {
            pet.setRegisteredDate(regDate.toLocalDate());
        }
        return pet;
    }
}
