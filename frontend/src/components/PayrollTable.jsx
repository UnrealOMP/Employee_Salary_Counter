import React from 'react';
import { Eye } from 'lucide-react';
import { SalarySlipButton } from './SalarySlipButton';
import { formatCurrency } from '../utils/formatCurrency';

/**
 * Main payroll summary table component listing all employee calculated results.
 *
 * @param {Object} props - Component props.
 * @param {Object[]} props.employees - List of employee PayrollResult objects.
 * @param {Function} props.onSelectEmployee - Employee row click handler.
 * @returns {JSX.Element}
 */
export function PayrollTable({ employees, onSelectEmployee }) {
  if (!employees || employees.length === 0) {
    return (
      <div className="glass-card" style={{ textAlign: 'center', padding: '3rem 1.5rem' }}>
        <p style={{ color: 'var(--text-secondary)' }}>No employee payroll data available. Please upload a monthly attendance Excel file.</p>
      </div>
    );
  }

  return (
    <div className="glass-card">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.25rem' }}>
        <h2 style={{ fontSize: '1.25rem', margin: 0 }}>Employee Payroll Records</h2>
        <span className="badge badge-neutral">{employees.length} Employees</span>
      </div>

      <div className="table-container">
        <table className="data-table">
          <thead>
            <tr>
              <th>Employee ID</th>
              <th>Employee Name</th>
              <th>Monthly Salary</th>
              <th style={{ textAlign: 'center' }}>Late Marks</th>
              <th style={{ textAlign: 'center' }}>Half Days</th>
              <th style={{ textAlign: 'center' }}>Early Leaves</th>
              <th style={{ textAlign: 'right' }}>Overtime</th>
              <th style={{ textAlign: 'right' }}>Deductions</th>
              <th style={{ textAlign: 'right' }}>Payable Salary</th>
              <th style={{ textAlign: 'center' }}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {employees.map((emp) => (
              <tr key={emp.employeeId} style={{ cursor: 'pointer' }} onClick={() => onSelectEmployee(emp.employeeId)}>
                <td style={{ fontWeight: 600, color: 'var(--accent-primary)' }}>{emp.employeeId}</td>
                <td style={{ fontWeight: 500 }}>{emp.employeeName}</td>
                <td>{formatCurrency(emp.monthlySalary)}</td>
                <td style={{ textAlign: 'center' }}>
                  <span className={`badge ${emp.lateMarks > emp.allowedLateMarks ? 'badge-warning' : 'badge-neutral'}`}>
                    {emp.lateMarks}
                  </span>
                </td>
                <td style={{ textAlign: 'center' }}>
                  <span className={`badge ${emp.halfDays > 0 ? 'badge-danger' : 'badge-neutral'}`}>
                    {emp.halfDays}
                  </span>
                </td>
                <td style={{ textAlign: 'center' }}>
                  <span className={`badge ${emp.earlyLeavingInstances > emp.allowedEarlyLeavingInstances ? 'badge-warning' : 'badge-neutral'}`}>
                    {emp.earlyLeavingInstances}
                  </span>
                </td>
                <td style={{ textAlign: 'right', fontWeight: 500, color: emp.overtimeHours > 0 ? 'var(--status-success)' : 'inherit' }}>
                  {emp.overtimeHours > 0 ? `${emp.overtimeHours} hrs (+${formatCurrency(emp.overtimePay)})` : '0 hrs'}
                </td>
                <td style={{ textAlign: 'right', color: emp.totalDeductions > 0 ? 'var(--status-danger)' : 'inherit' }}>
                  {emp.totalDeductions > 0 ? `-${formatCurrency(emp.totalDeductions)}` : '₹0.00'}
                </td>
                <td style={{ textAlign: 'right', fontWeight: 700, color: '#ffffff' }}>
                  {formatCurrency(emp.finalPayableSalary)}
                </td>
                <td style={{ textAlign: 'center' }} onClick={(e) => e.stopPropagation()}>
                  <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.5rem' }}>
                    <button
                      type="button"
                      className="btn btn-secondary"
                      onClick={() => onSelectEmployee(emp.employeeId)}
                      style={{ padding: '0.4rem 0.75rem', fontSize: '0.75rem' }}
                      title="View Employee Details"
                    >
                      <Eye size={14} /> View
                    </button>
                    <SalarySlipButton
                      employeeId={emp.employeeId}
                      employeeName={emp.employeeName}
                      payPeriod={emp.payPeriod}
                    />
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
