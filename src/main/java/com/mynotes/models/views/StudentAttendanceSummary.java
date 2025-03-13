package com.mynotes.models.views;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "student_attendance_summary")  // Match the view name
@Data
public class StudentAttendanceSummary {

    @Column(name = "student_id")
    private String studentId;

    @Column(name = "class_id")
    private Long classId;

    @Column(name = "subject_id")
    private Long subjectId;

    @Column(name = "total_present")
    private Long totalPresent;

    @Column(name = "total_absent")
    private Long totalAbsent;

    @Column(name = "total_classes")
    private Long totalClasses;
}

