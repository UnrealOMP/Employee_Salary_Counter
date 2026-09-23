package com.example.payroll.parser;

import com.example.payroll.model.AttendanceRecord;
import com.example.payroll.model.Employee;

import java.util.List;

/**
 * Container object encapsulating employees, attendance records, and pay period parsed from an Excel workbook.
 */
public class ParsedExcelData {

    private final String payPeriod;
    private final List<Employee> employees;
    private final List<AttendanceRecord> attendanceRecords;

    /**
     * Constructs a ParsedExcelData container.
     *
     * @param payPeriod         pay period string (e.g. "August 2026")
     * @param employees         list of parsed employees
     * @param attendanceRecords list of parsed attendance records
     */
    public ParsedExcelData(String payPeriod, List<Employee> employees, List<AttendanceRecord> attendanceRecords) {
        this.payPeriod = payPeriod;
        this.employees = employees;
        this.attendanceRecords = attendanceRecords;
    }

    public String getPayPeriod() {
        return payPeriod;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public List<AttendanceRecord> getAttendanceRecords() {
        return attendanceRecords;
    }
}
