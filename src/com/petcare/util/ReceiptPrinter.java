package com.petcare.util;

import com.petcare.model.Invoice;
import com.petcare.model.InvoiceItem;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.time.format.DateTimeFormatter;

/**
 * Unit 2 Syllabus Demonstration:
 * - StringBuilder & StringBuffer usage for dynamic text formatting
 * - java.awt & javax.swing packages
 */
public class ReceiptPrinter {

    private ReceiptPrinter() {}

    /**
     * Builds a printable text receipt utilizing StringBuilder (Unit 2 concept).
     */
    public static String generateTextReceipt(Invoice invoice) {
        StringBuilder sb = new StringBuilder();
        String line = "--------------------------------------------------------\n";
        String doubleLine = "========================================================\n";

        sb.append(doubleLine);
        sb.append("         SMART PET CARE & VETERINARY CLINIC             \n");
        sb.append("       12 Ring Road, Kalawad Junction, Rajkot           \n");
        sb.append("          Ph: +91 98765 43210 | info@petcare.in         \n");
        sb.append(doubleLine);
        sb.append(String.format("Invoice No : %-20s Date: %s\n", 
            invoice.getInvoiceNumber(), 
            invoice.getInvoiceDate() != null ? invoice.getInvoiceDate().format(DateTimeFormatter.ISO_DATE) : "N/A"));
        sb.append(String.format("Patient Pet: %-20s Owner: %s\n", 
            invoice.getPetName() != null ? invoice.getPetName() : "N/A", 
            invoice.getOwnerName() != null ? invoice.getOwnerName() : "N/A"));
        sb.append(String.format("Payment Mode: %-19s Status: %s\n", 
            invoice.getPaymentMethod(), invoice.getPaymentStatus()));
        sb.append(line);
        sb.append(String.format("%-30s %5s %8s %9s\n", "Item / Service", "Qty", "Price", "Total"));
        sb.append(line);

        for (InvoiceItem item : invoice.getItems()) {
            String desc = item.getDescription();
            if (desc.length() > 28) {
                desc = desc.substring(0, 25) + "...";
            }
            sb.append(String.format("%-30s %5d %8.2f %9.2f\n", 
                desc, item.getQuantity(), item.getUnitPrice(), item.getTotalPrice()));
        }

        sb.append(line);
        sb.append(String.format("%45s: %8.2f\n", "Subtotal (INR)", invoice.getSubtotal()));
        sb.append(String.format("%45s: %8.2f\n", "Tax (GST 5%)", invoice.getTaxAmount()));
        if (invoice.getDiscountAmount() > 0) {
            sb.append(String.format("%45s: -%7.2f\n", "Discount", invoice.getDiscountAmount()));
        }
        sb.append(doubleLine);
        sb.append(String.format("%45s: %8.2f\n", "GRAND TOTAL (INR)", invoice.getTotalAmount()));
        sb.append(doubleLine);
        sb.append("       Thank you for choosing Smart Pet Care!           \n");
        sb.append("           Wishing your pet good health!                \n");
        sb.append(doubleLine);

        return sb.toString();
    }

    /**
     * Builds HTML formatted receipt for modern view and direct printing
     */
    public static String generateHtmlReceipt(Invoice invoice) {
        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family: Arial, sans-serif; padding: 15px; color: #333;'>");
        html.append("<div style='text-align: center; border-bottom: 2px solid #2B579A; padding-bottom: 10px;'>");
        html.append("<h2 style='color: #2B579A; margin: 0;'>SMART PET CARE & VETERINARY CLINIC</h2>");
        html.append("<p style='margin: 4px 0; font-size: 12px; color: #666;'>12 Ring Road, Kalawad Junction, Rajkot | Ph: +91 98765 43210</p>");
        html.append("<h3 style='margin: 8px 0; color: #444;'>OFFICIAL TAX INVOICE</h3>");
        html.append("</div>");

        html.append("<table style='width: 100%; margin: 15px 0; font-size: 13px;'>");
        html.append("<tr><td><b>Invoice No:</b> ").append(invoice.getInvoiceNumber()).append("</td>");
        html.append("<td><b>Date:</b> ").append(invoice.getInvoiceDate()).append("</td></tr>");
        html.append("<tr><td><b>Pet Patient:</b> ").append(invoice.getPetName()).append("</td>");
        html.append("<td><b>Owner:</b> ").append(invoice.getOwnerName()).append("</td></tr>");
        html.append("<tr><td><b>Payment Mode:</b> ").append(invoice.getPaymentMethod()).append("</td>");
        html.append("<td><b>Status:</b> <span style='color: green; font-weight: bold;'>").append(invoice.getPaymentStatus()).append("</span></td></tr>");
        html.append("</table>");

        html.append("<table style='width: 100%; border-collapse: collapse; font-size: 13px;'>");
        html.append("<tr style='background-color: #2B579A; color: white;'>");
        html.append("<th style='padding: 8px; text-align: left;'>Service / Item</th>");
        html.append("<th style='padding: 8px; text-align: center;'>Qty</th>");
        html.append("<th style='padding: 8px; text-align: right;'>Unit Price (₹)</th>");
        html.append("<th style='padding: 8px; text-align: right;'>Total (₹)</th>");
        html.append("</tr>");

        boolean alt = false;
        for (InvoiceItem item : invoice.getItems()) {
            String bg = alt ? "#f9f9f9" : "#ffffff";
            html.append("<tr style='background-color: ").append(bg).append("; border-bottom: 1px solid #ddd;'>");
            html.append("<td style='padding: 8px;'>").append(item.getDescription()).append("</td>");
            html.append("<td style='padding: 8px; text-align: center;'>").append(item.getQuantity()).append("</td>");
            html.append("<td style='padding: 8px; text-align: right;'>₹").append(String.format("%.2f", item.getUnitPrice())).append("</td>");
            html.append("<td style='padding: 8px; text-align: right;'>₹").append(String.format("%.2f", item.getTotalPrice())).append("</td>");
            html.append("</tr>");
            alt = !alt;
        }
        html.append("</table>");

        html.append("<table style='width: 100%; margin-top: 15px; font-size: 13px;'>");
        html.append("<tr><td style='text-align: right;'>Subtotal:</td><td style='text-align: right; width: 120px;'>₹").append(String.format("%.2f", invoice.getSubtotal())).append("</td></tr>");
        html.append("<tr><td style='text-align: right;'>Tax (GST 5%):</td><td style='text-align: right;'>₹").append(String.format("%.2f", invoice.getTaxAmount())).append("</td></tr>");
        if (invoice.getDiscountAmount() > 0) {
            html.append("<tr><td style='text-align: right;'>Discount:</td><td style='text-align: right; color: red;'>-₹").append(String.format("%.2f", invoice.getDiscountAmount())).append("</td></tr>");
        }
        html.append("<tr style='font-size: 16px; font-weight: bold; border-top: 2px solid #2B579A;'><td style='text-align: right; padding-top: 6px;'>Grand Total:</td><td style='text-align: right; color: #2B579A; padding-top: 6px;'>₹").append(String.format("%.2f", invoice.getTotalAmount())).append("</td></tr>");
        html.append("</table>");

        html.append("<div style='margin-top: 30px; text-align: center; border-top: 1px dashed #aaa; padding-top: 10px; font-size: 12px; color: #777;'>");
        html.append("Thank you for trusting Smart Pet Care System. Get well soon!<br><i>Computer Generated Receipt</i>");
        html.append("</div>");
        html.append("</body></html>");

        return html.toString();
    }

    public static void showReceiptDialog(Window parent, Invoice invoice) {
        JDialog dialog = new JDialog(parent, "Invoice Receipt - " + invoice.getInvoiceNumber(), JDialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(560, 650);
        dialog.setLocationRelativeTo(parent);

        JEditorPane editorPane = new JEditorPane();
        editorPane.setContentType("text/html");
        editorPane.setText(generateHtmlReceipt(invoice));
        editorPane.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(editorPane);
        dialog.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton printBtn = new JButton("Print Receipt");
        printBtn.setBackground(UIStyleUtil.COLOR_PRIMARY);
        printBtn.setForeground(java.awt.Color.WHITE);
        printBtn.setFocusPainted(false);
        printBtn.addActionListener(e -> {
            try {
                boolean complete = editorPane.print();
                if (complete) {
                    JOptionPane.showMessageDialog(dialog, "Printing completed successfully.", "Print", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Print error: " + ex.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(printBtn);
        buttonPanel.add(closeBtn);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}
