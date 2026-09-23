import React from 'react';
import { Routes, Route } from 'react-router-dom';
import { Navbar } from './components/Navbar';
import { Dashboard } from './pages/Dashboard';
import { EmployeeDetailsPage } from './pages/EmployeeDetailsPage';

/**
 * Root application component configuring Navbar layout and page routes.
 *
 * @returns {JSX.Element}
 */
export function App() {
  return (
    <div className="app-container">
      <Navbar />
      <main className="main-content">
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/employee/:employeeId" element={<EmployeeDetailsPage />} />
        </Routes>
      </main>
    </div>
  );
}

export default App;
