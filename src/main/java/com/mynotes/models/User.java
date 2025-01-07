package com.mynotes.models;

import com.mynotes.enums.Role;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int user_id;
    private String first_name;
    private String last_name;
    private String email;
    private String password;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime created_at = LocalDateTime.now();  // Automatically set on creation

    @UpdateTimestamp
    private LocalDateTime updated_at;  // Automatically updated on update

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER; // Default role set to USER

    @PrePersist
    protected void onCreate() {
        this.created_at = LocalDateTime.now();
    }


    /**
     * ---------------------------------------
     *  GETTERS AND SETTERS:
     * -------------------------------------
     * */

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public LocalDateTime getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}

