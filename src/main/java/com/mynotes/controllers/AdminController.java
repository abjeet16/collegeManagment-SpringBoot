package com.mynotes.controllers;

import com.mynotes.dto.requests.AddClassReqDTO;
import com.mynotes.dto.requests.AddCourseReqDTO;
import com.mynotes.dto.requests.AddSubjectReqDTO;
import com.mynotes.dto.requests.AssignTeacherDTO;
import com.mynotes.models.ClassEntity;
import com.mynotes.models.Courses;
import com.mynotes.services.AssignedTeacherService;
import com.mynotes.services.ClassService;
import com.mynotes.services.CourseService;
import com.mynotes.services.SubjectService;
import com.mynotes.services.auth.MyCustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("Admin")
public class AdminController {

    @Autowired
    private CourseService courseService;

    private final PasswordEncoder passwordEncoder;

    private final SubjectService subjectService;

    private final ClassService classService;

    private final AssignedTeacherService assignedTeacherService;

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

    @PostMapping("/assignTeacher")
    public ResponseEntity<String> assignTeacher(@RequestBody AssignTeacherDTO assignTeacherDTO) {
        try {
            assignedTeacherService.assignTeacherToSubject(assignTeacherDTO);
            return ResponseEntity.ok("Teacher assigned successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/courses")
    public ResponseEntity<List<Courses>> getAllCourses() {
        List<Courses> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }

    @DeleteMapping("/deleteCourse/{courseId}")
    public ResponseEntity<String> deleteCourse(@PathVariable int courseId, @RequestParam String password) {
        MyCustomUserDetails user = (MyCustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        // Verify the password instead of decoding
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
        }

        try {
            courseService.deleteCourse(courseId);
            return ResponseEntity.ok("Course deleted successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/course/{courseId}/classes")
    public ResponseEntity<List<ClassEntity>> getClassesByCourseId(@PathVariable int courseId) {
        List<ClassEntity> classes = classService.getClassesByCourseId(courseId);
        return ResponseEntity.ok(classes);
    }
}
