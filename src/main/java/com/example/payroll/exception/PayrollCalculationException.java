package com.example.payroll.exception;

/**
 * Exception thrown when business calculation logic encounters unexpected errors.
 */
public class PayrollCalculationException extends RuntimeException {

    /**
     * Constructs a new PayrollCalculationException.
     *
     * @param message detail error message
     */
    public PayrollCalculationException(String message) {
        super(message);
    }

    /**
     * Constructs a new PayrollCalculationException with cause.
     *
     * @param message detail error message
     * @param cause   underlying exception cause
     */
    public PayrollCalculationException(String message, Throwable cause) {
        super(message, cause);
    }
}
