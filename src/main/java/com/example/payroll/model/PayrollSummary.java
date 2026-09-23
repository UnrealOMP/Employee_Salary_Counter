package com.example.payroll.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * Aggregated payroll response object summarizing monthly processing results across all employees.
 */
public class PayrollSummary {

    private final String payPeriod;
    private final int totalEmployees;
    private final int totalAttendanceRecords;
    private final int totalLateMarks;
    private final int totalHalfDays;
    private final int totalEarlyLeaves;
    private final double totalOvertimeHours;
    private final BigDecimal totalGrossSalary;
    private final BigDecimal totalOvertimePay;
    private final BigDecimal totalDeductions;
    private final BigDecimal totalNetSalary;
    private final List<PayrollResult> employeeResults;

    /**
     * Constructs a PayrollSummary.
     *
     * @param payPeriod              pay period description
     * @param totalEmployees         number of employees processed
     * @param totalAttendanceRecords total attendance logs parsed
     * @param totalLateMarks         sum of late marks across organization
     * @param totalHalfDays          sum of half days across organization
     * @param totalEarlyLeaves       sum of early departures across organization
     * @param totalOvertimeHours     sum of overtime hours across organization
     * @param totalGrossSalary       total monthly base salary sum
     * @param totalOvertimePay       total overtime pay sum
     * @param totalDeductions        total salary deductions sum
     * @param totalNetSalary         total net payable salary sum
     * @param employeeResults        list of individual employee payroll results
     */
    public PayrollSummary(String payPeriod, int totalEmployees, int totalAttendanceRecords,
                          int totalLateMarks, int totalHalfDays, int totalEarlyLeaves,
                          double totalOvertimeHours, BigDecimal totalGrossSalary,
                          BigDecimal totalOvertimePay, BigDecimal totalDeductions,
                          BigDecimal totalNetSalary, List<PayrollResult> employeeResults) {
        this.payPeriod = payPeriod;
        this.totalEmployees = totalEmployees;
        this.totalAttendanceRecords = totalAttendanceRecords;
        this.totalLateMarks = totalLateMarks;
        this.totalHalfDays = totalHalfDays;
        this.totalEarlyLeaves = totalEarlyLeaves;
        this.totalOvertimeHours = totalOvertimeHours;
        this.totalGrossSalary = totalGrossSalary;
        this.totalOvertimePay = totalOvertimePay;
        this.totalDeductions = totalDeductions;
        this.totalNetSalary = totalNetSalary;
        this.employeeResults = employeeResults;
    }

    public String getPayPeriod() {
        return payPeriod;
    }

    public int getTotalEmployees() {
        return totalEmployees;
    }

    public int getTotalAttendanceRecords() {
        return totalAttendanceRecords;
    }

    public int getTotalLateMarks() {
        return totalLateMarks;
    }

    public int getTotalHalfDays() {
        return totalHalfDays;
    }

    public int getTotalEarlyLeaves() {
        return totalEarlyLeaves;
    }

    public double getTotalOvertimeHours() {
        return totalOvertimeHours;
    }

    public BigDecimal getTotalGrossSalary() {
        return totalGrossSalary;
    }

    public BigDecimal getTotalOvertimePay() {
        return totalOvertimePay;
    }

    public BigDecimal getTotalDeductions() {
        return totalDeductions;
    }

    public BigDecimal getTotalNetSalary() {
        return totalNetSalary;
    }

    public List<PayrollResult> getEmployeeResults() {
        return employeeResults;
    }
}
