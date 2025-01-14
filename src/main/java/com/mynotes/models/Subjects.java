package com.mynotes.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class Subjects {

    @Id
    private String subjectId;

    private String subjectName;

    @ManyToOne // Assuming many subjects can be linked to one course
    @JoinColumn(name = "course_id") // The foreign key in the `Subject` table
    private Courses courses;
}
