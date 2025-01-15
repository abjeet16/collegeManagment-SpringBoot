package com.mynotes.repository;

import com.mynotes.models.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectRepository extends JpaRepository<Subject,Integer> {
    boolean existsBySubjectId(String subjectId);
}
