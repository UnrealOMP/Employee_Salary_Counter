package com.example.payroll.pdf;

import com.example.payroll.exception.PayrollCalculationException;
import com.example.payroll.model.PayrollResult;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Component generating PDF salary slips using OpenPDF.
 */
@Component
public class SalarySlipGenerator {

    private static final Logger log = LoggerFactory.getLogger(SalarySlipGenerator.class);

    private static final Font TITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.DARK_GRAY);
    private static final Font SUBTITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA, 11, Color.GRAY);
    private static final Font SECTION_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new Color(41, 128, 185));
    private static final Font BOLD_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.BLACK);
    private static final Font REGULAR_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.DARK_GRAY);
    private static final Font NET_PAY_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, new Color(39, 174, 96));

    /**
     * Generates a PDF salary slip for an employee.
     *
     * @param result employee payroll result data
     * @return byte array containing PDF document
     * @throws PayrollCalculationException if PDF generation fails
     */
    public byte[] generateSalarySlipPdf(PayrollResult result) {
        log.info("Generating PDF salary slip for employee ID: {}", result.getEmployeeId());

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(document, baos);
            document.open();

            // 1. Header Banner
            Paragraph title = new Paragraph("SQUARE ONE MEDIA PVT LTD", TITLE_FONT);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph subtitle = new Paragraph("Employee Payslip for " + result.getPayPeriod(), SUBTITLE_FONT);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(15);
            document.add(subtitle);

            // 2. Employee Details Table
            PdfPTable empTable = new PdfPTable(2);
            empTable.setWidthPercentage(100);
            empTable.setWidths(new float[]{1f, 1f});

            addHeaderCell(empTable, "EMPLOYEE INFORMATION", 2);
            addDetailRow(empTable, "Employee ID:", result.getEmployeeId(), "Employee Name:", result.getEmployeeName());
            addDetailRow(empTable, "Pay Period:", result.getPayPeriod(), "Working Days:", String.valueOf(result.getApplicableWorkingDays()));
            addDetailRow(empTable, "Days Present:", String.valueOf(result.getDaysWithAttendance()), "Status:", "Processed");
            empTable.setSpacingAfter(15);
            document.add(empTable);

            // 3. Attendance Summary Box
            PdfPTable attTable = new PdfPTable(4);
            attTable.setWidthPercentage(100);
            attTable.setWidths(new float[]{1f, 1f, 1f, 1f});

            addHeaderCell(attTable, "ATTENDANCE & LEAVE SUMMARY", 4);
            addStatCell(attTable, "Late Marks", String.valueOf(result.getLateMarks()));
            addStatCell(attTable, "Half Days", String.valueOf(result.getHalfDays()));
            addStatCell(attTable, "Early Leaves", String.valueOf(result.getEarlyLeavingInstances()));
            addStatCell(attTable, "Overtime Hours", result.getOvertimeHours() + " hrs");
            attTable.setSpacingAfter(15);
            document.add(attTable);

            // 4. Earnings & Deductions Table
            PdfPTable financialTable = new PdfPTable(4);
            financialTable.setWidthPercentage(100);
            financialTable.setWidths(new float[]{2f, 1.2f, 2f, 1.2f});

            // Column Headers
            addTableHeaderCell(financialTable, "EARNINGS");
            addTableHeaderCell(financialTable, "AMOUNT");
            addTableHeaderCell(financialTable, "DEDUCTIONS");
            addTableHeaderCell(financialTable, "AMOUNT");

            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

            // Row 1: Basic / Monthly Salary vs Late Deduction
            addTableCell(financialTable, "Basic / Monthly Salary", REGULAR_FONT, Element.ALIGN_LEFT);
            addTableCell(financialTable, currencyFormat.format(result.getMonthlySalary()), REGULAR_FONT, Element.ALIGN_RIGHT);
            addTableCell(financialTable, "Late Mark Deduction (" + result.getExcessLateMarks() + " excess)", REGULAR_FONT, Element.ALIGN_LEFT);
            addTableCell(financialTable, currencyFormat.format(result.getLateMarkDeduction()), REGULAR_FONT, Element.ALIGN_RIGHT);

            // Row 2: Overtime Pay vs Half Day Deduction
            addTableCell(financialTable, "Overtime Pay (" + result.getOvertimeHours() + " hrs)", REGULAR_FONT, Element.ALIGN_LEFT);
            addTableCell(financialTable, currencyFormat.format(result.getOvertimePay()), REGULAR_FONT, Element.ALIGN_RIGHT);
            addTableCell(financialTable, "Half Day Deduction (" + result.getHalfDays() + " days)", REGULAR_FONT, Element.ALIGN_LEFT);
            addTableCell(financialTable, currencyFormat.format(result.getHalfDayDeduction()), REGULAR_FONT, Element.ALIGN_RIGHT);

            // Row 3: Blank vs Early Leave Deduction
            addTableCell(financialTable, "-", REGULAR_FONT, Element.ALIGN_LEFT);
            addTableCell(financialTable, "₹0.00", REGULAR_FONT, Element.ALIGN_RIGHT);
            addTableCell(financialTable, "Early Leave Deduction (" + Math.max(0, result.getEarlyLeavingInstances() - result.getAllowedEarlyLeavingInstances()) + " excess)", REGULAR_FONT, Element.ALIGN_LEFT);
            addTableCell(financialTable, currencyFormat.format(result.getEarlyLeavingDeduction()), REGULAR_FONT, Element.ALIGN_RIGHT);

            // Totals Row
            addTableCell(financialTable, "Total Gross Earnings", BOLD_FONT, Element.ALIGN_LEFT);
            addTableCell(financialTable, currencyFormat.format(result.getMonthlySalary().add(result.getOvertimePay())), BOLD_FONT, Element.ALIGN_RIGHT);
            addTableCell(financialTable, "Total Deductions", BOLD_FONT, Element.ALIGN_LEFT);
            addTableCell(financialTable, currencyFormat.format(result.getTotalDeductions()), BOLD_FONT, Element.ALIGN_RIGHT);

            financialTable.setSpacingAfter(20);
            document.add(financialTable);

            // 5. Net Payable Salary Banner
            PdfPTable netTable = new PdfPTable(2);
            netTable.setWidthPercentage(100);
            netTable.setWidths(new float[]{1.5f, 1f});

            PdfPCell labelCell = new PdfPCell(new Phrase("NET PAYABLE SALARY:", BOLD_FONT));
            labelCell.setBorder(Rectangle.NO_BORDER);
            labelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            netTable.addCell(labelCell);

            PdfPCell amountCell = new PdfPCell(new Phrase(currencyFormat.format(result.getFinalPayableSalary()), NET_PAY_FONT));
            amountCell.setBorder(Rectangle.NO_BORDER);
            amountCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            netTable.addCell(amountCell);

            document.add(netTable);

            // Footer note
            Paragraph footer = new Paragraph("\n\nThis is a computer-generated salary slip and requires no physical signature.", SUBTITLE_FONT);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Failed to generate PDF salary slip: {}", e.getMessage(), e);
            throw new PayrollCalculationException("Failed to generate PDF salary slip for employee " + result.getEmployeeId() + ": " + e.getMessage(), e);
        }
    }

    private void addHeaderCell(PdfPTable table, String title, int colspan) {
        PdfPCell cell = new PdfPCell(new Phrase(title, SECTION_FONT));
        cell.setColspan(colspan);
        cell.setBackgroundColor(new Color(245, 247, 250));
        cell.setPadding(6);
        cell.setBorderColor(new Color(220, 224, 230));
        table.addCell(cell);
    }

    private void addTableHeaderCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, BOLD_FONT));
        cell.setBackgroundColor(new Color(236, 240, 241));
        cell.setPadding(6);
        cell.setBorderColor(new Color(220, 224, 230));
        table.addCell(cell);
    }

    private void addTableCell(PdfPTable table, String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        cell.setHorizontalAlignment(alignment);
        cell.setBorderColor(new Color(230, 233, 238));
        table.addCell(cell);
    }

    private void addDetailRow(PdfPTable table, String label1, String val1, String label2, String val2) {
        addTableCell(table, label1 + " " + val1, REGULAR_FONT, Element.ALIGN_LEFT);
        addTableCell(table, label2 + " " + val2, REGULAR_FONT, Element.ALIGN_LEFT);
    }

    private void addStatCell(PdfPTable table, String label, String value) {
        PdfPCell cell = new PdfPCell();
        cell.setPadding(6);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorderColor(new Color(220, 224, 230));
        cell.addElement(new Paragraph(label, SUBTITLE_FONT));
        cell.addElement(new Paragraph(value, BOLD_FONT));
        table.addCell(cell);
    }
}
