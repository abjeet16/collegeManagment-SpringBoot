package com.mynotes.repository;

import com.mynotes.models.Courses;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Courses, Integer> {
    Courses findByCourseName(String course_name);
}
