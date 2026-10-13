package com.petcare.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Unit 1 & Unit 2: Encapsulated Invoice model with Billable line items
 * and mathematical tax/discount calculations.
 */
public class Invoice implements Searchable {
    private int id;
    private String invoiceNumber;
    private int petId;
    private String petName;
    private int ownerId;
    private String ownerName;
    private LocalDate invoiceDate;
    private double subtotal;
    private double taxAmount;
    private double discountAmount;
    private double totalAmount;
    private String paymentMethod; // Cash, UPI, Credit Card, Debit Card, NetBanking
    private String paymentStatus; // PAID, PENDING, CANCELLED

    private List<InvoiceItem> items = new ArrayList<>();

    // Unit 2 Syllabus concept: 'final' keyword constant
    public static final double DEFAULT_TAX_RATE = 0.05; // 5% GST

    public Invoice() {
        this.invoiceDate = LocalDate.now();
        this.paymentMethod = "Cash";
        this.paymentStatus = "PAID";
    }

    public Invoice(int id, String invoiceNumber, int petId, int ownerId, LocalDate invoiceDate,
                   double subtotal, double taxAmount, double discountAmount, double totalAmount,
                   String paymentMethod, String paymentStatus) {
        this.id = id;
        this.invoiceNumber = invoiceNumber;
        this.petId = petId;
        this.ownerId = ownerId;
        this.invoiceDate = (invoiceDate != null) ? invoiceDate : LocalDate.now();
        this.subtotal = subtotal;
        this.taxAmount = taxAmount;
        this.discountAmount = discountAmount;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
    }

    public void addItem(InvoiceItem item) {
        items.add(item);
        recalculateTotals();
    }

    public void removeItem(int index) {
        if (index >= 0 && index < items.size()) {
            items.remove(index);
            recalculateTotals();
        }
    }

    public void recalculateTotals() {
        double sum = 0.0;
        for (InvoiceItem item : items) {
            sum += item.getTotalPrice();
        }
        this.subtotal = Math.round(sum * 100.0) / 100.0;
        this.taxAmount = Math.round((this.subtotal * DEFAULT_TAX_RATE) * 100.0) / 100.0;
        double net = this.subtotal + this.taxAmount - this.discountAmount;
        this.totalAmount = Math.max(0.0, Math.round(net * 100.0) / 100.0);
    }

    @Override
    public boolean matchesSearch(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return true;
        }
        String lower = keyword.toLowerCase();
        return (invoiceNumber != null && invoiceNumber.toLowerCase().contains(lower)) ||
               (petName != null && petName.toLowerCase().contains(lower)) ||
               (ownerName != null && ownerName.toLowerCase().contains(lower)) ||
               (paymentMethod != null && paymentMethod.toLowerCase().contains(lower)) ||
               (paymentStatus != null && paymentStatus.toLowerCase().contains(lower));
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public int getPetId() { return petId; }
    public void setPetId(int petId) { this.petId = petId; }

    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }

    public int getOwnerId() { return ownerId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public LocalDate getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(LocalDate invoiceDate) { this.invoiceDate = invoiceDate; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public double getTaxAmount() { return taxAmount; }
    public void setTaxAmount(double taxAmount) { this.taxAmount = taxAmount; }

    public double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(double discountAmount) { 
        this.discountAmount = discountAmount;
        recalculateTotals();
    }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public List<InvoiceItem> getItems() { return items; }
    public void setItems(List<InvoiceItem> items) { 
        this.items = items; 
        recalculateTotals();
    }
}
