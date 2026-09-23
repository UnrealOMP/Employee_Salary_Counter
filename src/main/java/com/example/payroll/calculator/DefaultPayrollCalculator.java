package com.example.payroll.calculator;

import com.example.payroll.model.AttendanceResult;
import com.example.payroll.model.Employee;
import com.example.payroll.model.PayrollResult;
import com.example.payroll.policy.PayrollPolicy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Default implementation of PayrollCalculator implementing assignment payroll business logic.
 */
@Component
public class DefaultPayrollCalculator implements PayrollCalculator {

    private final PayrollPolicy policy;
    private final OvertimeCalculator overtimeCalculator;

    /**
     * Constructs DefaultPayrollCalculator with required dependencies.
     *
     * @param policy             central payroll policy
     * @param overtimeCalculator overtime calculation engine
     */
    public DefaultPayrollCalculator(PayrollPolicy policy, OvertimeCalculator overtimeCalculator) {
        this.policy = policy;
        this.overtimeCalculator = overtimeCalculator;
    }

    /**
     * Computes the employee's monthly payroll including deductions and overtime pay.
     *
     * @param employee          employee record
     * @param attendanceResults daily attendance calculated results
     * @param payPeriod         pay period name (e.g. "August 2026")
     * @return complete PayrollResult
     */
    @Override
    public PayrollResult calculate(Employee employee, List<AttendanceResult> attendanceResults, String payPeriod) {
        BigDecimal monthlySalary = employee.getMonthlySalary();
        int applicableWorkingDays = policy.getApplicableWorkingDays();

        // Calculate daily salary rate = monthlySalary / applicableWorkingDays
        BigDecimal dailySalary = monthlySalary.divide(
                BigDecimal.valueOf(applicableWorkingDays),
                4,
                RoundingMode.HALF_UP
        );

        int daysWithAttendance = attendanceResults != null ? attendanceResults.size() : 0;
        int lateMarks = 0;
        int halfDays = 0;
        int earlyLeavingInstances = 0;
        long totalOtMinutes = 0;

        if (attendanceResults != null) {
            for (AttendanceResult res : attendanceResults) {
                if (res.isLateMark()) lateMarks++;
                if (res.isHalfDay()) halfDays++;
                if (res.isEarlyLeaving()) earlyLeavingInstances++;
                totalOtMinutes += res.getOvertimeMinutes();
            }
        }

        // Excess counts
        int excessLateMarks = Math.max(0, lateMarks - policy.getAllowedLateMarks());
        int excessEarlyLeaves = Math.max(0, earlyLeavingInstances - policy.getAllowedEarlyLeaves());

        // Deductions
        BigDecimal lateDeduction = dailySalary.multiply(BigDecimal.valueOf(excessLateMarks))
                .multiply(policy.getLateDeductionRatio())
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal halfDayDeduction = dailySalary.multiply(BigDecimal.valueOf(halfDays))
                .multiply(policy.getHalfDayDeductionRatio())
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal earlyLeavingDeduction = dailySalary.multiply(BigDecimal.valueOf(excessEarlyLeaves))
                .multiply(policy.getEarlyLeavingDeductionRatio())
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalDeductions = lateDeduction.add(halfDayDeduction).add(earlyLeavingDeduction)
                .setScale(2, RoundingMode.HALF_UP);

        // Overtime Pay
        BigDecimal overtimePay = overtimeCalculator.calculateTotalOvertimePay(totalOtMinutes, dailySalary);
        double overtimeHours = Math.round((totalOtMinutes / 60.0) * 100.0) / 100.0;

        // Final Payable Salary = Monthly Salary + Overtime Pay - Total Deductions
        BigDecimal netPayable = monthlySalary.add(overtimePay).subtract(totalDeductions)
                .setScale(2, RoundingMode.HALF_UP);

        if (netPayable.compareTo(BigDecimal.ZERO) < 0) {
            netPayable = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        return new PayrollResult(
                employee.getEmployeeId(),
                employee.getEmployeeName(),
                payPeriod != null ? payPeriod : "Monthly",
                monthlySalary.setScale(2, RoundingMode.HALF_UP),
                applicableWorkingDays,
                daysWithAttendance,
                lateMarks,
                policy.getAllowedLateMarks(),
                excessLateMarks,
                halfDays,
                earlyLeavingInstances,
                policy.getAllowedEarlyLeaves(),
                overtimeHours,
                overtimePay,
                lateDeduction,
                halfDayDeduction,
                earlyLeavingDeduction,
                totalDeductions,
                netPayable
        );
    }
}
