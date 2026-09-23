package com.example.payroll.model;

import java.math.BigDecimal;

/**
 * Domain model representing the comprehensive payroll calculation result for a single employee.
 */
public class PayrollResult {

    private final String employeeId;
    private final String employeeName;
    private final String payPeriod;
    private final BigDecimal monthlySalary;
    private final int applicableWorkingDays;
    private final int daysWithAttendance;
    private final int lateMarks;
    private final int allowedLateMarks;
    private final int excessLateMarks;
    private final int halfDays;
    private final int earlyLeavingInstances;
    private final int allowedEarlyLeavingInstances;
    private final double overtimeHours;
    private final BigDecimal overtimePay;
    private final BigDecimal lateMarkDeduction;
    private final BigDecimal halfDayDeduction;
    private final BigDecimal earlyLeavingDeduction;
    private final BigDecimal totalDeductions;
    private final BigDecimal finalPayableSalary;

    /**
     * Constructs a PayrollResult.
     *
     * @param employeeId                   employee ID
     * @param employeeName                 employee name
     * @param payPeriod                    pay period string (e.g. "August 2026")
     * @param monthlySalary                base monthly salary
     * @param applicableWorkingDays        total standard working days
     * @param daysWithAttendance           number of days attendance recorded
     * @param lateMarks                    total late marks
     * @param allowedLateMarks             permitted late marks
     * @param excessLateMarks              late marks exceeding allowed threshold
     * @param halfDays                     total half days
     * @param earlyLeavingInstances        total early departure instances
     * @param allowedEarlyLeavingInstances permitted early departures
     * @param overtimeHours                total overtime hours
     * @param overtimePay                  calculated overtime payment
     * @param lateMarkDeduction            salary deduction for excess late marks
     * @param halfDayDeduction             salary deduction for half days
     * @param earlyLeavingDeduction        salary deduction for excess early departures
     * @param totalDeductions              sum of all deductions
     * @param finalPayableSalary           net final payable salary
     */
    public PayrollResult(String employeeId, String employeeName, String payPeriod, BigDecimal monthlySalary,
                         int applicableWorkingDays, int daysWithAttendance, int lateMarks, int allowedLateMarks,
                         int excessLateMarks, int halfDays, int earlyLeavingInstances, int allowedEarlyLeavingInstances,
                         double overtimeHours, BigDecimal overtimePay, BigDecimal lateMarkDeduction,
                         BigDecimal halfDayDeduction, BigDecimal earlyLeavingDeduction, BigDecimal totalDeductions,
                         BigDecimal finalPayableSalary) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.payPeriod = payPeriod;
        this.monthlySalary = monthlySalary;
        this.applicableWorkingDays = applicableWorkingDays;
        this.daysWithAttendance = daysWithAttendance;
        this.lateMarks = lateMarks;
        this.allowedLateMarks = allowedLateMarks;
        this.excessLateMarks = excessLateMarks;
        this.halfDays = halfDays;
        this.earlyLeavingInstances = earlyLeavingInstances;
        this.allowedEarlyLeavingInstances = allowedEarlyLeavingInstances;
        this.overtimeHours = overtimeHours;
        this.overtimePay = overtimePay;
        this.lateMarkDeduction = lateMarkDeduction;
        this.halfDayDeduction = halfDayDeduction;
        this.earlyLeavingDeduction = earlyLeavingDeduction;
        this.totalDeductions = totalDeductions;
        this.finalPayableSalary = finalPayableSalary;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public String getPayPeriod() {
        return payPeriod;
    }

    public BigDecimal getMonthlySalary() {
        return monthlySalary;
    }

    public int getApplicableWorkingDays() {
        return applicableWorkingDays;
    }

    public int getDaysWithAttendance() {
        return daysWithAttendance;
    }

    public int getLateMarks() {
        return lateMarks;
    }

    public int getAllowedLateMarks() {
        return allowedLateMarks;
    }

    public int getExcessLateMarks() {
        return excessLateMarks;
    }

    public int getHalfDays() {
        return halfDays;
    }

    public int getEarlyLeavingInstances() {
        return earlyLeavingInstances;
    }

    public int getAllowedEarlyLeavingInstances() {
        return allowedEarlyLeavingInstances;
    }

    public double getOvertimeHours() {
        return overtimeHours;
    }

    public BigDecimal getOvertimePay() {
        return overtimePay;
    }

    public BigDecimal getLateMarkDeduction() {
        return lateMarkDeduction;
    }

    public BigDecimal getHalfDayDeduction() {
        return halfDayDeduction;
    }

    public BigDecimal getEarlyLeavingDeduction() {
        return earlyLeavingDeduction;
    }

    public BigDecimal getTotalDeductions() {
        return totalDeductions;
    }

    public BigDecimal getFinalPayableSalary() {
        return finalPayableSalary;
    }
}
