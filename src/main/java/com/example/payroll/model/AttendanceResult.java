package com.example.payroll.model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Domain object representing the calculated result of an employee's daily attendance.
 */
public class AttendanceResult {

    private final String employeeId;
    private final LocalDate date;
    private final LocalTime punchIn;
    private final LocalTime punchOut;
    private final long totalWorkedMinutes;
    private final double totalWorkedHours;
    private final boolean lateMark;
    private final boolean halfDay;
    private final boolean earlyLeaving;
    private final long overtimeMinutes;
    private final double overtimeHours;
    private final String notes;

    /**
     * Constructs an AttendanceResult.
     *
     * @param employeeId         employee ID
     * @param date               attendance date
     * @param punchIn            punch-in time
     * @param punchOut           punch-out time
     * @param totalWorkedMinutes total duration worked in minutes
     * @param totalWorkedHours   total duration worked in hours
     * @param lateMark           true if late mark assigned
     * @param halfDay            true if half-day assigned
     * @param earlyLeaving       true if early departure detected
     * @param overtimeMinutes    overtime duration in minutes
     * @param overtimeHours      overtime duration in hours
     * @param notes              descriptive notes or status message
     */
    public AttendanceResult(String employeeId, LocalDate date, LocalTime punchIn, LocalTime punchOut,
                            long totalWorkedMinutes, double totalWorkedHours, boolean lateMark,
                            boolean halfDay, boolean earlyLeaving, long overtimeMinutes,
                            double overtimeHours, String notes) {
        this.employeeId = employeeId;
        this.date = date;
        this.punchIn = punchIn;
        this.punchOut = punchOut;
        this.totalWorkedMinutes = totalWorkedMinutes;
        this.totalWorkedHours = totalWorkedHours;
        this.lateMark = lateMark;
        this.halfDay = halfDay;
        this.earlyLeaving = earlyLeaving;
        this.overtimeMinutes = overtimeMinutes;
        this.overtimeHours = overtimeHours;
        this.notes = notes;
    }

    /**
     * Gets the employee ID.
     *
     * @return employee ID
     */
    public String getEmployeeId() {
        return employeeId;
    }

    /**
     * Gets the date.
     *
     * @return date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Gets the punch-in time.
     *
     * @return punch-in time
     */
    public LocalTime getPunchIn() {
        return punchIn;
    }

    /**
     * Gets the punch-out time.
     *
     * @return punch-out time
     */
    public LocalTime getPunchOut() {
        return punchOut;
    }

    /**
     * Gets total worked minutes.
     *
     * @return worked minutes
     */
    public long getTotalWorkedMinutes() {
        return totalWorkedMinutes;
    }

    /**
     * Gets total worked hours.
     *
     * @return worked hours
     */
    public double getTotalWorkedHours() {
        return totalWorkedHours;
    }

    /**
     * Checks if late mark applied.
     *
     * @return true if late
     */
    public boolean isLateMark() {
        return lateMark;
    }

    /**
     * Checks if half-day applied.
     *
     * @return true if half day
     */
    public boolean isHalfDay() {
        return halfDay;
    }

    /**
     * Checks if early leaving detected.
     *
     * @return true if early leaving
     */
    public boolean isEarlyLeaving() {
        return earlyLeaving;
    }

    /**
     * Gets overtime minutes earned.
     *
     * @return overtime minutes
     */
    public long getOvertimeMinutes() {
        return overtimeMinutes;
    }

    /**
     * Gets overtime hours earned.
     *
     * @return overtime hours
     */
    public double getOvertimeHours() {
        return overtimeHours;
    }

    /**
     * Gets descriptive notes.
     *
     * @return notes
     */
    public String getNotes() {
        return notes;
    }
}
