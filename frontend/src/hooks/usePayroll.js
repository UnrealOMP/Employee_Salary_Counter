import { useState, useCallback } from 'react';
import { payrollService } from '../services/payrollService';

/**
 * Custom React hook managing payroll state, file upload operations, and API errors.
 *
 * @returns {Object} Hook state and handler methods.
 */
export function usePayroll() {
  const [summary, setSummary] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchPayroll = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await payrollService.getPayroll();
      setSummary(data);
    } catch (err) {
      // If 400 because no file uploaded yet, keep summary null without showing alarming error banner
      if (err.response && err.response.status === 400) {
        setSummary(null);
      } else {
        const msg = err.response?.data?.message || err.message || 'Failed to fetch payroll metrics.';
        setError(msg);
      }
    } finally {
      setLoading(false);
    }
  }, []);

  const uploadFile = async (file) => {
    setLoading(true);
    setError(null);
    try {
      const data = await payrollService.uploadPayroll(file);
      setSummary(data);
      return data;
    } catch (err) {
      const msg = err.response?.data?.message || err.message || 'Failed to upload and process Excel file.';
      setError(msg);
      throw new Error(msg);
    } finally {
      setLoading(false);
    }
  };

  return {
    summary,
    loading,
    error,
    setError,
    fetchPayroll,
    uploadFile,
  };
}
