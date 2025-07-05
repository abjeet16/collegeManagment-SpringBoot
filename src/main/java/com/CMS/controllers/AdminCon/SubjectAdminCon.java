package com.CMS.controllers.AdminCon;

import com.CMS.dto.requests.AddSubjectReqDTO;
import com.CMS.dto.responses.SubjectDTO;
import com.CMS.services.SubjectService;
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
public class SubjectAdminCon {

    private final SubjectService subjectService;

    private final PasswordEncoder passwordEncoder;


    // Add subject
    @PostMapping("/add_subject")
    private ResponseEntity<String> addSubject(@RequestBody AddSubjectReqDTO addSubjectReqDTO) {
        if (addSubjectReqDTO.getSubjectId() == null || addSubjectReqDTO.getSubjectName() == null || addSubjectReqDTO.getCourse() == null || addSubjectReqDTO.getSemester() <= 0 || addSubjectReqDTO.getSemester() > 8) {
            return ResponseEntity.badRequest().body("Invalid subject details");
        }
        try {
            subjectService.addSubject(addSubjectReqDTO);
            return ResponseEntity.ok("Subject added successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add subject: " + e.getMessage());
        }
    }//END OF ADD SUBJECT

    // Get subjects by course id and class id
    @GetMapping("/course/{courseId}/class/{classId}/subjects")
    public ResponseEntity<List<SubjectDTO>> getSubjectsByCourseId(@PathVariable int courseId, @PathVariable int classId) {
        List<SubjectDTO> subjects;
        // if the class id is -1 then get all subjects of the course
        if (classId == -1) {
            subjects = subjectService.getSubjectsByCourseId(courseId);
        }else {
            // if the class id not -1 then get all subjects of the class
            subjects = subjectService.getSubjectsOfAClass(classId,courseId);
        }
        return ResponseEntity.ok(subjects);
    }//END OF GET SUBJECTS BY COURSE ID AND CLASS ID

    @DeleteMapping("/subject/{subjectId}")
    public ResponseEntity<String> deleteSubject(@PathVariable int subjectId, @RequestParam String adminPassword) {
        MyCustomUserDetails user = (MyCustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        // Verify the password instead of decoding
        if (!passwordEncoder.matches(adminPassword, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
        }
        try {
            subjectService.deleteSubject(subjectId);
            return ResponseEntity.ok("Subject deleted successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    @PutMapping("/subject")
    public ResponseEntity<String> updateSubject(@RequestBody SubjectDTO subjectDTO) {
        try {
            subjectService.updateSubject(subjectDTO);
            return ResponseEntity.ok("Subject updated successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }
}
