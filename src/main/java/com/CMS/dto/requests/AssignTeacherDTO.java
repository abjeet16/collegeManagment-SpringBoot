package com.CMS.dto.requests;

import lombok.Data;

@Data
public class AssignTeacherDTO {
    private String subjectId;
    private String teacherId;
    private  int classId;
}
