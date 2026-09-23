import React from 'react';
import { Link } from 'react-router-dom';
import { Building2, FileSpreadsheet } from 'lucide-react';

/**
 * Navbar header component displaying system title and quick navigation.
 *
 * @returns {JSX.Element}
 */
export function Navbar() {
  return (
    <header className="navbar-header" style={{
      background: 'rgba(30, 41, 59, 0.8)',
      backdropFilter: 'blur(12px)',
      borderBottom: '1px solid var(--border-color)',
      padding: '1rem 1.5rem',
      position: 'sticky',
      top: 0,
      zIndex: 50
    }}>
      <div style={{
        maxWidth: '1400px',
        margin: '0 auto',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between'
      }}>
        <Link to="/" style={{ textDecoration: 'none', display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
          <div style={{
            background: 'var(--accent-light)',
            color: 'var(--accent-primary)',
            padding: '0.5rem',
            borderRadius: 'var(--radius-md)',
            display: 'flex'
          }}>
            <Building2 size={24} />
          </div>
          <div>
            <h1 style={{ fontSize: '1.125rem', margin: 0, lineHeight: 1.2 }}>Square One Media</h1>
            <span style={{ fontSize: '0.75rem', color: 'var(--text-secondary)' }}>Attendance & Payroll System</span>
          </div>
        </Link>
        <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
          <span className="badge badge-info" style={{ display: 'flex', alignItems: 'center', gap: '0.35rem' }}>
            <FileSpreadsheet size={14} /> Excel Import Mode
          </span>
        </div>
      </div>
    </header>
  );
}
