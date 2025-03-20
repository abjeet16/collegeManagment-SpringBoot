package com.mynotes.controllers;

import com.mynotes.dto.requests.*;
import com.mynotes.dto.responses.*;
import com.mynotes.enums.Role;
import com.mynotes.models.*;
import com.mynotes.services.*;
import com.mynotes.services.auth.MyCustomUserDetails;
import com.mynotes.services.auth.UserService;
import jakarta.validation.Valid;
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

    @PutMapping("/assignTeacher")
    public ResponseEntity<String> updateTeacher(@RequestBody AssignTeacherDTO assignTeacherDTO , @RequestParam String password) {
        MyCustomUserDetails user = (MyCustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        // Verify the password instead of decoding
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
        }
        try {
            assignedTeacherService.updateTeacher(assignTeacherDTO);
            return ResponseEntity.ok("Teacher updated successfully!");
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

    @GetMapping("/course/{courseId}/class/{classId}/subjects")
    public ResponseEntity<List<SubjectDTO>> getSubjectsByCourseId(@PathVariable int courseId,@PathVariable int classId) {
        List<SubjectDTO> subjects = subjectService.getSubjectsByCourseId(courseId,classId);
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

    @PostMapping("/addUser")
    public ResponseEntity<String> addUser(@Valid @RequestBody addUserRequest request) {

        // Check if email already exists
        if (userService.doesWithEmailExist(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Email is already registered.");
        }

        // Hash the password securely
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // Validate role and convert it to an enum
        Role userRole;
        try {
            userRole = Role.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Invalid role specified.");
        }

        // Store user in the database
        int result = userService.signUpUser(
                request.getUserName(),
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                hashedPassword,
                userRole.toString(),
                request.getPhone()
        );

        // Check if the user was successfully added
        if (result != 1) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: User registration failed.");
        }

        // Return success response
        return ResponseEntity.status(HttpStatus.CREATED).body("User Sign-Up Successful!");
    }

    @PostMapping("/addTeacher")
    public ResponseEntity<String> addTeacher(@Valid @RequestBody AddTeacherRequest request) {
        // Check if email already exists
        if (userService.doesWithEmailExist(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Email is already registered.");
        }

        // Hash the password securely
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User(request.getUserName(),request.getFirstName(),request.getLastName(),request.getEmail(),Long.parseLong(request.getPhone()), hashedPassword, Role.TEACHER);
        TeacherDetails teacherDetails = new TeacherDetails();
        teacherDetails.setUser(user);
        teacherDetails.setDepartment(request.getDepartment());

        // Store user in the database
        int result = userService.addTeacherUsers(
                request.getUserName(),
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                hashedPassword,
                Role.TEACHER.toString(),
                request.getPhone(),
                teacherDetails
        );

        // Check if the user was successfully added
        if (result != 1) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: Teacher registration failed.");
        }

        // Return success response
        return ResponseEntity.status(HttpStatus.CREATED).body("Teacher added successfully!");
    }
}
