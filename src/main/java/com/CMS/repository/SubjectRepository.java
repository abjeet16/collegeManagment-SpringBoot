package com.CMS.repository;

import com.CMS.dto.responses.SubjectDTO;
import com.CMS.models.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<Subject,Integer> {
    Subject findBySubjectId(String subjectId);

    @Query("SELECT new com.mynotes.dto.responses.SubjectDTO(s.id, s.subjectName, s.subjectId ,s.semester) FROM Subject s WHERE s.courses.id = :courseId")
    List<SubjectDTO> findSubjectsByCourseId(@Param("courseId") int courseId);

    @Query("SELECT new com.mynotes.dto.responses.SubjectDTO(s.id, s.subjectName, s.subjectId ,s.semester) FROM Subject s WHERE s.semester = :currentSemester And s.courses.id = :courseId")
    List<SubjectDTO> findSubjectsOfAClass(int courseId, int currentSemester);

    @Query("SELECT s.subjectName FROM Subject s WHERE s.id = :subjectId")
    String getSubjectNameById(Long subjectId);
}
