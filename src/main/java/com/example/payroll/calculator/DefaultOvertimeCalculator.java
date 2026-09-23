package com.example.payroll.calculator;

import com.example.payroll.model.AttendanceRecord;
import com.example.payroll.policy.PayrollPolicy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;

/**
 * Default implementation of OvertimeCalculator applying discrete 3.5-hour block rules.
 */
@Component
public class DefaultOvertimeCalculator implements OvertimeCalculator {

    private final PayrollPolicy policy;

    /**
     * Constructs DefaultOvertimeCalculator with policy dependency.
     *
     * @param policy central payroll business policy
     */
    public DefaultOvertimeCalculator(PayrollPolicy policy) {
        this.policy = policy;
    }

    /**
     * Calculates overtime pay for a single daily attendance record.
     *
     * @param record      employee attendance record
     * @param dailySalary computed daily salary rate
     * @return overtime pay for the day
     */
    @Override
    public BigDecimal calculateOvertimePay(AttendanceRecord record, BigDecimal dailySalary) {
        long workedMinutes = Duration.between(record.getPunchIn(), record.getPunchOut()).toMinutes();
        long extraMinutes = workedMinutes - policy.getRequiredShiftMinutes();
        if (extraMinutes < policy.getMinOvertimeMinutes()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return calculateTotalOvertimePay(extraMinutes, dailySalary);
    }

    /**
     * Calculates overtime pay for cumulative overtime minutes based on 3.5-hour (210-minute) blocks.
     *
     * <p>Formula:
     * {@code otBlocks = totalOvertimeMinutes / 210}
     * {@code otPay = otBlocks * 0.5 * dailySalary}
     * </p>
     *
     * @param totalOvertimeMinutes cumulative overtime minutes
     * @param dailySalary          computed daily salary rate
     * @return total overtime pay
     */
    @Override
    public BigDecimal calculateTotalOvertimePay(long totalOvertimeMinutes, BigDecimal dailySalary) {
        if (totalOvertimeMinutes < policy.getMinOvertimeMinutes() || dailySalary == null || dailySalary.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        long blocks = totalOvertimeMinutes / policy.getOvertimeBlockMinutes();
        if (blocks <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal blockFactor = BigDecimal.valueOf(blocks).multiply(policy.getOtBlockPayRatio());
        return dailySalary.multiply(blockFactor).setScale(2, RoundingMode.HALF_UP);
    }
}
