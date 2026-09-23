package com.example.payroll.parser;

import com.example.payroll.exception.InvalidExcelException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExcelAttendanceParserTest {

    private ExcelAttendanceParser parser;

    @BeforeEach
    void setUp() {
        parser = new ExcelAttendanceParser();
    }

    @Test
    @DisplayName("Scenario 18: Invalid or empty Excel file stream throws InvalidExcelException")
    void testEmptyFileStream() {
        InputStream emptyStream = new ByteArrayInputStream(new byte[0]);
        InvalidExcelException ex = assertThrows(InvalidExcelException.class, () ->
                parser.parse(emptyStream, "empty.xlsx"));

        assertTrue(ex.getMessage().toLowerCase().contains("failed") || ex.getMessage().toLowerCase().contains("empty"));
    }

    @Test
    @DisplayName("Scenario 18b: Unsupported file extension throws InvalidExcelException")
    void testUnsupportedExtension() {
        InputStream stream = new ByteArrayInputStream(new byte[]{1, 2, 3});
        InvalidExcelException ex = assertThrows(InvalidExcelException.class, () ->
                parser.parse(stream, "data.txt"));

        assertTrue(ex.getMessage().contains("Unsupported file type"));
    }
}
