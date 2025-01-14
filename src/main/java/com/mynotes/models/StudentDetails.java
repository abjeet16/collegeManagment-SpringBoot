package com.mynotes.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Entity
public class StudentDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // Foreign key column for User
    private User user;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Courses course;

    @Column(name = "section_name") // Optional: Customize column name in the database
    private String section;

    @Column(name = "batch_year") // Optional: Customize column name in the database
    private int batchYear;
}

