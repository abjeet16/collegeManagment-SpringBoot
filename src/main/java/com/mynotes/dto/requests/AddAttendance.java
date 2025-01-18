package com.mynotes.dto.requests;

import lombok.Data;

@Data
public class AddAttendance {
    private String studentId;
    private Long classId;
    private Boolean isPresent;
    private Long subjectId;
}
