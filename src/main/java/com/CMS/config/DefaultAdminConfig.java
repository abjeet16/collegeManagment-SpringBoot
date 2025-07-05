package com.CMS.config;

import com.CMS.enums.Role;
import com.CMS.models.User;
import com.CMS.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DefaultAdminConfig {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository; // Assuming you have a repository to interact with the database.

    @Bean
    public CommandLineRunner addDefaultAdmin() {
        return args -> {
            // Check if the admin already exists
            if (!userRepository.existsByEmail("admin@gmail.com")) {
                // Create the default admin user
                User admin = new User(
                        "ADMIN123", // Example unique ID, you can adjust it as per your logic.
                        "admin",
                        "admin",
                        "admin@gmail.com",
                        0L, // Phone number repeated 10 times, so 0 will work.
                        passwordEncoder.encode("admin@12345"), // Encrypted password for admin@12345
                        Role.ADMIN
                );
                // Save the admin user to the database
                userRepository.save(admin);
            }
        };
    }
}

