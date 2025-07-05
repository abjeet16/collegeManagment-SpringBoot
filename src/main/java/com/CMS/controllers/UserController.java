package com.CMS.controllers;

import com.CMS.dto.responses.UserProfileDTO;
import com.CMS.services.AttendanceService;
import com.CMS.services.JwtTokenService;
import com.CMS.services.auth.MyCustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("User")
@RequiredArgsConstructor
public class UserController {

    private final AttendanceService attendanceService;

    @Autowired
    private JwtTokenService jwtTokenService; // Service for generating and managing JWT tokens.

    @GetMapping("/my_profile")
    public ResponseEntity<UserProfileDTO> getMyProfile() {
        // Get the current authenticated user
        MyCustomUserDetails user = (MyCustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Check if the user is authenticated
        if (user == null) {
            return ResponseEntity.status(401).body(null);
        }

        // Map the user details to the UserProfileDTO
        UserProfileDTO userProfile = new UserProfileDTO(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone()
        );

        // Return the user profile
        return ResponseEntity.ok(userProfile);
    }
}
