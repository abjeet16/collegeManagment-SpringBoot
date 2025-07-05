package com.CMS.controllers;

import com.CMS.dto.responses.AttendanceResponseDTO;
import com.CMS.dto.responses.SubjectAndDateDTO;
import com.CMS.services.AttendanceService;
import com.CMS.services.auth.MyCustomUserDetails;
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

    @GetMapping("my_overall_attendance")
    public ResponseEntity<AttendanceResponseDTO> getStudentsOverAllAttendance() {
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

        if (user == null) {
            return ResponseEntity.status(401).body(null);
        }

        List<SubjectAndDateDTO> absentStudents = attendanceService.getSubjectAbsent(subjectId, user.getUserId());
        return ResponseEntity.ok(absentStudents);
    }//
}
