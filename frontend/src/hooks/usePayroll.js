import { useState, useCallback } from 'react';
import { payrollService } from '../services/payrollService';

/**
 * Custom React hook managing payroll state, file upload operations, and API errors.
 *
 * @returns {Object} Hook state and handler methods.
 */
export function usePayroll() {
  const [summary, setSummary] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchPayroll = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const isInitialized = sessionStorage.getItem('payrollInitialized');
      let data;
      if (!isInitialized) {
        data = await payrollService.resetPayroll();
        sessionStorage.setItem('payrollInitialized', 'true');
      } else {
        data = await payrollService.getPayroll();
      }
      setSummary(data);
    } catch (err) {
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
      sessionStorage.setItem('payrollInitialized', 'true');
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
