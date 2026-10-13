package com.petcare.service;

import com.petcare.dao.InvoiceDAO;
import com.petcare.exception.DatabaseException;
import com.petcare.exception.InvalidInputException;
import com.petcare.model.Invoice;
import com.petcare.model.InvoiceItem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Service orchestrating Invoicing, Tax Calculations, and Payment Recording
 */
public class BillingService {

    private final InvoiceDAO invoiceDAO;

    public BillingService() {
        this.invoiceDAO = new InvoiceDAO();
    }

    public void processInvoice(Invoice invoice) throws InvalidInputException, DatabaseException {
        if (invoice.getPetId() <= 0) {
            throw new InvalidInputException("Invoice must be associated with a registered pet.");
        }
        if (invoice.getOwnerId() <= 0) {
            throw new InvalidInputException("Invoice must have an associated pet owner.");
        }
        if (invoice.getItems() == null || invoice.getItems().isEmpty()) {
            throw new InvalidInputException("Cannot create an empty invoice. Please add at least one billable item.");
        }

        // Generate unique invoice number if not set
        if (invoice.getInvoiceNumber() == null || invoice.getInvoiceNumber().isEmpty()) {
            String timestamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
            invoice.setInvoiceNumber("INV-" + timestamp);
        }

        invoice.recalculateTotals();
        invoiceDAO.createInvoice(invoice);
    }

    public List<Invoice> getAllInvoices() throws DatabaseException {
        return invoiceDAO.findAll();
    }

    public Invoice getInvoiceById(int id) throws DatabaseException {
        return invoiceDAO.findById(id);
    }

    public double getTotalRevenue() throws DatabaseException {
        return invoiceDAO.calculateTotalRevenue();
    }
}
