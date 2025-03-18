package com.mynotes.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectDTO {
    private int subjectId;
    private String subjectName;
    private String subjectCode;
    private String assignedTeacher;

    public  SubjectDTO(int subjectId, String subjectName, String subjectCode) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.subjectCode = subjectCode;
    }
}
