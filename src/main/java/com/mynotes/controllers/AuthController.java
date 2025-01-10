package com.mynotes.controllers;

import com.mynotes.dto.requests.LoginRequest;
import com.mynotes.dto.responses.AuthResponse;
import com.mynotes.enums.Role;
import com.mynotes.services.JwtTokenService;
import com.mynotes.services.auth.MyCustomUserDetailService;
import com.mynotes.services.auth.MyCustomUserDetails;
import com.mynotes.services.auth.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth") // Base path for all endpoints in this controller.
public class AuthController {

    @Autowired
    private PasswordEncoder passwordEncoder; // Used to hash and compare passwords securely.

    @Autowired
    private AuthenticationManager authenticationManager; // Manages user authentication.

    @Autowired
    private UserService userService; // Handles user-related operations such as registration.

    @Autowired
    private MyCustomUserDetailService myCustomUserDetailService; // Custom service to load user details.

    @Autowired
    private JwtTokenService jwtTokenService; // Service for generating and managing JWT tokens.

    @PostMapping("user/login")
    public ResponseEntity signIn(@RequestBody LoginRequest loginRequest) {

        // SET AUTHENTICATION:
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(loginRequest.getUucms_id(), loginRequest.getPassword()));
        // The above line authenticates the user by checking the email and password.
        // If successful, it returns an authenticated `Authentication` object; otherwise, it throws an exception.

        // SET USER OBJECT:
        MyCustomUserDetails userDetails =
                (MyCustomUserDetails) myCustomUserDetailService.loadUserByUsername(loginRequest.getUucms_id());
        // Fetches the custom user details, such as roles and permissions, using the provided email.

        // SET SECURITY CONTEXT:
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Stores the authenticated user's information in the SecurityContext, making it available throughout the application.

        // GENERATE TOKEN:
        String token = jwtTokenService.generateToken(userDetails);
        // Generates a JWT token for the authenticated user using their details.

        System.out.println(token);

        // SET RESPONSE:
        AuthResponse response = new AuthResponse(token, userDetails);
        // Constructs a response object containing the token and user details.

        // RETURN RESPONSE:
        return new ResponseEntity(response, HttpStatus.ACCEPTED);
        // Returns the response with HTTP status 202 (Accepted).
    }
    // END OF USER SIGN IN POST METHOD.

    @PostMapping("user/register")
    public ResponseEntity signUp(@RequestParam("User_name") String userName,
                                 @RequestParam("first_name") String firstName,
                                 @RequestParam("last_name") String lastName,
                                 @RequestParam("email") String email,
                                 @RequestParam("password") String password) {

        // This step should verify if the email is already registered. Implementation of this logic is not shown here.
        if (userService.doesWithEmailExist(email)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Email already exists");
        }

        // HASH PASSWORD:
        String hashed_password = passwordEncoder.encode(password);
        // Hashes the user's password using a secure algorithm (e.g., bcrypt) before storing it in the database.
        System.out.println(firstName + " " + lastName + " " + email + " " + hashed_password);

        // STORE USER:
        int result = userService.signUpUser(userName,firstName, lastName, email, hashed_password, Role.USER.toString());
        // Calls the `userService` to save the user's details in the database.
        // Returns `1` if successful or some other value if there's an issue.

        // CHECK FOR RESULT SET:
        if (result != 1) {
            return new ResponseEntity("Something went wrong", HttpStatus.BAD_REQUEST);
            // Returns an error response if the user could not be registered.
        }
        // END OF CHECK FOR RESULT SET.

        // RETURN SUCCESS RESPONSE:
        return new ResponseEntity("User Sign Up Successful!", HttpStatus.CREATED);
        // Returns a success message with HTTP status 201 (Created) if the registration is successful.
    }
    // END OF USER SIGN UP POST METHOD.

    @PostMapping("/is_token_expired")
    public ResponseEntity checkTokenExpiry(@RequestParam("token") String token) {
        Boolean response = jwtTokenService.isTokenExpired(token);
        //returns true if token is expired
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    // END OF TOKEN EXPIRY CHECK POST METHOD

    public boolean checkEmailAndPassword(String email, String password) {
        if (!userService.doesWithEmailExist(email)) {
            return false;
        }
        if (userService.doesWithEmailExist(email)) {
            String hasPassword = userService.getPasswordByEmail(email);
            return passwordEncoder.matches(password, hasPassword);
        }
        return true;
    }
}
// END OF AUTH REST CONTROLLER CLASS.
