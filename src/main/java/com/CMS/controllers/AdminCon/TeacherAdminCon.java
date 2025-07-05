package com.CMS.controllers.AdminCon;

import com.CMS.dto.requests.AddTeacherRequest;
import com.CMS.dto.requests.AssignTeacherDTO;
import com.CMS.dto.responses.AllTeachersDTO;
import com.CMS.dto.responses.TeacherDetailResponse;
import com.CMS.enums.Role;
import com.CMS.models.TeacherDetails;
import com.CMS.models.User;
import com.CMS.services.AssignedTeacherService;
import com.CMS.services.auth.MyCustomUserDetails;
import com.CMS.services.auth.UserService;
import jakarta.validation.Valid;
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
public class TeacherAdminCon {

    private final UserService userService;

    private final AssignedTeacherService assignedTeacherService;

    private final PasswordEncoder passwordEncoder;

    @GetMapping("/Teachers")
    public ResponseEntity<List<AllTeachersDTO>> getAllTeachers() {
        return ResponseEntity.ok(userService.getAllTeachers());
    } // ResponseEntity<List<AllTeachersDTO>>

    @GetMapping("/Teacher/{teacherId}/details")
    public ResponseEntity<TeacherDetailResponse> getTeacherById(@PathVariable String teacherId) {
        return ResponseEntity.ok(userService.getTeacherById(teacherId));
    }

    // Assign teacher
    @PostMapping("/assignTeacher")
    public ResponseEntity<String> assignTeacher(@RequestBody AssignTeacherDTO assignTeacherDTO) {
        try {
            assignedTeacherService.assignTeacherToSubject(assignTeacherDTO);
            return ResponseEntity.ok("Teacher assigned successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }//END OF ASSIGN TEACHER

    // Update teacher
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
    }//END OF UPDATE TEACHER

    @DeleteMapping("/deleteTeacher/{teacherId}")
    public ResponseEntity<String> deleteTeacher(@PathVariable String teacherId,@RequestParam String adminPassword) {
        MyCustomUserDetails user = (MyCustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
        if (!passwordEncoder.matches(adminPassword, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
        }
        return ResponseEntity.ok(userService.deleteTeacher(teacherId));
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
        teacherDetails.setDepartment(request.getDepartment().toUpperCase());

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
