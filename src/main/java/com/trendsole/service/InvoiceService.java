package com.trendsole.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.trendsole.config.CompanyProperties;
import com.trendsole.model.Order;
import com.trendsole.model.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class InvoiceService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    @Autowired
    private CompanyProperties companyProperties;

    @Autowired
    private ResourceLoader resourceLoader;

    public byte[] generateInvoicePdf(Order order) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Palette & Fonts
            Color primaryColor = new Color(30, 41, 59);
            Color secondaryColor = new Color(71, 85, 105);
            Color lightBgColor = new Color(248, 250, 252);
            Color tableHeaderBg = new Color(241, 245, 249);
            Color borderColor = new Color(226, 232, 240);

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, primaryColor);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, primaryColor);
            Font subHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, primaryColor);
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 9, secondaryColor);
            Font bodyBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, primaryColor);
            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.ITALIC, secondaryColor);

            // 1. Company Header with Optional Logo
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{60, 40});

            PdfPCell companyCell = new PdfPCell();
            companyCell.setBorder(Rectangle.NO_BORDER);

            boolean logoLoaded = false;
            try {
                Resource logoResource = resourceLoader.getResource("classpath:static/images/logo.png");
                if (logoResource.exists()) {
                    try (InputStream is = logoResource.getInputStream()) {
                        byte[] logoBytes = is.readAllBytes();
                        Image logo = Image.getInstance(logoBytes);
                        logo.scaleToFit(120, 45);
                        companyCell.addElement(logo);
                        logoLoaded = true;
                    }
                }
            } catch (Exception ignored) {
                // Do NOT throw exception if logo is missing or invalid; fallback to text name
            }

            if (!logoLoaded) {
                companyCell.addElement(new Paragraph(companyProperties.getName(), titleFont));
            }

            if (companyProperties.getAddress() != null && !companyProperties.getAddress().isBlank()) {
                companyCell.addElement(new Paragraph(companyProperties.getAddress(), bodyFont));
            }
            if (companyProperties.getPhone() != null && !companyProperties.getPhone().isBlank()) {
                companyCell.addElement(new Paragraph("Phone: " + companyProperties.getPhone(), bodyFont));
            }
            companyCell.addElement(new Paragraph("Support: " + companyProperties.getEmail(), bodyFont));
            if (companyProperties.getWebsite() != null && !companyProperties.getWebsite().isBlank()) {
                companyCell.addElement(new Paragraph(companyProperties.getWebsite(), bodyFont));
            }
            headerTable.addCell(companyCell);

            // Invoice Title & Details Meta
            PdfPCell invoiceMetaCell = new PdfPCell();
            invoiceMetaCell.setBorder(Rectangle.NO_BORDER);

            Paragraph invTitle = new Paragraph("INVOICE", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, primaryColor));
            invTitle.setAlignment(Element.ALIGN_RIGHT);
            invoiceMetaCell.addElement(invTitle);

            String invNo = "INV-" + (order.getOrderNumber() != null ? order.getOrderNumber() : order.getId());
            Paragraph pInvNo = new Paragraph("Invoice No: " + invNo, bodyBold);
            pInvNo.setAlignment(Element.ALIGN_RIGHT);
            invoiceMetaCell.addElement(pInvNo);

            String orderDateStr = order.getOrderDate() != null ? order.getOrderDate().format(DATE_FORMATTER) : "N/A";
            Paragraph pOrderDate = new Paragraph("Order Date: " + orderDateStr, bodyFont);
            pOrderDate.setAlignment(Element.ALIGN_RIGHT);
            invoiceMetaCell.addElement(pOrderDate);

            headerTable.addCell(invoiceMetaCell);
            document.add(headerTable);

            document.add(new Paragraph(" "));

            // 2. Customer & Order Info Card
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new float[]{50, 50});

            PdfPCell custCell = new PdfPCell();
            custCell.setBackgroundColor(lightBgColor);
            custCell.setPadding(10);
            custCell.setBorderColor(borderColor);
            custCell.addElement(new Paragraph("CUSTOMER DETAILS", subHeaderFont));
            custCell.addElement(new Paragraph("Name: " + (order.getCustomerName() != null ? order.getCustomerName() : "N/A"), bodyFont));
            custCell.addElement(new Paragraph("Email: " + (order.getEmail() != null ? order.getEmail() : "N/A"), bodyFont));
            custCell.addElement(new Paragraph("Address: " + (order.getAddress() != null ? order.getAddress() : "N/A"), bodyFont));
            infoTable.addCell(custCell);

            PdfPCell payCell = new PdfPCell();
            payCell.setBackgroundColor(lightBgColor);
            payCell.setPadding(10);
            payCell.setBorderColor(borderColor);
            payCell.addElement(new Paragraph("ORDER & PAYMENT", subHeaderFont));
            payCell.addElement(new Paragraph("Order Number: " + (order.getOrderNumber() != null ? order.getOrderNumber() : order.getId()), bodyFont));
            payCell.addElement(new Paragraph("Order Status: " + (order.getStatus() != null ? order.getStatus().name() : "N/A"), bodyBold));
            payCell.addElement(new Paragraph("Payment Method: " + (order.getPaymentMethod() != null ? order.getPaymentMethod() : "N/A"), bodyFont));
            
            // TODO: Connect future payment gateway integration to retrieve real-time Payment Status
            payCell.addElement(new Paragraph("Payment Status: N/A", bodyFont));
            infoTable.addCell(payCell);

            document.add(infoTable);
            document.add(new Paragraph(" "));

            // 3. Line Items Table
            PdfPTable itemsTable = new PdfPTable(4);
            itemsTable.setWidthPercentage(100);
            itemsTable.setWidths(new float[]{45, 15, 20, 20});

            String[] headers = {"Product Name", "Quantity", "Unit Price", "Line Total"};
            for (String h : headers) {
                int align = "Product Name".equals(h) ? Element.ALIGN_LEFT : Element.ALIGN_RIGHT;
                itemsTable.addCell(createCell(h, subHeaderFont, align, tableHeaderBg, borderColor, 6));
            }

            if (order.getOrderItems() != null) {
                for (OrderItem item : order.getOrderItems()) {
                    String name = (item.getProduct() != null && item.getProduct().getName() != null)
                            ? item.getProduct().getName() : "Product";
                    int qty = item.getQuantity() != null ? item.getQuantity() : 1;
                    double unitPrice = item.getPrice() != null ? item.getPrice() : (item.getProduct() != null ? item.getProduct().getPrice() : 0.0);
                    double lineTotal = unitPrice * qty;

                    itemsTable.addCell(createCell(name, bodyFont, Element.ALIGN_LEFT, null, borderColor, 6));
                    itemsTable.addCell(createCell(String.valueOf(qty), bodyFont, Element.ALIGN_RIGHT, null, borderColor, 6));
                    itemsTable.addCell(createCell(formatCurrency(unitPrice), bodyFont, Element.ALIGN_RIGHT, null, borderColor, 6));
                    itemsTable.addCell(createCell(formatCurrency(lineTotal), bodyFont, Element.ALIGN_RIGHT, null, borderColor, 6));
                }
            }

            document.add(itemsTable);

            // 4. Financial Summary Table
            PdfPTable summaryTable = new PdfPTable(2);
            summaryTable.setWidthPercentage(45);
            summaryTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
            summaryTable.setWidths(new float[]{55, 45});

            double storedTotal = order.getTotalAmount() != null ? order.getTotalAmount() : 0.0;

            addSummaryRow(summaryTable, "Subtotal:", formatCurrency(storedTotal), bodyFont, borderColor);
            addSummaryRow(summaryTable, "Discount:", formatCurrency(0.0), bodyFont, borderColor);
            addSummaryRow(summaryTable, "Shipping:", formatCurrency(0.0), bodyFont, borderColor);
            addSummaryRow(summaryTable, "Tax:", formatCurrency(0.0), bodyFont, borderColor);
            addSummaryRow(summaryTable, "Grand Total:", formatCurrency(storedTotal), bodyBold, borderColor);

            document.add(summaryTable);

            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            // 5. Professional Footer
            Paragraph footerText1 = new Paragraph("Thank you for shopping with " + companyProperties.getName() + ".", headerFont);
            footerText1.setAlignment(Element.ALIGN_CENTER);
            document.add(footerText1);

            document.add(new Paragraph(" "));

            Paragraph footerText2 = new Paragraph("Support: " + companyProperties.getEmail(), bodyFont);
            footerText2.setAlignment(Element.ALIGN_CENTER);
            document.add(footerText2);

            Paragraph footerText3 = new Paragraph("This is a computer-generated invoice and does not require a signature.", footerFont);
            footerText3.setAlignment(Element.ALIGN_CENTER);
            document.add(footerText3);

            document.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Error generating PDF invoice", e);
        }

        return out.toByteArray();
    }

    private String formatCurrency(Double amount) {
        return currencyFormatter.format(amount != null ? amount : 0.0);
    }

    private PdfPCell createCell(String text, Font font, int alignment, Color bgColor, Color borderColor, float padding) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        if (bgColor != null) {
            cell.setBackgroundColor(bgColor);
        }
        if (borderColor != null) {
            cell.setBorderColor(borderColor);
        }
        cell.setPadding(padding);
        cell.setHorizontalAlignment(alignment);
        return cell;
    }

    private void addSummaryRow(PdfPTable table, String label, String value, Font font, Color borderColor) {
        table.addCell(createCell(label, font, Element.ALIGN_LEFT, null, borderColor, 4));
        table.addCell(createCell(value, font, Element.ALIGN_RIGHT, null, borderColor, 4));
    }
}
