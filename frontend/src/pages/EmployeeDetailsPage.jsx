import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { payrollService } from '../services/payrollService';
import { EmployeeDetails } from '../components/EmployeeDetails';
import { LoadingSpinner } from '../components/LoadingSpinner';
import { ErrorMessage } from '../components/ErrorMessage';

/**
 * Page component fetching and rendering individual employee details and attendance logs.
 *
 * @returns {JSX.Element}
 */
export function EmployeeDetailsPage() {
  const { employeeId } = useParams();
  const navigate = useNavigate();

  const [employee, setEmployee] = useState(null);
  const [attendanceList, setAttendanceList] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    async function loadData() {
      if (!employeeId) return;
      setLoading(true);
      setError(null);
      try {
        const [empData, attData] = await Promise.all([
          payrollService.getEmployeePayroll(employeeId),
          payrollService.getEmployeeAttendance(employeeId),
        ]);
        setEmployee(empData);
        setAttendanceList(attData);
      } catch (err) {
        const msg = err.response?.data?.message || err.message || 'Failed to load employee details.';
        setError(msg);
      } finally {
        setLoading(false);
      }
    }

    loadData();
  }, [employeeId]);

  return (
    <div>
      <ErrorMessage message={error} onClose={() => setError(null)} />

      {loading ? (
        <LoadingSpinner message={`Loading details for employee ${employeeId}...`} />
      ) : (
        <EmployeeDetails
          employee={employee}
          attendanceList={attendanceList}
          onBack={() => navigate('/')}
        />
      )}
    </div>
  );
}
