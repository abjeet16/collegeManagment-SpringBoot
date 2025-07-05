package com.CMS.models;

import com.CMS.enums.AttendanceStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "attendance")
@Data
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private String studentId;

    @Column(name = "class_id", nullable = false)
    private Long classId;

    @Column(name = "semester", nullable = false)
    private int semester;

    @Column(name = "subject_id", nullable = false)
    private Long subjectId;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AttendanceStatus status;

    @Column(name = "schedule_period", nullable = false)
    private int schedulePeriod;
}

