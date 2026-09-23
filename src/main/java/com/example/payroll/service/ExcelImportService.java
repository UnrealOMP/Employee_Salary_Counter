package com.example.payroll.service;

import com.example.payroll.exception.InvalidExcelException;
import com.example.payroll.parser.ExcelAttendanceParser;
import com.example.payroll.parser.ParsedExcelData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Service coordinating uploaded Excel file validation and delegate parsing.
 */
@Service
public class ExcelImportService {

    private static final Logger log = LoggerFactory.getLogger(ExcelImportService.class);

    private final ExcelAttendanceParser parser;

    /**
     * Constructs ExcelImportService with parser dependency injection.
     *
     * @param parser Excel workbook parser
     */
    public ExcelImportService(ExcelAttendanceParser parser) {
        this.parser = parser;
    }

    /**
     * Validates and imports an uploaded Excel file.
     *
     * @param file uploaded MultipartFile
     * @return parsed domain objects container
     * @throws InvalidExcelException if file is empty or invalid
     */
    public ParsedExcelData importExcel(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidExcelException("Uploaded file is empty or missing. Please select a valid Excel file.");
        }

        String filename = file.getOriginalFilename();
        log.info("Processing uploaded file: '{}', size: {} bytes", filename, file.getSize());

        try {
            return parser.parse(file.getInputStream(), filename);
        } catch (IOException e) {
            log.error("Failed to read uploaded file stream: {}", e.getMessage(), e);
            throw new InvalidExcelException("Failed to read uploaded file stream: " + e.getMessage(), e);
        }
    }
}
