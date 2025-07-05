package com.CMS.repository;

import com.CMS.models.Courses;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Courses, Integer> {
    Courses findByCourseName(String course_name);
}
