package com.example.payroll.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Global REST exception handler capturing domain and system exceptions and mapping them to structured HTTP error responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles invalid Excel file exceptions.
     *
     * @param ex      exception instance
     * @param request HTTP request
     * @return 400 Bad Request response with structured error JSON
     */
    @ExceptionHandler(InvalidExcelException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidExcelException(InvalidExcelException ex, HttpServletRequest request) {
        log.warn("Invalid Excel upload: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid Excel File", ex.getMessage(), request.getRequestURI());
    }

    /**
     * Handles employee not found exceptions.
     *
     * @param ex      exception instance
     * @param request HTTP request
     * @return 404 Not Found response with structured error JSON
     */
    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEmployeeNotFoundException(EmployeeNotFoundException ex, HttpServletRequest request) {
        log.warn("Employee not found: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Employee Not Found", ex.getMessage(), request.getRequestURI());
    }

    /**
     * Handles payroll calculation errors.
     *
     * @param ex      exception instance
     * @param request HTTP request
     * @return 422 Unprocessable Entity response with structured error JSON
     */
    @ExceptionHandler(PayrollCalculationException.class)
    public ResponseEntity<Map<String, Object>> handlePayrollCalculationException(PayrollCalculationException ex, HttpServletRequest request) {
        log.error("Payroll calculation error: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, "Calculation Error", ex.getMessage(), request.getRequestURI());
    }

    /**
     * Handles file upload size limit exceeded exception.
     *
     * @param ex      exception instance
     * @param request HTTP request
     * @return 400 Bad Request response
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> handleMaxSizeException(MaxUploadSizeExceededException ex, HttpServletRequest request) {
        log.warn("Upload size limit exceeded: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "File Too Large", "Uploaded file exceeds maximum permitted size limit.", request.getRequestURI());
    }

    /**
     * Handles unhandled generic internal server exceptions.
     *
     * @param ex      exception instance
     * @param request HTTP request
     * @return 500 Internal Server Error response without exposing stack trace
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unhandled internal server error on path {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "An unexpected error occurred. Please check system logs.", request.getRequestURI());
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String error, String message, String path) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        body.put("path", path);
        return new ResponseEntity<>(body, status);
    }
}
