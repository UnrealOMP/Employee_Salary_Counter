package com.example.payroll.controller;

import com.example.payroll.model.AttendanceResult;
import com.example.payroll.model.PayrollResult;
import com.example.payroll.model.PayrollSummary;
import com.example.payroll.service.PayrollService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * REST Controller handling monthly Excel upload and payroll metrics retrieval endpoints.
 */
@RestController
@RequestMapping("/api/v1/payroll")
@CrossOrigin(origins = "*")
public class PayrollController {

    private static final Logger log = LoggerFactory.getLogger(PayrollController.class);

    private final PayrollService payrollService;

    /**
     * Constructs PayrollController with constructor injection.
     *
     * @param payrollService payroll orchestration service
     */
    public PayrollController(PayrollService payrollService) {
        this.payrollService = payrollService;
    }

    /**
     * Uploads and processes a monthly Excel attendance file.
     *
     * @param file uploaded Excel file
     * @return ResponseEntity containing calculated PayrollSummary
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PayrollSummary> uploadPayrollFile(@RequestParam("file") MultipartFile file) {
        log.info("HTTP POST /api/v1/payroll/upload received file: '{}'", file.getOriginalFilename());
        PayrollSummary summary = payrollService.processPayrollUpload(file);
        return ResponseEntity.ok(summary);
    }

    /**
     * Retrieves the current active PayrollSummary.
     *
     * @return ResponseEntity containing PayrollSummary
     */
    @GetMapping
    public ResponseEntity<PayrollSummary> getCurrentPayroll() {
        log.info("HTTP GET /api/v1/payroll requested.");
        PayrollSummary summary = payrollService.getPayrollSummary();
        return ResponseEntity.ok(summary);
    }

    /**
     * Retrieves payroll calculation breakdown for a single employee.
     *
     * @param employeeId target employee ID
     * @return ResponseEntity containing PayrollResult
     */
    @GetMapping("/{employeeId}")
    public ResponseEntity<PayrollResult> getEmployeePayroll(@PathVariable("employeeId") String employeeId) {
        log.info("HTTP GET /api/v1/payroll/{} requested.", employeeId);
        PayrollResult result = payrollService.getEmployeePayroll(employeeId);
        return ResponseEntity.ok(result);
    }

    /**
     * Retrieves daily attendance calculations for a single employee.
     *
     * @param employeeId target employee ID
     * @return ResponseEntity containing list of AttendanceResult
     */
    @GetMapping("/{employeeId}/attendance")
    public ResponseEntity<List<AttendanceResult>> getEmployeeAttendance(@PathVariable("employeeId") String employeeId) {
        log.info("HTTP GET /api/v1/payroll/{}/attendance requested.", employeeId);
        List<AttendanceResult> attendanceList = payrollService.getEmployeeAttendance(employeeId);
        return ResponseEntity.ok(attendanceList);
    }
}
