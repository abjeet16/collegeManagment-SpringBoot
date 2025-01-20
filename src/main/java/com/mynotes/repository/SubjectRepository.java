package com.mynotes.repository;

import com.mynotes.models.Courses;
import com.mynotes.models.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<Subject,Integer> {
    boolean existsBySubjectId(String subjectId);

    Subject findBySubjectId(String subjectId);

    List<Subject> findByCourses(Courses course);
}
