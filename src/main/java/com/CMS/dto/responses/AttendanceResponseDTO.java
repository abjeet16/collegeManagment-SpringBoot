package com.CMS.dto.responses;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AttendanceResponseDTO {
    private double percentageCount;  // Percentage of attendance
    private List<SubjectAttendanceDTO> subjectAttendances = new ArrayList<>();  // List of attendance for each subject
}
