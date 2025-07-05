package com.CMS.controllers.AdminCon;

import com.CMS.dto.requests.AddClassReqDTO;
import com.CMS.models.ClassEntity;
import com.CMS.services.ClassService;
import com.CMS.services.StudentService;
import com.CMS.services.auth.MyCustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("Admin")
public class ClassAdminCon {

    private final ClassService classService;

    private final PasswordEncoder passwordEncoder;

    private final StudentService studentService;

    // Add class
    @PostMapping("/add_class")
    public ResponseEntity<String> addClass(@RequestBody AddClassReqDTO addClassReqDTO) {
        if (addClassReqDTO.getBatchYear() <= 2000 || addClassReqDTO.getCurrentSemester() <= 0 || addClassReqDTO.getCurrentSemester() > 8) {
            return ResponseEntity.badRequest().body("Invalid class details");
        }
        try {
            classService.addClass(addClassReqDTO);
            return ResponseEntity.ok("Class added successfully!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }//END OF ADD CLASS

    // Get classes by course id
    @GetMapping("/course/{courseId}/classes")
    public ResponseEntity<List<ClassEntity>> getClassesByCourseId(@PathVariable int courseId) {
        List<ClassEntity> classes = classService.getClassesByCourseId(courseId);
        return ResponseEntity.ok(classes);
    }//END OF GET CLASSES BY COURSE ID

    @DeleteMapping("/deleteStudents/{classId}")
    public ResponseEntity<String> deleteStudent(@PathVariable int classId,@RequestParam String password) {
        MyCustomUserDetails user = (MyCustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
        }
        return ResponseEntity.ok(studentService.deleteStudents(classId));
    }

    @PutMapping("/class/{classId}")
    public ResponseEntity<String> updateClass(@PathVariable int classId, @RequestBody AddClassReqDTO addClassReqDTO) {
        if (addClassReqDTO.getBatchYear() <= 2000 || addClassReqDTO.getCurrentSemester() <= 0 || addClassReqDTO.getCurrentSemester() > 8) {
            return ResponseEntity.badRequest().body("Invalid class details");
        }
        try {
            classService.updateClassDetails(classId, addClassReqDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
        return ResponseEntity.ok("Class updated successfully!");
    }
}
