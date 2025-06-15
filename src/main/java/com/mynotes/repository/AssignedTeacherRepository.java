package com.mynotes.repository;

import com.mynotes.models.AssignedTeacher;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AssignedTeacherRepository extends JpaRepository<AssignedTeacher, Integer> {
    @Query("SELECT at FROM AssignedTeacher at JOIN FETCH at.teacher t JOIN FETCH at.classEntity c JOIN FETCH at.subject s WHERE t.id = :teacherId")
    List<AssignedTeacher> findAssignedTeachersByTeacherId(@Param("teacherId") int teacherId);

    @Modifying
    @Transactional
    @Query("UPDATE AssignedTeacher at " +
            "SET at.teacher = (SELECT t FROM TeacherDetails t WHERE t.user.Uucms_id = :teacherUucmsId) " +
            "WHERE at.classEntity.id = :classId AND at.subject.id = (SELECT s.id FROM Subject s WHERE s.subjectId = :subjectId)") // ✅ Fetch id dynamically
    void updateTeacher(@Param("teacherUucmsId") String teacherUucmsId,
                       @Param("classId") int classId,
                       @Param("subjectId") String subjectId);

    @Query("SELECT CONCAT(at.teacher.user.first_name, ' ', at.teacher.user.last_name) " +
            "FROM AssignedTeacher at " +
            "WHERE at.subject.id = :subjectId AND at.classEntity.id = :classId")
    String getAssignedTeacherFullNameBySubjectIdAndClassId(@Param("subjectId") int subjectId,
                                                           @Param("classId") int classId);
}
