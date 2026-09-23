package com.example.payroll.policy;

import java.math.BigDecimal;
import java.time.LocalTime;

/**
 * Interface defining configurable business policies for attendance evaluation, overtime calculation, and payroll deductions.
 */
public interface PayrollPolicy {

    /**
     * Gets the standard shift start time (e.g. 09:00 AM).
     *
     * @return regular start time
     */
    LocalTime getRegularStartTime();

    /**
     * Gets the standard shift end time (e.g. 06:00 PM).
     *
     * @return regular end time
     */
    LocalTime getRegularEndTime();

    /**
     * Gets the required full daily shift duration in minutes (e.g. 540 minutes = 9 hours).
     *
     * @return required shift duration in minutes
     */
    long getRequiredShiftMinutes();

    /**
     * Gets the reporting flexibility duration in minutes (e.g. 60 minutes).
     *
     * @return flexibility duration in minutes
     */
    long getFlexibilityMinutes();

    /**
     * Gets the late mark threshold time (e.g. 10:00 AM).
     *
     * @return late threshold time
     */
    LocalTime getLateThresholdTime();

    /**
     * Gets the permitted number of late marks per month without penalty (e.g. 4).
     *
     * @return allowed late marks count
     */
    int getAllowedLateMarks();

    /**
     * Gets the half-day threshold punch-in time (e.g. 11:00 AM).
     *
     * @return half-day threshold time
     */
    LocalTime getHalfDayThresholdTime();

    /**
     * Gets the maximum worked minutes that classifies a shift as half-day (e.g. 240 minutes = 4 hours).
     *
     * @return half-day max worked minutes
     */
    long getHalfDayMaxWorkedMinutes();

    /**
     * Gets the permitted number of early departures per month without penalty (e.g. 2).
     *
     * @return allowed early departures count
     */
    int getAllowedEarlyLeaves();

    /**
     * Gets the minimum extra minutes required beyond regular shift to qualify for overtime (e.g. 210 minutes = 3.5 hours).
     *
     * @return minimum overtime minutes
     */
    long getMinOvertimeMinutes();

    /**
     * Gets the overtime block size in minutes (e.g. 210 minutes = 3.5 hours).
     *
     * @return overtime block minutes
     */
    long getOvertimeBlockMinutes();

    /**
     * Gets the pay ratio per overtime block (e.g. 0.5 day's pay per 3.5-hour block).
     *
     * @return pay ratio per block
     */
    BigDecimal getOtBlockPayRatio();

    /**
     * Gets the standard applicable working days per month used to compute daily salary (e.g. 22 days).
     *
     * @return applicable working days
     */
    int getApplicableWorkingDays();

    /**
     * Gets the salary deduction ratio per excess late mark (e.g. 0.5 day's salary).
     *
     * @return late mark deduction ratio
     */
    BigDecimal getLateDeductionRatio();

    /**
     * Gets the salary deduction ratio per half day (e.g. 0.5 day's salary).
     *
     * @return half day deduction ratio
     */
    BigDecimal getHalfDayDeductionRatio();

    /**
     * Gets the salary deduction ratio per excess early departure (e.g. 0.5 day's salary).
     *
     * @return early leave deduction ratio
     */
    BigDecimal getEarlyLeavingDeductionRatio();
}
