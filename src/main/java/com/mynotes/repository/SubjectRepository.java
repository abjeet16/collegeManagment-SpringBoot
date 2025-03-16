package com.mynotes.repository;

import com.mynotes.dto.responses.SubjectDTO;
import com.mynotes.models.Courses;
import com.mynotes.models.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<Subject,Integer> {
    boolean existsBySubjectId(String subjectId);

    Subject findBySubjectId(String subjectId);

    List<Subject> findByCourses(Courses course);

    @Query("SELECT new com.mynotes.dto.responses.SubjectDTO(s.id, s.subjectName, s.subjectId) FROM Subject s WHERE s.courses.id = :courseId")
    List<SubjectDTO> findSubjectsByCourseId(@Param("courseId") int courseId);
}
