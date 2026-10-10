package com.petcare.model;

import java.time.LocalDate;

/**
 * Unit 1: Encapsulation, Constructor Overloading, 'this' keyword
 * Unit 2: Implements Billable and Searchable interfaces (Multiple Interface Implementation)
 */
public class Medicine implements Billable, Searchable {
    private int id;
    private String name;
    private String category; // Antibiotic, Painkiller, Vaccine, Antiseptic, Vitamin, Antiparasitic
    private String batchNumber;
    private int stockQuantity;
    private int minStockAlert;
    private double unitPrice;
    private LocalDate expiryDate;
    private String supplier;

    // Temporary quantity field when used in billing / dispensing
    private int dispenseQuantity = 1;

    public Medicine() {
        this.minStockAlert = 10;
        this.unitPrice = 0.0;
        this.expiryDate = LocalDate.now().plusYears(1);
    }

    public Medicine(int id, String name, String category, String batchNumber, 
                    int stockQuantity, int minStockAlert, double unitPrice, 
                    LocalDate expiryDate, String supplier) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.batchNumber = batchNumber;
        this.stockQuantity = stockQuantity;
        this.minStockAlert = minStockAlert;
        this.unitPrice = unitPrice;
        this.expiryDate = expiryDate;
        this.supplier = supplier;
    }

    // Constructor Overloading
    public Medicine(String name, String category, String batchNumber, 
                    int stockQuantity, int minStockAlert, double unitPrice, 
                    LocalDate expiryDate, String supplier) {
        this(0, name, category, batchNumber, stockQuantity, minStockAlert, unitPrice, expiryDate, supplier);
    }

    public boolean isLowStock() {
        return this.stockQuantity <= this.minStockAlert;
    }

    public boolean isExpired() {
        return this.expiryDate != null && this.expiryDate.isBefore(LocalDate.now());
    }

    // Implementation of Billable
    @Override
    public String getItemDescription() {
        return name + " (" + category + ") [Batch: " + batchNumber + "]";
    }

    @Override
    public double getUnitPrice() {
        return unitPrice;
    }

    @Override
    public int getQuantity() {
        return dispenseQuantity;
    }

    public void setDispenseQuantity(int qty) {
        this.dispenseQuantity = qty;
    }

    @Override
    public double calculateTotalPrice() {
        return unitPrice * dispenseQuantity;
    }

    @Override
    public String getItemType() {
        return "MEDICINE";
    }

    // Implementation of Searchable
    @Override
    public boolean matchesSearch(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return true;
        }
        String lower = keyword.toLowerCase();
        return (name != null && name.toLowerCase().contains(lower)) ||
               (category != null && category.toLowerCase().contains(lower)) ||
               (batchNumber != null && batchNumber.toLowerCase().contains(lower)) ||
               (supplier != null && supplier.toLowerCase().contains(lower));
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }

    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }

    public int getMinStockAlert() { return minStockAlert; }
    public void setMinStockAlert(int minStockAlert) { this.minStockAlert = minStockAlert; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public String getSupplier() { return supplier; }
    public void setSupplier(String supplier) { this.supplier = supplier; }

    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    @Override
    public String toString() {
        return name + " (₹" + unitPrice + " | Stock: " + stockQuantity + ")";
    }
}
