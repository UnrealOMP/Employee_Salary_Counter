package com.example.payroll;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Employee Attendance and Payroll Management System application.
 *
 * <p>This application processes monthly employee attendance Excel files, applies business logic
 * for late marks, half-days, early departures, and overtime, and calculates payable salary.
 * It also exposes REST endpoints and generates PDF salary slips.</p>
 */
@SpringBootApplication
public class PayrollApplication {

    /**
     * Starts the Spring Boot application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(PayrollApplication.class, args);
    }
}
