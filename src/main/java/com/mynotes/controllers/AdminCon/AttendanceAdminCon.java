package com.mynotes.controllers.AdminCon;

import com.mynotes.dto.requests.AddAttendance;
import com.mynotes.dto.requests.AttendanceRequest;
import com.mynotes.dto.requests.AttendanceSheetRequest;
import com.mynotes.dto.requests.AttendanceUpdateRequest;
import com.mynotes.dto.responses.AttendanceTableResponse;
import com.mynotes.dto.responses.StudentsSubjectAttendance;
import com.mynotes.enums.AttendanceStatus;
import com.mynotes.models.Attendance;
import com.mynotes.services.AttendanceService;
import com.mynotes.services.ClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("Admin")
public class AttendanceAdminCon {

    private final AttendanceService attendanceService;

    private final ClassService classService;

    // Get student attendance
    @GetMapping("/attendance/student/{studentId}/subject/{subjectId}")
    public ResponseEntity<List<StudentsSubjectAttendance>> getStudentAttendance(@PathVariable String studentId , @PathVariable String subjectId) {
        List<StudentsSubjectAttendance> student = attendanceService.getStudentAttendance(studentId,subjectId);
        return ResponseEntity.ok(student);
    }//END OF GET STUDENT ATTENDANCE

    @PutMapping("/updateAttendance")
    public ResponseEntity<String> updateAttendance(@RequestBody AttendanceUpdateRequest request) {
        return ResponseEntity.ok(attendanceService.updateAttendance(
                request.getId(), request.isStatus()
        ));
    }

    @PostMapping("/attendanceTillDate")
    public ResponseEntity<List<AttendanceTableResponse>> getAttendanceTillDate(@RequestBody AttendanceSheetRequest attendanceSheetRequest) {
        List<AttendanceTableResponse> attendanceTable = attendanceService.getAttendanceTillDate(attendanceSheetRequest);
        return ResponseEntity.ok(attendanceTable);
    }

    @PostMapping("/attendance")
    public ResponseEntity<String> markAttendance(@RequestBody AttendanceRequest request) {
        // Start timer
        long startTime = System.currentTimeMillis();

        // Validate input
        List<AddAttendance> attendanceRecords = request.getAttendanceRecords();
        if (attendanceRecords == null || attendanceRecords.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Attendance records cannot be empty");
        }

        if (attendanceService.existsByClassIdAndSubjectIdAndSchedulePeriodAndAttendanceDate(request.getClassId(), request.getSubjectId(), request.getSchedulePeriod(), request.getAttendanceDate())) {
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
            attendance.setAttendanceDate(request.getAttendanceDate());
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

}
