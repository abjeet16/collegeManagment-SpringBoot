package com.CMS.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "classes")
@Data
@NoArgsConstructor
public class ClassEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    @NotNull(message = "Course cannot be null")
    private Courses course;

    @NotNull(message = "Batch year is required")
    private int batchYear;

    @NotBlank(message = "Section is required")
    private String section; // For example: A, B, C

    @NotNull(message = "Current semester is required")
    private int currentSemester;

    public void setSection(@NotBlank(message = "Section is required") String section) {
        this.section = section.toUpperCase();
    }
}

