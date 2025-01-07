package com.mynotes.services.auth;

import com.mynotes.models.User;
import com.mynotes.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service // Marks this class as a service, part of the Spring Service layer.
public class UserService {

    @Autowired
    private UserRepository userRepository;
    // Dependency injection for `UserRepository`, which handles database operations.

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
    public int signUpUser(String first_name, String last_name, String email, String password,String role) {
        return userRepository.signUpUser(first_name, last_name, email, password,role);
        // Calls the repository method `signUpUser` to save the new user to the database.
    }// END OF SIGN UP USER SERVICE METHOD.

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
