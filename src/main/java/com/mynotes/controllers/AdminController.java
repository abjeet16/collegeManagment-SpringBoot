package com.mynotes.controllers;

import com.mynotes.dto.requests.AddCourseReqDTO;
import com.mynotes.models.Courses;
import com.mynotes.services.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("Admin")
public class AdminController {

    @Autowired
    private CourseService courseService;

    @PostMapping("/add_course")
    private void addCourse(@RequestBody AddCourseReqDTO addCourseReqDTO) {
        Courses courses = new Courses();
        courses.setCourseName(addCourseReqDTO.getCourseName());
        courseService.addCourse(courses);
    }
}
