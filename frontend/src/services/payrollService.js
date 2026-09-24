import api from './api';

/**
 * Service object encapsulating all backend REST API endpoint calls for payroll and attendance.
 */
export const payrollService = {
  /**
   * Uploads a monthly Excel attendance file for processing.
   *
   * @param {File} file - Monthly attendance Excel file (.xlsx or .xls).
   * @returns {Promise<Object>} Calculated PayrollSummary response.
   */
  async uploadPayroll(file) {
    const formData = new FormData();
    formData.append('file', file);
    const response = await api.post('/payroll/upload', formData, {
    });
    return response.data;
  },

  /**
   * Fetches current active PayrollSummary metrics.
   *
   * @returns {Promise<Object>} Active PayrollSummary response.
   */
  async getPayroll() {
    const response = await api.get('/payroll');
    return response.data;
  },

  /**
   * Fetches payroll breakdown for a specific employee.
   *
   * @param {string} employeeId - Target employee ID.
   * @returns {Promise<Object>} Employee PayrollResult response.
   */
  async getEmployeePayroll(employeeId) {
    const response = await api.get(`/payroll/${encodeURIComponent(employeeId)}`);
    return response.data;
  },

  /**
   * Fetches daily attendance records for a specific employee.
   *
   * @param {string} employeeId - Target employee ID.
   * @returns {Promise<Object[]>} List of AttendanceResult objects.
   */
  async getEmployeeAttendance(employeeId) {
    const response = await api.get(`/payroll/${encodeURIComponent(employeeId)}/attendance`);
    return response.data;
  },

  /**
   * Requests PDF salary slip generation and triggers browser download.
   *
   * @param {string} employeeId - Target employee ID.
   * @returns {Promise<Blob>} Raw PDF blob data.
   */
  async downloadSalarySlip(employeeId) {
    const response = await api.get(`/payroll/${encodeURIComponent(employeeId)}/salary-slip`, {
      responseType: 'blob',
    });
    return response.data;
  },
};
