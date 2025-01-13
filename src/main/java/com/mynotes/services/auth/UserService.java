package com.mynotes.services.auth;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mynotes.models.StudentDetails;
import com.mynotes.models.TeacherDetails;
import com.mynotes.models.User;
import com.mynotes.repository.StudentDetailsRepository;
import com.mynotes.repository.TeacherDetailsRepository;
import com.mynotes.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service // Marks this class as a service, part of the Spring Service layer.
public class UserService {

    @Autowired
    private UserRepository userRepository;
    // Dependency injection for `UserRepository`, which handles database operations.

    @Autowired
    private StudentDetailsRepository studentDetailsRepository;

    @Autowired
    private TeacherDetailsRepository teacherDetailsRepository;

    /**
     * Method to load a user by their email.
     *
     * @param email The email of the user to load.
     * @return A `User` object with the details of the user corresponding to the email.
     */
    public User loadUserByEmail(String email) {
        return userRepository.getUserByEmail(email);
        // Calls the repository method `getUserByEmail` to fetch the user details.
    }
    // END OF LOAD USER BY EMAIL.

    public User loadUserByUucms_id(String uucms_id) {
        return userRepository.getUserByUucms_id(uucms_id);
    }

    /**
     * Method to check if an email already exists in the database.
     *
     * @param email The email to check.
     * @return A list of strings representing users with the given email.
     * If the list is empty, the email does not exist.
     */
    public List<String> doesEmailExist(String email) {
        return userRepository.doesEmailExist(email);
        // Calls the repository method `doesEmailExist` to check for duplicates.
    }
    // END OF CHECK IF EMAIL EXISTS SERVICE METHOD.

    /**
     * Method to register a new user in the database.
     *
     * @param first_name The first name of the user.
     * @param last_name  The last name of the user.
     * @param email      The email of the user.
     * @param password   The hashed password of the user.
     * @return An integer indicating the success (1) or failure (0) of the operation.
     */
    public int signUpUser(String User_name,String first_name, String last_name, String email, String password,String role,String phoneNumber) {
        return userRepository.signUpUser(User_name,first_name, last_name, email, password,role,phoneNumber);
        // Calls the repository method `signUpUser` to save the new user to the database.
    }// END OF SIGN UP USER SERVICE METHOD.

    @Transactional
    public int addStudentUsers(String User_name,String first_name, String last_name, String email, String password,String role,String phoneNumber,StudentDetails studentDetails) {
        int result = userRepository.signUpUser(User_name,first_name, last_name, email, password,role,phoneNumber);
        studentDetailsRepository.save(studentDetails);
        addStudentToFireBase(studentDetails);
        return result;
    }

    private void addStudentToFireBase(StudentDetails studentDetails) {
        try {
            // Get a reference to the Firebase Realtime Database
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

            // Extract necessary details from the StudentDetails object
            String courseName = studentDetails.getCourse();
            String section = studentDetails.getSection();
            int batchYear = studentDetails.getBatchYear();
            User user = studentDetails.getUser(); // Assuming `user` is not null
            String userId = user.getUucms_id();         // Unique identifier for the student
            String name = user.getFirst_name()+" "+user.getLast_name();         // Student's name

            // Validate necessary fields
            if (courseName == null || section == null || userId == null || name == null) {
                System.out.println("Invalid data. Cannot proceed.");
                return;
            }

            // Create the hierarchical key in the format `course_section_batchYear`
            String classKey = courseName + "_" + section + "_" + batchYear;

            // Create a reference for the specific student under the classKey
            DatabaseReference studentRef = databaseReference.child(classKey).child(userId);

            // Structure the student's data
            Map<String, Object> studentData = new HashMap<>();
            studentData.put("name", name);
            studentData.put("attendance", new HashMap<>()); // Empty map for attendance, to be updated later

            // Save the data for the student
            studentRef.setValueAsync(studentData);

            System.out.println("Student data saved successfully under: " + classKey + "/" + userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public int addTeacherUsers(String User_name, String first_name, String last_name, String email, String password, String role, String phoneNumber, TeacherDetails teacherDetails) {
        int result = userRepository.signUpUser(User_name,first_name, last_name, email, password,role,phoneNumber);
        teacherDetailsRepository.save(teacherDetails);
        return result;
    }

    public boolean doesWithEmailExist(String email) {
        return userRepository.existsByEmail(email);
    }// END OF CHECK IF EMAIL EXISTS SERVICE METHOD.

    public boolean doesUserWithEmailPasswordExist(String email, String password) {
        return userRepository.existsByEmailAndPassword(email, password);
    }// END OF CHECK IF USER WITH EMAIL AND PASSWORD EXISTS.

    public String getPasswordByEmail(String email) {
        return userRepository.getPasswordByEmail(email);
    }// END OF GET PASSWORD BY EMAIL
}
// END OF USER SERVICE CLASS.
