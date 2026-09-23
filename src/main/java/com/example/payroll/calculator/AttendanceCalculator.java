package com.example.payroll.calculator;

import com.example.payroll.model.AttendanceRecord;
import com.example.payroll.model.AttendanceResult;

/**
 * Interface defining attendance evaluation strategy for individual daily records.
 */
public interface AttendanceCalculator {

    /**
     * Calculates attendance information including late mark, half day, early departure, and overtime using punch-in and punch-out times.
     *
     * @param record employee daily attendance record
     * @return calculated attendance result
     */
    AttendanceResult calculate(AttendanceRecord record);
}
