package com.mynotes.repository;

import com.mynotes.models.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    // Get attendance for a specific student
    List<Attendance> findByStudentId(Long studentId);

    // Get attendance for a specific class and date
    @Query("SELECT a FROM Attendance a WHERE a.classId = :classId AND a.attendanceDate = :date")
    List<Attendance> findByClassAndDate(Long classId, LocalDate date);

    // Attendance summary for a specific subject and class
    @Query("SELECT a.status, COUNT(a) FROM Attendance a WHERE a.subjectId = :subjectId AND a.classId = :classId GROUP BY a.status")
    List<Object[]> getSummaryBySubjectAndClass(Long subjectId, Long classId);
}

