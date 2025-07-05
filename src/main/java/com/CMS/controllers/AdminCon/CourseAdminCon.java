package com.CMS.controllers.AdminCon;

import com.CMS.dto.requests.AddCourseReqDTO;
import com.CMS.models.Courses;
import com.CMS.services.CourseService;
import com.CMS.services.auth.MyCustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("Admin")
@RequiredArgsConstructor
public class CourseAdminCon {

    private final CourseService courseService;

    private final PasswordEncoder passwordEncoder;

    // Get all courses
    @GetMapping("/courses")
    public ResponseEntity<List<Courses>> getAllCourses() {
        List<Courses> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }//END OF GET ALL COURSES

    // Add course
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
    }//END OF ADD COURSE

    // Delete course
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
    }//END OF DELETE COURSE
    // Delete Course END
}
