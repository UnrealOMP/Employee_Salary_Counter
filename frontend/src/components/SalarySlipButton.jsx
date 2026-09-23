import React, { useState } from 'react';
import { Download, Loader2 } from 'lucide-react';
import { payrollService } from '../services/payrollService';

/**
 * Button component triggering PDF salary slip download.
 *
 * @param {Object} props - Component props.
 * @param {string} props.employeeId - Employee ID.
 * @param {string} props.employeeName - Employee name.
 * @param {string} props.payPeriod - Pay period.
 * @returns {JSX.Element}
 */
export function SalarySlipButton({ employeeId, employeeName, payPeriod }) {
  const [downloading, setDownloading] = useState(false);

  const handleDownload = async (e) => {
    e.stopPropagation(); // Prevent row selection event
    if (downloading) return;

    setDownloading(true);
    try {
      const blob = await payrollService.downloadSalarySlip(employeeId);
      const url = window.URL.createObjectURL(new Blob([blob], { type: 'application/pdf' }));
      const link = document.createElement('a');
      link.href = url;
      
      const safeName = (employeeName || 'Employee').replace(/[^a-zA-Z0-9]/g, '_');
      const safePeriod = (payPeriod || 'Monthly').replace(/[^a-zA-Z0-9]/g, '_');
      link.setAttribute('download', `${safeName}_${employeeId}_${safePeriod}_SalarySlip.pdf`);
      
      document.body.appendChild(link);
      link.click();
      link.parentNode.removeChild(link);
      window.URL.revokeObjectURL(url);
    } catch (err) {
      console.error('Failed to download salary slip:', err);
      alert('Failed to generate PDF salary slip. Please try again.');
    } finally {
      setDownloading(false);
    }
  };

  return (
    <button
      type="button"
      className="btn btn-secondary"
      onClick={handleDownload}
      disabled={downloading}
      title="Download PDF Salary Slip"
      style={{ padding: '0.4rem 0.75rem', fontSize: '0.75rem' }}
    >
      {downloading ? (
        <Loader2 className="spinner" size={14} />
      ) : (
        <Download size={14} />
      )}
      Salary Slip
    </button>
  );
}
