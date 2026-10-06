package com.petcare.service;

import com.petcare.dao.PetDAO;
import com.petcare.dao.PetOwnerDAO;
import com.petcare.exception.DatabaseException;
import com.petcare.exception.InvalidInputException;
import com.petcare.model.Pet;
import com.petcare.model.PetOwner;
import com.petcare.util.InputValidator;

import java.util.List;

/**
 * Service orchestrating Pet Owner & Pet Management business logic and validations.
 */
public class PetManagementService {

    private final PetOwnerDAO ownerDAO;
    private final PetDAO petDAO;

    public PetManagementService() {
        this.ownerDAO = new PetOwnerDAO();
        this.petDAO = new PetDAO();
    }

    // Owner Operations
    public void registerOwner(PetOwner owner) throws InvalidInputException, DatabaseException {
        InputValidator.validateNotEmpty(owner.getFullName(), "Owner Full Name");
        InputValidator.validatePhone(owner.getPhone(), "Owner Phone Number");
        InputValidator.validateEmail(owner.getEmail(), "Owner Email");
        ownerDAO.insert(owner);
    }

    public void updateOwner(PetOwner owner) throws InvalidInputException, DatabaseException {
        InputValidator.validateNotEmpty(owner.getFullName(), "Owner Full Name");
        InputValidator.validatePhone(owner.getPhone(), "Owner Phone Number");
        InputValidator.validateEmail(owner.getEmail(), "Owner Email");
        ownerDAO.update(owner);
    }

    public void deleteOwner(int ownerId) throws DatabaseException {
        ownerDAO.delete(ownerId);
    }

    public List<PetOwner> getAllOwners() throws DatabaseException {
        return ownerDAO.findAll();
    }

    public List<PetOwner> searchOwners(String query) throws DatabaseException {
        return ownerDAO.search(query);
    }

    // Pet Operations
    public void registerPet(Pet pet) throws InvalidInputException, DatabaseException {
        if (pet.getOwnerId() <= 0) {
            throw new InvalidInputException("Please select an existing pet owner.");
        }
        InputValidator.validateNotEmpty(pet.getName(), "Pet Name");
        InputValidator.validateNotEmpty(pet.getSpecies(), "Species");
        if (pet.getWeightKg() < 0) {
            throw new InvalidInputException("Pet weight cannot be negative.");
        }
        petDAO.insert(pet);
    }

    public void updatePet(Pet pet) throws InvalidInputException, DatabaseException {
        InputValidator.validateNotEmpty(pet.getName(), "Pet Name");
        InputValidator.validateNotEmpty(pet.getSpecies(), "Species");
        if (pet.getWeightKg() < 0) {
            throw new InvalidInputException("Pet weight cannot be negative.");
        }
        petDAO.update(pet);
    }

    public void deletePet(int petId) throws DatabaseException {
        petDAO.delete(petId);
    }

    public List<Pet> getAllPets() throws DatabaseException {
        return petDAO.findAll();
    }

    public List<Pet> searchPets(String query) throws DatabaseException {
        return petDAO.search(query);
    }

    public List<Pet> getPetsByOwner(int ownerId) throws DatabaseException {
        return petDAO.findByOwnerId(ownerId);
    }
}
