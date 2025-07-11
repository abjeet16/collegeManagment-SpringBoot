package com.CMS.services;

import com.CMS.models.Courses;
import com.CMS.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    public void addCourse(Courses course) {
        courseRepository.save(course);
    }

    public List<Courses> getAllCourses() {
        return courseRepository.findAll();
    }

    public void deleteCourse(int courseId) {
        courseRepository.deleteById(courseId);
    }
}
