package com.mynotes.dto.requests;

import lombok.Data;

@Data
public class AssignTeacherDTO {

    private String subjectId;
    private String teacherId;
    private String courseName;
    private int BatchYear;
    private String Section;
}
