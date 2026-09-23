package com.example.payroll.service;

import com.example.payroll.model.PayrollResult;
import com.example.payroll.pdf.SalarySlipGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service coordinating salary slip generation for individual employees.
 */
@Service
public class SalarySlipService {

    private static final Logger log = LoggerFactory.getLogger(SalarySlipService.class);

    private final PayrollService payrollService;
    private final SalarySlipGenerator salarySlipGenerator;

    /**
     * Constructs SalarySlipService with dependencies.
     *
     * @param payrollService      payroll service for fetching employee results
     * @param salarySlipGenerator PDF salary slip generator component
     */
    public SalarySlipService(PayrollService payrollService, SalarySlipGenerator salarySlipGenerator) {
        this.payrollService = payrollService;
        this.salarySlipGenerator = salarySlipGenerator;
    }

    /**
     * Generates a PDF salary slip byte array for a given employee ID.
     *
     * @param employeeId target employee ID
     * @return PDF file byte array
     */
    public byte[] generateSalarySlip(String employeeId) {
        log.info("Requesting PDF salary slip generation for employee ID: {}", employeeId);
        PayrollResult payrollResult = payrollService.getEmployeePayroll(employeeId);
        return salarySlipGenerator.generateSalarySlipPdf(payrollResult);
    }
}
