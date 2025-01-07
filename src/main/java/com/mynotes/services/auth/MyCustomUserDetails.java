package com.mynotes.services.auth;

import com.mynotes.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class MyCustomUserDetails implements UserDetails {

    // User object to encapsulate user-related information
    private User user;

    // Constructor to initialize the custom user details with a User object
    public MyCustomUserDetails(User user) {
        this.user = user;
    }

    /**
     * Returns the authorities granted to the user. In this case, every user gets a "ROLE_USER".
     * @return a collection containing a single authority
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Assign a single role of "ROLE_USER" to every user
        return Collections.singleton(new SimpleGrantedAuthority(user.getRole().toString()));
    }

    /**
     * Returns the user ID.
     * @return the user ID from the User object
     */
    public int getUserId() {
        return this.user.getUser_id();
    }

    /**
     * Returns the first name of the user.
     * @return the first name from the User object
     */
    public String getFirstName() {
        return this.user.getFirst_name();
    }

    /**
     * Returns the last name of the user.
     * @return the last name from the User object
     */
    public String getLastName() {
        return this.user.getLast_name();
    }

    /**
     * Returns the user's password for authentication purposes.
     * @return the password from the User object
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Returns the username (email in this case) for authentication purposes.
     * @return the email from the User object
     */
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    /**
     * Indicates whether the user's account is expired.
     * @return true since account expiration is not implemented
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user's account is locked.
     * @return true since account locking is not implemented
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indicates whether the user's credentials (password) are expired.
     * @return true since credential expiration is not implemented
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user's account is enabled.
     * @return true since account enabling/disabling is not implemented
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
// End of MyCustomUserDetails class

