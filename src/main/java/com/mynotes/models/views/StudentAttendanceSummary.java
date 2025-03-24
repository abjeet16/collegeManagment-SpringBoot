package com.mynotes.models.views;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "student_attendance_summary")
@Data
public class StudentAttendanceSummary {
    @EmbeddedId
    private AttendanceSummaryId id;

    @Column(name = "name")  // Concatenated first_name + last_name
    private String name;

    @Column(name = "total_present")
    private Long totalPresent;

    @Column(name = "total_absent")
    private Long totalAbsent;

    @Column(name = "total_classes")
    private Long totalClasses;

    @Column(name = "attendance_percentage")
    private Double attendancePercentage;  // Stores percentage
}




