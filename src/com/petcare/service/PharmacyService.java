package com.petcare.service;

import com.petcare.dao.MedicineDAO;
import com.petcare.exception.DatabaseException;
import com.petcare.exception.InsufficientStockException;
import com.petcare.exception.InvalidInputException;
import com.petcare.model.Medicine;
import com.petcare.util.InputValidator;

import java.util.List;

/**
 * Service managing Pharmacy Inventory and Stock Control
 */
public class PharmacyService {

    private final MedicineDAO medicineDAO;

    public PharmacyService() {
        this.medicineDAO = new MedicineDAO();
    }

    public void addMedicine(Medicine med) throws InvalidInputException, DatabaseException {
        InputValidator.validateNotEmpty(med.getName(), "Medicine Name");
        InputValidator.validateNotEmpty(med.getBatchNumber(), "Batch Number");
        if (med.getUnitPrice() <= 0) {
            throw new InvalidInputException("Unit price must be greater than zero.");
        }
        if (med.getStockQuantity() < 0) {
            throw new InvalidInputException("Stock quantity cannot be negative.");
        }
        medicineDAO.insert(med);
    }

    public void updateMedicine(Medicine med) throws InvalidInputException, DatabaseException {
        InputValidator.validateNotEmpty(med.getName(), "Medicine Name");
        InputValidator.validateNotEmpty(med.getBatchNumber(), "Batch Number");
        if (med.getUnitPrice() <= 0) {
            throw new InvalidInputException("Unit price must be greater than zero.");
        }
        medicineDAO.update(med);
    }

    public void deleteMedicine(int id) throws DatabaseException {
        medicineDAO.delete(id);
    }

    public List<Medicine> getAllMedicines() throws DatabaseException {
        return medicineDAO.findAll();
    }

    public List<Medicine> searchMedicines(String keyword) throws DatabaseException {
        return medicineDAO.search(keyword);
    }

    public List<Medicine> getLowStockMedicines() throws DatabaseException {
        return medicineDAO.findLowStockMedicines();
    }

    public void dispense(int medicineId, int qty) throws DatabaseException, InsufficientStockException {
        medicineDAO.dispenseMedicine(medicineId, qty);
    }
}
