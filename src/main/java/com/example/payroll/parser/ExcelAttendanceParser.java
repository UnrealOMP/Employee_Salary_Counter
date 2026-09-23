package com.example.payroll.parser;

import com.example.payroll.exception.InvalidExcelException;
import com.example.payroll.model.AttendanceRecord;
import com.example.payroll.model.Employee;
import com.example.payroll.util.DateTimeUtils;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

/**
 * Component responsible for parsing Excel attendance and salary workbooks using Apache POI.
 */
@Component
public class ExcelAttendanceParser {

    private static final Logger log = LoggerFactory.getLogger(ExcelAttendanceParser.class);

    /**
     * Parses an Excel input stream and extracts employee master details and attendance records.
     *
     * @param inputStream upload stream of .xlsx or .xls file
     * @param filename    original file name
     * @return parsed excel data container
     * @throws InvalidExcelException if file is corrupted, missing columns, or contains invalid data
     */
    public ParsedExcelData parse(InputStream inputStream, String filename) {
        if (inputStream == null) {
            throw new InvalidExcelException("Uploaded file stream is null.");
        }
        if (filename != null && !filename.toLowerCase().endsWith(".xlsx") && !filename.toLowerCase().endsWith(".xls")) {
            throw new InvalidExcelException("Unsupported file type: '" + filename + "'. Please upload an Excel file (.xlsx or .xls).");
        }

        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            if (workbook.getNumberOfSheets() == 0) {
                throw new InvalidExcelException("The uploaded Excel workbook contains no sheets.");
            }

            Map<String, Employee> employeeMap = new LinkedHashMap<>();
            List<AttendanceRecord> attendanceRecords = new ArrayList<>();
            String detectedPayPeriod = null;

            // 1. Check if there's a dedicated Salary sheet (e.g. "Salary Data" or "Salaries")
            Sheet salarySheet = findSheetByName(workbook, "salary");
            if (salarySheet != null) {
                parseSalarySheet(salarySheet, employeeMap);
            }

            // 2. Parse Attendance Sheet (e.g. "Punch In and Punch Out Data", "Sheet1", or first available sheet)
            Sheet attendanceSheet = findSheetByName(workbook, "punch");
            if (attendanceSheet == null) {
                attendanceSheet = findSheetByName(workbook, "attendance");
            }
            if (attendanceSheet == null) {
                // Pick sheet with attendance headers or default to sheet 0
                attendanceSheet = findBestAttendanceSheet(workbook);
            }

            if (attendanceSheet == null) {
                throw new InvalidExcelException("Could not find an attendance sheet in the workbook.");
            }

            detectedPayPeriod = parseAttendanceSheet(attendanceSheet, employeeMap, attendanceRecords);

            if (attendanceRecords.isEmpty()) {
                throw new InvalidExcelException("No valid attendance records were found in the uploaded file.");
            }

            if (employeeMap.isEmpty()) {
                throw new InvalidExcelException("No employee information or salary records could be resolved.");
            }

            // Fallback pay period if not auto-detected
            if (detectedPayPeriod == null && !attendanceRecords.isEmpty()) {
                LocalDate sampleDate = attendanceRecords.get(0).getDate();
                detectedPayPeriod = sampleDate.getMonth().name() + " " + sampleDate.getYear();
            }

            log.info("Excel parsing complete: {} employees, {} attendance records. Pay Period: {}",
                    employeeMap.size(), attendanceRecords.size(), detectedPayPeriod);

            return new ParsedExcelData(detectedPayPeriod, new ArrayList<>(employeeMap.values()), attendanceRecords);

        } catch (InvalidExcelException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to parse Excel file: {}", e.getMessage(), e);
            throw new InvalidExcelException("Failed to process Excel file: " + e.getMessage(), e);
        }
    }

    private Sheet findSheetByName(Workbook workbook, String keyword) {
        String lowerKw = keyword.toLowerCase();
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);
            if (sheet.getSheetName().toLowerCase().contains(lowerKw)) {
                return sheet;
            }
        }
        return null;
    }

    private Sheet findBestAttendanceSheet(Workbook workbook) {
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);
            int headerRowIdx = findHeaderRowIndex(sheet);
            if (headerRowIdx >= 0) {
                return sheet;
            }
        }
        return workbook.getSheetAt(0);
    }

    private void parseSalarySheet(Sheet sheet, Map<String, Employee> employeeMap) {
        int headerRowIdx = findHeaderRowIndex(sheet);
        if (headerRowIdx < 0) return;

        Row headerRow = sheet.getRow(headerRowIdx);
        Map<String, Integer> colMap = buildColumnHeaderMap(headerRow);

        Integer idCol = findColumn(colMap, "employee number", "employee id", "emp id", "emp no", "id");
        Integer nameCol = findColumn(colMap, "employee name", "emp name", "name");
        Integer salaryCol = findColumn(colMap, "monthly salary", "salary", "base salary");

        if (idCol == null) return;

        for (int r = headerRowIdx + 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (row == null || isRowEmpty(row)) continue;

            String empId = getCellValueAsString(row.getCell(idCol));
            if (empId == null || empId.trim().isEmpty()) continue;
            empId = empId.trim();

            String empName = nameCol != null ? getCellValueAsString(row.getCell(nameCol)) : "Employee " + empId;
            if (empName == null || empName.trim().isEmpty()) {
                empName = "Employee " + empId;
            } else {
                empName = empName.trim();
            }

            BigDecimal salary = BigDecimal.ZERO;
            if (salaryCol != null) {
                Cell salCell = row.getCell(salaryCol);
                salary = parseSalary(salCell, empId);
            }

            employeeMap.put(empId, new Employee(empId, empName, salary));
        }
    }

    private String parseAttendanceSheet(Sheet sheet, Map<String, Employee> employeeMap, List<AttendanceRecord> records) {
        int headerRowIdx = findHeaderRowIndex(sheet);
        if (headerRowIdx < 0) {
            throw new InvalidExcelException("Could not find required header row in attendance sheet '" + sheet.getSheetName() + "'.");
        }

        Row headerRow = sheet.getRow(headerRowIdx);
        Map<String, Integer> colMap = buildColumnHeaderMap(headerRow);

        Integer dateCol = findColumn(colMap, "date");
        Integer idCol = findColumn(colMap, "employee number", "employee id", "emp id", "emp no", "id");
        Integer nameCol = findColumn(colMap, "employee name", "emp name", "name");
        Integer inCol = findColumn(colMap, "in time (corrected)", "in time", "punch in", "in");
        Integer outCol = findColumn(colMap, "out time (corrected)", "out time", "punch out", "out");
        Integer salaryCol = findColumn(colMap, "monthly salary", "salary");

        if (dateCol == null) throw new InvalidExcelException("Missing required column: 'Date' in sheet '" + sheet.getSheetName() + "'.");
        if (idCol == null) throw new InvalidExcelException("Missing required column: 'Employee ID' in sheet '" + sheet.getSheetName() + "'.");
        if (inCol == null) throw new InvalidExcelException("Missing required column: 'In Time / Punch In' in sheet '" + sheet.getSheetName() + "'.");
        if (outCol == null) throw new InvalidExcelException("Missing required column: 'Out Time / Punch Out' in sheet '" + sheet.getSheetName() + "'.");

        String payPeriod = null;

        for (int r = headerRowIdx + 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (row == null || isRowEmpty(row)) continue;

            String dateStr = getCellValueAsString(row.getCell(dateCol));
            String empId = getCellValueAsString(row.getCell(idCol));
            String empName = nameCol != null ? getCellValueAsString(row.getCell(nameCol)) : null;
            String inStr = getCellValueAsString(row.getCell(inCol));
            String outStr = getCellValueAsString(row.getCell(outCol));

            if ((dateStr == null || dateStr.trim().isEmpty()) && (empId == null || empId.trim().isEmpty())) {
                continue; // trailing footer row
            }

            if (empId == null || empId.trim().isEmpty()) {
                throw new InvalidExcelException("Missing Employee ID at row " + (r + 1));
            }
            empId = empId.trim();

            if (empName == null || empName.trim().isEmpty()) {
                empName = "Employee " + empId;
            } else {
                empName = empName.trim();
            }

            // Ensure employee present in map
            if (!employeeMap.containsKey(empId)) {
                BigDecimal salary = BigDecimal.ZERO;
                if (salaryCol != null) {
                    salary = parseSalary(row.getCell(salaryCol), empId);
                }
                employeeMap.put(empId, new Employee(empId, empName, salary));
            }

            LocalDate date;
            try {
                date = DateTimeUtils.parseDate(dateStr);
            } catch (Exception e) {
                throw new InvalidExcelException("Invalid date format '" + dateStr + "' for employee " + empId + " at row " + (r + 1), e);
            }

            LocalTime punchIn;
            try {
                punchIn = DateTimeUtils.parseTime(inStr);
            } catch (Exception e) {
                throw new InvalidExcelException("Invalid punch-in time '" + inStr + "' for employee " + empId + " on " + dateStr + " at row " + (r + 1), e);
            }

            LocalTime punchOut;
            try {
                punchOut = DateTimeUtils.parseTime(outStr);
            } catch (Exception e) {
                throw new InvalidExcelException("Invalid punch-out time '" + outStr + "' for employee " + empId + " on " + dateStr + " at row " + (r + 1), e);
            }

            // Validate punchOut is not before punchIn
            if (punchOut.isBefore(punchIn)) {
                throw new InvalidExcelException("Punch-out cannot be earlier than punch-in. Employee " + empId + " on " + dateStr + " (in: " + punchIn + ", out: " + punchOut + ").");
            }

            if (payPeriod == null) {
                payPeriod = date.getMonth().name() + " " + date.getYear();
            }

            records.add(new AttendanceRecord(empId, empName, date, punchIn, punchOut));
        }

        return payPeriod;
    }

    private BigDecimal parseSalary(Cell cell, String empId) {
        if (cell == null) return BigDecimal.ZERO;
        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                double val = cell.getNumericCellValue();
                if (val < 0) {
                    throw new InvalidExcelException("Salary cannot be negative for employee " + empId);
                }
                return BigDecimal.valueOf(val);
            } else if (cell.getCellType() == CellType.STRING) {
                String str = cell.getStringCellValue().replaceAll("[^0-9.]", "");
                if (str.isEmpty()) return BigDecimal.ZERO;
                BigDecimal val = new BigDecimal(str);
                if (val.compareTo(BigDecimal.ZERO) < 0) {
                    throw new InvalidExcelException("Salary cannot be negative for employee " + empId);
                }
                return val;
            }
        } catch (NumberFormatException e) {
            throw new InvalidExcelException("Invalid numeric salary for employee " + empId);
        }
        return BigDecimal.ZERO;
    }

    private int findHeaderRowIndex(Sheet sheet) {
        for (int r = 0; r <= Math.min(10, sheet.getLastRowNum()); r++) {
            Row row = sheet.getRow(r);
            if (row == null) continue;
            Map<String, Integer> map = buildColumnHeaderMap(row);
            if (findColumn(map, "date") != null || findColumn(map, "employee number", "employee id", "emp id") != null) {
                return r;
            }
        }
        return -1;
    }

    private Map<String, Integer> buildColumnHeaderMap(Row row) {
        Map<String, Integer> map = new LinkedHashMap<>();
        for (int c = 0; c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            String val = getCellValueAsString(cell);
            if (val != null && !val.trim().isEmpty()) {
                map.put(val.trim().toLowerCase(), c);
            }
        }
        return map;
    }

    private Integer findColumn(Map<String, Integer> colMap, String... possibleHeaders) {
        for (String ph : possibleHeaders) {
            String lowerPh = ph.toLowerCase();
            for (Map.Entry<String, Integer> entry : colMap.entrySet()) {
                if (entry.getKey().contains(lowerPh)) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        CellType type = cell.getCellType();
        if (type == CellType.STRING) {
            return cell.getStringCellValue().trim();
        } else if (type == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(cell)) {
                return cell.getLocalDateTimeCellValue().toLocalDate().toString();
            }
            double val = cell.getNumericCellValue();
            if (val == (long) val) {
                return String.valueOf((long) val);
            }
            return String.valueOf(val);
        } else if (type == CellType.BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        } else if (type == CellType.FORMULA) {
            try {
                FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
                CellValue cellValue = evaluator.evaluate(cell);
                if (cellValue != null) {
                    if (cellValue.getCellType() == CellType.NUMERIC) {
                        double val = cellValue.getNumberValue();
                        if (val == (long) val) return String.valueOf((long) val);
                        return String.valueOf(val);
                    } else if (cellValue.getCellType() == CellType.STRING) {
                        return cellValue.getStringValue().trim();
                    }
                }
            } catch (Exception ignored) {
                // Fallback to raw string
            }
        }
        return "";
    }

    private boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK && !getCellValueAsString(cell).isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
