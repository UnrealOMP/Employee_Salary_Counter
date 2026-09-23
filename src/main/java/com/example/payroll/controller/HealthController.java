package com.example.payroll.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Health check REST controller providing system status metrics.
 */
@RestController
@RequestMapping("/api/v1/health")
@CrossOrigin(origins = "*")
public class HealthController {

    /**
     * Returns system health status.
     *
     * @return 200 OK status JSON
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getHealth() {
        Map<String, Object> status = new LinkedHashMap<>();
        status.put("status", "UP");
        status.put("service", "Employee Attendance & Payroll Management System");
        status.put("timestamp", Instant.now().toString());
        return ResponseEntity.ok(status);
    }
}
