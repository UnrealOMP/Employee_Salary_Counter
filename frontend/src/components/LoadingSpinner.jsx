import React from 'react';
import { Loader2 } from 'lucide-react';

/**
 * Custom loading spinner component.
 *
 * @param {Object} props - Component props.
 * @param {string} [props.message="Processing..."] - Loading text message.
 * @returns {JSX.Element}
 */
export function LoadingSpinner({ message = "Processing..." }) {
  return (
    <div style={{
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      justifyContent: 'center',
      padding: '3rem 1.5rem',
      gap: '1rem'
    }}>
      <Loader2 className="spinner" size={36} color="var(--accent-primary)" />
      <p style={{ color: 'var(--text-secondary)', fontSize: '0.95rem', fontWeight: 500 }}>{message}</p>
    </div>
  );
}
