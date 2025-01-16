package com.mynotes.models;

import com.mynotes.enums.Role;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "uucms_id")
    private String Uucms_id;
    private String first_name;
    private String last_name;
    @Column(unique = true)
    private String email;
    @Column(unique = true)
    private Long phone;
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

    public User(String uucms_id, String first_name, String last_name, String email, Long phone, String password, Role role) {
        Uucms_id = uucms_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.role = role;
    }

    public User(String uucms_id, String first_name, String last_name, String email, Long phone, String password, LocalDateTime created_at, LocalDateTime updated_at, Role role) {
        Uucms_id = uucms_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.role = role;
    }

    public User() {}

    /**
     * ---------------------------------------
     *  GETTERS AND SETTERS:
     * -------------------------------------
     * */

    public String getFirst_name() {
        return first_name;
    }

    public Long getPhone() {
        return phone;
    }

    public void setPhone(Long phone) {
        this.phone = phone;
    }

    public String getUucms_id() {
        return Uucms_id;
    }

    public void setUucms_id(String uucms_id) {
        Uucms_id = uucms_id;
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

