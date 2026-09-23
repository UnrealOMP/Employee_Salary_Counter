import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { usePayroll } from '../hooks/usePayroll';
import { FileUpload } from '../components/FileUpload';
import { SummaryCard } from '../components/SummaryCard';
import { PayrollTable } from '../components/PayrollTable';
import { LoadingSpinner } from '../components/LoadingSpinner';
import { ErrorMessage } from '../components/ErrorMessage';

/**
 * Dashboard page component rendering file upload, metric cards, and employee payroll table.
 *
 * @returns {JSX.Element}
 */
export function Dashboard() {
  const { summary, loading, error, setError, fetchPayroll, uploadFile } = usePayroll();
  const navigate = useNavigate();

  useEffect(() => {
    fetchPayroll();
  }, [fetchPayroll]);

  const handleSelectEmployee = (employeeId) => {
    navigate(`/employee/${encodeURIComponent(employeeId)}`);
  };

  return (
    <div>
      <div style={{ marginBottom: '1.5rem' }}>
        <h1 style={{ fontSize: '1.75rem', margin: 0 }}>Employee Attendance & Payroll Management</h1>
        <p style={{ margin: 0 }}>Upload monthly Excel attendance file to calculate late marks, half days, overtime, and final payable salary.</p>
      </div>

      <ErrorMessage message={error} onClose={() => setError(null)} />

      <FileUpload onUpload={uploadFile} loading={loading} />

      {loading ? (
        <LoadingSpinner message="Processing monthly attendance Excel file..." />
      ) : (
        <>
          <SummaryCard summary={summary} />
          <PayrollTable
            employees={summary ? summary.employeeResults : []}
            onSelectEmployee={handleSelectEmployee}
          />
        </>
      )}
    </div>
  );
}
