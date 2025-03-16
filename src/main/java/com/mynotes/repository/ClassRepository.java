package com.mynotes.repository;

import com.mynotes.dto.responses.ClassesDTO;
import com.mynotes.models.ClassEntity;
import com.mynotes.models.Courses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassRepository extends JpaRepository<ClassEntity, Integer> {
    ClassEntity findBySectionAndBatchYearAndCourse(String section, int batchYear, Courses course);


    @Query("SELECT new com.mynotes.dto.responses.ClassesDTO(c.id, c.course, (YEAR(CURRENT_DATE)+1 - c.batchYear), c.section) " +
            "FROM ClassEntity c WHERE c.course.id = :courseId")
    List<ClassesDTO> getClassesByCourseId(@Param("courseId") int courseId);

}
