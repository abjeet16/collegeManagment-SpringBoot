package com.mynotes.controllers;

import com.mynotes.dto.requests.AddClassReqDTO;
import com.mynotes.dto.requests.AddCourseReqDTO;
import com.mynotes.dto.requests.AddSubjectReqDTO;
import com.mynotes.models.Courses;
import com.mynotes.services.ClassService;
import com.mynotes.services.CourseService;
import com.mynotes.services.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("Admin")
public class AdminController {

    @Autowired
    private CourseService courseService;

    private final SubjectService subjectService;

    private final ClassService classService;

    @PostMapping("/add_course")
    private ResponseEntity<String> addCourse(@RequestBody AddCourseReqDTO addCourseReqDTO) {
        try {
            Courses courses = new Courses();
            courses.setCourseName(addCourseReqDTO.getCourseName());
            courseService.addCourse(courses);
            return ResponseEntity.ok("Course added successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add course: " + e.getMessage());
        }
    }

    @PostMapping("/add_subject")
    private ResponseEntity<String> addSubject(@RequestBody AddSubjectReqDTO addSubjectReqDTO) {
        try {
            subjectService.addSubject(addSubjectReqDTO);
            return ResponseEntity.ok("Subject added successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add subject: " + e.getMessage());
        }
    }

    @PostMapping("/add_class")
    public ResponseEntity<String> addClass(@RequestBody AddClassReqDTO addClassReqDTO) {
        try {
            classService.addClass(addClassReqDTO);
            return ResponseEntity.ok("Class added successfully!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }
}
