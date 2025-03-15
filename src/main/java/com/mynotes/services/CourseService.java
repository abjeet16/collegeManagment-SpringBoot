package com.mynotes.services;

import com.mynotes.dto.requests.AddCourseReqDTO;
import com.mynotes.models.Courses;
import com.mynotes.repository.CourseRepository;
import org.checkerframework.checker.units.qual.C;
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
}
