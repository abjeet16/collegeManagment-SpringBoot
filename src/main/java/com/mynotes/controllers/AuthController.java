package com.mynotes.controllers;

import com.mynotes.dto.requests.LoginRequest;
import com.mynotes.dto.responses.AuthResponse;
import com.mynotes.enums.Role;
import com.mynotes.models.StudentDetails;
import com.mynotes.models.TeacherDetails;
import com.mynotes.models.User;
import com.mynotes.services.JwtTokenService;
import com.mynotes.services.auth.MyCustomUserDetailService;
import com.mynotes.services.auth.MyCustomUserDetails;
import com.mynotes.services.auth.UserService;
import org.apache.poi.ss.formula.atp.Switch;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

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
                                 @RequestParam("password") String password,
                                 @RequestParam("phone") String phone) {

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
        int result = userService.signUpUser(userName, firstName, lastName, email, hashed_password, Role.ADMIN.toString(), phone);
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

    @PostMapping("student/register-bulk")
    public ResponseEntity<Map<String, Object>> registerStudentFromFile() {
        String filePath = "users/studentDetails.xlsx";

        List<String> failedEntries = new ArrayList<>();
        int successCount = 0;

        try {
            Path absolutePath = Paths.get(filePath).toAbsolutePath();

            try (Workbook workbook = new XSSFWorkbook(Files.newInputStream(absolutePath))) {
                Sheet sheet = workbook.getSheetAt(0);

                for (Row row : sheet) {
                    if (row.getRowNum() == 0) continue; // Skip header row

                    try {
                        // Extract cell values
                        String userName = getCellValueAsString(row.getCell(0));
                        String firstName = getCellValueAsString(row.getCell(1));
                        String lastName = getCellValueAsString(row.getCell(2));
                        String email = getCellValueAsString(row.getCell(3));
                        String password = getCellValueAsString(row.getCell(4));
                        String course = getCellValueAsString(row.getCell(5));
                        String section = getCellValueAsString(row.getCell(6));
                        int batchYear = (int) row.getCell(7).getNumericCellValue();
                        String phone = getCellValueAsString(row.getCell(8));

                        // Validate row data
                        if (!isRowValid(userName, firstName, lastName, email, password, phone)) {
                            failedEntries.add("Invalid data at row: " + row.getRowNum());
                            continue;
                        }

                        if (userService.doesWithEmailExist(email)) {
                            failedEntries.add("Email already exists: " + email);
                            continue;
                        }

                        // Create User
                        String hashedPassword = passwordEncoder.encode(password);
                        User user = new User(userName, firstName, lastName, email, Long.parseLong(phone), hashedPassword, Role.STUDENT);

                        // Create StudentDetails
                        StudentDetails studentDetails = new StudentDetails();
                        studentDetails.setUser(user);

                        // Save User and StudentDetails
                        int result = userService.addStudentUsers(userName, firstName, lastName, email, hashedPassword, Role.STUDENT.toString(), phone, studentDetails,course,section,batchYear);

                        if (result == 1) successCount++;
                        else failedEntries.add("Failed to register user: " + email);

                    } catch (Exception e) {
                        failedEntries.add("Error processing row: " + row.getRowNum() + " - " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error processing the file: " + e.getMessage()));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("successCount", successCount);
        response.put("failedEntries", failedEntries);

        return ResponseEntity.status(successCount > 0 ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST).body(response);
    }

    private boolean isRowValid(String userName, String firstName, String lastName, String email, String password, String phone) {
        // Add more validations as needed (e.g., regex for email, length checks)
        return userName != null && firstName != null && lastName != null &&
                email != null && email.contains("@") &&
                password != null && phone != null && !phone.isEmpty();
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue());
            default:
                return null;
        }
    }
}
// END OF AUTH REST CONTROLLER CLASS.
