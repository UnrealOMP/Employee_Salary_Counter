import React, { useState } from 'react';
import { Upload, FileSpreadsheet, CheckCircle2 } from 'lucide-react';

/**
 * File upload component accepting monthly Excel attendance file.
 *
 * @param {Object} props - Component props.
 * @param {Function} props.onUpload - File processing callback.
 * @param {boolean} props.loading - Processing indicator.
 * @returns {JSX.Element}
 */
export function FileUpload({ onUpload, loading }) {
  const [selectedFile, setSelectedFile] = useState(null);

  const handleFileChange = (e) => {
    if (e.target.files && e.target.files[0]) {
      setSelectedFile(e.target.files[0]);
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (selectedFile && !loading) {
      onUpload(selectedFile);
    }
  };

  return (
    <div className="glass-card" style={{ marginBottom: '2rem' }}>
      <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '1rem' }}>
        <FileSpreadsheet size={24} color="var(--accent-primary)" />
        <div>
          <h2 style={{ fontSize: '1.25rem', margin: 0 }}>Upload Monthly Attendance</h2>
          <p style={{ fontSize: '0.875rem', margin: 0 }}>Select a monthly attendance Excel spreadsheet (.xlsx or .xls) to parse attendance and calculate payroll.</p>
        </div>
      </div>

      <form onSubmit={handleSubmit} style={{ display: 'flex', flexWrap: 'wrap', alignItems: 'center', gap: '1rem' }}>
        <div style={{ flex: 1, minWidth: '280px' }}>
          <label
            htmlFor="excel-upload"
            style={{
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'space-between',
              padding: '0.75rem 1rem',
              background: 'var(--bg-secondary)',
              border: '1px dashed var(--border-color)',
              borderRadius: 'var(--radius-md)',
              cursor: 'pointer',
              transition: 'border-color 0.2s ease'
            }}
          >
            <span style={{ fontSize: '0.875rem', color: selectedFile ? 'var(--text-primary)' : 'var(--text-muted)' }}>
              {selectedFile ? selectedFile.name : 'Choose Excel File (.xlsx)'}
            </span>
            <Upload size={18} color="var(--text-secondary)" />
          </label>
          <input
            id="excel-upload"
            type="file"
            accept=".xlsx, .xls"
            onChange={handleFileChange}
            style={{ display: 'none' }}
          />
        </div>

        <button
          type="submit"
          className="btn btn-primary"
          disabled={!selectedFile || loading}
          style={{ minWidth: '160px' }}
        >
          {loading ? (
            'Processing payroll...'
          ) : (
            <>
              <CheckCircle2 size={18} />
              Process Payroll
            </>
          )}
        </button>
      </form>
    </div>
  );
}
