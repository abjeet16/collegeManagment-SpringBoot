package com.mynotes.dto.requests;

import com.mynotes.models.Courses;
import lombok.Data;

@Data
public class AddClassReqDTO {

    private String course;

    private int batchYear;

    private String section;

    private int currentSemester;
}
