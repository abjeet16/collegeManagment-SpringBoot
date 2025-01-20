package com.mynotes.controllers;

import com.mynotes.dto.requests.AddAttendance;
import com.mynotes.enums.AttendanceStatus;
import com.mynotes.models.AssignedTeacher;
import com.mynotes.models.Attendance;
import com.mynotes.services.AssignedTeacherService;
import com.mynotes.services.AttendanceService;
import com.mynotes.services.auth.MyCustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/teacher")
public class TeacherController {

    private final AssignedTeacherService assignedTeacherService;
    private final AttendanceService attendanceService;

    @GetMapping("/my_classes")
    public ResponseEntity<List<AssignedTeacher>> getMyClasses() {
        // Get the current authenticated user
        MyCustomUserDetails user = (MyCustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Check if the user is authenticated
        if (user == null) {
            return ResponseEntity.status(401).body(null);
        }

        System.out.println(user.getUserId());
        List<AssignedTeacher> assignedTeachers = assignedTeacherService.getMyClasses(user.getUserId());
        return ResponseEntity.ok(assignedTeachers);
    }

    @PostMapping("/mark_attendance")
    public ResponseEntity<String> markAttendance(
            @RequestParam Long classId,
            @RequestParam Long subjectId,
            @RequestParam int schedulePeriod,
            @RequestBody List<AddAttendance> attendanceRecords) {

        // Get the current authenticated user
        MyCustomUserDetails user = (MyCustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Check if the user is authenticated
        if (user == null) {
            return ResponseEntity.status(401).body("Unauthorized access");
        }

        // Validate and save attendance records
        attendanceRecords.forEach(record -> {
            Attendance attendance = new Attendance();
            attendance.setStudentId(record.getStudentId());
            attendance.setTeacherId(user.getUserId()); // From authenticated user
            attendance.setClassId(classId); // Common value
            attendance.setSubjectId(subjectId); // Common value
            attendance.setSchedulePeriod(schedulePeriod); // Common value
            attendance.setAttendanceDate(LocalDate.now()); // Common value

            // Set attendance status
            if (Boolean.TRUE.equals(record.getIsPresent())) {
                attendance.setStatus(AttendanceStatus.PRESENT);
            } else {
                attendance.setStatus(AttendanceStatus.ABSENT);
            }

            attendanceService.saveAttendance(attendance); // Save each attendance record
        });

        return ResponseEntity.ok("Attendance marked successfully!");
    }
}

