/**
 * Formats an ISO date string or LocalTime string into user-friendly display text.
 *
 * @param {string} dateStr - Date string (e.g. "2026-08-03").
 * @returns {string} Formatted date string (e.g. "03 Aug 2026").
 */
export function formatDate(dateStr) {
  if (!dateStr) return '-';
  try {
    const date = new Date(dateStr);
    if (isNaN(date.getTime())) return dateStr;
    return new Intl.DateTimeFormat('en-GB', {
      day: '2-digit',
      month: 'short',
      year: 'numeric',
    }).format(date);
  } catch (e) {
    return dateStr;
  }
}

/**
 * Formats a LocalTime string (e.g. "09:30:00" or "19:13") into 12-hour AM/PM format.
 *
 * @param {string} timeStr - Time string.
 * @returns {string} Formatted time string (e.g. "09:30 AM", "07:13 PM").
 */
export function formatTime(timeStr) {
  if (!timeStr) return '-';
  const parts = timeStr.split(':');
  if (parts.length < 2) return timeStr;
  
  let hours = parseInt(parts[0], 10);
  const minutes = parts[1].padStart(2, '0');
  const ampm = hours >= 12 ? 'PM' : 'AM';
  
  hours = hours % 12;
  hours = hours ? hours : 12; // Midnight / Noon 12
  const formattedHours = hours.toString().padStart(2, '0');
  
  return `${formattedHours}:${minutes} ${ampm}`;
}
