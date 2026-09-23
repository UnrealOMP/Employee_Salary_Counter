import React from 'react';
import { formatDate, formatTime } from '../utils/formatDate';

/**
 * Table component rendering daily attendance logs for an individual employee.
 *
 * @param {Object} props - Component props.
 * @param {Object[]} props.attendanceList - Array of AttendanceResult objects.
 * @returns {JSX.Element}
 */
export function AttendanceTable({ attendanceList }) {
  if (!attendanceList || attendanceList.length === 0) {
    return (
      <div style={{ textAlign: 'center', padding: '2rem 1rem', color: 'var(--text-muted)' }}>
        No daily attendance logs found for this employee.
      </div>
    );
  }

  return (
    <div className="table-container">
      <table className="data-table">
        <thead>
          <tr>
            <th>Date</th>
            <th>Punch In</th>
            <th>Punch Out</th>
            <th style={{ textAlign: 'right' }}>Working Hours</th>
            <th style={{ textAlign: 'center' }}>Late</th>
            <th style={{ textAlign: 'center' }}>Half Day</th>
            <th style={{ textAlign: 'center' }}>Early Leave</th>
            <th style={{ textAlign: 'right' }}>Overtime</th>
            <th>Status / Notes</th>
          </tr>
        </thead>
        <tbody>
          {attendanceList.map((row, idx) => (
            <tr key={idx}>
              <td style={{ fontWeight: 500 }}>{formatDate(row.date)}</td>
              <td>{formatTime(row.punchIn)}</td>
              <td>{formatTime(row.punchOut)}</td>
              <td style={{ textAlign: 'right', fontWeight: 600 }}>{row.totalWorkedHours} hrs</td>
              <td style={{ textAlign: 'center' }}>
                {row.lateMark ? (
                  <span className="badge badge-warning">Yes</span>
                ) : (
                  <span className="badge badge-neutral">No</span>
                )}
              </td>
              <td style={{ textAlign: 'center' }}>
                {row.halfDay ? (
                  <span className="badge badge-danger">Yes</span>
                ) : (
                  <span className="badge badge-neutral">No</span>
                )}
              </td>
              <td style={{ textAlign: 'center' }}>
                {row.earlyLeaving ? (
                  <span className="badge badge-warning">Yes</span>
                ) : (
                  <span className="badge badge-neutral">No</span>
                )}
              </td>
              <td style={{ textAlign: 'right', color: row.overtimeHours > 0 ? 'var(--status-success)' : 'inherit' }}>
                {row.overtimeHours > 0 ? `${row.overtimeHours} hrs` : '-'}
              </td>
              <td style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>
                {row.notes}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
