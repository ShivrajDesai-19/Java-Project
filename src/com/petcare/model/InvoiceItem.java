package com.petcare.model;

/**
 * Line item in an invoice representing a charge.
 */
public class InvoiceItem {
    private int id;
    private int invoiceId;
    private String description;
    private String itemType; // CONSULTATION, MEDICINE, LAB_TEST, GROOMING, BOARDING, VACCINATION
    private int quantity;
    private double unitPrice;
    private double totalPrice;

    public InvoiceItem() {
        this.quantity = 1;
    }

    public InvoiceItem(int id, int invoiceId, String description, String itemType, 
                       int quantity, double unitPrice, double totalPrice) {
        this.id = id;
        this.invoiceId = invoiceId;
        this.description = description;
        this.itemType = itemType;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
    }

    public InvoiceItem(String description, String itemType, int quantity, double unitPrice) {
        this(0, 0, description, itemType, quantity, unitPrice, quantity * unitPrice);
    }

    // Factory method from Billable interface (Polymorphism in action!)
    public static InvoiceItem fromBillable(Billable billable) {
        return new InvoiceItem(
            billable.getItemDescription(),
            billable.getItemType(),
            billable.getQuantity(),
            billable.getUnitPrice()
        );
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getInvoiceId() { return invoiceId; }
    public void setInvoiceId(int invoiceId) { this.invoiceId = invoiceId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { 
        this.quantity = quantity; 
        this.totalPrice = quantity * unitPrice;
    }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { 
        this.unitPrice = unitPrice; 
        this.totalPrice = quantity * unitPrice;
    }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
}
