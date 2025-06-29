package com.mynotes.dto.requests;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Data
public class AttendanceRequest {
    Long classId;
    Long subjectId;
    int schedulePeriod;
    LocalDate attendanceDate = LocalDate.now();
    private List<AddAttendance> attendanceRecords;
}
