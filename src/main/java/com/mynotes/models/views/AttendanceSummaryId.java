package com.mynotes.models.views;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceSummaryId implements Serializable {

    @Column(name = "student_id")
    private String studentId;

    @Column(name = "class_id")
    private Long classId;

    @Column(name = "subject_id")
    private Long subjectId;
}
