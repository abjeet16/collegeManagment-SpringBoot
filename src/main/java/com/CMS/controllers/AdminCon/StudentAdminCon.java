package com.CMS.controllers.AdminCon;

import com.CMS.dto.requests.BulkStudentRegistrationRequest;
import com.CMS.dto.requests.StudentRegistrationDTO;
import com.CMS.dto.responses.AllStudentsOfAClass;
import com.CMS.dto.responses.StudentDetailsResponse;
import com.CMS.models.ClassEntity;
import com.CMS.services.ClassService;
import com.CMS.services.StudentService;
import com.CMS.services.auth.MyCustomUserDetails;
import com.CMS.services.auth.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("Admin")
public class StudentAdminCon {

    private final StudentService studentService;

    @PersistenceContext
    private EntityManager entityManager;

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final ClassService classService;

    // Get students of a class
    @GetMapping("/class/{classId}/students")
    public ResponseEntity<List<AllStudentsOfAClass>> getStudentsOfAClass(@PathVariable int classId) {
        List<AllStudentsOfAClass> students = studentService.getStudentsOfAClass(classId);
        return ResponseEntity.ok(students);
    }//END OF GET STUDENTS OF A CLASS

    // Get student by id
    @GetMapping("/student/{studentId}")
    public ResponseEntity<StudentDetailsResponse> getStudentById(@PathVariable String studentId) {
        StudentDetailsResponse student = studentService.getStudentById(studentId);
        return ResponseEntity.ok(student);
    }//END OF GET STUDENT BY ID

    @PostMapping("student/register-bulk")
    public ResponseEntity<Map<String, Object>> registerStudentsFromBody(
            @RequestBody BulkStudentRegistrationRequest request) {

        // Get only a reference to the class (no DB fetch)
        ClassEntity classEntity = entityManager.getReference(ClassEntity.class, request.getClassEntityId());

        // 🔐 Encode passwords before saving
        for (StudentRegistrationDTO dto : request.getStudents()) {
            dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        Map<String, Object> result = userService.saveAllStudentsInBatch(
                request.getStudents(),
                classEntity
        );

        int successCount = (int) result.get("successCount");

        return ResponseEntity
                .status(successCount > 0 ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST)
                .body(result);
    }

    @PutMapping("/promoteStudents")
    public ResponseEntity<String> promoteStudents(@RequestParam String password) {
        MyCustomUserDetails user = (MyCustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
        }

        return ResponseEntity.ok(classService.promoteStudents());
    }

    @PutMapping("/demoteStudents")
    public ResponseEntity<String> demoteStudents(@RequestParam String password) {
        MyCustomUserDetails user = (MyCustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
        }

        return ResponseEntity.ok(classService.demoteStudents());
    }

    @DeleteMapping("/deleteStudent/{studentId}")
    public ResponseEntity<String> deleteStudent(@PathVariable String studentId,@RequestParam String adminPassword) {
        MyCustomUserDetails user = (MyCustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
        if (!passwordEncoder.matches(adminPassword, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
        }
        return ResponseEntity.ok(userService.deleteStudent(studentId));
    }

}
