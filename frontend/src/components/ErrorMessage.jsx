import React from 'react';
import { AlertCircle, X } from 'lucide-react';

/**
 * Accessible error message alert component.
 *
 * @param {Object} props - Component props.
 * @param {string} props.message - Error message string.
 * @param {Function} [props.onClose] - Callback when alert is closed.
 * @returns {JSX.Element|null}
 */
export function ErrorMessage({ message, onClose }) {
  if (!message) return null;

  return (
    <div role="alert" style={{
      background: 'var(--status-danger-bg)',
      border: '1px solid rgba(239, 68, 68, 0.4)',
      color: '#fca5a5',
      padding: '1rem 1.25rem',
      borderRadius: 'var(--radius-md)',
      display: 'flex',
      alignItems: 'flex-start',
      gap: '0.75rem',
      marginBottom: '1.5rem'
    }}>
      <AlertCircle size={20} style={{ flexShrink: 0, marginTop: '2px', color: 'var(--status-danger)' }} />
      <div style={{ flex: 1, fontSize: '0.875rem' }}>
        <strong style={{ display: 'block', color: '#ffffff', marginBottom: '0.25rem' }}>Processing Error</strong>
        {message}
      </div>
      {onClose && (
        <button
          onClick={onClose}
          aria-label="Close error message"
          style={{
            background: 'transparent',
            border: 'none',
            color: '#fca5a5',
            cursor: 'pointer',
            padding: '0.25rem'
          }}
        >
          <X size={16} />
        </button>
      )}
    </div>
  );
}
