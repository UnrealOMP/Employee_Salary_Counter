package com.example.payroll.calculator;

import com.example.payroll.model.AttendanceRecord;
import com.example.payroll.model.AttendanceResult;
import com.example.payroll.policy.DefaultPayrollPolicy;
import com.example.payroll.policy.PayrollPolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class AttendanceCalculatorTest {

    private AttendanceCalculator calculator;
    private PayrollPolicy policy;

    @BeforeEach
    void setUp() {
        policy = new DefaultPayrollPolicy();
        calculator = new DefaultAttendanceCalculator(policy);
    }

    @Test
    @DisplayName("Scenario 1: 9:00 AM punch-in to 6:00 PM punch-out (Regular 9h shift)")
    void testNineAmPunchIn() {
        AttendanceRecord record = new AttendanceRecord("E001", "John", LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(18, 0));
        AttendanceResult result = calculator.calculate(record);

        assertFalse(result.isLateMark(), "9:00 AM punch in should not be late");
        assertFalse(result.isHalfDay(), "9 hours worked should not be half day");
        assertFalse(result.isEarlyLeaving(), "Leaving at 6:00 PM for 9:00 AM start is not early");
        assertEquals(540, result.getTotalWorkedMinutes());
        assertEquals(0, result.getOvertimeMinutes());
    }

    @Test
    @DisplayName("Scenario 2: 9:30 AM punch-in to 6:30 PM punch-out (Flexibility window used, completed 9h)")
    void testNineThirtyAmPunchIn() {
        AttendanceRecord record = new AttendanceRecord("E001", "John", LocalDate.now(), LocalTime.of(9, 30), LocalTime.of(18, 30));
        AttendanceResult result = calculator.calculate(record);

        assertFalse(result.isLateMark(), "9:30 AM punch in with completed 9h shift should not be late");
        assertFalse(result.isHalfDay());
        assertFalse(result.isEarlyLeaving(), "Leaving at 6:30 PM completes 9h shift");
        assertEquals(540, result.getTotalWorkedMinutes());
    }

    @Test
    @DisplayName("Scenario 3: 10:00 AM punch-in to 7:00 PM punch-out (Boundary of flexibility window)")
    void testTenAmPunchIn() {
        AttendanceRecord record = new AttendanceRecord("E001", "John", LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(19, 0));
        AttendanceResult result = calculator.calculate(record);

        assertFalse(result.isLateMark(), "10:00 AM exactly is the flexibility boundary, not late");
        assertFalse(result.isHalfDay());
        assertFalse(result.isEarlyLeaving());
        assertEquals(540, result.getTotalWorkedMinutes());
    }

    @Test
    @DisplayName("Scenario 4: 10:01 AM punch-in (Exceeds flexibility window -> Late Mark triggered)")
    void testTenZeroOneAmPunchIn() {
        AttendanceRecord record = new AttendanceRecord("E001", "John", LocalDate.now(), LocalTime.of(10, 1), LocalTime.of(19, 1));
        AttendanceResult result = calculator.calculate(record);

        assertTrue(result.isLateMark(), "10:01 AM punch-in must trigger a Late Mark");
        assertFalse(result.isHalfDay());
    }

    @Test
    @DisplayName("Scenario 5: 11:01 AM punch-in (Exceeds half-day threshold -> Half Day triggered)")
    void testElevenZeroOneAmPunchIn() {
        AttendanceRecord record = new AttendanceRecord("E001", "John", LocalDate.now(), LocalTime.of(11, 1), LocalTime.of(19, 1));
        AttendanceResult result = calculator.calculate(record);

        assertTrue(result.isHalfDay(), "11:01 AM punch-in must trigger a Half Day");
    }

    @Test
    @DisplayName("Scenario 6: Exactly 4 hours worked (Half Day triggered)")
    void testExactlyFourHoursWorked() {
        AttendanceRecord record = new AttendanceRecord("E001", "John", LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(13, 0));
        AttendanceResult result = calculator.calculate(record);

        assertTrue(result.isHalfDay(), "Working exactly 4 hours (240 mins) must trigger Half Day");
    }

    @Test
    @DisplayName("Scenario 7: Less than 4 hours worked (Half Day triggered)")
    void testLessThanFourHoursWorked() {
        AttendanceRecord record = new AttendanceRecord("E001", "John", LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(12, 30));
        AttendanceResult result = calculator.calculate(record);

        assertTrue(result.isHalfDay(), "Working less than 4 hours must trigger Half Day");
    }

    @Test
    @DisplayName("Scenario 10: Early leaving detection (9:30 AM punch-in, leaving at 6:00 PM before completing 9h)")
    void testEarlyLeaving() {
        AttendanceRecord record = new AttendanceRecord("E001", "John", LocalDate.now(), LocalTime.of(9, 30), LocalTime.of(18, 0));
        AttendanceResult result = calculator.calculate(record);

        assertTrue(result.isEarlyLeaving(), "Leaving at 6:00 PM after 9:30 AM start is early leaving (8.5h worked)");
    }
}
