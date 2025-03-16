package com.mynotes.dto.responses;

import com.mynotes.models.Courses;
import lombok.Data;

@Data
public class classesDTO {
    private int classID;
    private Courses course;
    private int year;
    private String section;
}
