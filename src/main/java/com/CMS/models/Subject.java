package com.CMS.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true)
    private String subjectId;

    private String subjectName;

    private int semester;

    @ManyToOne // Assuming many subjects can be linked to one course
    @JoinColumn(name = "course_id") // The foreign key in the `Subject` table
    private Courses courses;
}
