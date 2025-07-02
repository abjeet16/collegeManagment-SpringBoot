package com.mynotes.controllers.AdminCon;

import com.mynotes.dto.requests.UserDetailChangeReq;
import com.mynotes.services.auth.MyCustomUserDetails;
import com.mynotes.services.auth.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("Admin")
@RequiredArgsConstructor
public class ProfileAdminCon {
    private final PasswordEncoder passwordEncoder;

    private final UserService userService;
    /*
     * if the password is not null then change the password
     * else change the details of the user
     */
    @PutMapping("user/changeDetails")
    public ResponseEntity<String> changeStudentDetails(@RequestBody UserDetailChangeReq studentDetails){
        MyCustomUserDetails user = (MyCustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        // Verify the password instead of decoding
        if (!passwordEncoder.matches(studentDetails.getAdminPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Admin password");
        }
        if (studentDetails.getPassword() != null) {
            studentDetails.setPassword(passwordEncoder.encode(studentDetails.getPassword()));
            return ResponseEntity.ok(userService.changeUserPassword(studentDetails));
        }
        return ResponseEntity.ok(userService.changeUserDetails(studentDetails));
    }
}
