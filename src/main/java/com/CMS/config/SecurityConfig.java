package com.CMS.config;

import com.CMS.config.filters.JwtAuthenticationFilter;
import com.CMS.enums.Role;
import com.CMS.services.auth.MyCustomUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    // Custom filter to process and validate JWT tokens.

    @Autowired
    private MyCustomUserDetailService myCustomUserDetailService;
    // Service to fetch user details from the database.

    // SECURITY BEAN / METHOD:
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf().disable()
                // Disables CSRF protection (not required for JWT-based stateless authentication).

                .authorizeHttpRequests()
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/teacher/**").hasAuthority(Role.TEACHER.toString())
                .requestMatchers("/Admin/**").hasAuthority(Role.ADMIN.toString())
                // Publicly accessible endpoints for authentication and general information.

                .anyRequest().authenticated()
                // All other requests require authentication.

                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                // Configures stateless session management (no server-side session storage for authentication).

                .and()
                .authenticationProvider(this.authenticationProvider())
                // Registers the custom `AuthenticationProvider` for authentication.

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        // Adds the JWT filter before Spring's default authentication filter.

        return httpSecurity.build();
    }
    // END OF SECURITY FILTER CHAIN METHOD.

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        // Provides authentication using a database-based user store.

        daoAuthenticationProvider.setUserDetailsService(myCustomUserDetailService);
        // Configures the custom `UserDetailsService` to fetch user details.

        daoAuthenticationProvider.setPasswordEncoder(this.passwordEncoder());
        // Configures the password encoder for secure password storage and verification.

        return daoAuthenticationProvider;
    }
    // END OF AUTHENTICATION PROVIDER METHOD.

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
    // END OF AUTHENTICATION MANAGER METHOD.

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    // END OF PASSWORD ENCODER METHOD.
}
// END OF SECURITY CONFIG CLASS.
