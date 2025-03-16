package com.mynotes.dto.responses;

import com.mynotes.models.Courses;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ClassesDTO {
    private int classID;
    private Courses course;
    private int year;
    private String section;

    public ClassesDTO(int classID, Courses course, int year, String section) {
        this.classID = classID;
        this.course = course;
        this.year = year;
        this.section = section;
    }
}
