package com.example.payroll.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Domain model representing a single raw attendance entry parsed from the Excel file.
 */
public class AttendanceRecord {

    private final String employeeId;
    private final String employeeName;
    private final LocalDate date;
    private final LocalTime punchIn;
    private final LocalTime punchOut;

    /**
     * Constructs an AttendanceRecord.
     *
     * @param employeeId   employee identifier
     * @param employeeName employee name
     * @param date         date of attendance
     * @param punchIn      time when employee punched in
     * @param punchOut     time when employee punched out
     */
    public AttendanceRecord(String employeeId, String employeeName, LocalDate date, LocalTime punchIn, LocalTime punchOut) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.date = date;
        this.punchIn = punchIn;
        this.punchOut = punchOut;
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
     * Gets the employee name.
     *
     * @return employee name
     */
    public String getEmployeeName() {
        return employeeName;
    }

    /**
     * Gets the attendance date.
     *
     * @return date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Gets the punch-in time.
     *
     * @return punch-in LocalTime
     */
    public LocalTime getPunchIn() {
        return punchIn;
    }

    /**
     * Gets the punch-out time.
     *
     * @return punch-out LocalTime
     */
    public LocalTime getPunchOut() {
        return punchOut;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttendanceRecord record = (AttendanceRecord) o;
        return Objects.equals(employeeId, record.employeeId) &&
               Objects.equals(date, record.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeId, date);
    }

    @Override
    public String toString() {
        return "AttendanceRecord{" +
                "employeeId='" + employeeId + '\'' +
                ", date=" + date +
                ", punchIn=" + punchIn +
                ", punchOut=" + punchOut +
                '}';
    }
}
