package com.example.payroll.util;

import com.example.payroll.exception.InvalidExcelException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Utility class for parsing and formatting dates and times across various Excel export formats.
 */
public final class DateTimeUtils {

    private static final DateTimeFormatter[] DATE_FORMATTERS = new DateTimeFormatter[]{
            DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy")
    };

    private DateTimeUtils() {
        // Private constructor for utility class
    }

    /**
     * Parses a date string into a LocalDate instance.
     *
     * @param dateStr raw date string from Excel
     * @return parsed LocalDate
     * @throws InvalidExcelException if date format cannot be recognized
     */
    public static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new InvalidExcelException("Date value cannot be empty or null.");
        }
        String cleanStr = dateStr.trim();
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(cleanStr, formatter);
            } catch (DateTimeParseException ignored) {
                // Try next formatter
            }
        }
        throw new InvalidExcelException("Unable to parse date string: '" + dateStr + "'. Expected format e.g. '03 Aug 2026' or 'yyyy-MM-dd'.");
    }

    /**
     * Parses a time string or numeric decimal time representation into a LocalTime.
     *
     * <p>Supports:
     * <ul>
     *   <li>Keka decimal format e.g. "9" (09:00), "10.09" (10:09), "19.13" (19:13), "9.5" (09:50)</li>
     *   <li>Standard format e.g. "10:09", "19:13:00", "09:00"</li>
     * </ul>
     * </p>
     *
     * @param timeStr raw time representation
     * @return parsed LocalTime
     * @throws InvalidExcelException if time string cannot be parsed
     */
    public static LocalTime parseTime(String timeStr) {
        if (timeStr == null || timeStr.trim().isEmpty()) {
            throw new InvalidExcelException("Time value cannot be empty or null.");
        }
        String clean = timeStr.trim();

        // 1. Try standard colon notation (HH:MM or HH:MM:SS)
        if (clean.contains(":")) {
            String[] parts = clean.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            int second = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;
            return LocalTime.of(hour, minute, second);
        }

        // 2. Keka decimal notation e.g. "10.09", "9.5", "19", "9"
        try {
            double val = Double.parseDouble(clean);
            if (val < 0 || val >= 24) {
                // If it's a fraction between 0.0 and 1.0 (Excel raw fraction of a day)
                if (val >= 0.0 && val < 1.0) {
                    long totalSeconds = Math.round(val * 86400);
                    int hour = (int) (totalSeconds / 3600);
                    int minute = (int) ((totalSeconds % 3600) / 60);
                    int second = (int) (totalSeconds % 60);
                    return LocalTime.of(hour % 24, minute % 60, second % 60);
                }
                throw new InvalidExcelException("Invalid time value out of 24h range: " + timeStr);
            }

            int hour = (int) val;
            int minute = 0;

            if (clean.contains(".")) {
                String[] parts = clean.split("\\.");
                if (parts.length > 1 && !parts[1].isEmpty()) {
                    String mStr = parts[1];
                    if (mStr.length() == 1) {
                        mStr = mStr + "0"; // e.g. 9.5 -> 50 minutes
                    } else if (mStr.length() > 2) {
                        mStr = mStr.substring(0, 2);
                    }
                    minute = Integer.parseInt(mStr);
                }
            }

            if (hour >= 24 || minute >= 60) {
                throw new InvalidExcelException("Invalid hour or minute parsed from time: " + timeStr);
            }

            return LocalTime.of(hour, minute);

        } catch (NumberFormatException e) {
            throw new InvalidExcelException("Unable to parse time representation: '" + timeStr + "'", e);
        }
    }
}
