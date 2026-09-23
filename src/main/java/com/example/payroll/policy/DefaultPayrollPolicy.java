package com.example.payroll.policy;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalTime;

/**
 * Default implementation of PayrollPolicy configuring assignment business rules.
 */
@Component
public class DefaultPayrollPolicy implements PayrollPolicy {

    private final LocalTime regularStartTime = LocalTime.of(9, 0);
    private final LocalTime regularEndTime = LocalTime.of(18, 0);
    private final long requiredShiftMinutes = 540; // 9 hours
    private final long flexibilityMinutes = 60; // 1 hour flexibility (9:00 - 10:00 AM)
    private final LocalTime lateThresholdTime = LocalTime.of(10, 0);
    private final int allowedLateMarks = 4;

    private final LocalTime halfDayThresholdTime = LocalTime.of(11, 0);
    private final long halfDayMaxWorkedMinutes = 240; // 4 hours

    private final int allowedEarlyLeaves = 2;

    private final long minOvertimeMinutes = 210; // 3.5 hours
    private final long overtimeBlockMinutes = 210; // 3.5 hours
    private final BigDecimal otBlockPayRatio = new BigDecimal("0.5");

    private final int applicableWorkingDays = 22; // Default standard monthly working days

    private final BigDecimal lateDeductionRatio = new BigDecimal("0.5");
    private final BigDecimal halfDayDeductionRatio = new BigDecimal("0.5");
    private final BigDecimal earlyLeavingDeductionRatio = new BigDecimal("0.5");

    @Override
    public LocalTime getRegularStartTime() {
        return regularStartTime;
    }

    @Override
    public LocalTime getRegularEndTime() {
        return regularEndTime;
    }

    @Override
    public long getRequiredShiftMinutes() {
        return requiredShiftMinutes;
    }

    @Override
    public long getFlexibilityMinutes() {
        return flexibilityMinutes;
    }

    @Override
    public LocalTime getLateThresholdTime() {
        return lateThresholdTime;
    }

    @Override
    public int getAllowedLateMarks() {
        return allowedLateMarks;
    }

    @Override
    public LocalTime getHalfDayThresholdTime() {
        return halfDayThresholdTime;
    }

    @Override
    public long getHalfDayMaxWorkedMinutes() {
        return halfDayMaxWorkedMinutes;
    }

    @Override
    public int getAllowedEarlyLeaves() {
        return allowedEarlyLeaves;
    }

    @Override
    public long getMinOvertimeMinutes() {
        return minOvertimeMinutes;
    }

    @Override
    public long getOvertimeBlockMinutes() {
        return overtimeBlockMinutes;
    }

    @Override
    public BigDecimal getOtBlockPayRatio() {
        return otBlockPayRatio;
    }

    @Override
    public int getApplicableWorkingDays() {
        return applicableWorkingDays;
    }

    @Override
    public BigDecimal getLateDeductionRatio() {
        return lateDeductionRatio;
    }

    @Override
    public BigDecimal getHalfDayDeductionRatio() {
        return halfDayDeductionRatio;
    }

    @Override
    public BigDecimal getEarlyLeavingDeductionRatio() {
        return earlyLeavingDeductionRatio;
    }
}
