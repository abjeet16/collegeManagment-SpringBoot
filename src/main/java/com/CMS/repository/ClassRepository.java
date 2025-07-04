package com.CMS.repository;

import com.CMS.models.ClassEntity;
import com.CMS.models.Courses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassRepository extends JpaRepository<ClassEntity, Integer> {
    ClassEntity findBySectionAndBatchYearAndCourse(String section, int batchYear, Courses course);

    List<ClassEntity> getClassesByCourseId(int courseId);

    @Query("SELECT c.currentSemester FROM ClassEntity c WHERE c.id = :classId")
    int getCurrentSemester(int classId);

    @Modifying
    @Query("UPDATE ClassEntity c SET c.currentSemester = c.currentSemester + 1")
    void promoteAllClasses();

    @Modifying
    @Query("UPDATE ClassEntity c SET c.currentSemester = c.currentSemester - 1 Where c.currentSemester > 1")
    void demoteAllClasses();

    boolean existsBySectionAndCourseAndCurrentSemester(String section, Courses course, int currentSemester);
}
