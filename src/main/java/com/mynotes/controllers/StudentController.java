package com.mynotes.controllers;

import com.mynotes.dto.responses.AttendanceResponseDTO;
import com.mynotes.dto.responses.SubjectAndDateDTO;
import com.mynotes.models.Attendance;
import com.mynotes.services.AttendanceService;
import com.mynotes.services.auth.MyCustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/student")
public class StudentController {

    private final AttendanceService attendanceService;

    @GetMapping("/my_total_attendences")
    public ResponseEntity<Integer> getMyTotalAttendences() {
        MyCustomUserDetails user = (MyCustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Check if the user is authenticated
        if (user == null) {
            return ResponseEntity.status(401).body(null);
        }
        int totalAttendences = attendanceService.getTotalAttendencePercentage(user.getUserId());
        return ResponseEntity.ok(totalAttendences);
    }

    @GetMapping("my_overall_attendences")
    public ResponseEntity<AttendanceResponseDTO> testing() {
        MyCustomUserDetails user = (MyCustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Check if the user is authenticated
        if (user == null) {
            return ResponseEntity.status(401).body(null);
        }
        AttendanceResponseDTO subjects = attendanceService.getAllMyAttendance(user.getUserId());
        return ResponseEntity.ok(subjects);
    }
    @GetMapping("/subject/{subjectId}/absents")
    public ResponseEntity<List<SubjectAndDateDTO>> getSubjectAbsent(@PathVariable String subjectId) {
        MyCustomUserDetails user = (MyCustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // Check if the user is authenticated
        if (user == null) {
            return ResponseEntity.status(401).body(null);
        }
        List<Attendance> attendances = attendanceService.getSubjectAbsent(subjectId, user.getUserId());

        List<SubjectAndDateDTO> absentStudents = attendances.stream().map(attendance -> new SubjectAndDateDTO(attendance.getSchedulePeriod(), attendance.getAttendanceDate())).toList();
        return ResponseEntity.ok(absentStudents);
    }
}
