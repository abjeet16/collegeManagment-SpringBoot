package com.CMS.controllers.AdminCon;

import com.CMS.dto.requests.*;
import com.CMS.dto.responses.*;
import com.CMS.enums.Role;
import com.CMS.services.auth.MyCustomUserDetails;
import com.CMS.services.auth.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("Admin")
public class AdminController {

    private final PasswordEncoder passwordEncoder;

    private final UserService userService;

    @PostMapping("/addAdmin")
    public ResponseEntity<String> addUser(@Valid @RequestBody addAdminRequest request) {

        // Check if email already exists
        if (userService.doesWithEmailExist(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Email is already registered.");
        }

        // Hash the password securely
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // Store user in the database
        int result = userService.signUpUser(
                request.getUserName(),
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                hashedPassword,
                Role.TEACHER.toString(),
                request.getPhone()
        );

        // Check if the user was successfully added
        if (result != 1) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: User registration failed.");
        }

        // Return success response
        return ResponseEntity.status(HttpStatus.CREATED).body("User Sign-Up Successful!");
    }

    // Get all admins
    // using the all students of a class as response
    @GetMapping
    public ResponseEntity<List<AllStudentsOfAClass>> getAdmins() {
        List<AllStudentsOfAClass> admins = userService.getAdmins();
        MyCustomUserDetails user = (MyCustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        admins.removeIf(admin -> Objects.equals(admin.getStudentId(), user.getUserId()));
        if (admins.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
        return ResponseEntity.ok(admins);
    }

    @PostMapping
    public ResponseEntity<String> addAdmin(@RequestBody addAdminRequest addAdminReqDTO) {
        try {
            addAdminReqDTO.setPassword(passwordEncoder.encode(addAdminReqDTO.getPassword()));
            String result = userService.addAdmin(addAdminReqDTO);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add admin: " + e.getMessage());
        }
    }

    @GetMapping("/{adminId}")
    public ResponseEntity<UserProfileDTO> getAdminById(@PathVariable String adminId) {
        UserProfileDTO admin = userService.getAdminById(adminId);
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(admin);
    }
}
