package com.example.payroll.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Domain model representing an employee master record.
 */
public class Employee {

    private final String employeeId;
    private final String employeeName;
    private final BigDecimal monthlySalary;

    /**
     * Constructs a new Employee instance.
     *
     * @param employeeId    unique employee identifier
     * @param employeeName  full name of the employee
     * @param monthlySalary base monthly salary
     */
    public Employee(String employeeId, String employeeName, BigDecimal monthlySalary) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.monthlySalary = monthlySalary != null ? monthlySalary : BigDecimal.ZERO;
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
     * Gets the employee's full name.
     *
     * @return employee name
     */
    public String getEmployeeName() {
        return employeeName;
    }

    /**
     * Gets the monthly base salary.
     *
     * @return monthly salary as BigDecimal
     */
    public BigDecimal getMonthlySalary() {
        return monthlySalary;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(employeeId, employee.employeeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeId);
    }

    @Override
    public String toString() {
        return "Employee{" +
                "employeeId='" + employeeId + '\'' +
                ", employeeName='" + employeeName + '\'' +
                ", monthlySalary=" + monthlySalary +
                '}';
    }
}
