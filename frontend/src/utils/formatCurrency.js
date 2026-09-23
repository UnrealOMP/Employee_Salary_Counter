/**
 * Formats a numeric value or BigDecimal representation into Indian Rupees (INR ₹) currency string.
 *
 * @param {number|string} amount - Number or numeric string to format.
 * @returns {string} Formatted currency string (e.g. ₹30,000.00).
 */
export function formatCurrency(amount) {
  if (amount === null || amount === undefined || isNaN(Number(amount))) {
    return '₹0.00';
  }
  
  const num = Number(amount);
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  }).format(num);
}
