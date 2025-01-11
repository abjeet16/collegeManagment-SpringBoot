package com.mynotes.controllers;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class DemoController {

    @GetMapping("/add-name")
    public String addName() {
        try {
            // Get a reference to the Firebase Realtime Database
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

            // Reference to the 'names' collection
            DatabaseReference namesRef = databaseReference.child("names");

            // Generate a unique key for the new name entry
            String nameKey = namesRef.push().getKey();

            // Check if the nameKey is null
            if (nameKey == null) {
                return "Failed to generate unique key.";
            }

            // Use a Map to structure the data
            Map<String, Object> nameData = new HashMap<>();
            nameData.put("name", "Abjeet");

            // Set the value using the Map (to handle data in a structured way)
            namesRef.child(nameKey).setValueAsync(nameData);

            return "Name 'Abjeet' added successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to add name: " + e.getMessage();
        }
    }

    private static final String FILE_DIRECTORY = "attendance_files/";

    @PostMapping("/save")
    public String saveAttendance(
            @RequestParam String course,
            @RequestParam String section,
            @RequestParam int batchYear,
            @RequestParam String subject,
            @RequestParam(required = false) String fileName,
            @RequestBody List<Map<String, String>> attendanceList // JSON with uucms_id, name, and attendance
    ) throws IOException {
        // Generate file name if not provided
        if (fileName == null || fileName.isEmpty()) {
            fileName = "attendance_sheet_" + course + "_" + section + "_"+ subject + "_" + batchYear + ".xlsx";
        }

        // Ensure the directory exists
        Files.createDirectories(Paths.get(FILE_DIRECTORY));

        Path filePath = Paths.get(FILE_DIRECTORY, fileName);
        boolean fileExists = Files.exists(filePath);

        // Handle file creation or update
        if (fileExists) {
            updateExistingExcelFile(filePath, attendanceList);
        } else {
            createNewExcelFile(filePath, attendanceList);
        }

        return "Attendance saved successfully!";
    }

    private void createNewExcelFile(Path filePath, List<Map<String, String>> attendanceList) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Attendance");

        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("UUcms ID");
        headerRow.createCell(1).setCellValue("Name");
        headerRow.createCell(2).setCellValue(LocalDate.now().toString()); // Today's date

        // Populate rows with attendance data
        int rowNum = 1;
        for (Map<String, String> attendance : attendanceList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(attendance.get("uucms_id"));
            row.createCell(1).setCellValue(attendance.get("name"));
            row.createCell(2).setCellValue(attendance.get("attendance"));
        }

        // Write to file
        try (FileOutputStream fileOut = new FileOutputStream(filePath.toFile())) {
            workbook.write(fileOut);
        }
        workbook.close();
    }

    private void updateExistingExcelFile(Path filePath, List<Map<String, String>> attendanceList) throws IOException {
        FileInputStream fileIn = new FileInputStream(filePath.toFile());
        Workbook workbook = new XSSFWorkbook(fileIn);
        Sheet sheet = workbook.getSheetAt(0);

        // Get today's date column
        String todayDate = LocalDate.now().toString();
        Row headerRow = sheet.getRow(0);
        int lastColumnIndex = headerRow.getLastCellNum();
        int dateColumnIndex = -1;

        for (int i = 2; i < lastColumnIndex; i++) {
            if (headerRow.getCell(i).getStringCellValue().equals(todayDate)) {
                dateColumnIndex = i;
                break;
            }
        }

        // Add new column if date doesn't exist
        if (dateColumnIndex == -1) {
            dateColumnIndex = lastColumnIndex;
            headerRow.createCell(dateColumnIndex).setCellValue(todayDate);
        }

        // Update attendance
        for (Map<String, String> attendance : attendanceList) {
            String uucmsId = attendance.get("uucms_id");
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row
                if (row.getCell(0).getStringCellValue().equals(uucmsId)) {
                    row.createCell(dateColumnIndex).setCellValue(attendance.get("attendance"));
                    break;
                }
            }
        }

        // Write changes back to file
        fileIn.close();
        try (FileOutputStream fileOut = new FileOutputStream(filePath.toFile())) {
            workbook.write(fileOut);
        }
        workbook.close();
    }
}




