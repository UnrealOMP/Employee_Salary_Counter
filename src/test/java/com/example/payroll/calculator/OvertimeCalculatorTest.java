package com.example.payroll.calculator;

import com.example.payroll.policy.DefaultPayrollPolicy;
import com.example.payroll.policy.PayrollPolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OvertimeCalculatorTest {

    private OvertimeCalculator overtimeCalculator;
    private final BigDecimal dailySalary = new BigDecimal("1000.00");

    @BeforeEach
    void setUp() {
        PayrollPolicy policy = new DefaultPayrollPolicy();
        overtimeCalculator = new DefaultOvertimeCalculator(policy);
    }

    @Test
    @DisplayName("Scenario 13: Exactly 3.5 hours (210 mins) overtime -> 0.5 day pay")
    void testThreeAndHalfHoursOvertime() {
        long otMinutes = 210; // 3.5 hours
        BigDecimal otPay = overtimeCalculator.calculateTotalOvertimePay(otMinutes, dailySalary);

        // 1 block * 0.5 * 1000 = 500.00
        assertEquals(new BigDecimal("500.00"), otPay);
    }

    @Test
    @DisplayName("Scenario 14: Exactly 7.0 hours (420 mins) overtime -> 1.0 day pay")
    void testSevenHoursOvertime() {
        long otMinutes = 420; // 7 hours
        BigDecimal otPay = overtimeCalculator.calculateTotalOvertimePay(otMinutes, dailySalary);

        // 2 blocks * 0.5 * 1000 = 1000.00
        assertEquals(new BigDecimal("1000.00"), otPay);
    }

    @Test
    @DisplayName("Scenario 15: Less than 3.5 hours (e.g. 200 mins) overtime -> 0.0 OT pay")
    void testLessThanThreeAndHalfHoursOvertime() {
        long otMinutes = 200; // Under 3.5 hours
        BigDecimal otPay = overtimeCalculator.calculateTotalOvertimePay(otMinutes, dailySalary);

        assertEquals(new BigDecimal("0.00"), otPay);
    }

    @Test
    @DisplayName("Scenario 16: Multiple overtime blocks (10.5 hours = 630 mins) -> 1.5 day pay")
    void testTenAndHalfHoursOvertime() {
        long otMinutes = 630; // 10.5 hours (3 blocks)
        BigDecimal otPay = overtimeCalculator.calculateTotalOvertimePay(otMinutes, dailySalary);

        // 3 blocks * 0.5 * 1000 = 1500.00
        assertEquals(new BigDecimal("1500.00"), otPay);
    }
}
