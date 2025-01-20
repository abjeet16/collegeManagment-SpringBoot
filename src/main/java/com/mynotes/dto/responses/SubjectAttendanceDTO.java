package com.mynotes.dto.responses;

import lombok.Data;

@Data
public class SubjectAttendanceDTO {
    private String subjectName;   // Name of the subject
    private int SubjectId;
    private long totalClasses;     // Total number of classes
    private long attendedClasses;  // Number of classes attended by the student
    private double attendancePercentage; // Percentage of attendance for the subject
}
