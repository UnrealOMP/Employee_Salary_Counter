import React from 'react';
import { Users, Calendar, AlertCircle, Clock, LogOut, Award } from 'lucide-react';

/**
 * Metric summary cards displaying high-level payroll statistics.
 *
 * @param {Object} props - Component props.
 * @param {Object} props.summary - Calculated PayrollSummary object.
 * @returns {JSX.Element}
 */
export function SummaryCard({ summary }) {
  if (!summary) return null;

  const stats = [
    {
      label: 'Employees',
      value: summary.totalEmployees,
      icon: Users,
      color: 'var(--accent-primary)',
      bgColor: 'var(--accent-light)'
    },
    {
      label: 'Attendance Records',
      value: summary.totalAttendanceRecords,
      icon: Calendar,
      color: '#38bdf8',
      bgColor: 'rgba(56, 189, 248, 0.15)'
    },
    {
      label: 'Late Marks',
      value: summary.totalLateMarks,
      icon: AlertCircle,
      color: 'var(--status-warning)',
      bgColor: 'var(--status-warning-bg)'
    },
    {
      label: 'Half Days',
      value: summary.totalHalfDays,
      icon: Clock,
      color: 'var(--status-danger)',
      bgColor: 'var(--status-danger-bg)'
    },
    {
      label: 'Early Leaves',
      value: summary.totalEarlyLeaves,
      icon: LogOut,
      color: '#f43f5e',
      bgColor: 'rgba(244, 63, 94, 0.15)'
    },
    {
      label: 'Overtime Hours',
      value: `${summary.totalOvertimeHours} hrs`,
      icon: Award,
      color: 'var(--status-success)',
      bgColor: 'var(--status-success-bg)'
    },
  ];

  return (
    <div style={{
      display: 'grid',
      gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))',
      gap: '1rem',
      marginBottom: '2rem'
    }}>
      {stats.map((stat, idx) => {
        const IconComponent = stat.icon;
        return (
          <div key={idx} className="glass-card" style={{ padding: '1.25rem', display: 'flex', alignItems: 'center', gap: '1rem' }}>
            <div style={{
              width: '44px',
              height: '44px',
              borderRadius: 'var(--radius-md)',
              background: stat.bgColor,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              flexShrink: 0
            }}>
              <IconComponent size={22} color={stat.color} />
            </div>
            <div>
              <span style={{ fontSize: '0.75rem', color: 'var(--text-secondary)', textTransform: 'uppercase', letterSpacing: '0.05em', fontWeight: 600 }}>
                {stat.label}
              </span>
              <div style={{ fontSize: '1.5rem', fontWeight: 700, color: 'var(--text-primary)', lineHeight: 1.2 }}>
                {stat.value}
              </div>
            </div>
          </div>
        );
      })}
    </div>
  );
}
