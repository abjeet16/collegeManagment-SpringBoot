package com.mynotes.config.filters;

import com.mynotes.services.JwtTokenService;
import com.mynotes.services.auth.MyCustomUserDetailService;
import com.mynotes.services.auth.MyCustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // Marks this class as a Spring-managed component for dependency injection.
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenService jwtTokenService;
    // Service for handling JWT operations such as extraction and validation.

    @Autowired
    private MyCustomUserDetailService myCustomUserDetailService;
    // Service for loading user details from the database.

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // GET AUTHENTICATION HEADER:
        String authHeader = request.getHeader("Authorization");
        // Retrieves the `Authorization` header from the incoming HTTP request.

        // SET JWT PROPERTY:
        String jwtToken = null; // Stores the extracted JWT token.
        // SET USERNAME PROPERTY:
        String userEmail = null; // Stores the extracted username (email).

        // CHECK / VALIDATE AUTHORIZATION HEADER:
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // If the `Authorization` header is missing or does not start with "Bearer ", continue with the filter chain.
            filterChain.doFilter(request, response);
            return; // End execution.
        }
        // END OF CHECK / VALIDATE AUTHORIZATION HEADER.

        // SET JWT TOKEN VALUE RETRIEVED FROM AUTHORIZATION HEADER:
        jwtToken = authHeader.substring(7);
        // Extracts the JWT token by removing the "Bearer " prefix.

        // EXTRACT THE USER EMAIL FROM JWT TOKEN:
        userEmail = jwtTokenService.extractUsername(jwtToken);
        // Retrieves the username (email) from the JWT payload.

        // CHECK IF USER NOT NULL AND IS AUTHENTICATED:
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // If a username is extracted and no authentication is already set in the security context:

            UserDetails userDetails = myCustomUserDetailService.loadUserByUsername(userEmail);
            // Load user details (like username, password, roles) from the database.

            // VALIDATE TOKEN:
            if (jwtTokenService.validateToken(jwtToken, userDetails)) {
                // If the token is valid:

                UsernamePasswordAuthenticationToken userAuthToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, // Principal (user data).
                                null,        // Credentials (password not required for JWT validation).
                                userDetails.getAuthorities() // Roles and permissions.
                        );
                // Creates an authentication token for Spring Security.

                userAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // Attaches additional request-related details (e.g., IP address).

                SecurityContextHolder.getContext().setAuthentication(userAuthToken);
                // Sets the authenticated user in the Spring Security context.
            }
            // END OF VALIDATE TOKEN.
        }
        // END OF CHECK IF USER NOT NULL AND IS AUTHENTICATED.

        // MOVE TO NEXT FILTER:
        filterChain.doFilter(request, response);
        // Pass the request and response to the next filter in the chain.
    }
    // END OF DO FILTER INTERNAL METHOD.
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Define public endpoints to bypass JWT validation
        String path = request.getRequestURI();
        return path.startsWith("/api/v1/auth/");
        // Exclude `/api/v1/auth/**` from the filter
    }
}
// END OF JWT AUTHENTICATION FILTER CLASS.
