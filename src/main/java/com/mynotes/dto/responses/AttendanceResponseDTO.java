package com.mynotes.dto.responses;

import lombok.Data;
import java.util.List;

@Data
public class AttendanceResponseDTO {
    private double percentageCount;  // Percentage of attendance
    private List<SubjectAttendanceDTO> subjectAttendances;  // List of attendance for each subject
}
