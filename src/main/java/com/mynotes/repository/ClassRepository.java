package com.mynotes.repository;

import com.mynotes.models.ClassEntity;
import com.mynotes.models.Courses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassRepository extends JpaRepository<ClassEntity, Integer> {
    ClassEntity findBySectionAndBatchYearAndCourse(String section, int batchYear, Courses course);
}
