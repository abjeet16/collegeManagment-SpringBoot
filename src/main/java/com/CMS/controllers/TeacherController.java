package com.CMS.controllers;

import com.CMS.dto.requests.AddAttendance;
import com.CMS.dto.requests.AttendanceRequest;
import com.CMS.dto.responses.AllStudentsOfAClass;
import com.CMS.dto.responses.ClassWithSubjectDto;
import com.CMS.dto.responses.studentsAttendenceSummuryDTO;
import com.CMS.enums.AttendanceStatus;
import com.CMS.models.AssignedTeacher;
import com.CMS.models.Attendance;
import com.CMS.services.AssignedTeacherService;
import com.CMS.services.AttendanceService;
import com.CMS.services.ClassService;
import com.CMS.services.StudentService;
import com.CMS.services.auth.MyCustomUserDetails;
import com.CMS.services.viewServices.StudentAttendanceSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    private final ClassService classService;
    private final StudentAttendanceSummaryService studentAttendanceSummaryService;

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
            @RequestBody AttendanceRequest request) {

        // Start timer
        long startTime = System.currentTimeMillis();

        // Authenticate user
        MyCustomUserDetails user = (MyCustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
        }

        // Validate input
        List<AddAttendance> attendanceRecords = request.getAttendanceRecords();
        if (attendanceRecords == null || attendanceRecords.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Attendance records cannot be empty");
        }

        if (attendanceService.existsByClassIdAndSubjectIdAndSchedulePeriodAndAttendanceDate(request.getClassId(), request.getSubjectId(), request.getSchedulePeriod(),LocalDate.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Attendance already marked");
        }

        // Convert to list of Attendance entities in bulk
        List<Attendance> attendanceList = new ArrayList<>(attendanceRecords.size());

        for (AddAttendance record : attendanceRecords) {
            Attendance attendance = new Attendance();
            attendance.setStudentId(record.getStudentId());
            attendance.setClassId(request.getClassId());
            attendance.setSubjectId(request.getSubjectId());
            attendance.setSemester(classService.getCurrentSemester(Math.toIntExact(request.getClassId())));
            attendance.setSchedulePeriod(request.getSchedulePeriod());
            attendance.setAttendanceDate(LocalDate.now());
            attendance.setStatus(Boolean.TRUE.equals(record.getIsPresent())
                    ? AttendanceStatus.PRESENT
                    : AttendanceStatus.ABSENT);
            attendanceList.add(attendance);
        }

        try {
            // **Use batch insert for better performance**
            attendanceService.saveAllBatch(attendanceList);

            // End timer
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            // Print execution time
            System.out.println("Execution Time: " + executionTime + " ms");

            return ResponseEntity.ok("Attendance marked successfully! Execution Time: " + executionTime + " ms");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to mark attendance due to an internal error.");
        }
    }

    @GetMapping("/{classId}/students")
    public ResponseEntity<List<AllStudentsOfAClass>> getStudentsOfAClass(@PathVariable int classId) {
        List<AllStudentsOfAClass> students = studentService.getStudentsOfAClass(classId);
        return ResponseEntity.ok(students);
    }

    @GetMapping("attendance/{classId}/{subjectId}")
    public List<studentsAttendenceSummuryDTO> getSummary(
            @PathVariable Long classId,
            @PathVariable Long subjectId){
        return studentAttendanceSummaryService.getAttendanceSummary(classId, subjectId);
    }
}

