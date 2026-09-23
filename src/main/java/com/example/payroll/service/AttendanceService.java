package com.example.payroll.service;

import com.example.payroll.calculator.AttendanceCalculator;
import com.example.payroll.model.AttendanceRecord;
import com.example.payroll.model.AttendanceResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service coordinating attendance record processing and calculator execution.
 */
@Service
public class AttendanceService {

    private static final Logger log = LoggerFactory.getLogger(AttendanceService.class);

    private final AttendanceCalculator attendanceCalculator;

    /**
     * Constructs AttendanceService with AttendanceCalculator dependency.
     *
     * @param attendanceCalculator calculator for daily attendance
     */
    public AttendanceService(AttendanceCalculator attendanceCalculator) {
        this.attendanceCalculator = attendanceCalculator;
    }

    /**
     * Processes raw attendance records and computes daily attendance results grouped by employee ID.
     *
     * @param records list of raw attendance records
     * @return map of employeeId -> list of calculated AttendanceResult
     */
    public Map<String, List<AttendanceResult>> processAttendanceRecords(List<AttendanceRecord> records) {
        if (records == null || records.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, List<AttendanceResult>> resultMap = new LinkedHashMap<>();

        for (AttendanceRecord record : records) {
            AttendanceResult result = attendanceCalculator.calculate(record);
            resultMap.computeIfAbsent(record.getEmployeeId(), k -> new ArrayList<>()).add(result);
        }

        log.info("Processed attendance calculations across {} distinct employees.", resultMap.size());
        return resultMap;
    }
}
