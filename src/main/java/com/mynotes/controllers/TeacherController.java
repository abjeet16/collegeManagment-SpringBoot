package com.mynotes.controllers;

import com.mynotes.dto.requests.AddAttendance;
import com.mynotes.dto.responses.AllStudentsOfAClass;
import com.mynotes.dto.responses.ClassWithSubjectDto;
import com.mynotes.enums.AttendanceStatus;
import com.mynotes.models.AssignedTeacher;
import com.mynotes.models.Attendance;
import com.mynotes.models.ClassEntity;
import com.mynotes.models.StudentDetails;
import com.mynotes.services.AssignedTeacherService;
import com.mynotes.services.AttendanceService;
import com.mynotes.services.StudentService;
import com.mynotes.services.auth.MyCustomUserDetails;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/teacher")
public class TeacherController {

    private final AssignedTeacherService assignedTeacherService;
    private final AttendanceService attendanceService;
    private final StudentService studentService;

    @GetMapping("/my_classes")
    public ResponseEntity<List<ClassWithSubjectDto>> getMyClasses() {
        // Get the current authenticated user
        MyCustomUserDetails user = (MyCustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Check if the user is authenticated
        if (user == null) {
            return ResponseEntity.status(401).body(null);
        }

        System.out.println(user.getUserId());
        List<AssignedTeacher> assignedTeachers = assignedTeacherService.getMyClasses(user.getUserId());
        List<ClassWithSubjectDto> classes = new ArrayList<>();
        for (AssignedTeacher assignedTeacher : assignedTeachers) {
            classes.add(new ClassWithSubjectDto(assignedTeacher.getClassEntity(), assignedTeacher.getSubject()));
        }
        return ResponseEntity.ok(classes);
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

    @GetMapping("/{classId}/students")
    public ResponseEntity<List<AllStudentsOfAClass>> getStudentsOfAClass(@PathVariable int classId) {
        List<AllStudentsOfAClass> students = studentService.getStudentsOfAClass(classId);
        return ResponseEntity.ok(students);
    }
}

