package com.petcare.model;

import java.time.LocalDate;

/**
 * Unit 1: Encapsulation, Constructor Overloading, 'this' keyword
 * Unit 2: Implements Searchable interface
 */
public class Pet implements Searchable {
    private int id;
    private int ownerId;
    private String ownerName; // Helpful cached display field
    private String name;
    private String species;   // Dog, Cat, Bird, Rabbit, etc.
    private String breed;
    private String gender;    // Male, Female
    private LocalDate dateOfBirth;
    private double weightKg;
    private String color;
    private String microchipId;
    private String allergies;
    private String bloodGroup;
    private LocalDate registeredDate;

    // Unit 1: Default Constructor
    public Pet() {
        this.registeredDate = LocalDate.now();
        this.weightKg = 0.0;
        this.gender = "Male";
        this.species = "Dog";
    }

    // Unit 1: Parameterized Constructor
    public Pet(int id, int ownerId, String name, String species, String breed, String gender,
               LocalDate dateOfBirth, double weightKg, String color, String microchipId,
               String allergies, String bloodGroup, LocalDate registeredDate) {
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.weightKg = weightKg;
        this.color = color;
        this.microchipId = microchipId;
        this.allergies = allergies;
        this.bloodGroup = bloodGroup;
        this.registeredDate = (registeredDate != null) ? registeredDate : LocalDate.now();
    }

    // Constructor Overloading (for new pet registration)
    public Pet(int ownerId, String name, String species, String breed, String gender,
               LocalDate dateOfBirth, double weightKg, String color, String microchipId,
               String allergies, String bloodGroup) {
        this(0, ownerId, name, species, breed, gender, dateOfBirth, weightKg, color, microchipId, allergies, bloodGroup, LocalDate.now());
    }

    // Unit 2: Implementation of Searchable
    @Override
    public boolean matchesSearch(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return true;
        }
        String lower = keyword.toLowerCase();
        return (name != null && name.toLowerCase().contains(lower)) ||
               (species != null && species.toLowerCase().contains(lower)) ||
               (breed != null && breed.toLowerCase().contains(lower)) ||
               (microchipId != null && microchipId.toLowerCase().contains(lower)) ||
               (ownerName != null && ownerName.toLowerCase().contains(lower));
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOwnerId() { return ownerId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }

    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public double getWeightKg() { return weightKg; }
    public void setWeightKg(double weightKg) { this.weightKg = weightKg; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getMicrochipId() { return microchipId; }
    public void setMicrochipId(String microchipId) { this.microchipId = microchipId; }

    public String getAllergies() { return allergies; }
    public void setAllergies(String allergies) { this.allergies = allergies; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public LocalDate getRegisteredDate() { return registeredDate; }
    public void setRegisteredDate(LocalDate registeredDate) { this.registeredDate = registeredDate; }

    @Override
    public String toString() {
        return name + " (" + species + (breed != null ? " - " + breed : "") + ")";
    }
}
