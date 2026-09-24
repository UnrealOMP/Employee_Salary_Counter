package com.example.payroll.service;

import com.example.payroll.calculator.PayrollCalculator;
import com.example.payroll.exception.EmployeeNotFoundException;
import com.example.payroll.exception.InvalidExcelException;
import com.example.payroll.model.*;
import com.example.payroll.parser.ParsedExcelData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Service orchestrating end-to-end payroll processing, employee lookups, and in-memory state management.
 */
@Service
public class PayrollService {

    private static final Logger log = LoggerFactory.getLogger(PayrollService.class);

    private final ExcelImportService excelImportService;
    private final AttendanceService attendanceService;
    private final PayrollCalculator payrollCalculator;

    // In-memory thread-safe state holding current active payroll processing results
    private final AtomicReference<PayrollSummary> currentSummary = new AtomicReference<>();
    private final Map<String, PayrollResult> employeePayrollMap = new ConcurrentHashMap<>();
    private final Map<String, List<AttendanceResult>> employeeAttendanceMap = new ConcurrentHashMap<>();

    /**
     * Constructs PayrollService with constructor dependency injection.
     *
     * @param excelImportService excel import service
     * @param attendanceService  attendance calculation service
     * @param payrollCalculator  payroll calculation engine
     */
    public PayrollService(ExcelImportService excelImportService,
                          AttendanceService attendanceService,
                          PayrollCalculator payrollCalculator) {
        this.excelImportService = excelImportService;
        this.attendanceService = attendanceService;
        this.payrollCalculator = payrollCalculator;
    }

    /**
     * Processes an uploaded Excel attendance file and calculates monthly payroll.
     *
     * @param file uploaded Excel file
     * @return summary of calculated payroll metrics
     */
    public PayrollSummary processPayrollUpload(MultipartFile file) {
        log.info("Starting monthly payroll upload processing...");

        // 1. Import and parse Excel data
        ParsedExcelData parsedData = excelImportService.importExcel(file);
        List<Employee> employees = parsedData.getEmployees();
        List<AttendanceRecord> attendanceRecords = parsedData.getAttendanceRecords();
        String payPeriod = parsedData.getPayPeriod();

        // 2. Compute attendance metrics
        Map<String, List<AttendanceResult>> attendanceResultsMap = attendanceService.processAttendanceRecords(attendanceRecords);

        // 3. Compute payroll metrics per employee
        List<PayrollResult> payrollResults = new ArrayList<>();
        Map<String, PayrollResult> newPayrollMap = new LinkedHashMap<>();

        int totalLateMarks = 0;
        int totalHalfDays = 0;
        int totalEarlyLeaves = 0;
        double totalOtHours = 0;
        BigDecimal totalGrossSalary = BigDecimal.ZERO;
        BigDecimal totalOtPay = BigDecimal.ZERO;
        BigDecimal totalDeductions = BigDecimal.ZERO;
        BigDecimal totalNetSalary = BigDecimal.ZERO;

        for (Employee emp : employees) {
            List<AttendanceResult> attResults = attendanceResultsMap.getOrDefault(emp.getEmployeeId(), Collections.emptyList());
            PayrollResult pResult = payrollCalculator.calculate(emp, attResults, payPeriod);

            payrollResults.add(pResult);
            newPayrollMap.put(emp.getEmployeeId(), pResult);

            totalLateMarks += pResult.getLateMarks();
            totalHalfDays += pResult.getHalfDays();
            totalEarlyLeaves += pResult.getEarlyLeavingInstances();
            totalOtHours += pResult.getOvertimeHours();
            totalGrossSalary = totalGrossSalary.add(pResult.getMonthlySalary());
            totalOtPay = totalOtPay.add(pResult.getOvertimePay());
            totalDeductions = totalDeductions.add(pResult.getTotalDeductions());
            totalNetSalary = totalNetSalary.add(pResult.getFinalPayableSalary());
        }

        totalOtHours = Math.round(totalOtHours * 100.0) / 100.0;

        PayrollSummary summary = new PayrollSummary(
                payPeriod,
                employees.size(),
                attendanceRecords.size(),
                totalLateMarks,
                totalHalfDays,
                totalEarlyLeaves,
                totalOtHours,
                totalGrossSalary,
                totalOtPay,
                totalDeductions,
                totalNetSalary,
                payrollResults
        );

        // 4. Update active in-memory state
        employeePayrollMap.clear();
        employeePayrollMap.putAll(newPayrollMap);

        employeeAttendanceMap.clear();
        employeeAttendanceMap.putAll(attendanceResultsMap);

        currentSummary.set(summary);

        log.info("Payroll successfully processed. Employees: {}, Records: {}, Net Salary: ₹{}",
                employees.size(), attendanceRecords.size(), totalNetSalary);

        return summary;
    }

    /**
     * Returns the active calculated PayrollSummary or an empty summary if no payroll has been processed yet.
     *
     * @return current or empty payroll summary
     */
    public PayrollSummary getPayrollSummary() {
        PayrollSummary summary = currentSummary.get();
        if (summary == null) {
            return PayrollSummary.empty();
        }
        return summary;
    }

    /**
     * Resets all in-memory payroll state including current summary and employee maps.
     *
     * @return empty PayrollSummary object
     */
    public PayrollSummary resetPayroll() {
        log.info("Resetting all active in-memory payroll state...");
        currentSummary.set(null);
        employeePayrollMap.clear();
        employeeAttendanceMap.clear();
        return PayrollSummary.empty();
    }

    /**
     * Gets individual payroll result for an employee.
     *
     * @param employeeId target employee ID
     * @return PayrollResult
     * @throws EmployeeNotFoundException if employee ID does not exist
     */
    public PayrollResult getEmployeePayroll(String employeeId) {
        PayrollResult result = employeePayrollMap.get(employeeId);
        if (result == null) {
            throw new EmployeeNotFoundException("Employee with ID '" + employeeId + "' was not found in active payroll data.");
        }
        return result;
    }

    /**
     * Gets daily attendance results for an employee.
     *
     * @param employeeId target employee ID
     * @return list of AttendanceResult objects
     * @throws EmployeeNotFoundException if employee ID does not exist
     */
    public List<AttendanceResult> getEmployeeAttendance(String employeeId) {
        if (!employeePayrollMap.containsKey(employeeId)) {
            throw new EmployeeNotFoundException("Employee with ID '" + employeeId + "' was not found in active payroll data.");
        }
        return employeeAttendanceMap.getOrDefault(employeeId, Collections.emptyList());
    }
}
