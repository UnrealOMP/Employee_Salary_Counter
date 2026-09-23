package com.example.payroll.calculator;

import com.example.payroll.model.AttendanceResult;
import com.example.payroll.model.Employee;
import com.example.payroll.model.PayrollResult;
import com.example.payroll.policy.DefaultPayrollPolicy;
import com.example.payroll.policy.PayrollPolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PayrollCalculatorTest {

    private PayrollCalculator payrollCalculator;
    private PayrollPolicy policy;

    @BeforeEach
    void setUp() {
        policy = new DefaultPayrollPolicy();
        OvertimeCalculator overtimeCalculator = new DefaultOvertimeCalculator(policy);
        payrollCalculator = new DefaultPayrollCalculator(policy, overtimeCalculator);
    }

    @Test
    @DisplayName("Scenario 8: 4 allowed late marks results in 0.0 late deduction")
    void testFourAllowedLateMarks() {
        Employee emp = new Employee("E001", "John", new BigDecimal("22000.00")); // Daily rate = 1000.00 (22 days)
        List<AttendanceResult> list = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            list.add(new AttendanceResult("E001", LocalDate.now().plusDays(i), LocalTime.of(10, 15), LocalTime.of(19, 15),
                    540, 9.0, true, false, false, 0, 0, "Late Mark"));
        }

        PayrollResult result = payrollCalculator.calculate(emp, list, "August 2026");

        assertEquals(4, result.getLateMarks());
        assertEquals(0, result.getExcessLateMarks());
        assertEquals(new BigDecimal("0.00"), result.getLateMarkDeduction());
        assertEquals(new BigDecimal("22000.00"), result.getFinalPayableSalary());
    }

    @Test
    @DisplayName("Scenario 9: 5th late mark results in 1 excess late mark -> 0.5 day salary deduction")
    void testFifthLateMarkDeduction() {
        Employee emp = new Employee("E001", "John", new BigDecimal("22000.00")); // Daily rate = 1000.00
        List<AttendanceResult> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add(new AttendanceResult("E001", LocalDate.now().plusDays(i), LocalTime.of(10, 15), LocalTime.of(19, 15),
                    540, 9.0, true, false, false, 0, 0, "Late Mark"));
        }

        PayrollResult result = payrollCalculator.calculate(emp, list, "August 2026");

        assertEquals(5, result.getLateMarks());
        assertEquals(1, result.getExcessLateMarks());
        // 1 excess * 0.5 * 1000 = 500.00 deduction
        assertEquals(new BigDecimal("500.00"), result.getLateMarkDeduction());
        assertEquals(new BigDecimal("21500.00"), result.getFinalPayableSalary());
    }

    @Test
    @DisplayName("Scenario 11: 2 allowed early leaves results in 0.0 early leave deduction")
    void testTwoAllowedEarlyLeaves() {
        Employee emp = new Employee("E001", "John", new BigDecimal("22000.00"));
        List<AttendanceResult> list = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            list.add(new AttendanceResult("E001", LocalDate.now().plusDays(i), LocalTime.of(9, 0), LocalTime.of(17, 30),
                    510, 8.5, false, false, true, 0, 0, "Early Leave"));
        }

        PayrollResult result = payrollCalculator.calculate(emp, list, "August 2026");

        assertEquals(2, result.getEarlyLeavingInstances());
        assertEquals(new BigDecimal("0.00"), result.getEarlyLeavingDeduction());
    }

    @Test
    @DisplayName("Scenario 12: 3rd early leave results in 1 excess early leave -> 0.5 day salary deduction")
    void testThirdEarlyLeaveDeduction() {
        Employee emp = new Employee("E001", "John", new BigDecimal("22000.00"));
        List<AttendanceResult> list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            list.add(new AttendanceResult("E001", LocalDate.now().plusDays(i), LocalTime.of(9, 0), LocalTime.of(17, 30),
                    510, 8.5, false, false, true, 0, 0, "Early Leave"));
        }

        PayrollResult result = payrollCalculator.calculate(emp, list, "August 2026");

        assertEquals(3, result.getEarlyLeavingInstances());
        assertEquals(new BigDecimal("500.00"), result.getEarlyLeavingDeduction());
        assertEquals(new BigDecimal("21500.00"), result.getFinalPayableSalary());
    }

    @Test
    @DisplayName("Scenario 17: Salary rounding behavior using HALF_UP rounding mode")
    void testSalaryRoundingHalfUp() {
        // Salary = 33000, 22 working days -> daily rate = 1500.00
        Employee emp = new Employee("E001", "John", new BigDecimal("33000.00"));
        List<AttendanceResult> list = new ArrayList<>();
        // 5 late marks -> 1 excess late mark -> 0.5 * 1500 = 750.00
        for (int i = 0; i < 5; i++) {
            list.add(new AttendanceResult("E001", LocalDate.now().plusDays(i), LocalTime.of(10, 15), LocalTime.of(19, 15),
                    540, 9.0, true, false, false, 0, 0, "Late Mark"));
        }

        PayrollResult result = payrollCalculator.calculate(emp, list, "August 2026");

        assertEquals(new BigDecimal("750.00"), result.getLateMarkDeduction());
        assertEquals(new BigDecimal("32250.00"), result.getFinalPayableSalary());
    }
}
