package com.mynotes.repository;

import com.mynotes.dto.responses.StudentsSubjectAttendance;
import com.mynotes.dto.responses.SubjectAndDateDTO;
import com.mynotes.models.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
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

    @Modifying
    @Transactional
    @Query("DELETE FROM Attendance a WHERE a.classId = :classId")
    void deleteByClassId(int classId);

    @Modifying
    @Transactional
    @Query("Delete from Attendance a where a.studentId = :studentId")
    void deleteByStudentId(@Param("studentId") String studentId);

    List<Attendance> findByClassIdAndSubjectId(Long classId, Long subjectId);

    boolean existsByClassIdAndSubjectIdAndSchedulePeriodAndAttendanceDate(Long classId, Long subjectId, int schedulePeriod, LocalDate now);
}

