package com.example.payroll.service;

import com.example.payroll.calculator.PayrollCalculator;
import com.example.payroll.model.*;
import com.example.payroll.parser.ParsedExcelData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PayrollServiceTest {

    @Mock
    private ExcelImportService excelImportService;

    @Mock
    private AttendanceService attendanceService;

    @Mock
    private PayrollCalculator payrollCalculator;

    private PayrollService payrollService;

    @BeforeEach
    void setUp() {
        payrollService = new PayrollService(excelImportService, attendanceService, payrollCalculator);
    }

    @Test
    @DisplayName("Verify PayrollService orchestration with mocked dependencies")
    void testProcessPayrollUploadOrchestration() {
        MockMultipartFile file = new MockMultipartFile("file", "test.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new byte[]{1, 2, 3});

        Employee emp = new Employee("E001", "John Doe", new BigDecimal("30000.00"));
        AttendanceRecord record = new AttendanceRecord("E001", "John Doe", LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(18, 0));
        ParsedExcelData parsedData = new ParsedExcelData("August 2026", Collections.singletonList(emp), Collections.singletonList(record));

        AttendanceResult attResult = new AttendanceResult("E001", LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(18, 0), 540, 9.0, false, false, false, 0, 0, "Regular");

        PayrollResult mockPayrollResult = new PayrollResult("E001", "John Doe", "August 2026", new BigDecimal("30000.00"), 22, 1, 0, 4, 0, 0, 0, 2, 0, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("30000.00"));

        when(excelImportService.importExcel(file)).thenReturn(parsedData);
        when(attendanceService.processAttendanceRecords(any())).thenReturn(new HashMap<String, java.util.List<AttendanceResult>>() {{
            put("E001", Collections.singletonList(attResult));
        }});
        when(payrollCalculator.calculate(eq(emp), any(), eq("August 2026"))).thenReturn(mockPayrollResult);

        PayrollSummary summary = payrollService.processPayrollUpload(file);

        assertNotNull(summary);
        assertEquals(1, summary.getTotalEmployees());
        assertEquals("August 2026", summary.getPayPeriod());
        assertEquals(new BigDecimal("30000.00"), summary.getTotalNetSalary());
    }

    @Test
    @DisplayName("Verify getPayrollSummary returns empty summary when no file has been uploaded")
    void testGetPayrollSummaryWhenNoUpload() {
        PayrollSummary summary = payrollService.getPayrollSummary();
        assertNotNull(summary);
        assertEquals(0, summary.getTotalEmployees());
        assertEquals(0, summary.getTotalAttendanceRecords());
        assertNotNull(summary.getEmployeeResults());
        assertEquals(0, summary.getEmployeeResults().size());
    }
}
