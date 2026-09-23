package com.example.payroll.calculator;

import com.example.payroll.model.AttendanceRecord;

import java.math.BigDecimal;

/**
 * Interface defining overtime pay calculation strategies.
 */
public interface OvertimeCalculator {

    /**
     * Calculates overtime pay earned for a single daily attendance record based on daily salary.
     *
     * @param record      employee attendance record
     * @param dailySalary computed daily salary rate
     * @return calculated overtime pay as BigDecimal
     */
    BigDecimal calculateOvertimePay(AttendanceRecord record, BigDecimal dailySalary);

    /**
     * Calculates overtime pay earned from cumulative overtime minutes in discrete 3.5-hour blocks.
     *
     * @param totalOvertimeMinutes cumulative overtime minutes
     * @param dailySalary          computed daily salary rate
     * @return calculated overtime pay as BigDecimal
     */
    BigDecimal calculateTotalOvertimePay(long totalOvertimeMinutes, BigDecimal dailySalary);
}
