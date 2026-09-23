package com.example.payroll.calculator;

import com.example.payroll.model.AttendanceResult;
import com.example.payroll.model.Employee;
import com.example.payroll.model.PayrollResult;

import java.util.List;

/**
 * Interface defining payroll calculation strategy for an individual employee based on attendance results.
 */
public interface PayrollCalculator {

    /**
     * Calculates payroll result for an employee incorporating attendance penalties, overtime additions, and deductions.
     *
     * @param employee          employee master details
     * @param attendanceResults daily calculated attendance records for the pay period
     * @param payPeriod         pay period description (e.g. "August 2026")
     * @return calculated comprehensive payroll result
     */
    PayrollResult calculate(Employee employee, List<AttendanceResult> attendanceResults, String payPeriod);
}
