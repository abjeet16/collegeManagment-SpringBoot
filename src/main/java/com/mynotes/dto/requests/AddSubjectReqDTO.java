package com.mynotes.dto.requests;

import com.mynotes.models.Courses;
import lombok.Data;

@Data
public class AddSubjectReqDTO {

    private String subjectId;

    private String subjectName;

    private String course;
}
