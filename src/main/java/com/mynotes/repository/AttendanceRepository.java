package com.mynotes.repository;

import com.mynotes.dto.responses.AttendanceResponseDTO;
import com.mynotes.enums.AttendanceStatus;
import com.mynotes.models.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    // Get attendance count for present days by student ID
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.studentId = :studentId AND a.status = 'PRESENT'")
    Long countPresentByStudentId(@Param("studentId") String studentId);

    // Get attendance count for absent days by student ID
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.studentId = :studentId AND a.status = 'ABSENT'")
    Long countAbsentByStudentId(@Param("studentId") String studentId);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.studentId = :studentId AND a.subjectId = :subjectId AND a.classId = :classId AND a.status = :status")
    long countByStudentAndSubjectAndClassAndStatus(String studentId, Long subjectId, Long classId, AttendanceStatus status);

    long countByStudentIdAndSubjectIdAndClassId(String studentId, Long subjectId, Long classId);

    List<Attendance> findBySubjectIdAndStudentIdAndStatus(long subjectId, String studentId, AttendanceStatus attendanceStatus);
}

