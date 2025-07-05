package com.CMS.dto.responses;

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
    private int semester;

    public  SubjectDTO(int subjectId, String subjectName, String subjectCode,int semester) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.subjectCode = subjectCode;
        this.semester = semester;
    }
}
