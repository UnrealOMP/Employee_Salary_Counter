import React from 'react';
import { AttendanceTable } from './AttendanceTable';
import { SalarySlipButton } from './SalarySlipButton';
import { formatCurrency } from '../utils/formatCurrency';
import { User, Calendar, DollarSign, ArrowLeft } from 'lucide-react';

/**
 * Employee detail drill-down component displaying profile, attendance metrics, salary breakdown, and daily logs.
 *
 * @param {Object} props - Component props.
 * @param {Object} props.employee - PayrollResult object.
 * @param {Object[]} props.attendanceList - List of AttendanceResult objects.
 * @param {Function} props.onBack - Back navigation handler.
 * @returns {JSX.Element}
 */
export function EmployeeDetails({ employee, attendanceList, onBack }) {
  if (!employee) return null;

  return (
    <div>
      {/* Top Header & Back Button */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '1.5rem' }}>
        <button type="button" className="btn btn-secondary" onClick={onBack}>
          <ArrowLeft size={16} /> Back to Dashboard
        </button>
        <SalarySlipButton
          employeeId={employee.employeeId}
          employeeName={employee.employeeName}
          payPeriod={employee.payPeriod}
        />
      </div>

      {/* Grid Layout for Info Cards */}
      <div style={{
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))',
        gap: '1.5rem',
        marginBottom: '2rem'
      }}>
        {/* Card 1: Employee Overview */}
        <div className="glass-card">
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '1rem' }}>
            <div style={{ background: 'var(--accent-light)', padding: '0.5rem', borderRadius: 'var(--radius-md)' }}>
              <User size={20} color="var(--accent-primary)" />
            </div>
            <h3 style={{ fontSize: '1.125rem', margin: 0 }}>Employee Profile</h3>
          </div>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.75rem', fontSize: '0.875rem' }}>
            <div>
              <span style={{ color: 'var(--text-muted)', display: 'block' }}>Employee Name</span>
              <strong style={{ color: '#ffffff' }}>{employee.employeeName}</strong>
            </div>
            <div>
              <span style={{ color: 'var(--text-muted)', display: 'block' }}>Employee ID</span>
              <strong style={{ color: 'var(--accent-primary)' }}>{employee.employeeId}</strong>
            </div>
            <div>
              <span style={{ color: 'var(--text-muted)', display: 'block' }}>Pay Period</span>
              <span>{employee.payPeriod}</span>
            </div>
            <div>
              <span style={{ color: 'var(--text-muted)', display: 'block' }}>Days Logged</span>
              <span>{employee.daysWithAttendance} / {employee.applicableWorkingDays} days</span>
            </div>
          </div>
        </div>

        {/* Card 2: Attendance Summary */}
        <div className="glass-card">
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '1rem' }}>
            <div style={{ background: 'var(--status-info-bg)', padding: '0.5rem', borderRadius: 'var(--radius-md)' }}>
              <Calendar size={20} color="var(--status-info)" />
            </div>
            <h3 style={{ fontSize: '1.125rem', margin: 0 }}>Attendance Summary</h3>
          </div>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.75rem', fontSize: '0.875rem' }}>
            <div>
              <span style={{ color: 'var(--text-muted)', display: 'block' }}>Late Marks</span>
              <span className={`badge ${employee.lateMarks > employee.allowedLateMarks ? 'badge-warning' : 'badge-neutral'}`}>
                {employee.lateMarks} ({employee.excessLateMarks} excess)
              </span>
            </div>
            <div>
              <span style={{ color: 'var(--text-muted)', display: 'block' }}>Half Days</span>
              <span className={`badge ${employee.halfDays > 0 ? 'badge-danger' : 'badge-neutral'}`}>
                {employee.halfDays} days
              </span>
            </div>
            <div>
              <span style={{ color: 'var(--text-muted)', display: 'block' }}>Early Leaves</span>
              <span className={`badge ${employee.earlyLeavingInstances > employee.allowedEarlyLeavingInstances ? 'badge-warning' : 'badge-neutral'}`}>
                {employee.earlyLeavingInstances} instances
              </span>
            </div>
            <div>
              <span style={{ color: 'var(--text-muted)', display: 'block' }}>Overtime</span>
              <span className="badge badge-success">{employee.overtimeHours} hrs</span>
            </div>
          </div>
        </div>

        {/* Card 3: Salary Breakdown */}
        <div className="glass-card">
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '1rem' }}>
            <div style={{ background: 'var(--status-success-bg)', padding: '0.5rem', borderRadius: 'var(--radius-md)' }}>
              <DollarSign size={20} color="var(--status-success)" />
            </div>
            <h3 style={{ fontSize: '1.125rem', margin: 0 }}>Salary Breakdown</h3>
          </div>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem', fontSize: '0.875rem' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span style={{ color: 'var(--text-secondary)' }}>Base Monthly Salary:</span>
              <span>{formatCurrency(employee.monthlySalary)}</span>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between', color: 'var(--status-success)' }}>
              <span>+ Overtime Pay:</span>
              <span>{formatCurrency(employee.overtimePay)}</span>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between', color: 'var(--status-danger)' }}>
              <span>- Late Mark Deduction:</span>
              <span>-{formatCurrency(employee.lateMarkDeduction)}</span>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between', color: 'var(--status-danger)' }}>
              <span>- Half Day Deduction:</span>
              <span>-{formatCurrency(employee.halfDayDeduction)}</span>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between', color: 'var(--status-danger)' }}>
              <span>- Early Leave Deduction:</span>
              <span>-{formatCurrency(employee.earlyLeavingDeduction)}</span>
            </div>
            <div style={{
              display: 'flex',
              justify: 'space-between',
              paddingTop: '0.5rem',
              borderTop: '1px solid var(--border-color)',
              fontWeight: 700,
              fontSize: '1rem',
              color: '#ffffff'
            }}>
              <span>Final Payable Salary:</span>
              <span style={{ color: 'var(--status-success)' }}>{formatCurrency(employee.finalPayableSalary)}</span>
            </div>
          </div>
        </div>
      </div>

      {/* Daily Attendance Logs Table */}
      <div className="glass-card">
        <h3 style={{ fontSize: '1.125rem', marginBottom: '1rem' }}>Daily Attendance Logs</h3>
        <AttendanceTable attendanceList={attendanceList} />
      </div>
    </div>
  );
}
