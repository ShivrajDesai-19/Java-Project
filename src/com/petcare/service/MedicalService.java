package com.petcare.service;

import com.petcare.dao.MedicalRecordDAO;
import com.petcare.exception.DatabaseException;
import com.petcare.exception.InvalidInputException;
import com.petcare.model.DiagnosisRecord;
import com.petcare.model.LabTestRecord;
import com.petcare.model.VaccinationRecord;
import com.petcare.util.InputValidator;

import java.util.List;

/**
 * Service managing Clinical Consultations, Vaccinations, and Laboratory Tests
 */
public class MedicalService {

    private final MedicalRecordDAO medicalRecordDAO;

    public MedicalService() {
        this.medicalRecordDAO = new MedicalRecordDAO();
    }

    // Diagnosis
    public void recordDiagnosis(DiagnosisRecord record) throws InvalidInputException, DatabaseException {
        if (record.getPetId() <= 0) {
            throw new InvalidInputException("Please select a registered pet patient.");
        }
        if (record.getVetId() <= 0) {
            throw new InvalidInputException("Please assign a consulting veterinarian.");
        }
        InputValidator.validateNotEmpty(record.getDiagnosis(), "Clinical Diagnosis");
        medicalRecordDAO.insertDiagnosis(record);
    }

    public List<DiagnosisRecord> getDiagnosesForPet(int petId) throws DatabaseException {
        return medicalRecordDAO.findDiagnosesByPetId(petId);
    }

    // Vaccinations
    public void recordVaccination(VaccinationRecord vacc) throws InvalidInputException, DatabaseException {
        if (vacc.getPetId() <= 0) {
            throw new InvalidInputException("Please select a registered pet patient.");
        }
        InputValidator.validateNotEmpty(vacc.getVaccineName(), "Vaccine Name");
        if (vacc.getDateAdministered() == null) {
            throw new InvalidInputException("Please enter the administered date.");
        }
        if (vacc.getNextDueDate() == null) {
            throw new InvalidInputException("Please enter the next due booster date.");
        }
        medicalRecordDAO.insertVaccination(vacc);
    }

    public List<VaccinationRecord> getVaccinationsForPet(int petId) throws DatabaseException {
        return medicalRecordDAO.findVaccinationsByPetId(petId);
    }

    public List<VaccinationRecord> getUpcomingDueVaccinations() throws DatabaseException {
        return medicalRecordDAO.findUpcomingDueVaccinations();
    }

    // Lab Tests
    public void recordLabTest(LabTestRecord test) throws InvalidInputException, DatabaseException {
        if (test.getPetId() <= 0) {
            throw new InvalidInputException("Please select a registered pet patient.");
        }
        InputValidator.validateNotEmpty(test.getTestName(), "Test Name");
        InputValidator.validateNotEmpty(test.getTestCategory(), "Test Category");
        medicalRecordDAO.insertLabTest(test);
    }

    public List<LabTestRecord> getLabTestsForPet(int petId) throws DatabaseException {
        return medicalRecordDAO.findLabTestsByPetId(petId);
    }
}
