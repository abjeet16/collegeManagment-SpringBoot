package com.CMS.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "courses")
public class Courses {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "course_name", unique = true, nullable = false)
    private String courseName;

    public void setCourseName(String courseName) {
        this.courseName = courseName.toUpperCase();
    }
}
