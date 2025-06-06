package com.mynotes.controllers;

import com.mynotes.dto.responses.UserProfileDTO;
import com.mynotes.services.AttendanceService;
import com.mynotes.services.auth.MyCustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequestMapping("User")
@RequiredArgsConstructor
public class UserController {

    private final AttendanceService attendanceService;

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
