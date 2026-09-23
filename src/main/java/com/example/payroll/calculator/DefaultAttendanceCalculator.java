package com.example.payroll.calculator;

import com.example.payroll.model.AttendanceRecord;
import com.example.payroll.model.AttendanceResult;
import com.example.payroll.policy.PayrollPolicy;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalTime;

/**
 * Default implementation of AttendanceCalculator implementing assignment attendance business rules.
 */
@Component
public class DefaultAttendanceCalculator implements AttendanceCalculator {

    private final PayrollPolicy policy;

    /**
     * Constructs DefaultAttendanceCalculator with constructor injection.
     *
     * @param policy central payroll business policy
     */
    public DefaultAttendanceCalculator(PayrollPolicy policy) {
        this.policy = policy;
    }

    /**
     * Calculates attendance metrics for an individual daily record.
     *
     * <p>Rules applied:
     * <ul>
     *   <li>Late Mark: Punch-in after 10:00 AM or failing to complete required shift time during flexibility.</li>
     *   <li>Half Day: Punch-in after 11:00 AM or working $\le$ 4 hours (240 minutes).</li>
     *   <li>Early Leaving: Departing before completing required shift (if punch-in $\le$ 10:00 AM) or departing before 4:00 PM (if punch-in > 10:00 AM).</li>
     *   <li>Overtime: Work duration beyond standard shift ($\ge$ 3.5 hours).</li>
     * </ul>
     * </p>
     *
     * @param record employee attendance record
     * @return attendance calculation result
     */
    @Override
    public AttendanceResult calculate(AttendanceRecord record) {
        LocalTime in = record.getPunchIn();
        LocalTime out = record.getPunchOut();

        long workedMinutes = Duration.between(in, out).toMinutes();
        if (workedMinutes < 0) {
            workedMinutes = 0;
        }
        double workedHours = Math.round((workedMinutes / 60.0) * 100.0) / 100.0;

        // 1. Late Mark
        boolean lateMark = isLateMark(in, workedMinutes);

        // 2. Half Day
        boolean halfDay = isHalfDay(in, workedMinutes);

        // 3. Early Leaving
        boolean earlyLeaving = isEarlyLeaving(in, out);

        // 4. Overtime Minutes
        long otMinutes = calculateOvertimeMinutes(workedMinutes);
        double otHours = Math.round((otMinutes / 60.0) * 100.0) / 100.0;

        String notes = generateNotes(lateMark, halfDay, earlyLeaving, otMinutes);

        return new AttendanceResult(
                record.getEmployeeId(),
                record.getDate(),
                in,
                out,
                workedMinutes,
                workedHours,
                lateMark,
                halfDay,
                earlyLeaving,
                otMinutes,
                otHours,
                notes
        );
    }

    /**
     * Determines whether punch-in qualifies as a late mark.
     *
     * @param in            punch-in time
     * @param workedMinutes total worked minutes
     * @return true if late mark applies
     */
    private boolean isLateMark(LocalTime in, long workedMinutes) {
        if (in.isAfter(policy.getLateThresholdTime())) {
            return true;
        }
        // If punch-in is within flexibility window (09:00 - 10:00 AM) but employee did not complete 9h required work
        return (in.isAfter(policy.getRegularStartTime()) || in.equals(policy.getRegularStartTime()))
                && !in.isAfter(policy.getLateThresholdTime())
                && workedMinutes < policy.getRequiredShiftMinutes();
    }

    /**
     * Determines whether employee shift is marked as half day.
     *
     * @param in            punch-in time
     * @param workedMinutes total worked minutes
     * @return true if half day applies
     */
    private boolean isHalfDay(LocalTime in, long workedMinutes) {
        return in.isAfter(policy.getHalfDayThresholdTime()) || workedMinutes <= policy.getHalfDayMaxWorkedMinutes();
    }

    /**
     * Determines whether employee left early.
     *
     * @param in  punch-in time
     * @param out punch-out time
     * @return true if early departure detected
     */
    private boolean isEarlyLeaving(LocalTime in, LocalTime out) {
        if (!in.isAfter(policy.getLateThresholdTime())) {
            // Flexibility window punch-in: must complete 9 hours from punch-in time
            LocalTime requiredOut = in.plusMinutes(policy.getRequiredShiftMinutes());
            return out.isBefore(requiredOut);
        } else {
            // Coming after 10:00 AM: allowed leave threshold is 4:00 PM (16:00)
            LocalTime thresholdFourPm = LocalTime.of(16, 0);
            return out.isBefore(thresholdFourPm);
        }
    }

    /**
     * Calculates overtime minutes earned beyond regular shift.
     *
     * @param workedMinutes total worked minutes
     * @return overtime minutes
     */
    private long calculateOvertimeMinutes(long workedMinutes) {
        long extraMinutes = workedMinutes - policy.getRequiredShiftMinutes();
        if (extraMinutes >= policy.getMinOvertimeMinutes()) {
            return extraMinutes;
        }
        return 0;
    }

    private String generateNotes(boolean late, boolean half, boolean early, long otMins) {
        StringBuilder sb = new StringBuilder();
        if (half) sb.append("Half Day. ");
        else if (late) sb.append("Late Mark. ");
        if (early) sb.append("Early Departure. ");
        if (otMins > 0) sb.append("OT Earned: ").append(otMins / 60).append("h ").append(otMins % 60).append("m. ");
        if (sb.length() == 0) sb.append("Regular Shift Complete.");
        return sb.toString().trim();
    }
}
