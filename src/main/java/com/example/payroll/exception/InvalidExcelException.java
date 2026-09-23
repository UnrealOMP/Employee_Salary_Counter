package com.example.payroll.exception;

/**
 * Exception thrown when uploaded Excel file is missing, unreadable, invalid, or violates required format.
 */
public class InvalidExcelException extends RuntimeException {

    /**
     * Constructs a new InvalidExcelException.
     *
     * @param message error description
     */
    public InvalidExcelException(String message) {
        super(message);
    }

    /**
     * Constructs a new InvalidExcelException with cause.
     *
     * @param message error description
     * @param cause   underlying exception cause
     */
    public InvalidExcelException(String message, Throwable cause) {
        super(message, cause);
    }
}
