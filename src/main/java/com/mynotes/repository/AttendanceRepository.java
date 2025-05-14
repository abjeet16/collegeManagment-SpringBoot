package com.mynotes.repository;

import com.mynotes.dto.responses.AttendanceResponseDTO;
import com.mynotes.dto.responses.StudentsSubjectAttendance;
import com.mynotes.dto.responses.SubjectAndDateDTO;
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

    @Query("SELECT (SUM(CASE WHEN a.status = 'PRESENT' THEN 1 ELSE 0 END) * 100) / COUNT(a) " +
            "FROM Attendance a WHERE a.studentId = :studentId")
    Integer getAttendancePercentageByStudentId(@Param("studentId") String studentId);

    List<Attendance> findBySubjectIdAndStudentIdAndStatus(long subjectId, String studentId, AttendanceStatus attendanceStatus);


    @Query("SELECT a.subjectId, COUNT(a), SUM(CASE WHEN a.status = 'PRESENT' THEN 1 ELSE 0 END) " +
            "FROM Attendance a WHERE a.studentId = :studentId AND a.classId = :classId " +
            "GROUP BY a.subjectId")
    List<Object[]> findAttendanceSummaryByStudent(@Param("studentId") String studentId,
                                                  @Param("classId") Long classId);

    @Query("SELECT new com.mynotes.dto.responses.SubjectAndDateDTO(a.schedulePeriod, a.attendanceDate) " +
            "FROM Attendance a WHERE a.subjectId = :subjectId AND a.studentId = :userId AND a.status = 'ABSENT'")
    List<SubjectAndDateDTO> findAbsentRecords(@Param("subjectId") Long subjectId, @Param("userId") String userId);

    @Query("SELECT new com.mynotes.dto.responses.StudentsSubjectAttendance(a.id, a.attendanceDate, a.status,a.schedulePeriod) " +
            "FROM Attendance a " +
            "WHERE a.subjectId = :subjectId AND a.studentId = :studentId")
    List<StudentsSubjectAttendance> findBySubjectIdAndStudentId(
            @Param("subjectId") Long subjectId,
            @Param("studentId") String studentId
    );

    Attendance findBySubjectIdAndStudentIdAndAttendanceDate(long subjectId, String studentId, LocalDate date);

    @Query("DELETE FROM Attendance a WHERE a.classId = :classId")
    void deleteByClassId(int classId);
}

