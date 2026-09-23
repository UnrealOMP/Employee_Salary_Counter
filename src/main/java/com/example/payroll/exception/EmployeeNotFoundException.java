package com.example.payroll.exception;

/**
 * Exception thrown when a requested employee ID is not present in processed payroll data.
 */
public class EmployeeNotFoundException extends RuntimeException {

    /**
     * Constructs a new EmployeeNotFoundException.
     *
     * @param message detail error message
     */
    public EmployeeNotFoundException(String message) {
        super(message);
    }
}
