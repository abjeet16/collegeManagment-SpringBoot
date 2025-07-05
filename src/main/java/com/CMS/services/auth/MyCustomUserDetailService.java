package com.CMS.services.auth;

import com.CMS.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service // Marks this class as a service in Spring, specifically for handling user authentication.
public class MyCustomUserDetailService implements UserDetailsService {

    @Autowired
    private UserService userService;
    // Dependency injection for `UserService`, which handles the retrieval of user data.

    /**
     * Loads a user by their username (email in this case) for authentication.
     * @param username The username (email) of the user to load.
     * @return A `UserDetails` object containing user-specific details.
     * @throws UsernameNotFoundException if the user cannot be found in the system.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Fetch the user from the database using the email as the identifier.
        User user = userService.loadUserByUucms_id(username);

        // Check if the user exists:
        if (user == null) {
            throw new UsernameNotFoundException("Unable To Load User");
            // Throw an exception if the user is not found.
        }

        // Return a custom `UserDetails` object with user data for Spring Security.
        return new MyCustomUserDetails(user);
    }
    // END OF LOAD USER BY USERNAME METHOD.
}
// END OF MY CUSTOM USER DETAILS SERVICE CLASS.
