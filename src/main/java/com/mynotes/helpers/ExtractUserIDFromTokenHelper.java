package com.mynotes.helpers;

import com.mynotes.services.JwtTokenService;
import com.mynotes.services.auth.MyCustomUserDetailService;
import com.mynotes.services.auth.MyCustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExtractUserIDFromTokenHelper {

    // Autowire the JwtTokenService to handle JWT operations
    @Autowired
    private JwtTokenService jwtTokenService;

    // Autowire the custom user detail service to fetch user details
    @Autowired
    private MyCustomUserDetailService myCustomUserDetailService;

    // Property to hold custom user details
    private MyCustomUserDetails myCustomUserDetails;

    /**
     * Method to extract the user ID from the JWT token present in the request.
     * @param request The HttpServletRequest object
     * @return The user ID extracted from the token
     */
    public int getUserIdFromToken(HttpServletRequest request) {
        // Initialize the user ID variable
        Integer user_id = null;

        // Retrieve the "Authorization" header from the request
        String authHeader = request.getHeader("Authorization");

        // Initialize variables to hold the JWT token and user email
        String jwtToken = null;
        String userEmail = null;

        // Check if the "Authorization" header exists and starts with "Bearer "
        if (authHeader != null || authHeader.startsWith("Bearer ")) {
            // Extract the JWT token from the header (remove "Bearer " prefix)
            jwtToken = authHeader.substring(7);

            // Extract the username (email) from the token
            userEmail = jwtTokenService.extractUsername(jwtToken);
        }

        // If a valid username is extracted, proceed to fetch user details
        if (userEmail != null) {
            // Load the user details using the custom user detail service
            myCustomUserDetails = (MyCustomUserDetails) myCustomUserDetailService.loadUserByUsername(userEmail);

            // Extract and set the user ID from the custom user details
            user_id = myCustomUserDetails.getUserId();
        }

        // Return the extracted user ID
        return user_id;
    }
    // End of getUserIdFromToken method
}
// End of ExtractUserIDFromTokenHelper class
