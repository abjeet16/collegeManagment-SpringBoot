package com.mynotes.controllers;

import com.mynotes.dto.requests.*;
import com.mynotes.dto.responses.*;
import com.mynotes.models.Attendance;
import com.mynotes.models.ClassEntity;
import com.mynotes.models.Courses;
import com.mynotes.models.StudentDetails;
import com.mynotes.services.*;
import com.mynotes.services.auth.MyCustomUserDetails;
import com.mynotes.services.auth.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("Admin")
public class AdminController {

    @Autowired
    private CourseService courseService;

    private final PasswordEncoder passwordEncoder;

    private final AttendanceService attendanceService;

    private final SubjectService subjectService;

    private final ClassService classService;

    private final UserService userService;

    private final StudentService studentService;

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

    /*@PutMapping("/assignTeacher")
    public ResponseEntity<String> updateTeacher(@RequestBody AssignTeacherDTO assignTeacherDTO) {
        try {
            assignedTeacherService.updateTeacher(assignTeacherDTO);
            return ResponseEntity.ok("Teacher updated successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }*/

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

    @GetMapping("/course/{courseId}/subjects")
    public ResponseEntity<List<SubjectDTO>> getSubjectsByCourseId(@PathVariable int courseId) {
        List<SubjectDTO> subjects = subjectService.getSubjectsByCourseId(courseId);
        return ResponseEntity.ok(subjects);
    }

    @GetMapping("/class/{classId}/students")
    public ResponseEntity<List<AllStudentsOfAClass>> getStudentsOfAClass(@PathVariable int classId) {
        List<AllStudentsOfAClass> students = studentService.getStudentsOfAClass(classId);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<StudentDetailsResponse> getStudentById(@PathVariable String studentId) {
        StudentDetailsResponse student = studentService.getStudentById(studentId);
        return ResponseEntity.ok(student);
    }

    @GetMapping("/attendence")
    public ResponseEntity<List<StudentsSubjectAttendance>> getStudentAttendence(@RequestParam String studentId , @RequestParam String subjectId) {
        List<StudentsSubjectAttendance> student = attendanceService.getStudentAttendence(studentId,subjectId);
        return ResponseEntity.ok(student);
    }

    @PutMapping("/updateAttendance")
    public ResponseEntity<String> updateAttendance(@RequestBody AttendanceUpdateRequest request) {
        return ResponseEntity.ok(attendanceService.updateAttendance(
                request.getStudentId(), request.getSubjectId(), request.getDate(), request.isStatus()
        ));
    }

    @GetMapping("/Teachers")
    public ResponseEntity<List<AllTeachersDTO>> getAllTeachers() {
        return ResponseEntity.ok(userService.getAllTeachers());
    } // ResponseEntity<List<AllTeachersDTO>>

    @GetMapping("/Teacher/{teacherId}/details")
    public ResponseEntity<TeacherDetailResponse> getTeacherById(@PathVariable String teacherId) {
        return ResponseEntity.ok(userService.getTeacherById(teacherId));
    }
}
