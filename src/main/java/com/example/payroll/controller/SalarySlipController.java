package com.example.payroll.controller;

import com.example.payroll.model.PayrollResult;
import com.example.payroll.service.PayrollService;
import com.example.payroll.service.SalarySlipService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller handling PDF salary slip generation and download requests.
 */
@RestController
@RequestMapping("/api/v1/payroll")
@CrossOrigin(origins = "*")
public class SalarySlipController {

    private static final Logger log = LoggerFactory.getLogger(SalarySlipController.class);

    private final SalarySlipService salarySlipService;
    private final PayrollService payrollService;

    /**
     * Constructs SalarySlipController with service dependencies.
     *
     * @param salarySlipService salary slip service
     * @param payrollService    payroll service
     */
    public SalarySlipController(SalarySlipService salarySlipService, PayrollService payrollService) {
        this.salarySlipService = salarySlipService;
        this.payrollService = payrollService;
    }

    /**
     * Generates and downloads the PDF salary slip for a given employee.
     *
     * @param employeeId target employee ID
     * @return ResponseEntity with application/pdf byte stream
     */
    @GetMapping("/{employeeId}/salary-slip")
    public ResponseEntity<byte[]> downloadSalarySlip(@PathVariable("employeeId") String employeeId) {
        log.info("HTTP GET /api/v1/payroll/{}/salary-slip requested.", employeeId);

        PayrollResult emp = payrollService.getEmployeePayroll(employeeId);
        byte[] pdfBytes = salarySlipService.generateSalarySlip(employeeId);

        String safeName = emp.getEmployeeName().replaceAll("[^a-zA-Z0-9]", "_");
        String safePeriod = emp.getPayPeriod().replaceAll("[^a-zA-Z0-9]", "_");
        String filename = safeName + "_" + emp.getEmployeeId() + "_" + safePeriod + "_SalarySlip.pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdfBytes.length)
                .body(pdfBytes);
    }
}
